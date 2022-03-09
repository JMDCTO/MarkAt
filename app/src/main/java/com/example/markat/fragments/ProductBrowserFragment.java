package com.example.markat.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.markat.R;
import com.example.markat.adapters.ProductBrowserCategoriesAdapter;
import com.example.markat.adapters.ProductBrowserTagsAdapter;
import com.example.markat.models.CustomCategory;
import com.example.markat.models.CustomTag;
import com.example.markat.utils.CustomDataHolder;

import java.util.List;
import java.util.Locale;

public class ProductBrowserFragment extends Fragment {

  private Context context;
  private View mainView;
  
  private RecyclerView recyclerViewCategories;
  private RecyclerView recyclerViewTags;
  
  private CustomCategory preChosenCategory = null;
  private ProductBrowserCategoriesAdapter categoriesAdapter;
  private ProductBrowserTagsAdapter tagsAdapter;
  
  public ProductBrowserFragment(Context context, CustomCategory chosenCategory) {
    this.context = context;
    
    if(chosenCategory != null) {
      preChosenCategory = chosenCategory;
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_products, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    this.mainView = view;
    findViewsById();
  }

  private void findViewsById() {
    recyclerViewCategories = mainView.findViewById(R.id.recyclerView_product_browser_categories);
    recyclerViewTags = mainView.findViewById(R.id.recyclerView_product_browser_tags);
    
    if(preChosenCategory != null) {
      categoriesAdapter = new ProductBrowserCategoriesAdapter(context, preChosenCategory, () -> getChosenCategory());
    } else {
      categoriesAdapter = new ProductBrowserCategoriesAdapter(context, null, () -> getChosenCategory());
    }
    
    RecyclerView.LayoutManager layoutManagerProducts = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
    recyclerViewCategories.setLayoutManager(layoutManagerProducts);
    recyclerViewCategories.setAdapter(categoriesAdapter);
    
    if(preChosenCategory != null) {
      tagsAdapter = new ProductBrowserTagsAdapter(context, preChosenCategory);
    } else {
      List<CustomCategory> categories = CustomDataHolder.DataHolderObject.getCategories();
      tagsAdapter = new ProductBrowserTagsAdapter(context, categories.get(0));
    }
    RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
    recyclerViewTags.setLayoutManager(layoutManagerTags);
    recyclerViewTags.setAdapter(tagsAdapter);
  }
  
  public void getChosenCategory() {
    preChosenCategory = categoriesAdapter.getChosenCategory();
    tagsAdapter.setNewChosenCategory(preChosenCategory);
  }
}
