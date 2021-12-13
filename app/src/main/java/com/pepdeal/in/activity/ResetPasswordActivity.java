package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityLoginBinding;
import com.pepdeal.in.databinding.ActivityResetPasswordBinding;
import com.pepdeal.in.model.requestModel.ResetPasswordRequestModel;
import com.pepdeal.in.model.requestModel.UserRegisterModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    ActivityResetPasswordBinding binding;
    ProgressDialog dialog;
    String mobile_no = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password);
        binding.setHandler(new ClickHandler());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        mobile_no = getIntent().getStringExtra("mobile_no");
    }

    public class ClickHandler {
        public void onResetClick(View view) {
            if (binding.edtPassword.getText().toString().equals("")) {
                Toasty.info(ResetPasswordActivity.this, "Enter valid password", Toasty.LENGTH_SHORT, true).show();
            } else if (!binding.edtPassword.getText().toString().equals(binding.edtConfirmPassword.getText().toString())) {
                Toasty.info(ResetPasswordActivity.this, "Both password should match", Toasty.LENGTH_SHORT, true).show();
            } else {

                if (Utils.isNetwork(ResetPasswordActivity.this)) {
                    ResetPasswordRequestModel model = new ResetPasswordRequestModel();
                    model.setPassword(binding.edtPassword.getText().toString());
                    model.setMobileNo(mobile_no);

                    changePassword(model);
                } else {
                    Utils.InternetAlertDialog(ResetPasswordActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }
        }

        private void dismissDialog() {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        }

        private void changePassword(ResetPasswordRequestModel model) {

            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "", "");
            apiInterface.forgot_password(model).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        String status = jsonObject.getString("status");

                        if (status.equals("1")) {
                            Toast.makeText(ResetPasswordActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ResetPasswordActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_FORBIDDEN:
                                Toast.makeText(ResetPasswordActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                Toast.makeText(ResetPasswordActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_BAD_REQUEST:
                                Toast.makeText(ResetPasswordActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(ResetPasswordActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}