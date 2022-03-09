package com.example.markat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.markat.api.APIHttpsUtils;
import com.example.markat.databinding.ActivityLoginBinding;
import com.example.markat.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    EditText emailEditText;
    EditText passwordEditText;
    Button loginButton;
    ImageView registerIcon;
    Context context;

    private static long userId;
    private final String postUrl = "/users";
    private final String postLoginUrl = "/users/sessions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        findViewsByIds();

        loginButton.setOnClickListener(view -> {

            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            String hackedE = "haha@web.de";
            String hackedP = "Immerich3!";
            sendLoginPOSTMethod(hackedE, hackedP);
            //sendLoginPOSTMethod(email, password);

        });

        registerIcon.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void sendLoginPOSTMethod(String email, String password) {
        RequestParams rp = new RequestParams();
        rp.add("email", email);
        rp.add("password", password);

        APIHttpsUtils.post(postUrl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("POST_METHOD_LOGIN_USER", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONArray data = serverResp.getJSONArray("user");

                    if(data.length() != 0) {
                        for (int i = 0; i < data.length(); i++) {

                            JSONObject user = data.getJSONObject(i);
                            String userIdResponse = user.getString("id");
                            userId = Integer.parseInt(userIdResponse);
                            String emailOfResponse = user.getString("email");
                            String passwordOfResponse = user.getString("password");
                            String aliasOfResponse = user.getString("alias");

                            if (email.matches(emailOfResponse) && password.matches(passwordOfResponse)) {
                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
                                sendLoginStatusPOSTMethod(email, aliasOfResponse);
                            }
                        }
                    } else {
                        Toast.makeText(context, "Email oder Passwort falsch", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Login user error", errorResponse.toString());
            }
        });

    }

    private void sendLoginStatusPOSTMethod(String email, String alias) {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;

        RequestParams rp = new RequestParams();
        Log.d("LoginStatus:", String.valueOf(userId));
        rp.add("userid", String.valueOf(userId));
        rp.add("email", email);
        rp.add("year", String.valueOf(year));
        rp.add("month", String.valueOf(month));
        rp.add("day", String.valueOf(day));

        APIHttpsUtils.post(postLoginUrl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("POST_METHOD_LOGIN_STAT", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONArray data = serverResp.getJSONArray("feedback");

                    if(data.length() != 0) {

                        for (int i = 0; i < data.length(); i++) {

                            JSONObject user = data.getJSONObject(i);
                            String loggedInResponse = user.getString("loggedIn");

                            boolean isLoggedIn = Boolean.parseBoolean(loggedInResponse);

                            if(isLoggedIn) {
                                User locationUser = new User(userId, email, alias);
                                initPreferences(locationUser);
                                Intent intent = new Intent(context, MainActivity.class);
                                startActivity(intent);

                            } else {}
                        }
                    } else {
                        Toast.makeText(context, "API Error when setting login status", Toast.LENGTH_SHORT).show();
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

    private void initPreferences(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putLong("id", user.getId());
        myEdit.putString("email", user.getEmail());
        myEdit.putString("alias", user.getAlias());
        myEdit.apply();
    }

    private void findViewsByIds() {
        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        loginButton = findViewById(R.id.button_login);
        registerIcon = findViewById(R.id.imageView_register);
    }

    @Override
    public void onBackPressed () {
        //Do nothing here
    }

}
