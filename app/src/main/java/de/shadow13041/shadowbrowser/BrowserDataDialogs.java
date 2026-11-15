package de.shadow13041.shadowbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.webkit.WebView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public final class BrowserDataDialogs {
    private BrowserDataDialogs() {}

    public static void show(Activity activity, WebView webView,
                            boolean history) {
        JSONArray data = history
                ? BrowserDataStore.getHistory(activity)
                : BrowserDataStore.getBookmarks(activity);

        ArrayList<String> labels = new ArrayList<>();
        ArrayList<String> urls = new ArrayList<>();

        for (int i = data.length() - 1; i >= 0; i--) {
            JSONObject item = data.optJSONObject(i);
            if (item == null) continue;
            String url = item.optString("url");
            String title = item.optString("title", url);
            if (url.isEmpty()) continue;
            labels.add(title);
            urls.add(url);
        }

        String[] entries = labels.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(history ? "Verlauf" : "Lesezeichen");

        if (entries.length == 0) {
            builder.setMessage("Noch keine Einträge vorhanden.")
                    .setPositiveButton("OK", null).show();
            return;
        }

        builder.setItems(entries, (dialog, which) ->
                webView.loadUrl(urls.get(which)));

        if (history) {
            builder.setNeutralButton("Verlauf löschen", (dialog, which) -> {
                BrowserDataStore.clearHistory(activity);
            });
        }

        builder.setNegativeButton("Schließen", null).show();
    }
}
