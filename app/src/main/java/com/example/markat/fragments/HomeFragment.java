package com.example.markat.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.markat.R;
import com.example.markat.adapters.HorizontalCardAdapter;
import com.example.markat.api.APIHttpsUtils;
import com.example.markat.models.BusinessHome;
import com.example.markat.models.BusinessMap;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    private final Context context;
    RecyclerView recyclerViewProducts;
    RecyclerView recyclerViewTopPartners;

    private final String postUrl = "/business";
    private final String businessLogoUrl = "/business/logos";

    private List<BusinessMap> partnerInfo;
    private List<byte[]> partnerLogos = new ArrayList<byte[]>();

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

        HorizontalCardAdapter horizontalCardAdapterProducts = new HorizontalCardAdapter(productNames, productIcons);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewProducts = view.findViewById(R.id.recyclerView_products);
        recyclerViewProducts.setLayoutManager(layoutManager);
        recyclerViewProducts.setAdapter(horizontalCardAdapterProducts);

        recyclerViewTopPartners = view.findViewById(R.id.recyclerView_top_partners);

        Log.d("home_location", region);
        Log.d("home_userID", userID);

        textViewTitleCity = view.findViewById(R.id.textView_main_location);
    }

    public void passDetailedInfo(List<BusinessMap> partnerInfo) {

        this.partnerInfo = partnerInfo;
        List<Bitmap> productIcons = new ArrayList<Bitmap>();
        List<String> partnerNames = new ArrayList<String>();

        for(BusinessMap business : partnerInfo) {
            partnerNames.add(business.getAlias());
        }

        requestLogos(partnerNames, productIcons);
    }

    private void requestLogos(List<String> partnerNames, List<Bitmap> partnerIcons) {

        RequestParams rp = new RequestParams();
        rp.add("method", "GET_PARTNER_LOGOS");

        for(BusinessMap business : this.partnerInfo) {
            rp.add("partner", business.getOfficial());
        }

        APIHttpsUtils.post(businessLogoUrl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("POST_METHOD_GET_LOGOS", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONArray data = serverResp.getJSONArray("partners");

                    if(data.length() != 0) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject partner = data.getJSONObject(i);
                            String nameOfResponse = partner.getString("official_name_business");
                            String imageOfResponse = partner.getString("encode");
                            parseImage(imageOfResponse);
                            Log.d(String.valueOf(i), imageOfResponse);
                        }

                        HorizontalCardAdapter horizontalCardAdapterPartners = new HorizontalCardAdapter(partnerNames, partnerLogos, context);
                        RecyclerView.LayoutManager layoutManagerPartners = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewTopPartners.setLayoutManager(layoutManagerPartners);
                        recyclerViewTopPartners.setAdapter(horizontalCardAdapterPartners);
                    } else {
                        Toast.makeText(context, "API Error when requesting partners", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Login cache error", errorResponse.toString());
            }
        });
    }

    private void parseImage(String imageOfResponse) {

        byte[] decodedString = Base64.decode(imageOfResponse, Base64.DEFAULT);

        String imageString = Base64.encodeToString(decodedString, Base64.DEFAULT);

        this.partnerLogos.add(decodedString);
    }

}