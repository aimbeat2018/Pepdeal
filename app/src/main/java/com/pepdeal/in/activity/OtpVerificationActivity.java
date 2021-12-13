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
import com.pepdeal.in.databinding.ActivityOtpVerificationBinding;
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
    String mobileNo = "", from = "", name = "", Intentotp = "", password = "", devicetoken = "";
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
        name = getIntent().getStringExtra("name");
        password = getIntent().getStringExtra("password");

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        if (from.equals("register")) {
            name = getIntent().getStringExtra("name");
            password = getIntent().getStringExtra("password");
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
            if (from.equals("forgot")) {
                startActivity(new Intent(OtpVerificationActivity.this, ForgotPasswordActivity.class));
            } else {
                startActivity(new Intent(OtpVerificationActivity.this, RegistrationActivity.class));
            }
        }

        public void onVerifyClick(View view) {
            if (userEnteredOTP.equals("")) {
                Toast.makeText(OtpVerificationActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
            } else if (!userEnteredOTP.equals(Intentotp)) {
                Toast.makeText(OtpVerificationActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
            } else {
                if (from.equals("register")) {

                    if (Utils.isNetwork(OtpVerificationActivity.this)) {


                        UserRegisterModel model = new UserRegisterModel();
                        model.setUsername(name);
                        model.setMobileNo(mobileNo);
                        model.setPassword(password);
                        model.setDeviceToken(devicetoken);

                        // sendOtp(model);
                        saveuserdata(model);

                        //saveuserdata(name,mobileNo,password,devicetoken);
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }

                    // startActivity(new Intent(OtpVerificationActivity.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class));
                }
            }
        }


        private void dismissDialog() {
            if (dialog != null && dialog.isShowing()) ;
            dialog.dismiss();
        }


        // private void saveuserdata(String name, String mobileNo, String password,String devicetoken)
        private void saveuserdata(UserRegisterModel model) {
            dialog.show();

            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "", "");

            apiInterface.registerUser(model).enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        String status = jsonObject.getString("code");

                       /* String username=jsonObject.getString("username");
                        String password=jsonObject.getString("password");
                        String device_token=jsonObject.getString("device_token");
                        String mobile_no=jsonObject.getString("mobile_no");
*/


                        if (status.equals("200")) {
                            Toast.makeText(OtpVerificationActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            // String otp = jsonObject.getString("otp");

                            Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);

                            startActivity(intent);
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


    }
}