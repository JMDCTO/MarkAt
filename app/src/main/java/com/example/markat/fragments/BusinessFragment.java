package com.example.markat.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.markat.R;
import com.example.markat.models.BusinessMap;
import com.example.markat.utils.CustomDataHolder;

public class BusinessFragment extends Fragment {

    private BusinessMap info;

    private TextView aliasTextView;
    private TextView locationTextView;
    private ImageView iconImageView;
    private Button buttonShowImpressum;
    private Runnable toolbarChange;
    
    Context context;
    FragmentManager manager;
    
    public BusinessFragment(Context context, BusinessMap info, Runnable toolbarChange, FragmentManager manager) {
        // Required empty public constructor
        this.info = info;
        this.toolbarChange = toolbarChange;
        this.context = context;
        this.manager = manager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        toolbarChange.run();

        aliasTextView = view.findViewById(R.id.textView_business_alias);
        locationTextView = view.findViewById(R.id.textView_location_data);
        iconImageView = view.findViewById(R.id.imageView_business_info_icon);
        buttonShowImpressum = view.findViewById(R.id.button_show_impressum);
        
        aliasTextView.setText(info.getAlias());
        byte[] iconByName = CustomDataHolder.DataHolderObject.getIconByName(info.getOfficial());
        Bitmap bitmap = BitmapFactory.decodeByteArray(iconByName, 0, iconByName.length);
        iconImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));

        String location = info.getStreet() + " " + info.getNumber() + ", " + info.getPostal();
        locationTextView.setText(location);
        
        buttonShowImpressum.setOnClickListener(view1 -> {
          ImpressumFragment impressumPopup = new ImpressumFragment(context, this.info);
          impressumPopup.show(manager, "impressum");
        });
    }
}
