package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityContactUsBinding;
import com.pepdeal.in.databinding.ActivityHomeBinding;
import com.pepdeal.in.model.requestModel.HelpRequestModel;

import org.json.JSONObject;

import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ContactUsActivity extends AppCompatActivity {

    ActivityContactUsBinding binding;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us);
        binding.setHandler(new ClickHandler());
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        binding.ivMenu.setOnClickListener(view -> onBackPressed());

        binding.cardSubmit.setOnClickListener(view -> {
            if (binding.edtName.getText().toString().equals("")) {
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            } else if (binding.edtEmail.getText().toString().equals("") || !Utils.isValidEmail(binding.edtEmail.getText().toString())) {
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            } else if (binding.edtMobile.getText().toString().equals("") || binding.edtMobile.getText().length() != 10) {
                Toast.makeText(this, "Enter Mobile number", Toast.LENGTH_SHORT).show();
            } else if (binding.edtQuery.getText().toString().equals("")) {
                Toast.makeText(this, "Enter Query", Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.isNetwork(this)) {
                    addQuery();
                } else {
                    Utils.InternetAlertDialog(this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }
        });

    }
    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }

    private void addQuery() {
        dialog.show();
        HelpRequestModel model = new HelpRequestModel();
//        model.setUserId(SharedPref.getVal(activity, SharedPref.user_id));
        model.setEnquiryName(Objects.requireNonNull(binding.edtName.getText()).toString());
        model.setEmailId(Objects.requireNonNull(binding.edtEmail.getText()).toString());
        model.setQuery(Objects.requireNonNull(binding.edtQuery.getText()).toString());
        model.setMobileNo(Objects.requireNonNull(binding.edtMobile.getText()).toString());

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.enquiryForm(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(ContactUsActivity.this, "Query Added Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ContactUsActivity.this, HomeActivity.class));
                        HomeActivity.pos = 1;
                    } else {
                        Toast.makeText(ContactUsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ContactUsActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(ContactUsActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(ContactUsActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(ContactUsActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ContactUsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ContactUsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}