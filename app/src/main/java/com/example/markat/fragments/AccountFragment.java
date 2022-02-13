package com.example.markat.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.markat.R;
import com.example.markat.api.APIHttpsUtils;
import com.example.markat.models.User;
import com.example.markat.models.UserBillingAddress;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class AccountFragment extends Fragment {

    private User user;
    private Context context;

    public TextView textViewAlias;
    public TextView textViewEmail;
    public TextView textViewPwd;
    public TextView textViewDate;


    //Address
    public EditText editTextAddressStreet;
    public EditText editTextAddressPostalCode;
    public EditText editTextAddressPostalNumber;
    public EditText editTextAddressCityName;
    public Button buttonSaveAddressData;
    public TextView textViewVerified;
    public ImageView imageViewVerified;

    private final String postUrl = "/users";
    private final String postBillingAddressUrl = "/users/billingaddress";

    private UserBillingAddress billingAddress;

    public AccountFragment(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            requestAllUserCredentials();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewAlias = view.findViewById(R.id.textView_owner_alias);
        textViewEmail = view.findViewById(R.id.textView_owner_email);
        textViewDate = view.findViewById(R.id.textView_owner_date);
        textViewPwd = view.findViewById(R.id.textView_owner_pwd);

        editTextAddressCityName = view.findViewById(R.id.editText_city);
        editTextAddressPostalCode = view.findViewById(R.id.editText_postal_code);
        editTextAddressPostalNumber = view.findViewById(R.id.editText_postal_number);
        editTextAddressStreet = view.findViewById(R.id.editText_street);
        buttonSaveAddressData = view.findViewById(R.id.button_save_address);
        imageViewVerified = view.findViewById(R.id.imageView_address_verification);
        textViewVerified = view.findViewById(R.id.textView_address_verified);


        buttonSaveAddressData.setOnClickListener(view1 -> {

            if(!(editTextAddressCityName.getText().toString().equals(""))) {
                if(!editTextAddressStreet.getText().toString().equals("")) {
                    if(!editTextAddressPostalNumber.getText().toString().equals("")) {
                        if(!editTextAddressPostalCode.getText().toString().equals("")) {

                            String city = editTextAddressCityName.getText().toString();
                            String street = editTextAddressStreet.getText().toString();
                            String postalNumber = editTextAddressPostalNumber.getText().toString();
                            String postalCode = editTextAddressPostalCode.getText().toString();

                            UserBillingAddress billingAddress = new UserBillingAddress(city, street, postalNumber, postalCode);
                            verifyBillingAddress(billingAddress);

                        } else {
                            Toast.makeText(context, "no postal code", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "no house number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "no street name", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "no city name", Toast.LENGTH_SHORT).show();
            }

        });

        checkBillingAddressData();

    }

    private void verifyBillingAddress(UserBillingAddress address) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> fromLocationName = geocoder.getFromLocationName(address.getLocationName(), 3);

            if(fromLocationName != null) {
                if (fromLocationName.size() > 1) {
                    Toast.makeText(context, "Addressdaten nicht eindeutig ?", Toast.LENGTH_SHORT).show();
                    imageViewVerified.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_cancel_24));
                    textViewVerified.setText("nicht verifiziert");
                } else {
                    if(isAddressValid(fromLocationName.get(0))) {
                        imageViewVerified.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_check_24));
                        textViewVerified.setText("verifiziert");
                        if(this.billingAddress.isVerified()) {
                            postUserBillingAddressData(this.billingAddress);
                        } else {
                            Log.d("Billing_Address", "billing address has not all attributes");
                        }
                    } else {
                        Toast.makeText(context, "Addressdaten nicht eindeutig ?", Toast.LENGTH_SHORT).show();
                        imageViewVerified.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_cancel_24));
                        textViewVerified.setText("nicht verifiziert");
                    }
                }
            } else {
                Toast.makeText(context, "Addressdaten nicht eindeutig ?", Toast.LENGTH_SHORT).show();
                imageViewVerified.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_cancel_24));
                textViewVerified.setText("nicht verifiziert");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isAddressValid(Address address) {

        String cityName = address.getLocality();
        String streetName = "";
        String postalCode = "";
        String houseNumber = "";

        String[] splitAddress = address.getAddressLine(0).split(",");
        String result = address.getAddressLine(0);
        if(result.contains(editTextAddressCityName.getText())) {

        }

        for(int i = 0; i < splitAddress.length; i++) {
            if(i == 0) {
                String [] splitComma = splitAddress[i].split(" ");
                streetName = splitComma[0];
                if(splitComma.length > 1) {
                    houseNumber = splitComma[1];
                } else {
                    return false;
                }
            }
            if(i == 1) {
                String [] splitComma = splitAddress[i].split(" ");
                postalCode = splitComma[1];

                try {
                    int code = Integer.parseInt(postalCode);
                } catch (Exception e) {
                    return false;
                }
            }
        }

        if(!cityName.equals("") && !streetName.equals("") && !houseNumber.equals("") && !postalCode.equals("")) {
            billingAddress = new UserBillingAddress(cityName, streetName, houseNumber, postalCode);
            billingAddress.setVerified(true);
            Log.d("billingInfo", billingAddress.getLocationName());
            return true;
        }
        return false;
    }

    private void postUserBillingAddressData(UserBillingAddress address) {
        //Send Post Method here!
        if(address.getPostalCode().length() == 5 && address.isVerified()) {
            int postal = Integer.parseInt(address.getPostalCode());
            Log.d("postal", String.valueOf(postal));

            RequestParams rp = new RequestParams();
            rp.add("method", "SET_BILLING_ADDRESS");
            rp.add("id", String.valueOf(user.getId()));
            rp.add("street", address.getStreet());
            rp.add("housenumber", address.getPostalNumber());
            rp.add("postalcode", address.getPostalCode());
            rp.add("city", address.getCity());

            APIHttpsUtils.post(postBillingAddressUrl, rp, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    Log.d("POST_METHOD_BLNG_ADDRESS", "---------------- this is response : " + response);

                    SharedPreferences preferences = context.getSharedPreferences("billing_address", Context.MODE_PRIVATE);
                    preferences.edit().putBoolean("hasAddress", true).apply();
                    preferences.edit().putString("city", address.getCity()).apply();
                    preferences.edit().putString("street", address.getStreet()).apply();
                    preferences.edit().putString("housenr", address.getPostalNumber()).apply();
                    preferences.edit().putString("postalcode", address.getPostalCode()).apply();

                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("Billing Address cache error", errorResponse.toString());
                }
            });
        }
    }

    private void requestAllUserCredentials() {

        SharedPreferences preferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        long id = preferences.getLong("id", 0);

        RequestParams rp = new RequestParams();
        rp.add("id", String.valueOf(id));
        rp.add("method", "OWN_CREDENTIALS");

        APIHttpsUtils.post(postUrl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("POST_METHOD_ACCOUNT_DATA", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONArray data = serverResp.getJSONArray("user");

                    if(data.length() != 0) {
                        for (int i = 0; i < data.length(); i++) {

                            JSONObject user = data.getJSONObject(i);
                            String emailOfResponse = user.getString("email");
                            String passwordOfResponse = user.getString("password");
                            String aliasOfResponse = user.getString("alias");
                            String dateOfResponse = user.getString("date");

                            User allCredentialsUser = new User(Long.valueOf(id), emailOfResponse, passwordOfResponse, aliasOfResponse, dateOfResponse);

                            refreshUiWithCredentials(allCredentialsUser);
                        }
                    } else {
                        Toast.makeText(context, "Email oder Passwort falsch", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Login user error", errorResponse.toString());
            }
        });

    }

    private void refreshUiWithCredentials(User user) {

        textViewPwd.setText("********");

        textViewEmail.setText(user.getEmail());
        textViewAlias.setText(user.getAlias());

        String dateForUI = user.getDateForUi();

        textViewDate.setText(dateForUI);
    }

    private void checkBillingAddressData() {
        SharedPreferences preferences = context.getSharedPreferences("billing_address", Context.MODE_PRIVATE);
        boolean hasAddress = preferences.getBoolean("hasAddress", false);

        if(hasAddress) {
            editTextAddressStreet.setText(preferences.getString("street", "Maximilianstraße"));
            editTextAddressCityName.setText(preferences.getString("city", "Reg"));
            editTextAddressPostalNumber.setText(preferences.getString("housenr", "1"));
            editTextAddressPostalCode.setText(preferences.getString("postalcode", "93047"));

            imageViewVerified.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_check_24));
            textViewVerified.setText("verifiziert");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }
}