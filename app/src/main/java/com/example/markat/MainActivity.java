package com.example.markat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.markat.api.APIHttpsUtils;
import com.example.markat.fragments.AccountFragment;
import com.example.markat.fragments.HomeFragment;
import com.example.markat.fragments.LocationPopup;
import com.example.markat.fragments.MapFragment;
import com.example.markat.fragments.ProductsFragment;
import com.example.markat.fragments.SystemSettingsFragment;
import com.example.markat.models.BusinessHome;
import com.example.markat.models.BusinessMap;
import com.example.markat.models.User;
import com.example.markat.models.UserLocation;
import com.example.markat.utils.NavigationDrawerManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    FragmentManager manager;
    MapFragment mapFragment;
    HomeFragment homeFragment;
    AccountFragment accountFragment;
    SystemSettingsFragment systemSettingsFragment;
    ProductsFragment productsFragment;
    LocationPopup locationPopup;

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    NavigationDrawerManager navigationManager;
    DrawerLayout drawer;
    ActivityResultLauncher<String[]> requestPermissionLauncher;
    Toolbar toolbar;

    Context context = this;

    private List<BusinessHome> businessForScrollbar = new ArrayList<BusinessHome>();
    private List<BusinessMap> businessForMapMarkers = new ArrayList<BusinessMap>();

    //Location
    private FusedLocationProviderClient fusedLocationClient;
    double latitude;
    double longitude;

    private User user;
    private UserLocation userLocation;

    private final String postUrl = "/users/location";
    private final String logoutUrl = "/users/sessions";
    private final String businessUrl = "/business";
    private final String businessLogoUrl = "/business/logos";

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Home");
        toolbar.setSubtitle("Standort: dein Standort");
        toolbar.setBackgroundColor(getColor(R.color.main));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        manager = getSupportFragmentManager();

        drawer = findViewById(R.id.drawerLayout_main);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string. navigation_drawer_open,
                R.string. navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        navigationView = findViewById(R.id.navigationView_main);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        checkOrRequestLocation();

        getCurrentUserFromPreferences();

        homeFragment = new HomeFragment(this, " ", String.valueOf(user.getId()));
        manager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        locationPopup = new LocationPopup(() -> refreshLocation(), this);
    }

    private void getCurrentUserFromPreferences() {

        SharedPreferences userPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        String email = userPreferences.getString("email", "");
        long id = userPreferences.getLong("id", 0);
        String alias = userPreferences.getString("alias", "");

        user = new User(id, email, alias);

        if(navigationManager == null) {
            navigationManager = new NavigationDrawerManager(this, navigationView, drawer);
        }
        navigationManager.setUsernameInTitle(user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
        if (item.getItemId() == R.id.action_location) {
            locationPopup.show(manager, "");
            return true;
        }
        return false;
    }

    public void refreshLocation() {
        SharedPreferences preferences = getSharedPreferences("user_location", MODE_PRIVATE);
        String city = preferences.getString("city", "");
        String latitude = preferences.getString("latitude", "");
        String longitude = preferences.getString("longitude", "");

        if(city.equals("own")) {
            manager.popBackStack();
            checkOrRequestLocation();
        } else {
            if (userLocation == null) {
                userLocation = new UserLocation(city, Double.parseDouble(latitude), Double.parseDouble(longitude));
            } else {
                userLocation.setCity(city);
                userLocation.setLatitude(Double.parseDouble(latitude));
                userLocation.setLongitude(Double.parseDouble(longitude));
            }
            Objects.requireNonNull(getSupportActionBar()).setSubtitle("Standort: " + userLocation.getCity());

            if (mapFragment != null && mapFragment.isPresent()) {
                mapFragment.passUserMainLocation(city);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if(navigationManager == null) {
            navigationManager = new NavigationDrawerManager(this, navigationView, drawer);

        }
        if(user != null) {
            navigationManager.setUsernameInTitle(user);
        }

        switch (itemId) {
            case R.id.action_home:
                    homeFragment = new HomeFragment(this, userLocation.getCity(), String.valueOf(user.getId()));
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
                    if (userLocation == null) {
                        getSupportActionBar().setSubtitle("Standort: Dein Standort");
                    } else {
                        getSupportActionBar().setSubtitle("Standort: " + userLocation.getCity());
                    }
                    if (mapFragment != null) mapFragment.isPresent(false);
                    manager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                    navigationManager.setNavigationViewChecked("HOME");
                    drawer.closeDrawers();
                    if(businessForMapMarkers != null) {
                        homeFragment.passDetailedInfo(businessForMapMarkers);
                    }

                break;

            case R.id.action_map:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Deine Umgebung");
                if(userLocation == null) {
                    getSupportActionBar().setSubtitle("Standort: Dein Standort");
                } else {
                    getSupportActionBar().setSubtitle("Standort: " + userLocation.getCity());
                }
                mapFragment = new MapFragment(this, requestPermissionLauncher, userLocation.getLatitude(), userLocation.getLongitude());
                mapFragment.isPresent(true);
                mapFragment.passMapInfo(this.businessForMapMarkers, userLocation.getCity());
                manager.beginTransaction().replace(R.id.fragment_container, mapFragment).commit();
                navigationManager.setNavigationViewChecked("MAP");
                drawer.closeDrawers();
                break;

            case R.id.action_products:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Produkte");
                if(userLocation == null) {
                    getSupportActionBar().setSubtitle("Standort: Dein Standort");
                } else {
                    getSupportActionBar().setSubtitle("Standort: " + userLocation.getCity());
                }
                productsFragment = new ProductsFragment(this);
                if(mapFragment != null) mapFragment.isPresent(false);
                manager.beginTransaction().replace(R.id.fragment_container, productsFragment).commit();
                navigationManager.setNavigationViewChecked("PRODUCTS");
                drawer.closeDrawers();
                break;

            case R.id.action_account:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Dein Account");
                if(userLocation == null) {
                    getSupportActionBar().setSubtitle("Standort: Dein Standort");
                } else {
                    getSupportActionBar().setSubtitle("Standort: " + userLocation.getCity());
                }
                accountFragment = new AccountFragment(this, user);
                if(mapFragment != null) mapFragment.isPresent(false);
                manager.beginTransaction().replace(R.id.fragment_container, accountFragment).commit();
                navigationManager.setNavigationViewChecked("ACCOUNT");
                drawer.closeDrawers();
                break;

            case R.id.action_settings:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Allgemeine Einstellungen");
                getSupportActionBar().setSubtitle("");
                systemSettingsFragment = new SystemSettingsFragment(this);
                if(mapFragment != null) mapFragment.isPresent(false);
                manager.beginTransaction().replace(R.id.fragment_container, systemSettingsFragment).commit();
                navigationManager.setNavigationViewChecked("SETTINGS");
                drawer.closeDrawers();
                break;

            case R.id.action_logout:
                drawer.closeDrawers();
                new AlertDialog.Builder(this)
                        .setTitle(R.string.popup_logout_title)
                        .setMessage(R.string.popup_logout_message)
                        .setPositiveButton(R.string.popup_logout_pos_button, (dialogInterface, i) -> {
                            logoutUser();
                        })
                        .setNegativeButton(R.string.popup_logout_neg_button, (dialogInterface, i) -> { })
                        .create()
                        .show();
                break;
            default: break;
        }
        return true;
    }

    private void logoutUser() {
        Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;

        RequestParams rp = new RequestParams();
        Log.d("UserLogout:", " " + user.getAlias());
        rp.add("method", "LOGOUT");
        rp.add("id", String.valueOf(user.getId()));
        rp.add("year", String.valueOf(year));
        rp.add("month", String.valueOf(month));
        rp.add("day", String.valueOf(day));

        APIHttpsUtils.post(logoutUrl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("POST_METHOD_LOGOUT", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONArray data = serverResp.getJSONArray("feedback");

                    if(data.length() != 0) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject innerResponse = data.getJSONObject(i);
                            String logout = innerResponse.getString("logout");

                            if(logout.equals("true")) {
                                sendUserBackToLogin();
                            }

                        }
                    } else {
                        Toast.makeText(context, "API Error when setting user location", Toast.LENGTH_SHORT).show();
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

    private void sendUserBackToLogin() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed () {
        DrawerLayout drawer = findViewById(R.id.drawerLayout_main) ;
        if (drawer.isDrawerOpen(GravityCompat.START )) {
            drawer.closeDrawer(GravityCompat.START ) ;
        }
    }

    public void checkOrRequestLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton("ok", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            processLocationData(location);
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, location -> {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    processLocationData(location);
                                }
                            });
                }
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                new LocationPopup(() -> refreshLocation(), this).show(manager, "");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            processLocationData(location);
                        }
                    });
        }
         */
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //maybe needed later
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        longitude = location.getLongitude();
        latitude = location.getLatitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String cityName;
        try {

            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            cityName = addresses.get(0).getLocality();
            Log.d("admin_area", addresses.get(0).getAdminArea());
            Log.d("sub_admin_area", addresses.get(0).getSubAdminArea());
            Log.d("locality", addresses.get(0).getLocality());
            Log.d("country", addresses.get(0).getCountryName());

            userLocation = new UserLocation(cityName, longitude, latitude);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(homeFragment != null) {
            homeFragment.setUserRegionAsync(userLocation.getCity());
            Objects.requireNonNull(getSupportActionBar()).setSubtitle("Standort: " + userLocation.getCity());
        }
        Log.d("location", longitude + String.valueOf(latitude));

        sendLocationData(location.getLongitude(), location.getLatitude());

    }

    private void processLocationData(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String cityName;
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            cityName = addresses.get(0).getLocality();
            Log.d("admin_area", addresses.get(0).getAdminArea());
            Log.d("sub_admin_area", addresses.get(0).getSubAdminArea());
            Log.d("locality", addresses.get(0).getLocality());
            Log.d("country", addresses.get(0).getCountryName());

            userLocation = new UserLocation(cityName, longitude, latitude);

            requestMarkerInfoForMap(cityName);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(homeFragment != null) {
            homeFragment.setUserRegionAsync(userLocation.getCity());
            Objects.requireNonNull(getSupportActionBar()).setSubtitle("Standort: " + userLocation.getCity());
        }
        Log.d("location", longitude + String.valueOf(latitude));

        sendLocationData(location.getLongitude(), location.getLatitude());
    }

    private void requestMarkerInfoForMap(String cityName) {

        RequestParams rp = new RequestParams();
        rp.add("method", "GET_PARTNERS");
        rp.add("city", cityName);

        APIHttpsUtils.post(businessUrl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("POST_METHOD_GET_PARTNERS", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONArray data = serverResp.getJSONArray("partners");

                    if(data.length() != 0) {

                        for (int i = 0; i < data.length(); i++) {

                            JSONObject partner = data.getJSONObject(i);
                            String businessIdResponse = partner.getString("id");
                            String officialOfResponse = partner.getString("official_name");
                            String aliasOfResponse = partner.getString("alias");
                            String streetOfResponse = partner.getString("street");
                            String numberOfResponse = partner.getString("house_number");
                            String postalOfResponse = partner.getString("postal");
                            String latitudeOfResponse = partner.getString("latitude");
                            String longitudeOfResponse = partner.getString("longitude");

                            if(!businessIdResponse.equals("") && !aliasOfResponse.equals("")) {
                                BusinessHome business = new BusinessHome(businessIdResponse, aliasOfResponse);
                                businessForScrollbar.add(business);
                                if(!latitudeOfResponse.equals("") && !longitudeOfResponse.equals("")) {
                                    BusinessMap businessMap = new BusinessMap(businessIdResponse, officialOfResponse, aliasOfResponse, streetOfResponse, numberOfResponse, postalOfResponse, latitudeOfResponse, longitudeOfResponse);
                                    businessForMapMarkers.add(businessMap);
                                }
                                Log.d("data", business.getAlias());
                            }
                        }
                        homeFragment.passDetailedInfo(businessForMapMarkers);

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

    private void requestLogos() {

        RequestParams rp = new RequestParams();
        rp.add("method", "GET_PARTNER_LOGOS");

        for(BusinessMap business : this.businessForMapMarkers) {
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
                            /*
                            if(!businessIdResponse.equals("") && !aliasOfResponse.equals("")) {
                                BusinessHome business = new BusinessHome(businessIdResponse, aliasOfResponse);
                                businessForScrollbar.add(business);
                                if(!latitudeOfResponse.equals("") && !longitudeOfResponse.equals("")) {
                                    BusinessMap businessMap = new BusinessMap(businessIdResponse, aliasOfResponse, streetOfResponse, numberOfResponse, postalOfResponse, latitudeOfResponse, longitudeOfResponse);
                                    businessForMapMarkers.add(businessMap);
                                    requestLogos();
                                }
                                Log.d("data", business.getAlias());
                            }
                             */
                        }
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

        String image = imageOfResponse.replace("\n", "");
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);

        //BitmapFactory.Options options = new BitmapFactory.Options();
        //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
        Drawable imageDrawable = new BitmapDrawable(getResources(),BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));

        int a = 0;
    }

    private void sendLocationData(double longitude, double latitude) {

        RequestParams rp = new RequestParams();
        Log.d("UserLocation:", longitude + String.valueOf(latitude));
        rp.add("method", "SET_LOCATION");
        rp.add("id", String.valueOf(user.getId()));
        rp.add("longitude", String.valueOf(longitude));
        rp.add("latitude", String.valueOf(latitude));
        rp.add("city", userLocation.getCity());

        APIHttpsUtils.post(postUrl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("POST_METHOD_LOCATION", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONArray data = serverResp.getJSONArray("feedback");

                    if(data.length() != 0) {

                        for (int i = 0; i < data.length(); i++) {

                            JSONObject innerResponse = data.getJSONObject(i);
                        }
                    } else {
                        Toast.makeText(context, "API Error when setting user location", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public List<BusinessHome> getInfoForHomeFragment() {
        return new ArrayList<BusinessHome>(this.businessForScrollbar);
    }

    public List<BusinessMap> getInfoForMapFragment() {
        return new ArrayList<BusinessMap>(businessForMapMarkers);
    }
}