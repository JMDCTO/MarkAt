package com.example.markat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.markat.R;

import java.util.ArrayList;
import java.util.List;

public class HorizontalCardAdapter extends RecyclerView.Adapter<HorizontalCardAdapter.ViewHolder> {

    private List<String> title = new ArrayList<String>();
    private List<Bitmap> icons = new ArrayList<Bitmap>();

    private List<byte[]> images = new ArrayList<byte[]>();
    private Context context;
    private View view;

    public HorizontalCardAdapter(List<String> categories, List<Bitmap> icons) {
        this.title = new ArrayList<String>(categories);
        this.icons = new ArrayList<Bitmap>(icons);
    }

    public HorizontalCardAdapter(List<String> title, List<byte[]> images, Context context) {
        this.title = title;
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.container_layout_home_cards, parent, false);
        this.view = view;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(title.get(position));

        if(!images.isEmpty()) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(images.get(position), 0, images.get(position).length);
            holder.getIcon().setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView icon;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.textview_cardview);
            icon = (ImageView) view.findViewById(R.id.imageView_card_icon);

        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getIcon() {
            return icon;
        }
    }
}
