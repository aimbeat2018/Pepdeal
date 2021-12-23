package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.adapter.ProductAdapter;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityAboutUsBinding;
import com.pepdeal.in.model.AppDataModel;
import com.pepdeal.in.model.productdetailsmodel.ProductDetailsDataModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopDetailsDataModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopServiceAvailableModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class AboutUsActivity extends AppCompatActivity {

    ActivityAboutUsBinding binding;
    String from = "";
    ProgressDialog dialog;
    AppDataModel appDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);
        binding.setHandler(new ClickHandler());
        from = getIntent().getStringExtra("from");

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        if (from.equals("about")) {
            binding.txtTitle.setText("About Us");
        } else if (from.equals("privacy")) {
            binding.txtTitle.setText("Privacy policy");
        } else if (from.equals("terms")) {
            binding.txtTitle.setText("Terms & Condition");
        } else if (from.equals("contact")) {
            binding.txtTitle.setText("Contact Us");
        }

        if (Utils.isNetwork(AboutUsActivity.this)) {
            getData();
        } else {
            Toast.makeText(AboutUsActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void getData() {
        dialog.show();

        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(AboutUsActivity.this, SharedPref.user_id));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.pagesList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {

                        /*Data json*/
                        Gson gson1 = new Gson();
                        appDataModel = gson1.fromJson(jsonObject.getString("Data"), AppDataModel.class);

                        if (from.equals("about")) {
                            binding.txtData.setText(Html.fromHtml(appDataModel.getAboutUs()));
                        } else if (from.equals("privacy")) {
                            binding.txtData.setText(Html.fromHtml(appDataModel.getPrivacy()));
                        } else if (from.equals("terms")) {
                            binding.txtData.setText(Html.fromHtml(appDataModel.getTermsCondition()));
                        } else if (from.equals("contact")) {
                            binding.txtData.setText(Html.fromHtml(appDataModel.getContactUs()));
                        }

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
                            Toast.makeText(AboutUsActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AboutUsActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AboutUsActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AboutUsActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AboutUsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AboutUsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}