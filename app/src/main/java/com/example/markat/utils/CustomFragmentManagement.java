package com.example.markat.utils;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.example.markat.fragments.HomeFragment;

public class CustomFragmentManagement {

    public static class CustomFragmentManager {

        private static Context context;
        private static CustomFragmentManager.FragmentType type;
        private static FragmentManager manager;

        private HomeFragment homeFragment;
        public static CustomFragmentManager.FragmentType comingFromType = CustomFragmentManager.FragmentType.HOME;

        public CustomFragmentManager(Context context, FragmentManager manager) {
            this.context = context;
            this.manager = manager;
        }

        public static void setFragmentType(CustomFragmentManager.FragmentType typeOfFragment) {
            comingFromType = type;
            type = typeOfFragment;
        }

        public static CustomFragmentManager.FragmentType getPresentFragment() {
            return type;
        }

        public static CustomFragmentManager.FragmentType comingFrom() {
            return comingFromType;
        }

        public static enum FragmentType {
            HOME,
            MAP,
            ACCOUNT,
            CART,
            PRODUCT,
            BUSINESS
        }
    }
}
