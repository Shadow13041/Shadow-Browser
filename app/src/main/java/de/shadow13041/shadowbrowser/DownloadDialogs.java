package de.shadow13041.shadowbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.database.Cursor;
import android.widget.Toast;
import java.util.ArrayList;

public final class DownloadDialogs {
    private DownloadDialogs() {}

    public static void show(Activity activity) {
        DownloadManager manager = (DownloadManager)
                activity.getSystemService(Activity.DOWNLOAD_SERVICE);

        if (manager == null) return;

        Cursor cursor = manager.query(new DownloadManager.Query());
        ArrayList<String> entries = new ArrayList<>();

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(
                            DownloadManager.COLUMN_TITLE));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(
                            DownloadManager.COLUMN_STATUS));

                    String state;
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        state = "Abgeschlossen";
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        state = "Fehlgeschlagen";
                    } else if (status == DownloadManager.STATUS_RUNNING) {
                        state = "Wird heruntergeladen";
                    } else if (status == DownloadManager.STATUS_PAUSED) {
                        state = "Pausiert";
                    } else {
                        state = "Wartet";
                    }

                    entries.add(title + " – " + state);
                }
            } finally {
                cursor.close();
            }
        }

        if (entries.isEmpty()) {
            new AlertDialog.Builder(activity)
                    .setTitle("Downloads")
                    .setMessage("Noch keine Downloads vorhanden.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Downloads")
                .setItems(entries.toArray(new String[0]), (dialog, which) ->
                        Toast.makeText(activity, entries.get(which),
                                Toast.LENGTH_SHORT).show())
                .setPositiveButton("Schließen", null)
                .show();
    }
}
