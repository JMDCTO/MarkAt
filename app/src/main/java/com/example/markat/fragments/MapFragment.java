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
import androidx.fragment.app.FragmentManager;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.markat.R;
import com.example.markat.models.BusinessMap;
import com.example.markat.utils.CustomDataHolder;
import com.example.markat.utils.CustomFragmentManagement;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;

public class MapFragment extends Fragment implements ActivityResultCallback {

    private Context context;
    ActivityResultLauncher<String[]> requestPermissionLauncher;

    FragmentManager manager;

    private double latitude;
    private double longitude;

    private boolean hasAllInformation = false;

    List<BusinessMap> markers;

    WebView myWebView;
    WebViewJavaScriptInterface jsInterface;

    private boolean present;
    private Runnable changeToolbar;

    public MapFragment(Context context, ActivityResultLauncher<String[]> requestPermissionLauncher, double latitude, double longitude, Runnable changeToolbar) {
        // Required empty public constructor
        this.context = context;
        this.requestPermissionLauncher = requestPermissionLauncher;
        this.latitude = latitude;
        this.longitude = longitude;
        this.changeToolbar = changeToolbar;

        jsInterface = new WebViewJavaScriptInterface(context, latitude, longitude);
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

    public void setManager(FragmentManager manager) {
        this.manager = manager;
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

        loadMap();
    }

    public void passUserMainLocation(String city) {

        SharedPreferences preferences = context.getSharedPreferences("user_location", Context.MODE_PRIVATE);
        String lat = preferences.getString("latitude", "");
        String longitude = preferences.getString("longitude", "");

        if(lat != null && longitude != null) {
            jsInterface.setUserLatitude(Double.parseDouble(lat));
            jsInterface.setUserLongitude(Double.parseDouble(longitude));
            jsInterface.setCity(city);
        }
    }

    public void passMapInfo(List<BusinessMap> markerList, String city) {
        if(markerList.isEmpty()) {
            this.markers = new ArrayList<BusinessMap>();
        } else {
            this.markers = new ArrayList<BusinessMap>(markerList);
            setupMapWithMarkers(city);
        }
    }

    public void setupMapWithMarkers(String city) {

            String[] names = new String[markers.size()];
            double[] latitudes = new double[markers.size()];
            double[] longitudes = new double[markers.size()];
            String[] streetNames = new String[markers.size()];
            String[] houseNumbers = new String[markers.size()];
            String[] postalCodes = new String[markers.size()];
            String[] icons = new String[markers.size()];

            for(int i = 0; i < markers.size(); i++) {

                byte[] iconByName = CustomDataHolder.DataHolderObject.getIconByName(markers.get(i).getOfficial());
                icons[i] = Base64.encodeToString(iconByName, Base64.DEFAULT);
                names[i] = this.markers.get(i).getAlias();
                latitudes[i] = Double.parseDouble(this.markers.get(i).getLatitude());
                longitudes[i] = Double.parseDouble(this.markers.get(i).getLongitude());
                streetNames[i] = this.markers.get(i).getStreet();
                houseNumbers[i] = this.markers.get(i).getNumber();
                postalCodes[i] = this.markers.get(i).getPostal();
            }

            jsInterface.setNames(names);
            jsInterface.setLatitudes(latitudes);
            jsInterface.setLongitudes(longitudes);
            jsInterface.setStreetNames(streetNames);
            jsInterface.setHouseNumbers(houseNumbers);
            jsInterface.setPostal(postalCodes);
            jsInterface.setCity(city);

            jsInterface.setIcons(icons);

        hasAllInformation = true;
    }

    public void loadMap() {
        myWebView.loadUrl("file:///android_asset/map.html");
    }

    public void openBusinessActivity(int position) {

        String officialName = markers.get(position).getOfficial();
        BusinessFragment businessFragment = new BusinessFragment(context, markers.get(position), changeToolbar, manager);
        manager.beginTransaction().replace(R.id.fragment_container, businessFragment).setTransition(TRANSIT_FRAGMENT_FADE).commit();
        CustomFragmentManagement.CustomFragmentManager.setFragmentType(CustomFragmentManagement.CustomFragmentManager.FragmentType.BUSINESS);
        Toast.makeText(context, String.valueOf(position), Toast.LENGTH_SHORT).show();
    }

    public boolean hasAllInformation() {
        return this.hasAllInformation;
    }

    @Override
    public void onActivityResult(Object result) {
        Object res = result;
    }

    public class WebViewJavaScriptInterface{

        private Context context;

        private Runnable openBusiness;

        double userLatitude;
        double userLongitude;

        String[] icons;

        String[] names;
        String[] latitudes;
        String[] longitudes;
        String[] streetNames;
        String[] houseNumbers;
        String[] postal;
        String city;

        /*
         * Need a reference to the context in order to sent a post message
         */
        public WebViewJavaScriptInterface(Context context, double latitude, double longitude){
            this.context = context;
            this.userLatitude = latitude;
            this.userLongitude = longitude;
        }

        public void setUserLongitude(double userLongitude) {
            this.userLongitude = userLongitude;
        }

        public void setUserLatitude(double userLatitude) {
            this.userLatitude = userLatitude;
        }

        public void setNames(String[] names) {
            this.names = names;
        }

        public void setIcons(String[] icons) { this.icons = icons; }

        public void setLatitudes(double[] latitudes) {
            String[] stringRep = new String[latitudes.length];

            for(int i = 0; i < latitudes.length; i++) {
                stringRep[i] = String.valueOf(latitudes[i]);
            }
            this.latitudes = stringRep;
        }

        public void setLongitudes(double[] longitudes) {
            String[] stringRep = new String[longitudes.length];

            for(int i = 0; i < longitudes.length; i++) {
                stringRep[i] = String.valueOf(longitudes[i]);
            }
            this.longitudes = stringRep;
        }

        public void setStreetNames(String[] streetNames) {
            this.streetNames = streetNames;
        }

        public void setHouseNumbers(String[] houseNumbers) {
            this.houseNumbers = houseNumbers;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setPostal(String[] postals) {
            this.postal = postals;
        }


        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */

        @JavascriptInterface
        public double getLatitude(){
            return userLatitude;
        }

        @JavascriptInterface
        public String getIcons() {
            JSONArray icons;

            try {
                JSONArray myArray = new JSONArray(this.icons);
                icons = myArray;

            } catch (JSONException e) {
                e.printStackTrace();
                icons = new JSONArray();
            }
            String value = icons.toString();
            Log.d("values", value);
            return value;
        }

        @JavascriptInterface
        public double getLongitude() {
            return userLongitude;
        }

        @JavascriptInterface
        public String getNames() {
            JSONArray names;

            try {
                JSONArray myArray = new JSONArray(this.names);
                names = myArray;

            } catch (JSONException e) {
                e.printStackTrace();
                names = new JSONArray();
            }
            String value = names.toString();
            Log.d("values", value);
            return value;
        }

        @JavascriptInterface
        public String getLatitudes() {

            JSONArray latitudes;

            try {
                JSONArray myArray = new JSONArray(this.latitudes);
                latitudes = myArray;

            } catch (JSONException e) {
                e.printStackTrace();
                latitudes = new JSONArray();
            }

            return latitudes.toString();
        }

        @JavascriptInterface
        public String getLongitudes() {
            JSONArray longitudes;

            try {
                JSONArray myArray = new JSONArray(this.longitudes);
                longitudes = myArray;

            } catch (JSONException e) {
                e.printStackTrace();
                longitudes = new JSONArray();
            }
            String value = longitudes.toString();
            Log.d("values", value);
            return value;

        }

        @JavascriptInterface
        public String getStreetNames() {
            JSONArray streetNames;

            try {
                JSONArray myArray = new JSONArray(this.streetNames);
                streetNames = myArray;

            } catch (JSONException e) {
                e.printStackTrace();
                streetNames = new JSONArray();
            }
            String value = streetNames.toString();
            Log.d("values", value);
            return value;
        }

        @JavascriptInterface
        public String getHouseNumbers() {

            JSONArray houseNumbers;

            try {
                JSONArray myArray = new JSONArray(this.houseNumbers);
                houseNumbers = myArray;

            } catch (JSONException e) {
                e.printStackTrace();
                houseNumbers = new JSONArray();
            }
            String value = houseNumbers.toString();
            Log.d("values", value);
            return value;
        }

        @JavascriptInterface
        public String getCity() {
            return city;
        }

        @JavascriptInterface
        public String getPostal() {

            JSONArray postalCodes;

            try {
                JSONArray myArray = new JSONArray(this.postal);
                postalCodes = myArray;

            } catch (JSONException e) {
                e.printStackTrace();
                postalCodes = new JSONArray();
            }
            String value = postalCodes.toString();
            Log.d("values", value);
            return value;
        }

        @JavascriptInterface
        public void openBusiness(int position) {
            openBusinessActivity(position);
        }
    }
}
