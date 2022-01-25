package com.example.markat.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.markat.R;
import com.example.markat.adapters.HorizontalCardAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private final Context context;
    RecyclerView recyclerViewProducts;
    RecyclerView recyclerViewTopPartners;

    LinearLayoutManager layoutManager;

    TextView textViewTitleCity;

    private String region;
    private String userID;

    private View mainView;

    public HomeFragment(Context context, String region, String userID) {
        // Required empty public constructor
        this.context = context;
        this.region = region;
        this.userID = userID;
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

        List<Bitmap> productIcons = new ArrayList<Bitmap>();
        List<String> productNames = new ArrayList<String>();
        productNames.add("Lebensmittel");
        productNames.add("Kleidung");
        productNames.add("Für Zuhause");
        productNames.add("Elektronik");
        productNames.add("Sonstiges");

        List<String> partnerNames = new ArrayList<String>();
        partnerNames.add("Aamu");
        partnerNames.add("Papier Liebl");
        partnerNames.add("Titus");
        partnerNames.add("Lampen Uhl");
        partnerNames.add("WhiskeyBrothers");

        HorizontalCardAdapter horizontalCardAdapterProducts = new HorizontalCardAdapter(productNames, productIcons);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewProducts = view.findViewById(R.id.recyclerView_products);
        recyclerViewProducts.setLayoutManager(layoutManager);
        recyclerViewProducts.setAdapter(horizontalCardAdapterProducts);

        HorizontalCardAdapter horizontalCardAdapterPartners = new HorizontalCardAdapter(partnerNames, productIcons);
        RecyclerView.LayoutManager layoutManagerPartners = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTopPartners = view.findViewById(R.id.recyclerView_top_partners);
        recyclerViewTopPartners.setLayoutManager(layoutManagerPartners);
        recyclerViewTopPartners.setAdapter(horizontalCardAdapterPartners);

        Log.d("home_location", region);
        Log.d("home_userID", userID);

        textViewTitleCity = view.findViewById(R.id.textView_main_location);
    }
}