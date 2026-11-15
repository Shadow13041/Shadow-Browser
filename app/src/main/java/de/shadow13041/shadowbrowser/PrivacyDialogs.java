package de.shadow13041.shadowbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.Toast;

public final class PrivacyDialogs {
    private PrivacyDialogs() {}

    public static void show(Activity activity, WebView webView) {
        boolean[] checked = {
                PrivacySettings.blockThirdPartyCookies(activity),
                PrivacySettings.safeBrowsing(activity)
        };

        String[] options = {
                "Drittanbieter-Cookies blockieren",
                "Safe Browsing aktivieren"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Datenschutz")
                .setMultiChoiceItems(options, checked,
                        (dialog, which, enabled) -> checked[which] = enabled)
                .setPositiveButton("Speichern", (dialog, which) -> {
                    PrivacySettings.setBlockThirdPartyCookies(
                            activity, checked[0]);
                    PrivacySettings.setSafeBrowsing(activity, checked[1]);

                    CookieManager.getInstance().setAcceptThirdPartyCookies(
                            webView, !checked[0]);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        webView.getSettings().setSafeBrowsingEnabled(checked[1]);
                    }

                    Toast.makeText(activity, "Datenschutz gespeichert",
                            Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Websitedaten löschen", (dialog, which) ->
                        new AlertDialog.Builder(activity)
                                .setTitle("Websitedaten löschen?")
                                .setMessage("Cookies, Cache und lokale Websitedaten werden gelöscht.")
                                .setNegativeButton("Abbrechen", null)
                                .setPositiveButton("Löschen", (d, w) -> {
                                    CookieManager.getInstance().removeAllCookies(null);
                                    CookieManager.getInstance().flush();
                                    WebStorage.getInstance().deleteAllData();
                                    webView.clearCache(true);
                                    Toast.makeText(activity,
                                            "Websitedaten gelöscht",
                                            Toast.LENGTH_SHORT).show();
                                }).show())
                .setNegativeButton("Schließen", null)
                .show();
    }
}
