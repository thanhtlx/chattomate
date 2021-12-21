package com.example.chattomate.activities;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.chattomate.App;
import com.example.chattomate.MainActivity;
import com.example.chattomate.R;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.databinding.ActivityMapsBinding;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.interfaces.OnMapChangeCallBack;
import com.example.chattomate.models.User;
import com.example.chattomate.service.API;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
//import com.example.chattomate.activities.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String messageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle extras = getIntent().getExtras();
        messageID = extras.getString("id");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        HashMap<String,String> token = new HashMap<>();
        token.put("auth-token", new AppPreferenceManager(getApplicationContext()).getToken(getApplicationContext()));
        API api = new API(this);
        api.Call(Request.Method.GET, Config.HOST+Config.MESSAGE_URL + "/location/"+messageID, null, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String location = result.getString("data");
                    String [] locations = location.split(",");
                    if(locations.length != 2) {
                        Log.d("DEBUG","ERROR PARSER LOCATION");
                        Log.d("DEBUG",String.valueOf(result));
                        return;
                    }
                    LatLng sydney = new LatLng(Double.parseDouble(locations[0]), Double.parseDouble(locations[1]));
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Location of your friend"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10f));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onError(JSONObject result) {
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        App.getInstance().getSocket().setOnMapChangeCallBack(new OnMapChangeCallBack() {
            @Override
            public void onMapChange(JSONObject obj) {
                try {
                    JSONObject data = obj.getJSONObject("data");
                    String d = data.getString("location");
                    String id = data.getString("messageID");
                    if(!messageID.equals(id)){
                        Log.d("DEBUG","DIFFERENT ID");
                        Log.d("DEBUG",String.valueOf(messageID));
                        Log.d("DEBUG",String.valueOf(id));
                        return;
                    }
                    String[] location = d.split(",");
                    if(location.length != 2) {
                        Log.d("DEBUG","ERROR PARSER LOCATION");
                        Log.d("DEBUG",String.valueOf(data));
                        return;
                    }
                    Log.d("DEBUG","move map");
                    LatLng sydney = new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1]));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap.addMarker(new MarkerOptions().position(sydney).title("Location of your friend"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14f));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("DEBUG","ERRROR PARSER LOCATION");
                    Log.d("DEBUG",String.valueOf(obj));
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.getInstance().getSocket().unSetSocketCallBack();
    }
}