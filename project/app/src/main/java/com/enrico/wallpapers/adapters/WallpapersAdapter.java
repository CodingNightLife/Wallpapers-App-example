package com.enrico.wallpapers.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.enrico.wallpapers.R;
import com.enrico.wallpapers.WallpaperViewerActivity;
import com.enrico.wallpapers.utils.Wallpaper;
import com.enrico.wallpapers.utils.WallpapersUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.SimpleViewHolder> {

    private final List<Wallpaper> mWallpapers;
    private Context mContext;
    private int mImageSize;

    public WallpapersAdapter(Context context, int size) {
        mContext = context;
        mWallpapers = WallpapersUtils.getWallpapersList();
        mImageSize = size;
    }

    @Override
    @NonNull
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_wallpaper, parent, false);

        return new SimpleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final @NonNull SimpleViewHolder holder, int position) {

        final String wallpaperThumb = mWallpapers.get(position).getThumb();

        final String wallpaperName = mWallpapers.get(position).getName();
        holder.name.setText(wallpaperName);

        final String wallPaperAuthor = mWallpapers.get(position).getAuthor();
        holder.author.setText(wallPaperAuthor);

        final String wallPaperYear = mWallpapers.get(position).getYear();
        holder.year.setText(wallPaperYear);

        Picasso.get()
                .load(wallpaperThumb)
                .resize(mImageSize, mImageSize)
                .centerCrop()
                .noFade()
                .into(holder.wall);
    }

    @Override
    public int getItemCount() {
        return mWallpapers.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView wall;
        final TextView name, author, year;

        SimpleViewHolder(View itemView) {
            super(itemView);
            wall = itemView.findViewById(R.id.wall);
            name = itemView.findViewById(R.id.name);
            author = itemView.findViewById(R.id.author);
            year = itemView.findViewById(R.id.year);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent wallpaperIntent = new Intent(mContext, WallpaperViewerActivity.class);
            wallpaperIntent.putExtra(WallpapersUtils.WALLPAPERS_SLIDER_POSITION, getAdapterPosition());
            mContext.startActivity(wallpaperIntent);
        }
    }
}