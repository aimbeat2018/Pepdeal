package com.pepdeal.in.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityOtpVerificationBinding;
import com.pepdeal.in.model.ForgotPasswordOTPRequestModel;
import com.pepdeal.in.model.requestModel.LoginRequestModel;
import com.pepdeal.in.model.requestModel.UserRegisterModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;

import in.aabhasjindal.otptextview.OTPListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    ActivityOtpVerificationBinding binding;
    String mobileNo = "", from = "", name = "", Intentotp = "", password = "", devicetoken = "", email = "";
    String userEnteredOTP = "";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verification);
        binding.setHandler(new ClickHandler());

        mobileNo = getIntent().getStringExtra("mobile_no");
        from = getIntent().getStringExtra("from");
        Intentotp = getIntent().getStringExtra("otp");
      /*  name = getIntent().getStringExtra("name");
        password = getIntent().getStringExtra("password");*/

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        if (from.equals("register")) {
            name = getIntent().getStringExtra("name");
            password = getIntent().getStringExtra("password");
            email = getIntent().getStringExtra("email");
        }
        binding.txtOtpMsg.setText(getString(R.string.enter_otp_sent_to) + " " + mobileNo);


        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                userEnteredOTP = otp;
            }
        });
    }

    public class ClickHandler {
        public void onEditClick(View view) {
           /* if (from.equals("forgot")) {
                startActivity(new Intent(OtpVerificationActivity.this, OtpVerificationActivity.class));
            } else {
                startActivity(new Intent(OtpVerificationActivity.this, RegistrationActivity.class));
            }*/
            onBackPressed();
        }

        public void onVerifyClick(View view) {
            if (userEnteredOTP.equals("")) {
                Toast.makeText(OtpVerificationActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
            } else if (!userEnteredOTP.equals(Intentotp)) {
                Toast.makeText(OtpVerificationActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
            } else {
                if (from.equals("register")) {

                    if (Utils.isNetwork(OtpVerificationActivity.this)) {


                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (task.isComplete()) {
                                    devicetoken = task.getResult();
                                    Log.e("AppConstants", "onComplete: new Token got: " + devicetoken);
                                    UserRegisterModel model = new UserRegisterModel();
                                    model.setUsername(name);
                                    model.setMobileNo(mobileNo);
                                    model.setPassword(password);
                                    model.setEmail(email);
                                    model.setDeviceToken(devicetoken);
                                    saveUserData(model);
                                }
                            }
                        });

                        //saveuserdata(name,mobileNo,password,devicetoken);
                    } else {
                        Utils.InternetAlertDialog(OtpVerificationActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                    }

                    // startActivity(new Intent(OtpVerificationActivity.this, HomeActivity.class));
                } else {
                    Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("mobile_no", mobileNo);
                    startActivity(intent);
                    finish();
                }
            }
        }

        public void resendOTP(View view) {
            if (Utils.isNetwork(OtpVerificationActivity.this)) {
                ForgotPasswordOTPRequestModel model = new ForgotPasswordOTPRequestModel();
                model.setMobileNo(mobileNo);
                sendOtp(model);
            } else {
                Utils.InternetAlertDialog(OtpVerificationActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
            }
        }
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }


    // private void saveuserdata(String name, String mobileNo, String password,String devicetoken)
    private void saveUserData(UserRegisterModel model) {
        dialog.show();

        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "", "");

        apiInterface.registerUser(model).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    String status = jsonObject.getString("code");

                    if (status.equals("200")) {
                        Toast.makeText(OtpVerificationActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();

                    } else {
                        Toast.makeText(OtpVerificationActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(OtpVerificationActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(OtpVerificationActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(OtpVerificationActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(OtpVerificationActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(OtpVerificationActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OtpVerificationActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendOtp(ForgotPasswordOTPRequestModel model) {
        dialog.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "", "");
        apiInterface.forgotpassword_send_otp(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    JSONObject jsonObject = new JSONObject(response.body().string());

                    String status = jsonObject.getString("status");
                    if (status.equals("1")) {
                        Toast.makeText(OtpVerificationActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        Intentotp = jsonObject.getString("otp");
                        /*Intent intent = new Intent(OtpVerificationActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("otp", otp);
                        intent.putExtra("mobile_no", mobileNo);
                        intent.putExtra("from", from);
                        startActivity(intent);
                        finish();*/
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(OtpVerificationActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(OtpVerificationActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(OtpVerificationActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(OtpVerificationActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(OtpVerificationActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OtpVerificationActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
}