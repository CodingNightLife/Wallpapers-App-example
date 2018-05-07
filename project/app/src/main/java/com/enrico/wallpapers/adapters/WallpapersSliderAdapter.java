package com.enrico.wallpapers.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.enrico.wallpapers.R;
import com.enrico.wallpapers.utils.Wallpaper;
import com.enrico.wallpapers.utils.WallpapersUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WallpapersSliderAdapter extends PagerAdapter {

    private Context mContext;
    private List<Wallpaper> mWallpapers;

    public WallpapersSliderAdapter(Context context) {
        mContext = context;
        mWallpapers = WallpapersUtils.getWallpapersList();
    }

    @Override
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {

        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.wallpaper_view, collection, false);

        final Wallpaper wallpaper = mWallpapers.get(position);
        ImageView wallpaper_view = viewGroup.findViewById(R.id.wallpaper_view);
        wallpaper_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();

                alertDialog.setTitle(wallpaper.getName());
                alertDialog.setMessage(mContext.getString(R.string.wallpaper_summary, wallpaper.getAuthor(), wallpaper.getYear()));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return false;
            }
        });
        Picasso.get()
                .load(wallpaper.getUrl())
                .noFade()
                .into(wallpaper_view);

        collection.addView(viewGroup);
        return viewGroup;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mWallpapers.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}