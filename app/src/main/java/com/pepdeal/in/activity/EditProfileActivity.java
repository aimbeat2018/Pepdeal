package com.pepdeal.in.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityEditProfileBinding;
import com.pepdeal.in.model.UserProfileUpdateModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding binding;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);

        binding.setHandler(new ClickHandler(EditProfileActivity.this));

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        if (Utils.isNetwork(EditProfileActivity.this)) {
            requestUsersProfileParams();
        } else {
            Utils.InternetAlertDialog(EditProfileActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }

    }

    public class ClickHandler {
        Context context;

        public ClickHandler(Context context) {
            this.context = context;
        }

        public void onBackClick(View view) {
            onBackPressed();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void onProfileClick(View view) {
            checkPermission();
        }

        private void checkPermission() {

        }

        public void onSaveDetailsClick(View view) {



            if (Utils.isNetwork(EditProfileActivity.this)) {

                String user_id = SharedPref.getVal(EditProfileActivity.this,SharedPref.user_id);
                UserProfileUpdateModel model = new UserProfileUpdateModel();
                model.setId(user_id);
                model.setUsername(binding.edtName.getText().toString());
                model.setMobileNo(binding.edtmobile.getText().toString());

                saveuserdata(model);

            } else {
                Toast.makeText(EditProfileActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        }

        private void dismissDialog() {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        }


        private void saveuserdata(UserProfileUpdateModel model) {
            dialog.show();

            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "", "");

            apiInterface.updateUserDetail(model).enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        String status = jsonObject.getString("status");
                        if (status.equals("1")) {
                            Toast.makeText(EditProfileActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            finish();
                           // requestUsersProfileParams();
                        } else {
                            Toast.makeText(EditProfileActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException | NumberFormatException | IOException e) {
                        e.printStackTrace();
                    }
                    dismissDialog();
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable error) {
                    dismissDialog();
                    error.printStackTrace();
                    if (error instanceof HttpException) {
                        switch (((HttpException) error).code()) {
                            case HttpsURLConnection.HTTP_UNAUTHORIZED:
                                Toast.makeText(EditProfileActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_FORBIDDEN:
                                Toast.makeText(EditProfileActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                Toast.makeText(EditProfileActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_BAD_REQUEST:
                                Toast.makeText(EditProfileActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(EditProfileActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }


    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void requestUsersProfileParams() {

        UserProfileRequestModel requestModel = new UserProfileRequestModel();
        requestModel.setUserId(SharedPref.getVal(EditProfileActivity.this, SharedPref.user_id));

        userProfile(requestModel);

    }

    private void userProfile(UserProfileRequestModel model) {
        dialog.show();

        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "", "");

        apiInterface.user_detail(model).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    String status = jsonObject.getString("status");

                    if (status.equals("1")) {

                        JSONObject jsonObject1 = jsonObject.getJSONObject("body");

                        String username = jsonObject1.getString("first_name");
                        String mobile = jsonObject1.getString("mobile_no");
                        binding.edtName.setText(username);
                        binding.edtmobile.setText(mobile);

                    } else {
                        binding.edtName.setText("");
                        binding.edtmobile.setText("");
                    }

                } catch (JSONException | NumberFormatException | IOException e) {
                    e.printStackTrace();
                }
                dismissDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable error) {
                dismissDialog();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(EditProfileActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(EditProfileActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(EditProfileActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(EditProfileActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(EditProfileActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}

