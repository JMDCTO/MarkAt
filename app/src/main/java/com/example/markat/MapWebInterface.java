package com.example.markat;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class MapWebInterface {

    Context context;

    public MapWebInterface(Context c) {
        context = c;
    }

    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    public void getGeoLocation() {

    }
}
