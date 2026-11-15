package de.shadow13041.shadowbrowser;

import android.content.Context;
import android.content.SharedPreferences;

public final class PrivacySettings {
    private static final String PREFS = "shadow_browser_privacy";

    private PrivacySettings() {}

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static boolean blockThirdPartyCookies(Context context) {
        return prefs(context).getBoolean("block_third_party_cookies", true);
    }

    public static void setBlockThirdPartyCookies(
            Context context, boolean enabled) {
        prefs(context).edit()
                .putBoolean("block_third_party_cookies", enabled).apply();
    }

    public static boolean safeBrowsing(Context context) {
        return prefs(context).getBoolean("safe_browsing", true);
    }

    public static void setSafeBrowsing(Context context, boolean enabled) {
        prefs(context).edit().putBoolean("safe_browsing", enabled).apply();
    }
}
