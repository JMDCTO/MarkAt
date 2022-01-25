package com.example.markat.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.markat.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements ActivityResultCallback {

    private Context context;
    ActivityResultLauncher<String[]> requestPermissionLauncher;

    private double latitude;
    private double longitude;

    WebView myWebView;
    WebViewJavaScriptInterface jsInterface;

    private boolean present;

    public MapFragment(Context context, ActivityResultLauncher<String[]> requestPermissionLauncher, double latitude, double longitude) {
        // Required empty public constructor
        this.context = context;
        this.requestPermissionLauncher = requestPermissionLauncher;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(double latitude, double longitude, Context context, ActivityResultLauncher<String[]> requestPermissionLauncher) {
        MapFragment fragment = new MapFragment(context, requestPermissionLauncher, latitude, longitude);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public boolean isPresent() {
        return present;
    }

    public void isPresent(boolean value) {
        this.present = value;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        myWebView = (WebView) view.findViewById(R.id.webView_map);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setGeolocationDatabasePath(context.getFilesDir().getPath());
        jsInterface = new WebViewJavaScriptInterface(context, latitude, longitude);
        myWebView.addJavascriptInterface(jsInterface, "app");

        myWebView.setWebChromeClient(new android.webkit.WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("LoBuy_Map", consoleMessage.message() + " -- From line " +
                        consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                return true;
            }

            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {

                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    callback.invoke(origin, true, false);

                } else if (false) {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected. In this UI,
                    // include a "cancel" or "no thanks" button that allows the user to
                    // continue using your app without granting the permission.
                    //showInContextUI(...);
                    callback.invoke(origin, true, true);

                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    });
                    callback.invoke(origin, true, true);
                }
            }
        });

        myWebView.loadUrl("file:///android_asset/map.html");
    }

    public void refreshMap() {

        SharedPreferences preferences = context.getSharedPreferences("user_location", Context.MODE_PRIVATE);
        String lat = preferences.getString("latitude", "");
        String longt = preferences.getString("longitude", "");

        if(lat != null && longt != null) {
            jsInterface.setLatitude(Double.parseDouble(lat));
            jsInterface.setLongitude(Double.parseDouble(longt));
        }
        myWebView.loadUrl("file:///android_asset/map.html");

    }

    @Override
    public void onActivityResult(Object result) {
        Object res = result;
    }

    public class WebViewJavaScriptInterface{

        private Context context;
        double latitude;
        double longitude;
        /*
         * Need a reference to the context in order to sent a post message
         */
        public WebViewJavaScriptInterface(Context context, double latitude, double longitude){
            this.context = context;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        public double getLatitude(){
            return latitude;
            //return 49.016864;
        }

        @JavascriptInterface
        public double getLongitude() {
            return longitude;
            //return 12.097408;
        }
    }
}