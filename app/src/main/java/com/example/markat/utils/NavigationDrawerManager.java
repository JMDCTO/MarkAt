package com.example.markat.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.example.markat.R;
import com.example.markat.models.User;
import com.google.android.material.navigation.NavigationView;

public class NavigationDrawerManager {

    Context context;

    NavigationView navigationView;
    DrawerLayout drawerLayout;

    String presentFragment;
    String lastFragment = "";

    public NavigationDrawerManager(Context context, NavigationView navView, DrawerLayout drawerLayout) {
        this.context = context;
        this.navigationView = navView;
        this.drawerLayout = drawerLayout;
        this.presentFragment = "HOME";
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void setNavigationViewChecked(String tag) {

        switch (tag) {
            case "HOME":
                navigationView.getMenu().getItem(0).setChecked(true);
                lastFragment = presentFragment;
                presentFragment = tag;
                uncheckPrevious();
                break;
            case "MAP":
                navigationView.getMenu().getItem(1).setChecked(true);
                lastFragment = presentFragment;
                presentFragment = tag;
                uncheckPrevious();
                break;
            case "PRODUCTS":
                navigationView.getMenu().getItem(2).setChecked(true);
                lastFragment = presentFragment;
                presentFragment = tag;
                uncheckPrevious();
                break;
            case "ACCOUNT":
                navigationView.getMenu().getItem(3).setChecked(true);
                lastFragment = presentFragment;
                presentFragment = tag;
                uncheckPrevious();
                break;
            case "SETTINGS":
                navigationView.getMenu().getItem(4).setChecked(true);
                lastFragment = presentFragment;
                presentFragment = tag;
                uncheckPrevious();
                break;
            default: break;
        }
    }

    private void uncheckPrevious() {
        switch (lastFragment) {
            case "HOME":
                navigationView.getMenu().getItem(0).setChecked(false);
                break;
            case "MAP":
                navigationView.getMenu().getItem(1).setChecked(false);
                break;
            case "PRODUCTS":
                navigationView.getMenu().getItem(2).setChecked(false);
                break;
            case "ACCOUNT":
                navigationView.getMenu().getItem(3).setChecked(false);
                break;
            case "SETTINGS":
                navigationView.getMenu().getItem(4).setChecked(false);
                break;
            default: break;
        }
    }

    public void setUsernameInTitle(User user) {
        navigationView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                navigationView.removeOnLayoutChangeListener(this);
                TextView title = navigationView.findViewById(R.id.textView_title_navview);
                String titleString = "Eingeloggt als: " + user.getAlias();
                title.setTextColor(context.getColor(R.color.purple_700));
                title.setText(titleString);
            }
        });
    }
}
