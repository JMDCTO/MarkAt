package com.example.markat.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.markat.R;
import com.example.markat.api.APIHttpsUtils;
import com.example.markat.models.BusinessMap;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ImpressumFragment extends DialogFragment {
  
    TextView textViewContactName;
    TextView textViewContactEmail;
    
    Button buttonClosePopup;
    Context context;
    BusinessMap business;
    
  private final String postUrl = "/business/impressum";

  public ImpressumFragment(Context context, BusinessMap business) {
        // Required empty public constructor
      this.context = context;
      this.business = business;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_impressum, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      buttonClosePopup = view.findViewById(R.id.button_close_impressum);
      textViewContactName = view.findViewById(R.id.textView_contact_name);
      textViewContactEmail = view.findViewById(R.id.textView_contact_email);
      
      requestBusinessImpressum();
      
      buttonClosePopup.setOnClickListener(view1 -> dismiss());
    }
    
    private void requestBusinessImpressum() {
      RequestParams rp = new RequestParams();
      Log.d("Business Impressum: ", business.getOfficial());
      rp.add("method", "GET_IMPRESSUM");
      rp.add("officialname", business.getOfficial());

      APIHttpsUtils.post(postUrl, rp, new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

          Log.d("POST_METHOD_IMPRESSUM", "---------------- this is response : " + response);
          try {
            JSONObject serverResp = new JSONObject(response.toString());
            JSONArray data = serverResp.getJSONArray("impressum");

            if(data.length() != 0) {
              JSONObject impressum = data.getJSONObject(0);
              String officialEmailOfResponse = impressum.getString("email");
              String officialContactOfResponse = impressum.getString("official_contact");
              
              if(!officialContactOfResponse.equals("") && !officialEmailOfResponse.equals("")) {
                textViewContactEmail.setText(officialEmailOfResponse);
                textViewContactName.setText(officialContactOfResponse);
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
}
