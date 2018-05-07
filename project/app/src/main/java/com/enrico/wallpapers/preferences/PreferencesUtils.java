package com.enrico.wallpapers.preferences;

import android.app.Activity;
import android.preference.PreferenceManager;

import com.enrico.wallpapers.R;

public class PreferencesUtils {

    static final String PREF_GRID_KEY = "com.enrico.wallpapers.grid";
    static final String PREF_THEME_KEY = "com.enrico.wallpapers.theme";

    public static int resolveColumnsNumber(Activity activity) {

        String choice = PreferenceManager.getDefaultSharedPreferences(activity)
                .getString(PREF_GRID_KEY, String.valueOf(1));
        switch (Integer.parseInt(choice)) {
            case 0:
                return 2;
            default:
            case 1:
                return 3;
            case 2:
                return 4;
        }
    }

    public static int resolveMainTheme(Activity activity) {
        boolean isDark = PreferenceManager.getDefaultSharedPreferences(activity)
                .getBoolean(PREF_THEME_KEY, false);
        return isDark ? R.style.AppThemeDark : R.style.AppTheme;
    }

    public static int resolveWallpaperViewTheme(Activity activity) {
        boolean isDark = PreferenceManager.getDefaultSharedPreferences(activity)
                .getBoolean(PREF_THEME_KEY, false);
        return isDark ? R.style.WallpaperViewerThemeDark : R.style.WallpaperViewerTheme;
    }
}
