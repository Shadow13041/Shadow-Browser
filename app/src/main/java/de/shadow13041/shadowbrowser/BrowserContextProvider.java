package de.shadow13041.shadowbrowser;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.content.pm.PackageManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class BrowserContextProvider extends ContentProvider {
    public static final String AUTHORITY =
            "de.shadow13041.shadowbrowser.context";

    private static final String SHADOW_AI_PACKAGE = "com.shadowai.app";
    private static final String[] COLUMNS = {"title", "url", "active"};

    @Override
    public boolean onCreate() {
        return true;
    }

    private void checkAccess() {
        int uid = Binder.getCallingUid();
        String[] packages = getContext().getPackageManager()
                .getPackagesForUid(uid);
        boolean allowedPackage = false;

        if (packages != null) {
            for (String name : packages) {
                if (SHADOW_AI_PACKAGE.equals(name)) {
                    allowedPackage = true;
                    break;
                }
            }
        }

        boolean enabled = getContext().getSharedPreferences(
                "shadow_browser_privacy", 0)
                .getBoolean("shadowai_access_enabled", false);

        if (!allowedPackage || !enabled) {
            throw new SecurityException(
                    "ShadowAI hat keinen Zugriff auf diese Browserdaten.");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        checkAccess();

        String section = uri.getLastPathSegment();
        JSONArray data;

        if ("tabs".equals(section)) {
            data = BrowserContextStore.getTabs(getContext());
        } else if ("history".equals(section)) {
            data = BrowserDataStore.getHistory(getContext());
        } else if ("bookmarks".equals(section)) {
            data = BrowserDataStore.getBookmarks(getContext());
        } else {
            throw new IllegalArgumentException("Unbekannte Browserdaten.");
        }

        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.optJSONObject(i);
            if (item == null) continue;
            cursor.addRow(new Object[] {
                    item.optString("title", ""),
                    item.optString("url", ""),
                    item.optInt("active", 0)
            });
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/vnd.shadowbrowser.context";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Read-only");
    }

    @Override
    public int delete(Uri uri, String selection, String[] args) {
        throw new UnsupportedOperationException("Read-only");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] args) {
        throw new UnsupportedOperationException("Read-only");
    }
}
