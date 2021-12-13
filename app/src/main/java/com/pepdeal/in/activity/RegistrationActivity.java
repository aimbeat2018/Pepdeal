package com.pepdeal.in.activity;

import android.app.ProgressDialog;
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
import com.pepdeal.in.databinding.ActivityRegistrationBinding;
import com.pepdeal.in.model.requestModel.OTPRequestModel;

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

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding binding;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        binding.setHandler(new ClickHandler());
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
    }

    public class ClickHandler {

        public void onRegistration(View view) {
            if (binding.edtName.getText().toString().equals("")) {
                Toasty.info(RegistrationActivity.this, "Enter name to continue", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtMobileNo.getText().toString().equals("") || binding.edtMobileNo.getText().length() != 10) {
                Toasty.info(RegistrationActivity.this, "Enter valid mobile number", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtPassword.getText().toString().equals("")) {
                Toasty.info(RegistrationActivity.this, "Enter valid password", Toasty.LENGTH_SHORT, true).show();
            } else if (!binding.edtPassword.getText().toString().equals(binding.edtConfirmPassword.getText().toString())) {
                Toasty.info(RegistrationActivity.this, "Both password should match", Toasty.LENGTH_SHORT, true).show();
            } else {
               /* Intent intent = new Intent(RegistrationActivity.this, OtpVerificationActivity.class);
                intent.putExtra("mobile_no", binding.edtMobileNo.getText().toString());
                intent.putExtra("from", "register");
                startActivity(intent);*/

                if (Utils.isNetwork(RegistrationActivity.this)) {

                    OTPRequestModel model = new OTPRequestModel();
                    model.setMobileNo(binding.edtMobileNo.getText().toString().trim());
                    sendOtp(model);
                } else {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        }

        public void onLoginClick(View view) {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        }
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) ;
        dialog.dismiss();
    }

    private void sendOtp(OTPRequestModel model) {
        dialog.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "", "");
        apiInterface.send_otp(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                  JSONObject  jsonObject = new JSONObject(response.body().string());

                    String status = jsonObject.getString("status");
                    if (status.equals("1")) {
                        Toast.makeText(RegistrationActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        String otp = jsonObject.getString("otp");
                        Intent intent = new Intent(RegistrationActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("name", binding.edtName.getText().toString());
                        intent.putExtra("mobile_no", binding.edtMobileNo.getText().toString());
                        intent.putExtra("otp", otp);
                        intent.putExtra("password", binding.edtPassword.getText().toString());
                        intent.putExtra("from", "register");
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegistrationActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException | IOException jsonException) {
                    jsonException.printStackTrace();
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
                            Toast.makeText(RegistrationActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(RegistrationActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(RegistrationActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(RegistrationActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(RegistrationActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }



            }
        });




    }
}