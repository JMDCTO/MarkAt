package com.example.markat.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.markat.R;
import com.example.markat.models.CustomCategory;
import com.example.markat.models.CustomTag;
import com.example.markat.utils.CustomDataHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductBrowserTagsAdapter extends RecyclerView.Adapter<ProductBrowserTagsAdapter.TagViewHolder>{
  private Context context;
  private View view;
  
  private Map<CustomCategory, List<CustomTag>> mapTagToCategory;
  private CustomCategory chosenCategory;
  private List<CustomTag> tags = new ArrayList<>();
  
  public ProductBrowserTagsAdapter(Context context, CustomCategory chosenCategory) {
    this.context = context;
    this.mapTagToCategory = new HashMap<>(CustomDataHolder.DataHolderObject.getMapTagsToCategory());
    if(chosenCategory != null) {
      this.chosenCategory = chosenCategory;
    } else {
      Set<CustomCategory> customCategories = mapTagToCategory.keySet();
      this.chosenCategory = customCategories.iterator().next();
    }

    Collection<List<CustomTag>> values = mapTagToCategory.values();
    for(List<CustomTag> list : values) {
      for(CustomTag tag : list) {
        if(tag.getParentCategory().equals(this.chosenCategory.getId())) {
          this.tags = new ArrayList<>(list);
        }
      }
    }
  }
  
  @NonNull
  @Override
  public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.container_layout_tag_cards, parent, false);
    this.view = view;
    //CardView cardView = view.findViewById(R.id.cardview);
    //cardView.setCardBackgroundColor(context.getColor(R.color.custom_grey));
    return new TagViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
    holder.getTextView().setText(tags.get(position).getTitle());
    holder.getIcon().setImageBitmap(BitmapFactory.decodeByteArray(tags.get(position).getLogo(), 0, tags.get(position).getLogo().length));
    
    holder.getBackground().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(context, tags.get(holder.getAdapterPosition()).getTitle(), Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public int getItemCount() {
    return tags.size();
  }
  
  public void setNewChosenCategory(CustomCategory newChosenCategory) {
    this.chosenCategory = newChosenCategory;
    Collection<List<CustomTag>> values = mapTagToCategory.values();
    for(List<CustomTag> list : values) {
      for(CustomTag tag : list) {
        if(tag.getParentCategory().equals(chosenCategory.getId())) {
          this.tags = new ArrayList<>(list);
        }
      }
    }
    
    notifyDataSetChanged();
  }

  public static class TagViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;
    private final ImageView icon;
    private final CardView background;

    public TagViewHolder(View view) {
      super(view);
      textView = (TextView) view.findViewById(R.id.textview_tag);
      icon = (ImageView) view.findViewById(R.id.imageView_tag_icon);
      background = (CardView) view.findViewById(R.id.cardview_tag);
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
