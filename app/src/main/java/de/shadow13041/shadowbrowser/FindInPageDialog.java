package de.shadow13041.shadowbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.webkit.WebView;

public final class FindInPageDialog {
    private FindInPageDialog() {}

    public static void show(Activity activity, WebView webView) {
        if (webView == null) return;

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = Math.round(16 * activity.getResources()
                .getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, 0);

        EditText query = new EditText(activity);
        query.setSingleLine(true);
        query.setHint("Suchbegriff eingeben");
        layout.addView(query, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView count = new TextView(activity);
        count.setText("0 Treffer");
        layout.addView(count);

        LinearLayout buttons = new LinearLayout(activity);
        buttons.setOrientation(LinearLayout.HORIZONTAL);

        Button previous = new Button(activity);
        previous.setText("Zurück");
        buttons.addView(previous, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        Button next = new Button(activity);
        next.setText("Weiter");
        buttons.addView(next, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        layout.addView(buttons);

        webView.setFindListener((activeMatchOrdinal, numberOfMatches,
                                 isDoneCounting) -> {
            if (isDoneCounting) {
                count.setText(numberOfMatches == 0
                        ? "Keine Treffer"
                        : (activeMatchOrdinal + 1) + " / "
                        + numberOfMatches + " Treffer");
            }
        });

        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                webView.clearMatches();
                if (s.length() > 0) {
                    webView.findAllAsync(s.toString());
                } else {
                    count.setText("0 Treffer");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        previous.setOnClickListener(v -> webView.findNext(false));
        next.setOnClickListener(v -> webView.findNext(true));

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Auf Seite suchen")
                .setView(layout)
                .setNegativeButton("Schließen", (d, w) -> {})
                .create();

        dialog.setOnDismissListener(d -> {
            webView.clearMatches();
            webView.setFindListener(null);
        });
        dialog.show();
        query.requestFocus();
    }
}
