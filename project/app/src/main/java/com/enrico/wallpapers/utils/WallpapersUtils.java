package com.enrico.wallpapers.utils;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.enrico.wallpapers.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WallpapersUtils {

    public static String WALLPAPERS_SLIDER_POSITION = "com.enrico.wallpapers";

    private static List<Wallpaper> mWallpapers;

    public static List<Wallpaper> getWallpapersList() {
        return mWallpapers;
    }

    static List<Wallpaper> getWallpapersFromJSON(Context context) {

        final String JSON_URL = context.getString(R.string.json_url);
        final String JSON_ARRAY_IDENTIFIER = context.getString(R.string.array_identifier);

        final String ZERO_IDENTIFIER = context.getString(R.string.zero_identifier);
        final String FIRST_IDENTIFIER = context.getString(R.string.first_identifier);
        final String SECOND_IDENTIFIER = context.getString(R.string.second_identifier);
        final String THIRD_IDENTIFIER = context.getString(R.string.third_identifier);
        final String FOURTH_IDENTIFIER = context.getString(R.string.fourth_identifier);

        final List<Wallpaper> mEntries = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(JSON_URL)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (response.isSuccessful() && body != null) {

                String jsonData = body.string();

                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY_IDENTIFIER);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject arrayJSONObject = jsonArray.getJSONObject(i);
                    String url = arrayJSONObject.getString(ZERO_IDENTIFIER);
                    String thumb = arrayJSONObject.getString(FIRST_IDENTIFIER);
                    String name = arrayJSONObject.getString(SECOND_IDENTIFIER);
                    String author = arrayJSONObject.getString(THIRD_IDENTIFIER);
                    String year = arrayJSONObject.getString(FOURTH_IDENTIFIER);
                    mEntries.add(new Wallpaper(url, thumb, name, author, year));
                }
                response.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        mWallpapers = mEntries;
        return mEntries;
    }

    public static void setBitmapFromURL(Context context, String url) {

        new WallpapersUtils.DownloadImageTask(new WeakReference<>(context), true).execute(url);

    }

    private static File downloadFromUrl(Context context, String url) {

        try {
            File file = null;

            URL u = new URL(url);
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            //save to Simply Solid folder
            String path = Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.app_folder_name);

            boolean isDirectoryCreated = new File(path).exists();
            if (!isDirectoryCreated) {
                isDirectoryCreated = new File(path).mkdir();
            }

            if (isDirectoryCreated) {
                file = new File(path
                        + File.separator + url.substring(url.lastIndexOf(File.separator) + 1, url.length()));


                FileOutputStream fos = new FileOutputStream(file);
                while ((length = dis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.close();

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);
            }

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Uri getImageContentUri(Context context, String absPath) {

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media._ID}
                , MediaStore.Images.Media.DATA + "=? "
                , new String[]{absPath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));

        } else if (!absPath.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, absPath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return null;
        }
    }

    private static void applyWallpaperFromIntent(Context context, String url, File file) {
        String type = context.getString(R.string.imageType) + url.substring(url.lastIndexOf("."));

        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(getImageContentUri(context, file.getAbsolutePath()), type);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.set_as)));
    }

    public static class AsyncWallpapersLoader extends WrappedAsyncTaskLoader<Object> {

        public AsyncWallpapersLoader(Context context) {
            super(context);
        }

        @Override
        public Object loadInBackground() {
            return WallpapersUtils.getWallpapersFromJSON(getContext());
        }
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, File> {

        WeakReference<Context> mContextWeakReference;
        boolean sFromSetWallpaper;
        ProgressDialog progressDialog;
        private String mUrl;

        public DownloadImageTask(WeakReference<Context> contextWeakReference, boolean fromSetWallpaper) {
            mContextWeakReference = contextWeakReference;
            sFromSetWallpaper = fromSetWallpaper;
        }

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mContextWeakReference.get());
            progressDialog.setMessage(mContextWeakReference.get().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        protected File doInBackground(String... urls) {

            mUrl = urls[0];
            return downloadFromUrl(mContextWeakReference.get(), mUrl);
        }

        protected void onPostExecute(File file) {
            progressDialog.dismiss();
            if (sFromSetWallpaper && file != null) {
                applyWallpaperFromIntent(mContextWeakReference.get(), mUrl, file);
            } else {
                Toast.makeText(mContextWeakReference.get(), mContextWeakReference.get().getString(R.string.done), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
