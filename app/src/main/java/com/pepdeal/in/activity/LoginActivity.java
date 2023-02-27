package com.pepdeal.in.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
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
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final String VERSION_CODE_KEY = "version_code";
    private AlertDialog updateDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setHandler(new ClickHandler(this));

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        initRemoteConfig();

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
            } else if (!validatePassword()) {
                Toasty.info(LoginActivity.this, "Password should be alphanumerical with minimum 8 characters", Toasty.LENGTH_SHORT, true).show();
            } else {
                if (Utils.isNetwork(LoginActivity.this)) {
                    requestParams();
                } else {
                    Utils.InternetAlertDialog(LoginActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }
        }

        public void onPasswordClick(View view) {
            if (binding.edtPassword.getTransformationMethod().getClass().getSimpleName().equals("PasswordTransformationMethod")) {
                binding.edtPassword.setTransformationMethod(new SingleLineTransformationMethod());
                binding.imgPassword.setImageResource(R.drawable.ic_baseline_visibility_24);
            } else {
                binding.edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                binding.imgPassword.setImageResource(R.drawable.ic_baseline_visibility_off_24);
            }

            binding.edtPassword.setSelection(binding.edtPassword.getText().length());
        }
    }

    private void requestParams() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isComplete()) {
                firebaseToken = task.getResult();
                Log.e("AppConstants", "onComplete: new Token got: " + firebaseToken);
                LoginRequestModel loginRequestModel = new LoginRequestModel();
                loginRequestModel.setMobileNo(binding.edtMobile.getText().toString());
                loginRequestModel.setPassword(binding.edtPassword.getText().toString());
                loginRequestModel.setDeviceToken(firebaseToken);

                loginUser(loginRequestModel);
            }
        });
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private boolean validatePassword() {
        String passwordInput = binding.edtPassword.getText().toString().trim();

        if (!passwordInput.matches(".*[0-9].*")) {
//            Toast.makeText(LoginActivity.this, "Password should contain at least 1 digit", Toast.LENGTH_SHORT).show();
            return false;
        }
        /*else if (!passwordInput.matches(".*[a-z].*")) {
            Toast.makeText(RegistrationActivity.this, "Password should contain at least 1 lower case letter", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!passwordInput.matches(".*[A-Z].*")) {
            Toast.makeText(RegistrationActivity.this, "Password should contain at least 1 upper case letter", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        else if (!passwordInput.matches(".*[a-zA-Z].*")) {
//            Toast.makeText(LoginActivity.this, "Password should contain a letter", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!passwordInput.matches(".{8,}")) {
//            Toast.makeText(LoginActivity.this, "Password should contain 8 characters", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
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

    private void initRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//Setting the Default Map Value with the current app version code
//default values are used for safety if on backend version_code is not set in remote config
        HashMap<String, Object> firebaseDefaultMap = new HashMap<>();
        firebaseDefaultMap.put(VERSION_CODE_KEY, getCurrentVersionCode());
        mFirebaseRemoteConfig.setDefaultsAsync(firebaseDefaultMap);
//setMinimumFetchIntervalInSeconds to 0 during development to fast retrieve the values
//in production set it to 12 which means checks for firebase remote config values for every 12 hours
        mFirebaseRemoteConfig.setConfigSettingsAsync(
                new FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(TimeUnit.HOURS.toSeconds(0))
                        .build());
//Fetching remote firebase version_code value here
        mFirebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//activate most recently fetch config value
                    mFirebaseRemoteConfig.activate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            if (task.isSuccessful()) {
//calling function to check if new version is available or not
                                final int latestAppVersion = (int) mFirebaseRemoteConfig.getDouble(VERSION_CODE_KEY);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkForUpdate(latestAppVersion);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkForUpdate(int latestAppVersion) {
        if (latestAppVersion > getCurrentVersionCode()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update App");
            builder.setMessage("New Version Available On Play store.\n" + "Please Update Your App");
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goToPlayStore();
                    updateDailog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateDailog.dismiss();
                }
            });
            // create and show the alert dialog
            updateDailog = builder.create();
            updateDailog.show();
        }
    }

    private int getCurrentVersionCode() {
        int versionCode = 1;
        try {
            final PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = (int) pInfo.getLongVersionCode();
            } else {
                versionCode = pInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            //log exception
        }
        return versionCode;
    }

    private void goToPlayStore() {
        try {
            Intent appStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
            appStoreIntent.setPackage("com.android.vending");
            startActivity(appStoreIntent);
        } catch (android.content.ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
}
