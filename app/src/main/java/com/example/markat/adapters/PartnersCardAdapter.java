package com.example.markat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markat.R;
import com.example.markat.fragments.BusinessFragment;
import com.example.markat.models.BusinessMap;
import com.example.markat.utils.CustomFragmentManagement;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;

public class PartnersCardAdapter extends RecyclerView.Adapter<PartnersCardAdapter.ViewHolder> {
    private List<BusinessMap> info;

    private Context context;
    private View view;
    private FragmentManager manager;

    private Runnable setToolbar;

    public PartnersCardAdapter(Context context, List<BusinessMap> businessMap, Runnable setToolbar) {
        this.context = context;
        this.setToolbar = setToolbar;
        this.info = new ArrayList<BusinessMap>(businessMap);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.container_layout_home_cards, parent, false);
        this.view = view;
        
        CardView cardView = view.findViewById(R.id.cardview);
        cardView.setCardBackgroundColor(context.getColor(R.color.custom_grey));
    
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(!info.isEmpty()) {
          holder.getTextView().setText(info.get(position).getAlias());

          BusinessMap itemInfo = info.get(position);
            Bitmap bitmap = BitmapFactory.decodeByteArray(itemInfo.getLogo(), 0, itemInfo.getLogo().length);
            holder.getIcon().setImageBitmap(bitmap);
            holder.getIcon().setOnClickListener(view -> {
                setToolbar.run();
                BusinessFragment businessFragment = new BusinessFragment(context, itemInfo, () ->{}, manager);
                manager.beginTransaction().replace(R.id.fragment_container, businessFragment).setTransition(TRANSIT_FRAGMENT_FADE).commit();
                CustomFragmentManagement.CustomFragmentManager.setFragmentType(CustomFragmentManagement.CustomFragmentManager.FragmentType.BUSINESS);
            });
        }
    }

    @Override
    public int getItemCount() {
          return info.size();
    }

    public void setManager(FragmentManager manager) {
        this.manager = manager;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView icon;
        private final CardView background;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textview_cardview);
            icon = (ImageView) view.findViewById(R.id.imageView_card_icon);
            background = (CardView) view.findViewById(R.id.cardview);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getIcon() {
            return icon;
        }
        
        public CardView getBackground() {
          return background;
        }
    }
}
