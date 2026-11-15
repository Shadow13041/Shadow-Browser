package de.shadow13041.shadowbrowser;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;

public final class BrowserContextStore {
    private static final String PREFS = "browser_context";
    private static final String KEY_TABS = "normal_tabs";

    private BrowserContextStore() {}

    public static void update(Context context, JSONArray tabs) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY_TABS, tabs.toString()).apply();
    }

    public static JSONArray getTabs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        try {
            return new JSONArray(prefs.getString(KEY_TABS, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }
}
