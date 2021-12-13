package com.pepdeal.in.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityLoginBinding;
import com.pepdeal.in.model.requestModel.LoginRequestModel;

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


public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    ActivityLoginBinding binding;
    //Boolean mobileValid = false, passwordValid = false;

    String firebaseToken = "";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setHandler(new ClickHandler(this));

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Utils.InternetAlertDialog(LoginActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));

    }

    public class ClickHandler {
        Context context;

        public ClickHandler(LoginActivity loginActivity) {
            this.context = context;
        }

        public void registerClick(View view) {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        }


        public void ForgotPasswordClick(View view) {

            Intent intent = new Intent(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            intent.putExtra("from", "forgot");
            startActivity(intent);
        }

        public void onLogin(View view) {

            if (binding.edtMobile.getText().toString().equals("") || binding.edtMobile.getText().length() != 10) {
                Toasty.info(LoginActivity.this, "Enter valid mobile number", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtPassword.getText().toString().equals("")) {
                Toasty.info(LoginActivity.this, "Enter valid password", Toasty.LENGTH_SHORT, true).show();
            } else {
                validation();

                //  startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
        }


        private void validation() {


            //if (ConnectivityReceiver.isConnected()) {
            requestParams();

            //} else {
            // Utils.InternetAlertDialog(LoginActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
            // }

        }

        private void requestParams() {


           /* FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isComplete()) {
                        firebaseToken = task.getResult();
                        Log.e("AppConstants", "onComplete: new Token got: " + firebaseToken);
*/
            LoginRequestModel loginRequestModel = new LoginRequestModel();
            loginRequestModel.setMobileNo(binding.edtMobile.getText().toString());
            loginRequestModel.setPassword(binding.edtPassword.getText().toString());
            //loginRequestModel.setToken(firebaseToken);

            loginUser(loginRequestModel);
        }

        private void dismissDialog() {
            if (dialog != null && dialog.isShowing()) ;
            dialog.dismiss();
        }

        private void loginUser(LoginRequestModel model) {
            dialog.show();
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "", "");
            apiInterface.login(model).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String status = jsonObject.getString("status");


                        if (status.equals("1")) {

                            JSONObject jsonObject1 = jsonObject.getJSONObject("body");

                            String userID = jsonObject1.getString("user_id");
                            String user_Name = jsonObject1.getString("username");
                            String password = jsonObject1.getString("password");
                            String user_Mobile = jsonObject1.getString("mobile_no");

                            SharedPreferences sharedPref = getSharedPreferences("userdata", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("user_id", userID);
                            editor.putString("username", user_Name);
                            editor.putString("password", password);
                            editor.putString("mobile_no", user_Mobile);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);


                            /*intent.putExtra("name", binding.edtName.getText().toString());

                          intent.putExtra("from", "register");*/
                        } else {
                            Toast.makeText(LoginActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(LoginActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_FORBIDDEN:
                                Toast.makeText(LoginActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                Toast.makeText(LoginActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_BAD_REQUEST:
                                Toast.makeText(LoginActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(LoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }


                }
            });


        }


       /* private void showProgress() {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.txtLogin.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        private void dismissProgress() {
            binding.progressBar.setVisibility(View.GONE);
            binding.txtLogin.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }*/


       /* private void loginUser(LoginRequestModel params) {
            showProgress();
            ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
            client.login(params).enqueue(new Callback<UserRegisterModel>() {

                @Override
                public void onResponse(@NonNull Call<UserRegisterModel> call, @NonNull Response<UserRegisterModel> response) {
                    if (response.isSuccessful()) {

                        if (Objects.requireNonNull(response.body()).getStatus().equals("1")) {
//                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            //  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();

                            if (response.body().getBody().get(0).getFlag().equals("0")) {

                                *//*SharedPref.putVal(LoginActivity.this, SharedPref.user_id, response.body().getData().get(0).getUserId());
                                SharedPref.putVal(LoginActivity.this, SharedPref.user_fullname, response.body().getData().get(0).getUserFullname());
                                SharedPref.putVal(LoginActivity.this, SharedPref.email_id, response.body().getData().get(0).getEmailId());
                                SharedPref.putVal(LoginActivity.this, SharedPref.mobile_no, response.body().getData().get(0).getMobileNo());
                                SharedPref.putVal(LoginActivity.this, SharedPref.password, binding.edtPassword.getText().toString().trim());
                                SharedPref.putVal(LoginActivity.this, SharedPref.userType, "wholesale");
*//*

                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                //  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            } else {
                                android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(LoginActivity.this);
                                alertBuilder.setCancelable(true);
                                alertBuilder.setTitle(getString(R.string.alert));
                                alertBuilder.setMessage(getString(R.string.blocked_msg));
                                alertBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
//                                    startActivity(new Intent(getActivity(), BusinessDetailsActivity.class));
                                        dialog.dismiss();
                                    }
                                });

                           alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                                android.app.AlertDialog alert = alertBuilder.create();
                                alert.setCancelable(false);
                                alert.show();
                            }
//                        showRoleDialog();

                            dismissProgress();
                        } else {
                            Toast.makeText(LoginActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                            dismissProgress();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        dismissProgress();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserRegisterModel> call, @NonNull Throwable error) {
                    dismissProgress();
                    error.printStackTrace();
                    if (error instanceof HttpException) {
                        switch (((HttpException) error).code()) {
                            case HttpsURLConnection.HTTP_UNAUTHORIZED:
                                Toast.makeText(LoginActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_FORBIDDEN:
                                Toast.makeText(LoginActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                Toast.makeText(LoginActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_BAD_REQUEST:
                                Toast.makeText(LoginActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(LoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }*/
    }
}
