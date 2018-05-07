package com.enrico.wallpapers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.enrico.wallpapers.adapters.WallpapersSliderAdapter;
import com.enrico.wallpapers.preferences.PreferencesUtils;
import com.enrico.wallpapers.utils.WallpapersUtils;

import java.lang.ref.WeakReference;

public class WallpaperViewerActivity extends Activity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(PreferencesUtils.resolveWallpaperViewTheme(this));
        setContentView(R.layout.wallpaper_viewer_activity);
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(new WallpapersSliderAdapter(this));
        mViewPager.setCurrentItem(getIntent().getIntExtra(WallpapersUtils.WALLPAPERS_SLIDER_POSITION, 0));
    }

    public void navigateBack(View view) {
        onBackPressed();
    }

    public void setWallpaper(View view) {

        WallpapersUtils.setBitmapFromURL(this, WallpapersUtils.getWallpapersList().get(mViewPager.getCurrentItem()).getUrl());
    }

    public void downloadWallpaper(View view) {

        new WallpapersUtils.DownloadImageTask(new WeakReference<Context>(this), false).execute(WallpapersUtils.getWallpapersList().get(mViewPager.getCurrentItem()).getUrl());

    }
}
