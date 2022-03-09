package com.example.markat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markat.R;
import com.example.markat.fragments.BusinessFragment;
import com.example.markat.fragments.ProductBrowserFragment;
import com.example.markat.models.BusinessMap;
import com.example.markat.models.CustomCategory;
import com.example.markat.utils.CustomFragmentManagement;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;

public class CategoriesCardAdapter extends RecyclerView.Adapter<PartnersCardAdapter.ViewHolder> {
  
  private List<CustomCategory> categories;
  private Context context;
  private View view;
  private FragmentManager manager;
  private Runnable changeToolbar;

  public CategoriesCardAdapter(List<CustomCategory> categories, Context context, Runnable changeToolbar) {
    this.categories = new ArrayList<>(categories);
    this.context = context;
    this.changeToolbar = changeToolbar;
  }

  @NonNull
  @Override
  public PartnersCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.container_layout_home_cards, parent, false);
    this.view = view;
    return new PartnersCardAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull PartnersCardAdapter.ViewHolder holder, int position) {
      if(position < categories.size()) {
        holder.getTextView().setText(categories.get(position).getTitle());

        Bitmap bitmap = BitmapFactory.decodeByteArray(categories.get(position).getLogo(), 0, categories.get(position).getLogo().length);
        holder.getIcon().setImageBitmap(bitmap);
        holder.getIcon().setOnClickListener(view -> {
          changeToolbar.run();
          CustomCategory chosenCategory = categories.get(position);
          ProductBrowserFragment productBrowserFragment = new ProductBrowserFragment(context, chosenCategory);
          manager.beginTransaction().replace(R.id.fragment_container, productBrowserFragment).setTransition(TRANSIT_FRAGMENT_FADE).commit();
          CustomFragmentManagement.CustomFragmentManager.setFragmentType(CustomFragmentManagement.CustomFragmentManager.FragmentType.PRODUCT);
        });
      } else {
        holder.getTextView().setText("Alle Kategorien");
        holder.getIcon().setImageDrawable(context.getDrawable(R.drawable.ic_baseline_more_horiz_24));
        holder.getIcon().setOnClickListener(view -> {
          changeToolbar.run();
          ProductBrowserFragment productBrowserFragment = new ProductBrowserFragment(context, null);
          manager.beginTransaction().replace(R.id.fragment_container, productBrowserFragment).setTransition(TRANSIT_FRAGMENT_FADE).commit();
          CustomFragmentManagement.CustomFragmentManager.setFragmentType(CustomFragmentManagement.CustomFragmentManager.FragmentType.PRODUCT);
        });
      }
  }

  @Override
  public int getItemCount() {
    return this.categories.size() + 1;
  }

  public void setManager(FragmentManager manager) {
    this.manager = manager;
  }
  
}
