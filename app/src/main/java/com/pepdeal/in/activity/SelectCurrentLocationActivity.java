package com.pepdeal.in.activity;

import static android.view.View.GONE;

import static com.pepdeal.in.activity.HomeActivity.REQUEST_CHECK_SETTINGS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.adapter.PlaceAutoCompleteAdapter;
import com.pepdeal.in.adapter.RecyclerItemClickListener;
import com.pepdeal.in.databinding.ActivitySelectCurrentLocationBinding;
import com.pepdeal.in.model.location.Prediction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SelectCurrentLocationActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RecyclerItemClickListener.OnItemClickListener,
        LocationListener, GpsStatus.Listener{

    ActivitySelectCurrentLocationBinding binding;
    private LocationManager mLocationManager;

    private PlaceAutoCompleteAdapter mAutoCompleteAdapter;
    private PlacesClient placesClient;
    private AutocompleteSessionToken token;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private String placesApi = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
    String lat = "";
    String longi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_current_location);
        binding.setHandler(new ClickHandler());
        // Initialize Places.
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_map_key));

        // Create a new Places client instance.
        placesClient = Places.createClient(this);

        // Create a new token for the autocomplete session. Pass this to
        // FindAutocompletePredictionsRequest, and once again when the user makes a selection
        // (for example when calling fetchPlace()).
        token = AutocompleteSessionToken.newInstance();

        // Create a RectangularBounds object.
//        bounds = RectangularBounds.newInstance(BOUNDS_INDIA);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this);
        binding.recLocationList.setLayoutManager(new LinearLayoutManager(this));
//        locationsRv.setAdapter(mAutoCompleteAdapter);
        binding.recLocationList.setAdapter(mAutoCompleteAdapter);

        binding.recLocationList.addOnItemTouchListener(new RecyclerItemClickListener(this, this));

        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && charSequence.length() > 2 && placesClient != null) {
                    getAutocompleteLocations(charSequence.toString());
//                getAutocompletePredictions(s.toString());
//                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else
                    binding.recLocationList.setVisibility(GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getAutocompleteLocations(String query) {
        String searchRequest = placesApi + query + "&components=country:in&radius=500&location=" +
                lat + "," + longi + "&key=AIzaSyCzKGcIt8zhR27g3luGG4Vk3BSWLTxjbC0";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, searchRequest, response ->
        {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Log.d("RESPONSE : ", response);
                if (jsonObject.getString("status").equals("OK")) {
                    List<Prediction> predictions;
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Prediction>>() {
                    }.getType();
                    predictions = new ArrayList<>();
                    predictions.addAll(gson.fromJson(jsonObject.getString("predictions"), listType));
                    Collections.reverse(predictions);

                    mAutoCompleteAdapter.setPredictions(predictions);
                    mAutoCompleteAdapter.notifyDataSetChanged();
                    binding.recLocationList.setVisibility(View.VISIBLE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> error.printStackTrace()) {
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);
        Log.e("request-getUrl",stringRequest.getUrl());

      /*  RectangularBounds bounds = RectangularBounds.newInstance(new LatLng(23.63936, 68.14712),
                new LatLng(28.20453, 97.34466));
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
//                .setLocationBias(bounds)
                .setCountry("IN")
                .setTypeFilter(TypeFilter.REGIONS)
//                .setLocationRestriction(bounds)
//                .setTypeFilter(TypeFilter.GEOCODE)
                .setSessionToken(token)
                .setQuery(query)
                .build();
        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener((response) -> {
                    mAutoCompleteAdapter.setPredictions(response.getAutocompletePredictions());
                    mAutoCompleteAdapter.notifyDataSetChanged();
                    binding.recLocationList.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("Ta", "Place not found: " + apiException.getStatusCode());
                        Toast.makeText(getApplicationContext(), apiException.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });*/
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClick(View view, int position) {
        CharacterStyle styleBold = new StyleSpan(Typeface.BOLD);

      /*  if (mAutoCompleteAdapter.getItemCount() == 0) return;
        String locationName = mAutoCompleteAdapter.getPredictions()
                .get(position).getPrimaryText(styleBold).toString();
        Geocoder geocoder = new Geocoder(this, Locale.US);
        try {
            List<android.location.Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (!addresses.isEmpty()) {
                setLocationText(locationName,
                        new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()), addresses.get(0).getAdminArea());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.println(Log.ERROR, "Tag", e.toString());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }*/

        if (mAutoCompleteAdapter.getItemCount() == 0) return;
        String locationName = mAutoCompleteAdapter.getPredictions()
                .get(position).getDescription();
        Geocoder geocoder = new Geocoder(this, Locale.US);
        try {
            List<android.location.Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (!addresses.isEmpty()) {
                setLocationText(locationName,
                        new LatLng(addresses.get(0).getLatitude(),
                                addresses.get(0).getLongitude()),
                        ""+addresses.get(0).getLatitude(),
                        ""+addresses.get(0).getLongitude(),
                        addresses.get(0).getLocality(),
                        addresses.get(0).getAdminArea(),
                        addresses.get(0).getFeatureName());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.println(Log.ERROR, "Tag", e.toString());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setLocationText(String address, LatLng latLng, String lat,String lng,
                                 String city,String state,String area) {
        if (address != null && latLng != null) {

            Intent intent = new Intent();
            intent.putExtra("d_address", address);
            intent.putExtra("lat", lat);
            intent.putExtra("long", lng);
            intent.putExtra("city", city);
            intent.putExtra("state", state);
            intent.putExtra("area", area);
            setResult(Activity.RESULT_OK, intent);
            finish();

        }
    }

    private void switchOnGPS() {
        @SuppressLint("RestrictedApi") LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(SelectCurrentLocationActivity.this).checkLocationSettings(builder.build());
        task.addOnCompleteListener(task1 -> {
            try {
                LocationSettingsResponse response = task1.getResult(ApiException.class);
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult(SelectCurrentLocationActivity.this,
                                    REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e1) {
                            e1.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //open setting and switch on GPS manually

                        break;
                }
            }
        });
        //Give permission to access GPS
        ActivityCompat.requestPermissions(SelectCurrentLocationActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 11);
    }


    @Override
    public void onGpsStatusChanged(int i) {
        switch (i) {
            case GpsStatus.GPS_EVENT_STOPPED:
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    switchOnGPS();
                }
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lat = String.valueOf(location.getLongitude());
        longi = String.valueOf(location.getLatitude());

    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }
}