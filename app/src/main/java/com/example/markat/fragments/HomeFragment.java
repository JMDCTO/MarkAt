package com.example.markat.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.markat.R;
import com.example.markat.adapters.CategoriesCardAdapter;
import com.example.markat.adapters.PartnersCardAdapter;
import com.example.markat.models.BusinessMap;
import com.example.markat.models.CustomCategory;
import com.example.markat.utils.CustomDataHolder;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private final Context context;
    RecyclerView recyclerViewProducts;
    RecyclerView recyclerViewTopPartners;

    private final String postUrl = "/business";
    private final String businessLogoUrl = "/business/logos";

    private List<BusinessMap> partnerInfo = new ArrayList<>();
    private List<byte[]> partnerLogos = new ArrayList<byte[]>();
    private List<CustomCategory> categories = new ArrayList<>();
    private FragmentManager manager;

    TextView textViewTitleCity;

    private String region;
    private String userID;

    private View mainView;

    private Runnable changeToolbarBusiness;
    private Runnable changeToolbarCategories;

    public HomeFragment(Context context, String region,
                        String userID,
                        Runnable changeToolbarBusiness, Runnable changeToolbarCategories) {
        // Required empty public constructor
        this.context = context;
        this.region = region;
        this.userID = userID;
        this.changeToolbarBusiness = changeToolbarBusiness;
        this.changeToolbarCategories = changeToolbarCategories;
    }

    public void setUserRegionAsync(String region) {
        this.region = region;

        if(this.textViewTitleCity == null) {
            textViewTitleCity = mainView.findViewById(R.id.textView_main_location);
        }
        textViewTitleCity.setText("Händler in " + region);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mainView = view;
        recyclerViewTopPartners = view.findViewById(R.id.recyclerView_top_partners);

        Log.d("home_location", region);
        Log.d("home_userID", userID);

        textViewTitleCity = view.findViewById(R.id.textView_main_location);
        
        if(partnerInfo.isEmpty() || categories.isEmpty()) {
          passInfoToAdapters();
        }
        
        if(!this.region.equals("")) {
          setUserRegionAsync(this.region);
        }
    }

    public void passInfoToAdapters() {

        this.partnerInfo = CustomDataHolder.DataHolderObject.getLocalBusinesses();
        this.categories = CustomDataHolder.DataHolderObject.getCategories();
        
        if(!partnerInfo.isEmpty()) {
          PartnersCardAdapter partnersCardAdapterPartners = new PartnersCardAdapter(context, partnerInfo, changeToolbarBusiness);
          RecyclerView.LayoutManager layoutManagerPartners = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
          if (recyclerViewTopPartners == null && mainView != null) {
            recyclerViewTopPartners = mainView.findViewById(R.id.recyclerView_top_partners);
          }
          
          recyclerViewTopPartners.setLayoutManager(layoutManagerPartners);
          recyclerViewTopPartners.setAdapter(partnersCardAdapterPartners);
          partnersCardAdapterPartners.setManager(manager);
        }
        
        if(!categories.isEmpty() && mainView != null) {
          CategoriesCardAdapter categoriesCardAdapter = new CategoriesCardAdapter(categories, context, changeToolbarCategories);
          RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
          recyclerViewProducts = mainView.findViewById(R.id.recyclerView_products);
          recyclerViewProducts.setLayoutManager(layoutManager);
          recyclerViewProducts.setAdapter(categoriesCardAdapter);
          categoriesCardAdapter.setManager(manager);
        }
    }

    public void setManager(FragmentManager manager) {
        this.manager = manager;
    }
}
