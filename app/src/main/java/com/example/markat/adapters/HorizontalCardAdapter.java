package com.example.markat.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markat.R;

import java.util.ArrayList;
import java.util.List;

public class HorizontalCardAdapter extends RecyclerView.Adapter<HorizontalCardAdapter.ViewHolder> {

    private List<String> title;
    private List<Bitmap> icons;

    public HorizontalCardAdapter(List<String> categories, List<Bitmap> icons) {
        this.title = new ArrayList<String>(categories);
        this.icons = new ArrayList<Bitmap>(icons);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.container_layout_home_cards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(title.get(position));
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
