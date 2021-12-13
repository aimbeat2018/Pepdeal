package com.pepdeal.in.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityForgotPasswordBinding;
import com.pepdeal.in.model.ForgotPasswordOTPRequestModel;

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

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    String from = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        binding.setHandler(new ClickHandler());
        from = getIntent().getStringExtra("from");
    }

    public class ClickHandler {
        public void registerClick(View view) {
            startActivity(new Intent(ForgotPasswordActivity.this, RegistrationActivity.class));
        }

        public void onSendOtp(View view) {
            if (binding.edtMobileNo.getText().toString().equals("") || binding.edtMobileNo.getText().length() != 10) {
                Toasty.info(ForgotPasswordActivity.this, "Enter valid mobile number", Toasty.LENGTH_SHORT, true).show();
            } else {

                // validation();
                if (Utils.isNetwork(ForgotPasswordActivity.this)) {
                    ForgotPasswordOTPRequestModel model = new ForgotPasswordOTPRequestModel();
                    model.setMobileNo(binding.edtMobileNo.getText().toString().trim());
                    sendOtp(model);
                } else {
                    Utils.InternetAlertDialog(ForgotPasswordActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }
        }

        private void sendOtp(ForgotPasswordOTPRequestModel model) {
            //dialog.show();
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "", "");
            apiInterface.forgotpassword_send_otp(model).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());

                        String status = jsonObject.getString("status");
                        if (status.equals("1")) {
                            Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            String otp = jsonObject.getString("otp");
                            Intent intent = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
                            intent.putExtra("otp", otp);
                            intent.putExtra("mobile_no", binding.edtMobileNo.getText().toString());
                            intent.putExtra("from", "forgot");
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException | IOException jsonException) {
                        jsonException.printStackTrace();
                    }


                    // dismissDialog();

                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable error) {

                    //  dismissDialog();
                    error.printStackTrace();
                    if (error instanceof HttpException) {
                        switch (((HttpException) error).code()) {
                            case HttpsURLConnection.HTTP_UNAUTHORIZED:
                                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_FORBIDDEN:
                                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_BAD_REQUEST:
                                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(ForgotPasswordActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }


                }
            });


        }


    }
}