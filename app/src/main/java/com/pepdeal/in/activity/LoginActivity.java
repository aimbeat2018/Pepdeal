package com.pepdeal.in.activity;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.pepdeal.in.constants.ConnectivityReceiver;
import com.pepdeal.in.constants.SharedPref;
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
                if (Utils.isNetwork(LoginActivity.this)) {
                    requestParams();
                } else {
                    Utils.InternetAlertDialog(LoginActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }
        }

    }

    private void requestParams() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isComplete()) {
                    firebaseToken = task.getResult();
                    Log.e("AppConstants", "onComplete: new Token got: " + firebaseToken);

                    LoginRequestModel loginRequestModel = new LoginRequestModel();
                    loginRequestModel.setMobileNo(binding.edtMobile.getText().toString());
                    loginRequestModel.setPassword(binding.edtPassword.getText().toString());
                    loginRequestModel.setDeviceToken(firebaseToken);

                    loginUser(loginRequestModel);
                }
            }
        });

    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
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
                        String user_Mobile = jsonObject1.getString("mobile_no");

                        SharedPref.putVal(LoginActivity.this, SharedPref.user_id, userID);
                        SharedPref.putVal(LoginActivity.this, SharedPref.mobile_no, user_Mobile);
                        SharedPref.putVal(LoginActivity.this, SharedPref.password, binding.edtPassword.getText().toString());

                        Toast.makeText(LoginActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

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
}
