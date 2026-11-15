package de.shadow13041.shadowbrowser;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

public final class BrowserDataStore {
    private static final String PREFS = "shadow_browser_data";
    private static final int MAX_HISTORY = 500;

    private BrowserDataStore() {}

    private static JSONArray read(Context c, String key) {
        try {
            return new JSONArray(c.getSharedPreferences(PREFS, 0)
                    .getString(key, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private static void write(Context c, String key, JSONArray data) {
        c.getSharedPreferences(PREFS, 0).edit()
                .putString(key, data.toString()).apply();
    }

    public static JSONArray getBookmarks(Context c) {
        return read(c, "bookmarks");
    }

    public static JSONArray getHistory(Context c) {
        return read(c, "history");
    }

    public static void addBookmark(Context c, String url, String title) {
        add(c, "bookmarks", url, title, false);
    }

    public static void addHistory(Context c, String url, String title) {
        add(c, "history", url, title, true);
    }

    private static void add(Context c, String key, String url,
                            String title, boolean limit) {
        if (url == null || url.isEmpty()) return;

        JSONArray old = read(c, key);
        JSONArray result = new JSONArray();

        for (int i = 0; i < old.length(); i++) {
            JSONObject item = old.optJSONObject(i);
            if (item != null && !url.equals(item.optString("url"))) {
                result.put(item);
            }
        }

        JSONObject entry = new JSONObject();
        try {
            entry.put("url", url);
            entry.put("title", title == null || title.isEmpty()
                    ? url : title);
            entry.put("time", System.currentTimeMillis());
            result.put(entry);
        } catch (Exception ignored) {}

        JSONArray saved = new JSONArray();
        int start = limit ? Math.max(0, result.length() - MAX_HISTORY) : 0;
        for (int i = start; i < result.length(); i++) {
            saved.put(result.opt(i));
        }

        write(c, key, saved);
    }

    public static void removeBookmark(Context c, String url) {
        remove(c, "bookmarks", url);
    }

    public static void removeHistory(Context c, String url) {
        remove(c, "history", url);
    }

    private static void remove(Context c, String key, String url) {
        JSONArray old = read(c, key);
        JSONArray result = new JSONArray();

        for (int i = 0; i < old.length(); i++) {
            JSONObject item = old.optJSONObject(i);
            if (item != null && !url.equals(item.optString("url"))) {
                result.put(item);
            }
        }

        write(c, key, result);
    }

    public static void clearHistory(Context c) {
        write(c, "history", new JSONArray());
    }
}
