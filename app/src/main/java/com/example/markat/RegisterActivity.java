package com.example.markat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.markat.api.APIHttpsUtils;
import com.example.markat.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    Button registerButton;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextAlias;

    String checkErrorMessage;
    Context context;

    private final String postUrl = "/users/reg";
    private final String postLoginUrl = "/users/sessions";
    private final String getUrl = "/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;

        findViewsById();

        registerButton.setOnClickListener(view -> {
            String emailForRegistration = editTextEmail.getText().toString();
            String passwordForRegistration = editTextPassword.getText().toString();
            String aliasForRegistration = editTextAlias.getText().toString();

            //Check input from user
            if(checkEmail(emailForRegistration) && checkPassword(passwordForRegistration) && checkAlias(aliasForRegistration)) {

                sendRegisterGETMethod(emailForRegistration, passwordForRegistration, aliasForRegistration);

            } else {
                Toast.makeText(this,  checkErrorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkAlias(String alias) {
        if(!alias.equals("")) {
            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(alias);
            boolean isSpecialChar = m.find();

            if (isSpecialChar) {
                checkErrorMessage = "Alias contains special characters";
                return false;
            } else {
                return true;
            }
        } else {
            checkErrorMessage = "No Alias";
            return false;
        }
    }

    private boolean checkPassword(String password) {
        if(!password.equals("")) {
            if(password.length() >= 8) {
                Pattern pattern;
                Matcher matcher;
                final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
                pattern = Pattern.compile(PASSWORD_PATTERN);
                matcher = pattern.matcher(password);

                if(matcher.matches()) {
                    return true;
                } else {
                    checkErrorMessage = "1 special char, 1 alphabet necessary";
                    return false;
                }
            } else {
                checkErrorMessage = "Password min length is 8!";
                return false;
            }
        } else {
            checkErrorMessage = "No Password";
            return false;
        }
    }

    private boolean checkEmail(String email) {
        if(!email.equals("")) {
            //Check if email is existing email
            final boolean isValidEmail = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if(isValidEmail) {
                return true;
            } else {
                checkErrorMessage = "Email is not valid";
                return false;
            }
        } else {
            checkErrorMessage = "No Email";
            return false;
        }
    }

    private void findViewsById() {

        registerButton = findViewById(R.id.button_register_acc);
        editTextAlias = findViewById(R.id.editTextTextAliasReg);
        editTextEmail = findViewById(R.id.editTextTextEmailAddressReg);
        editTextPassword = findViewById(R.id.editTextTextPasswordReg);
    }

    private void sendRegisterGETMethod(String email, String password, String alias) {

        RequestParams rp = new RequestParams();
        rp.add("email", email);

        APIHttpsUtils.get(getUrl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("GET_METHOD_CHECK_USER", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONArray data = serverResp.getJSONArray("feedback");

                    if(data.length() != 0) {
                        for (int i = 0; i < data.length(); i++) {

                            JSONObject feedback = data.getJSONObject(i);
                            String isValidResponse = feedback.getString("continue_reg");
                            String errorResponse = feedback.getString("error");

                            boolean isValid = Boolean.parseBoolean(isValidResponse);

                            if(isValid) {
                                sendRegisterPOSTMethod(email, password, alias);
                            } else {
                                Toast.makeText(context, "Nutzer existiert bereits", Toast.LENGTH_SHORT).show();
                            }

                            Log.d("GET_RESPONSE", "feedback : " + isValidResponse + ", " + errorResponse);
                        }
                    } else {
                        Toast.makeText(context, "Nutzer existiert bereits", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    private void sendRegisterPOSTMethod(String email, String password, String alias) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;

        RequestParams rp = new RequestParams();
        rp.add("email", email);
        rp.add("password", password);
        rp.add("alias", alias);
        rp.add("year", String.valueOf(year));
        rp.add("month", String.valueOf(month));
        rp.add("day", String.valueOf(day));
        rp.add("premium", "false");

        APIHttpsUtils.post(postUrl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("POST_METHOD_REG_USER", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONArray data = serverResp.getJSONArray("feedback");

                    if(data.length() != 0) {
                        for (int i = 0; i < data.length(); i++) {

                            JSONObject feedback = data.getJSONObject(i);
                            String isValidResponse = feedback.getString("finished_reg");
                            boolean isRegistered = Boolean.parseBoolean(isValidResponse);

                            if(isRegistered) {
                                String idOfResponse = feedback.getString("id");
                                String emailOfResponse = feedback.getString("email");
                                String aliasOfResponse =feedback.getString("alias");
                                int userId = Integer.parseInt(idOfResponse);
                                User registeredUser = new User(userId, emailOfResponse, aliasOfResponse);

                                updateSavedUserLogin(registeredUser);
                                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, MainActivity.class);
                                startActivity(intent);
                            }
                            Log.d("POST_RESPONSE", "feedback : " + isValidResponse);
                        }
                    } else {
                        Toast.makeText(context, "Registration failed, try later..", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

  private void updateSavedUserLogin(User user) {
      
      SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);
      SharedPreferences.Editor myEdit = sharedPreferences.edit();

      myEdit.putLong("id", user.getId());
      myEdit.putString("email", user.getEmail());
      myEdit.putString("alias", user.getAlias());
      myEdit.apply();
    
  }
}
