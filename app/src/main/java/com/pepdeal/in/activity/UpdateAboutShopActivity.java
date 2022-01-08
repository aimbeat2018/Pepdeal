package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityAddShopBinding;
import com.pepdeal.in.databinding.ActivityUpdateAboutShopBinding;
import com.pepdeal.in.model.requestModel.AddBackgroundColorResponseModel;
import com.pepdeal.in.model.requestModel.AddShopRequestModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopDetailsDataModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopFontColorModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopFontStyleModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class UpdateAboutShopActivity extends AppCompatActivity {
    ActivityUpdateAboutShopBinding binding;
    ProgressDialog dialog;
    ShopDetailsDataModel shopDetailsDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_about_shop);
        binding.setHandler(new ClickHandler());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getShopDetails(true);
    }

    private void addShop() {
        dialog.show();
        AddShopRequestModel model = new AddShopRequestModel();
        model.setUserId(SharedPref.getVal(UpdateAboutShopActivity.this, SharedPref.user_id));
        model.setShopId(SharedPref.getVal(UpdateAboutShopActivity.this, SharedPref.shop_id));
        model.setShopDescription(Objects.requireNonNull(binding.edtAbout.getText()).toString());

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.aboutshopupdate(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(UpdateAboutShopActivity.this, "About shop updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UpdateAboutShopActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(UpdateAboutShopActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(UpdateAboutShopActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(UpdateAboutShopActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(UpdateAboutShopActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(UpdateAboutShopActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpdateAboutShopActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }

        public void onAddShop(View view) {
            if (binding.edtAbout.getText().toString().equals("")) {
                Toast.makeText(UpdateAboutShopActivity.this, "Enter About Shop", Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.isNetwork(UpdateAboutShopActivity.this)) {
                    addShop();
                } else {
                    Utils.InternetAlertDialog(UpdateAboutShopActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }
        }
    }

    private void getShopDetails(boolean isLoading) {
        if (isLoading)
            dialog.show();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(UpdateAboutShopActivity.this, SharedPref.user_id));
        model.setShop_id(SharedPref.getVal(UpdateAboutShopActivity.this, SharedPref.shop_id));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.shopListWithDetail(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {

                        /*Shop Details json*/
                        Gson gson2 = new Gson();
                        shopDetailsDataModel = gson2.fromJson(jsonObject.getString("ShopDetail"), ShopDetailsDataModel.class);

                        binding.edtAbout.setText(shopDetailsDataModel.getShopDescription());


                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismissDialog();

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                dismissDialog();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(UpdateAboutShopActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(UpdateAboutShopActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(UpdateAboutShopActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(UpdateAboutShopActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(UpdateAboutShopActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpdateAboutShopActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}