package com.pepdeal.in.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.GPSTracker;
import com.pepdeal.in.constants.LocationTrack;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityAddShopBinding;
import com.pepdeal.in.model.requestModel.AddBackgroundColorResponseModel;
import com.pepdeal.in.model.requestModel.AddShopFontResponseModel;
import com.pepdeal.in.model.requestModel.AddShopRequestModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopFontColorModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopFontStyleModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class AddShopActivity extends AppCompatActivity implements

        LocationListener, GpsStatus.Listener {

    ActivityAddShopBinding binding;
    ArrayList<AddBackgroundColorResponseModel> backgroundcolorModelList = new ArrayList<>();
    ArrayList<ShopFontStyleModel> shopFontModelList = new ArrayList<>();
    ArrayList<ShopFontColorModel> shopFontModelListSize = new ArrayList<>();
    String backgroundColor = "", fontStyle = "", fontSize = "", fontColor = "";
    ProgressDialog dialog;
    public static final int REQUEST_CHECK_SETTINGS = 125;
    public static final int PERMISSIONS_LOCATION_REQUEST = 124;
    public static final int SELECT_STATE = 100;
    public static final int SELECT_CITY = 101;
    private LocationManager mLocationManager;
    LocationTrack locationTrack;
    Geocoder geocoder;
    GPSTracker gpsTracker;
    List<Address> addresses;
    Location myLocation;
    double longitude = 0.0;
    double latitude = 0.0;
    String stateId = "", stateName = "", cityId = "", cityName = "";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_shop);
        binding.setHandler(new ClickHandler());
        geocoder = new Geocoder(this, Locale.getDefault());
        gpsTracker = new GPSTracker(this);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        if (Utils.isNetwork(AddShopActivity.this)) {
            requestBackgroundColor();
//            requestShopFont();
            setFontStyle();
            requestShopFontSize();
        } else {
            Utils.InternetAlertDialog(AddShopActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }

        onSpinnerSelected();
        checkLocationPermission();
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void onSpinnerSelected() {
        binding.spinbackcolor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                backgroundColor = backgroundcolorModelList.get(i).getBgColorId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinFontStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                fontStyle = shopFontModelList.get(i).getFontStyleId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinBoardSignFontColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                fontColor = shopFontModelListSize.get(i).getFontColorId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setFontStyle() {
        ShopFontStyleModel model0 = new ShopFontStyleModel();
        model0.setFontStyleId("0");
        model0.setFontStyleName(getString(R.string.board_sign_font_style));
        model0.setFontStyle(R.string.board_sign_font_style);
        shopFontModelList.add(model0);
        ShopFontStyleModel model = new ShopFontStyleModel();
        model.setFontStyleId("1");
        model.setFontStyleName("Anton Regular");
        model.setFontStyle(R.font.anton_regular);
        shopFontModelList.add(model);
        ShopFontStyleModel model1 = new ShopFontStyleModel();
        model1.setFontStyleId("2");
        model1.setFontStyleName("Berkshire Swash Regular");
        model1.setFontStyle(R.font.berkshireswash_regular);
        shopFontModelList.add(model1);
        ShopFontStyleModel model2 = new ShopFontStyleModel();
        model2.setFontStyleId("3");
        model2.setFontStyleName("Brasika Display");
        model2.setFontStyle(R.font.brasika_display);
        shopFontModelList.add(model2);
        ShopFontStyleModel model3 = new ShopFontStyleModel();
        model3.setFontStyleId("4");
        model3.setFontStyleName("Carter One");
        model3.setFontStyle(R.font.carterone_regular);
        shopFontModelList.add(model3);
        ShopFontStyleModel model4 = new ShopFontStyleModel();
        model4.setFontStyleId("5");
        model4.setFontStyleName("Fredokaone");
        model4.setFontStyle(R.font.fredokaone_regular);
        shopFontModelList.add(model4);
        ShopFontStyleModel model5 = new ShopFontStyleModel();
        model5.setFontStyleId("6");
        model5.setFontStyleName("Gagalin");
        model5.setFontStyle(R.font.gagalin_regular);
        shopFontModelList.add(model5);
        ShopFontStyleModel model6 = new ShopFontStyleModel();
        model6.setFontStyleId("7");
        model6.setFontStyleName("Lato");
        model6.setFontStyle(R.font.lato_regular);
        shopFontModelList.add(model6);
        ShopFontStyleModel model7 = new ShopFontStyleModel();
        model7.setFontStyleId("8");
        model7.setFontStyleName("League Spartan");
        model7.setFontStyle(R.font.leaguespartan_bold);
        shopFontModelList.add(model7);
        ShopFontStyleModel model8 = new ShopFontStyleModel();
        model8.setFontStyleId("9");
        model8.setFontStyleName("Lovelo Black");
        model8.setFontStyle(R.font.lovelo_black);
        shopFontModelList.add(model8);
        ShopFontStyleModel model9 = new ShopFontStyleModel();
        model9.setFontStyleId("10");
        model9.setFontStyleName("Opensans Bold");
        model9.setFontStyle(R.font.opensans_bold);
        shopFontModelList.add(model9);
        ShopFontStyleModel model10 = new ShopFontStyleModel();
        model10.setFontStyleId("11");
        model10.setFontStyleName("Quicksand Bold");
        model10.setFontStyle(R.font.quicksand_bold);
        shopFontModelList.add(model10);
        ShopFontStyleModel model11 = new ShopFontStyleModel();
        model11.setFontStyleId("12");
        model11.setFontStyleName("Yeseva One");
        model11.setFontStyle(R.font.yesevaone_regular);
        shopFontModelList.add(model11);
        /*ShopFontStyleModel model12 = new ShopFontStyleModel();
        model12.setFontStyleId("13");
        model12.setFontStyleName("Roboto Medium Italic");
        model12.setFontStyle(R.font.roboto_mediumitalic);
        shopFontModelList.add(model12);
        ShopFontStyleModel model13 = new ShopFontStyleModel();
        model13.setFontStyleId("14");
        model13.setFontStyleName("Roboto Regular");
        model13.setFontStyle(R.font.roboto_regular);
        shopFontModelList.add(model13);
        ShopFontStyleModel model14 = new ShopFontStyleModel();
        model14.setFontStyleId("15");
        model14.setFontStyleName("Roboto Thin");
        model14.setFontStyle(R.font.roboto_thin);
        shopFontModelList.add(model14);
        ShopFontStyleModel model15 = new ShopFontStyleModel();
        model15.setFontStyleId("16");
        model15.setFontStyleName("Roboto Thin Italic");
        model15.setFontStyle(R.font.roboto_thinitalic);
        shopFontModelList.add(model15);*/

        setShopFontStyle(shopFontModelList, binding.spinFontStyle);
    }

    private void setShopFontStyle(ArrayList<ShopFontStyleModel> shopFontModelList, AppCompatSpinner spinnershopfont) {


        ArrayAdapter<ShopFontStyleModel> modelArrayAdapter = new ArrayAdapter<ShopFontStyleModel>(AddShopActivity.this,
                R.layout.custom_spinner_shopfont, shopFontModelList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_shopfont, null);
                }
                AppCompatTextView colorname = v.findViewById(R.id.fontname);

                colorname.setText(shopFontModelList.get(position).getFontStyleName());

                switch (position) {
                    case 0:
                        colorname.setTextColor(Color.GRAY);
                        break;
                    default:
                        colorname.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_shopfont1, null);
                }

                AppCompatTextView colorname = v.findViewById(R.id.fontname);
                colorname.setText(shopFontModelList.get(position).getFontStyleName());

                switch (position) {
                    case 0:
                        colorname.setTextColor(Color.GRAY);
                        break;
                    default:
                        colorname.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }
        };
        spinnershopfont.setAdapter(modelArrayAdapter);
        modelArrayAdapter.notifyDataSetChanged();


    }

    private void addShop() {
        dialog.show();
        AddShopRequestModel model = new AddShopRequestModel();
        model.setUserId(SharedPref.getVal(AddShopActivity.this, SharedPref.user_id));
        model.setShopName(Objects.requireNonNull(binding.edtShopName.getText()).toString());
        model.setShopAddress(Objects.requireNonNull(binding.edtShopAddress.getText()).toString());
        model.setShopMobileNo(Objects.requireNonNull(binding.edtMobileNumber.getText()).toString());
        model.setShopDescription(Objects.requireNonNull(binding.edtAbout.getText()).toString());
        model.setShopArea(Objects.requireNonNull(binding.edtShopArea.getText()).toString());
        model.setCity(cityName);
        model.setState(stateName);
        model.setBgColorId(backgroundColor);
        model.setFontStyleId(fontStyle);
        model.setFontSizeId(fontSize);
        model.setFontColorId(fontColor);
        model.setLatitude(String.valueOf(latitude));
        model.setLongitude(String.valueOf(longitude));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.shopAdd(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                       /* Toast.makeText(AddShopActivity.this, "Shop Added Successfully", Toast.LENGTH_SHORT).show();
                        finish();*/


                        new AlertDialog.Builder(AddShopActivity.this)
                                .setTitle("Alert!!!")
                                .setMessage("Shop added successfully and sent to our team for verification.")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        finish();
                                    }
                                })
                                .show();
                    } else {
                        Toast.makeText(AddShopActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismissDialog();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
                dismissDialog();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(AddShopActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AddShopActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AddShopActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AddShopActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AddShopActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddShopActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    /*private void requestShopFont() {
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId("");

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.fontstyleList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    shopFontModelList = new ArrayList<>();
                    AddShopFontResponseModel model1 = new AddShopFontResponseModel();

                    model1.setFontStyleId("");
                    model1.setFontStyleName("Select Font Style");
                    model1.setIsActive("");
                    shopFontModelList.add(model1);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            AddShopFontResponseModel model = new AddShopFontResponseModel();

                            model.setFontStyleId(jsonObject1.getString("font_style_id"));
                            model.setFontStyleName(jsonObject1.getString("font_style_name"));
                            model.setIsActive(jsonObject1.getString("isActive"));

                            shopFontModelList.add(model);
                        }
                    }
                    setShopFontStyle(shopFontModelList, binding.spinfont);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(AddShopActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AddShopActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AddShopActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AddShopActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AddShopActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddShopActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }*/

    private void requestShopFontSize() {
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId("");

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.fontcolorList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    shopFontModelListSize = new ArrayList<>();
                    ShopFontColorModel model1 = new ShopFontColorModel();

                    model1.setFontColorId("");
                    model1.setFontColorName("Select Font Color");
                    model1.setFontColorName2("Select Font Color");
                    shopFontModelListSize.add(model1);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            ShopFontColorModel model = new ShopFontColorModel();

                            model.setFontColorId(jsonObject1.getString("font_color_id"));
                            model.setFontColorName(jsonObject1.getString("font_color_name"));
                            model.setFontColorName2(jsonObject1.getString("font_color_name2"));

                            shopFontModelListSize.add(model);
                        }
                    }
                    setShopFontColor(shopFontModelListSize, binding.spinBoardSignFontColor);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(AddShopActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AddShopActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AddShopActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AddShopActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AddShopActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddShopActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setShopFontColor(ArrayList<ShopFontColorModel> shopFontModelList, AppCompatSpinner spinnershopfont) {


        ArrayAdapter<ShopFontColorModel> modelArrayAdapter = new ArrayAdapter<ShopFontColorModel>(AddShopActivity.this,
                R.layout.custom_spinner_backgroundcolor, shopFontModelList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_backgroundcolor, null);
                }
                View color = v.findViewById(R.id.bacKcolor);
                AppCompatTextView colorname = v.findViewById(R.id.colorname);
                colorname.setText(shopFontModelList.get(position).getFontColorName2());
                if (!shopFontModelList.get(position).getFontColorId().equals("")) {
                    color.setBackgroundColor(Color.parseColor(shopFontModelList.get(position).getFontColorName()));
                }

                switch (position) {
                    case 0:
                        colorname.setTextColor(Color.GRAY);
                        break;
                    default:
                        colorname.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_backgroundcolor1, null);
                }

                View color = v.findViewById(R.id.bacKcolor);
                AppCompatTextView colorname = v.findViewById(R.id.colorname);
                colorname.setText(shopFontModelList.get(position).getFontColorName2());
                if (!shopFontModelList.get(position).getFontColorId().equals("")) {
                    color.setBackgroundColor(Color.parseColor(shopFontModelList.get(position).getFontColorName()));
                }

                switch (position) {
                    case 0:
                        colorname.setTextColor(Color.GRAY);
                        break;
                    default:
                        colorname.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }
        };
        spinnershopfont.setAdapter(modelArrayAdapter);
        modelArrayAdapter.notifyDataSetChanged();


    }

    private void requestBackgroundColor() {
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId("");

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.bgcolorList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    backgroundcolorModelList = new ArrayList<>();
                    AddBackgroundColorResponseModel model1 = new AddBackgroundColorResponseModel();

                    model1.setBgColorId("");
                    model1.setBgColorName("Select Background Color");
                    model1.setBgColorName2("Select Background Color");
                    model1.setIsActive("");
                    backgroundcolorModelList.add(model1);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            AddBackgroundColorResponseModel model = new AddBackgroundColorResponseModel();

                            model.setBgColorId(jsonObject1.getString("bg_color_id"));
                            model.setBgColorName(jsonObject1.getString("bg_color_name"));
                            model.setBgColorName2(jsonObject1.getString("bg_color_name2"));
                            model.setIsActive(jsonObject1.getString("isActive"));

                            backgroundcolorModelList.add(model);
                        }
                    }
                    setShopBackgroundcolor(backgroundcolorModelList, binding.spinbackcolor);

//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(AddShopActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AddShopActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AddShopActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AddShopActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AddShopActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddShopActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setShopBackgroundcolor(ArrayList<AddBackgroundColorResponseModel> backgroundcolorModelList, AppCompatSpinner spinbackcolor) {

        ArrayAdapter<AddBackgroundColorResponseModel> modelArrayAdapter = new ArrayAdapter<AddBackgroundColorResponseModel>(AddShopActivity.this,
                R.layout.custom_spinner_backgroundcolor, backgroundcolorModelList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_backgroundcolor, null);
                }
                View color = (View) v.findViewById(R.id.bacKcolor);
                AppCompatTextView colorname = (AppCompatTextView) v.findViewById(R.id.colorname);
                colorname.setText(backgroundcolorModelList.get(position).getBgColorName2());
                if (!backgroundcolorModelList.get(position).getBgColorId().equals("")) {
                    color.setBackgroundColor(Color.parseColor(backgroundcolorModelList.get(position).getBgColorName()));
                }

                switch (position) {
                    case 0:
                        colorname.setTextColor(Color.GRAY);
                        break;
                    default:
                        colorname.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_backgroundcolor1, null);
                }

                View color = (View) v.findViewById(R.id.bacKcolor);
                AppCompatTextView colorname = (AppCompatTextView) v.findViewById(R.id.colorname);
                colorname.setText(backgroundcolorModelList.get(position).getBgColorName2());
                if (!backgroundcolorModelList.get(position).getBgColorId().equals("")) {
                    color.setBackgroundColor(Color.parseColor(backgroundcolorModelList.get(position).getBgColorName()));
                }

                switch (position) {
                    case 0:
                        colorname.setTextColor(Color.GRAY);
                        break;
                    default:
                        colorname.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }
        };
        spinbackcolor.setAdapter(modelArrayAdapter);
        modelArrayAdapter.notifyDataSetChanged();


    }

    public class ClickHandler {

        public void onSelectAddress(View view) {
            Intent destinationIntent = new Intent(AddShopActivity.this, SelectCurrentLocationActivity.class);
//                Intent destinationIntent = new Intent(this, SelectLocationActivity.class);
            destinationIntent.putExtra("data", true);
            startActivityForResult(destinationIntent, 100);
        }

        public void onBackClick(View view) {
            onBackPressed();
        }

        public void onAddShop(View view) {
            if (binding.edtShopName.getText().toString().equals("")) {
                Toast.makeText(AddShopActivity.this, "Enter Shop Name", Toast.LENGTH_SHORT).show();
            } else if (binding.edtShopAddress.getText().toString().equals("")) {
                Toast.makeText(AddShopActivity.this, "Enter Shop Address", Toast.LENGTH_SHORT).show();
            } else if (binding.edtShopCity.getText().toString().equals("")) {
                Toast.makeText(AddShopActivity.this, "Enter Shop City", Toast.LENGTH_SHORT).show();
            } else if (binding.edtShopArea.getText().toString().equals("")) {
                Toast.makeText(AddShopActivity.this, "Enter Shop Area", Toast.LENGTH_SHORT).show();
            } else if (binding.edtShopState.getText().toString().equals("")) {
                Toast.makeText(AddShopActivity.this, "Enter Shop State", Toast.LENGTH_SHORT).show();
            } else if (binding.edtMobileNumber.getText().toString().equals("")) {
                Toast.makeText(AddShopActivity.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
            } else if (binding.edtMobileNumber.getText().length() != 10) {
                Toast.makeText(AddShopActivity.this, "Enter valid Mobile Number", Toast.LENGTH_SHORT).show();
            } else if (backgroundColor.equals("")) {
                Toast.makeText(AddShopActivity.this, "Select Background Color", Toast.LENGTH_SHORT).show();
            } else if (fontStyle.equals("")) {
                Toast.makeText(AddShopActivity.this, "Select Font Style", Toast.LENGTH_SHORT).show();
            } else if (fontColor.equals("")) {
                Toast.makeText(AddShopActivity.this, "Select Font Color", Toast.LENGTH_SHORT).show();
            } else if (binding.edtAbout.getText().toString().equals("")) {
                Toast.makeText(AddShopActivity.this, "Enter About Shop", Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.isNetwork(AddShopActivity.this)) {
                    addShop();
                } else {
                    Utils.InternetAlertDialog(AddShopActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }
        }

        public void onStateClick(View view) {
            Intent intent = new Intent(AddShopActivity.this, StateListActivity.class);
            startActivityForResult(intent, SELECT_STATE);

        }

        public void onCityClick(View view) {
            if (stateId.equals("")) {
                Toast.makeText(AddShopActivity.this, "Select State", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(AddShopActivity.this, CityListActivity.class);
                intent.putExtra("state_id", stateId);
                startActivityForResult(intent, SELECT_CITY);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(AddShopActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) + ContextCompat
                .checkSelfPermission(AddShopActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (AddShopActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (AddShopActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Snackbar.make(AddShopActivity.this.findViewById(android.R.id.content),
                        "Please Grant Permissions to access your location",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                        new String[]{Manifest.permission
                                                .ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_LOCATION_REQUEST);
//                                }
                            }
                        }).show();
            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission
                                .ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_LOCATION_REQUEST);
//                }
            }
        } else {
            // write your logic code if permission already granted
            mLocationManager.addGpsStatusListener(this);
            boolean isclick = true;
            if (isclick) {
                locationTrack = new LocationTrack(AddShopActivity.this);

                if (gpsTracker.canGetLocation()) {

                    longitude = gpsTracker.getLongitude();
                    latitude = gpsTracker.getLatitude();


                    try {
                        binding.edtShopLatLong.setText(String.valueOf(latitude) + "," + String.valueOf(longitude));
                        /*addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        if (addresses.size() == 0) {
                            Toast.makeText(AddShopActivity.this, "unable to get location", Toast.LENGTH_SHORT).show();
                        } else {
                            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        }*/
                     /*   String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        String name = getRegionName(latitude, longitude);
                        binding.txtSelectedLocation.setVisibility(View.VISIBLE);
//                        binding.txtSelectedLocation.setText(address + " " + city + " " + state + " " + country + " " + knownName + " " + name + " " + postalCode);
                        binding.txtSelectedLocation.setText(address);
                        checkPincode(postalCode);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    binding.txtSelectedLocation.setText(name);
//                    checkCity(name);
//                    Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                } else {

//                    locationTrack.showSettingsAlert();
                    switchOnGPS();
                }
                isclick = false;
            } else {
               /* dialog.dismiss();
                isclick = true;*/
                isclick = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_LOCATION_REQUEST:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraPermission && readExternalFile && writeExternalFile) {
                        // write your logic here
                        boolean isclick = true;
                        if (isclick) {
                            locationTrack = new LocationTrack(AddShopActivity.this);

                            if (locationTrack.canGetLocation()) {

                                longitude = locationTrack.getLongitude();
                                latitude = locationTrack.getLatitude();

                                try {
                                    binding.edtShopLatLong.setText(String.valueOf(latitude) + "," + String.valueOf(longitude));
                                    /*addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                                    if (addresses.size() == 0) {
                                        Toast.makeText(AddShopActivity.this, "unable to get location", Toast.LENGTH_SHORT).show();
                                    } else {
                                        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    }*/
                                    /* address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName();
                                    String name = getRegionName(latitude, longitude);
                                    binding.txtSelectedLocation.setVisibility(View.VISIBLE);
                                    binding.txtSelectedLocation.setText(address);
                                    checkPincode(postalCode);*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {

                                locationTrack.showSettingsAlert();
                            }
                            isclick = false;
                        } else {
                           /* dialog.dismiss();
                            isclick = true;*/
                            isclick = true;
                        }
                    } else {
                        Snackbar.make(AddShopActivity.this.findViewById(android.R.id.content),
                                "Please Grant Permissions to Access your current location",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(
                                                    new String[]{Manifest.permission
                                                            .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                                    PERMISSIONS_LOCATION_REQUEST);
                                        }
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    private void switchOnGPS() {
        @SuppressLint("RestrictedApi") LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        task.addOnCompleteListener(task1 -> {
            try {
                LocationSettingsResponse response = task1.getResult(ApiException.class);
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult(AddShopActivity.this, REQUEST_CHECK_SETTINGS);

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
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 11);
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
        myLocation = location;

        longitude = myLocation.getLongitude();
        latitude = myLocation.getLatitude();


        try {
            binding.edtShopLatLong.setText(String.valueOf(latitude) + "," + String.valueOf(longitude));
            /*addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses.size() == 0) {
                Toast.makeText(UploadImageActivity.this, "unable to get location", Toast.LENGTH_SHORT).show();
            } else {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            }*/
                     /*   String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        String name = getRegionName(latitude, longitude);
                        binding.txtSelectedLocation.setVisibility(View.VISIBLE);
//                        binding.txtSelectedLocation.setText(address + " " + city + " " + state + " " + country + " " + knownName + " " + name + " " + postalCode);
                        binding.txtSelectedLocation.setText(address);
                        checkPincode(postalCode);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == SELECT_STATE) {
            if (resultCode == RESULT_OK) {
                stateId = data.getStringExtra("state_id");
                stateName = data.getStringExtra("state_name");

                binding.edtShopState.setText(stateName);
            }
        } else if (requestCode == SELECT_CITY) {
            if (resultCode == RESULT_OK) {
                cityId = data.getStringExtra("city_id");
                cityName = data.getStringExtra("city_name");

                binding.edtShopCity.setText(cityName);
            }
        }else */
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String address = data.getStringExtra("d_address");
                binding.edtShopAddress.setText(address);
                latitude = Double.parseDouble(data.getStringExtra("lat"));
                longitude = Double.parseDouble(data.getStringExtra("long"));
                stateName = data.getStringExtra("state");
                cityName = data.getStringExtra("city");
                String area = data.getStringExtra("area");
                binding.edtShopCity.setText(cityName);
                binding.edtShopState.setText(stateName);
                binding.edtShopArea.setText(area);
                binding.edtShopLatLong.setText(String.valueOf(latitude) + "," + String.valueOf(longitude));
            }

        }
    }


}