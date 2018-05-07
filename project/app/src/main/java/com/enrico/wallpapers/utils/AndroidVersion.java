package com.enrico.wallpapers.utils;

public class AndroidVersion {

    public static boolean isMarshmallow() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M;
    }
}
