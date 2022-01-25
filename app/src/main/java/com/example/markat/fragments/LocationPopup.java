package com.example.markat.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.markat.R;

public class LocationPopup extends DialogFragment {

    Button buttonChooseLocation;
    CheckBox checkBoxOwnLocation;
    CheckBox checkBoxMuc;
    CheckBox checkBoxFfm;
    CheckBox checkBoxHbg;
    CheckBox checkBoxBln;
    Runnable finishRunnable;
    Context context;

    public LocationPopup(Runnable finish, Context context) {
        this.finishRunnable = finish;
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.popup_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonChooseLocation = view.findViewById(R.id.button_popup_user_location);
        checkBoxOwnLocation = view.findViewById(R.id.checkBox_own_location);
        checkBoxMuc = view.findViewById(R.id.checkBox_muc);
        checkBoxFfm = view.findViewById(R.id.checkBox_ffm);
        checkBoxHbg = view.findViewById(R.id.checkBox_hbg);
        checkBoxBln = view.findViewById(R.id.checkBox_bln);

        buttonChooseLocation.setOnClickListener(view1 -> {
            SharedPreferences preferences = context.getSharedPreferences("user_location", Context.MODE_PRIVATE);
            boolean alreadyChecked = false;
            boolean secondCheck = false;
            if(checkBoxOwnLocation.isChecked()) {
                preferences.edit().putString("city","own").apply();
                preferences.edit().putString("latitude", "").apply();
                preferences.edit().putString("longitude", "").apply();
                alreadyChecked = true;
            }
            if(checkBoxMuc.isChecked()) {
                if(alreadyChecked) {
                    Toast.makeText(context, "Du kannst nich an zwei Orten gleichzeitig sein", Toast.LENGTH_LONG).show();
                    secondCheck = true;
                } else {
                    preferences.edit().putString("city","München").apply();
                    preferences.edit().putString("latitude", "48.135125").apply();
                    preferences.edit().putString("longitude", "11.581981").apply();
                    alreadyChecked = true;

                }
            }
            if(checkBoxFfm.isChecked()) {
                if(alreadyChecked) {
                    Toast.makeText(context, "Du kannst nich an zwei Orten gleichzeitig sein", Toast.LENGTH_LONG).show();
                    secondCheck = true;

                } else {
                    preferences.edit().putString("city","Frankfurt").apply();
                    preferences.edit().putString("latitude", "50.1109221").apply();
                    preferences.edit().putString("longitude", "8.6821267").apply();
                    alreadyChecked = true;

                }
            }
            if(checkBoxHbg.isChecked()) {
                if(alreadyChecked) {
                    Toast.makeText(context, "Du kannst nich an zwei Orten gleichzeitig sein", Toast.LENGTH_LONG).show();
                    secondCheck = true;

                } else {
                    preferences.edit().putString("city","Hamburg").apply();
                    preferences.edit().putString("latitude", "53.551085").apply();
                    preferences.edit().putString("longitude", "9.993682").apply();
                    alreadyChecked = true;

                }
            }
            if(checkBoxBln.isChecked()) {
                if(alreadyChecked) {
                    Toast.makeText(context, "Du kannst nich an zwei Orten gleichzeitig sein", Toast.LENGTH_LONG).show();
                    secondCheck = true;

                } else {
                    preferences.edit().putString("city","Berlin").apply();
                    preferences.edit().putString("latitude", "52.520007").apply();
                    preferences.edit().putString("longitude", "13.404954").apply();
                    alreadyChecked = true;

                }
            }

            if(!secondCheck) {
                finishRunnable.run();
                this.dismiss();
            }
        });
    }

    public void closeDialog() {
        this.dismiss();
    }
}
