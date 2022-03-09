package com.example.markat.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markat.R;
import com.example.markat.models.CustomCategory;
import com.example.markat.utils.CustomDataHolder;
import java.util.List;

public class ProductBrowserCategoriesAdapter  extends RecyclerView.Adapter<PartnersCardAdapter.ViewHolder> {
  
  private Context context;
  private View view;
  private List<CustomCategory> categories;
  private CustomCategory chosenCategory = null;
  private Runnable changeTag;
  
  public ProductBrowserCategoriesAdapter(Context context, CustomCategory preChosenCategory, Runnable changeTag) {
    this.context = context;
    categories = CustomDataHolder.DataHolderObject.getCategories();
    if(preChosenCategory != null) {
      this.chosenCategory = preChosenCategory;
    } else {
      this.chosenCategory = categories.get(0);
    }
    this.changeTag = changeTag;
  }
  
  @NonNull
  @Override
  public PartnersCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.container_layout_home_cards, parent, false);
    this.view = view;
    CardView cardView = view.findViewById(R.id.cardview);
    cardView.setCardBackgroundColor(context.getColor(R.color.custom_grey));
    return new PartnersCardAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull PartnersCardAdapter.ViewHolder holder, int position) {
    holder.getTextView().setText(categories.get(position).getTitle());
    holder.getIcon().setImageBitmap(BitmapFactory.decodeByteArray(categories.get(position).getLogo(), 0, categories.get(position).getLogo().length));
    if(categories.get(position).equals(chosenCategory)) {
      holder.getBackground().setCardBackgroundColor(context.getColor(R.color.teal_700));
    } else {
      holder.getBackground().setCardBackgroundColor(context.getColor(R.color.custom_grey));
    }

    holder.getIcon().setOnClickListener(view -> {
      chosenCategory = categories.get(position);
      holder.getBackground().setCardBackgroundColor(context.getColor(R.color.teal_700));
      notifyDataSetChanged();
      changeTag.run();
    });
  }
  public CustomCategory getChosenCategory() {
    return this.chosenCategory;
  }

  @Override
  public int getItemCount() {
    return categories.size();
  }
}
