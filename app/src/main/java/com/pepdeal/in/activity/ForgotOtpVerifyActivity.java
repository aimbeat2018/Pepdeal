package com.pepdeal.in.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.databinding.ActivityVerrifyForgotOtpBinding;
import com.pepdeal.in.model.requestModel.UserRegisterModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ForgotOtpVerifyActivity extends AppCompatActivity {

    ActivityVerrifyForgotOtpBinding binding;
    String mobileNo = "", from = "", name = "", Intentotp = "", password = "", devicetoken = "";
    String userEnteredOTP = "";
    ProgressDialog dialog;
    AppCompatTextView verifyotp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verrify_forgot_otp);
        // binding.setHandler(new ClickHandler());
        verifyotp = (AppCompatTextView) findViewById(R.id.forgototp);
        // mobileNo = getIntent().getStringExtra("mobile_no");
        // from = getIntent().getStringExtra("from");
        Intentotp = getIntent().getStringExtra("otp");
        // name = getIntent().getStringExtra("name");
        // password = getIntent().getStringExtra("password");
        OtpTextView otpView = (OtpTextView) findViewById(R.id.otpView);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        // getUserEnterOtp();

        verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyotp();


            }
        });


        if (from.equals("register")) {
            name = getIntent().getStringExtra("name");
            password = getIntent().getStringExtra("password");
        }
        // binding.txtOtpMsg.setText(getString(R.string.enter_otp_sent_to) + " " + mobileNo);

        otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                userEnteredOTP = otp;
            }
        });
    }

    private void verifyotp() {
        if (userEnteredOTP.equals("")) {
            Toast.makeText(ForgotOtpVerifyActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
        } else if (!userEnteredOTP.equals(Intentotp)) {
            Toast.makeText(ForgotOtpVerifyActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
        } else {
            if (from.equals("register")) {
                Intent intent = new Intent(ForgotOtpVerifyActivity.this, RegistrationActivity.class);
                intent.putExtra("mobile", mobileNo);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(ForgotOtpVerifyActivity.this, ResetPasswordActivity.class);
                intent.putExtra("mobile", mobileNo);
                startActivity(intent);
                finish();
            }
        }


    }


   /* private void getUserEnterOtp() {
        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                userEnteredOTP = otp;
            }
        });

    }*/

    /*public class ClickHandler {
        public void onEditClick(View view) {
            if (from.equals("forgot")) {
                startActivity(new Intent(ForgotOtpVerifyActivity.this, ForgotPasswordActivity.class));
            } else {
                startActivity(new Intent(ForgotOtpVerifyActivity.this, RegistrationActivity.class));
            }
        }


        private void onVerifyClick() {
            if (userEnteredOTP.equals("")) {
                Toast.makeText(ForgotOtpVerifyActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
            } else if (!userEnteredOTP.equals(Intentotp)) {
                Toast.makeText(ForgotOtpVerifyActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
            } else {
                if (from.equals("register")) {
                    Intent intent = new Intent(ForgotOtpVerifyActivity.this, RegistrationActivity.class);
                    intent.putExtra("mobile", mobileNo);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(ForgotOtpVerifyActivity.this, ForgotPasswordActivity.class);
                    intent.putExtra("mobile", mobileNo);
                    startActivity(intent);
                    finish();
                }
            }
        }


        *//*public void onVerifyClick(View view) {
            if (userEnteredOTP.equals("")) {
                Toast.makeText(ForgotOtpVerifyActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
            } else if (!userEnteredOTP.equals(Intentotp)) {
                Toast.makeText(ForgotOtpVerifyActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
            } else {
                if (from.equals("register")) {

                    if (Utils.isNetwork(ForgotOtpVerifyActivity.this)) {

                        UserRegisterModel model = new UserRegisterModel();
                        model.setUsername(name);
                        model.setMobileNo(mobileNo);
                        model.setPassword(password);
                        model.setDeviceToken(devicetoken);

                        // sendOtp(model);
                        saveuserdata(model);

                        //saveuserdata(name,mobileNo,password,devicetoken);
                    } else {
                        Toast.makeText(ForgotOtpVerifyActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }

                    // startActivity(new Intent(OtpVerificationActivity.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(ForgotOtpVerifyActivity.this, ResetPasswordActivity.class));
                }
            }
        }
*//*


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

                       *//* String username=jsonObject.getString("username");
                        String password=jsonObject.getString("password");
                        String device_token=jsonObject.getString("device_token");
                        String mobile_no=jsonObject.getString("mobile_no");
*//*


                        if (status.equals("200")) {
                            Toast.makeText(ForgotOtpVerifyActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            // String otp = jsonObject.getString("otp");

                            Intent intent = new Intent(ForgotOtpVerifyActivity.this, LoginActivity.class);

                            startActivity(intent);
                        } else {
                            Toast.makeText(ForgotOtpVerifyActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ForgotOtpVerifyActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_FORBIDDEN:
                                Toast.makeText(ForgotOtpVerifyActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                Toast.makeText(ForgotOtpVerifyActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_BAD_REQUEST:
                                Toast.makeText(ForgotOtpVerifyActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(ForgotOtpVerifyActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ForgotOtpVerifyActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }*/
}
