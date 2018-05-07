package com.enrico.wallpapers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.enrico.wallpapers.adapters.WallpapersAdapter;
import com.enrico.wallpapers.preferences.PreferencesUtils;
import com.enrico.wallpapers.preferences.SettingsActivity;
import com.enrico.wallpapers.utils.AndroidVersion;
import com.enrico.wallpapers.utils.PermissionDialog;
import com.enrico.wallpapers.utils.WallpapersUtils;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mGridRecyclerView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
        }
        return false;
    }

    @Override
    public void onRefresh() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(PreferencesUtils.resolveMainTheme(this));
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mGridRecyclerView = findViewById(R.id.gridView);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Toast.makeText(this, getString(R.string.warning), Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
    }

    @Override
    @NonNull
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new WallpapersUtils.AsyncWallpapersLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        int numColumns = PreferencesUtils.resolveColumnsNumber(this);
        final WallpapersAdapter wallpapersAdapter = new WallpapersAdapter(this, getResources().getDisplayMetrics().widthPixels / numColumns);
        mGridRecyclerView.setLayoutManager(new GridLayoutManager(this, numColumns));
        mGridRecyclerView.setAdapter(wallpapersAdapter);
        checkReadStoragePermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            PermissionDialog.showPermissionDialog(getSupportFragmentManager());
        }
    }

    private void checkReadStoragePermissions() {
        if (AndroidVersion.isMarshmallow() && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            PermissionDialog.showPermissionDialog(getSupportFragmentManager());
        }
    }
}
