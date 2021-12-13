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
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityEditProfileBinding;
import com.pepdeal.in.model.UserProfileUpdateModel;

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

        binding.setHandler(new ClickHandler());
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");


        SharedPreferences sharedPreferences = getSharedPreferences("userdata", MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", "");
        String mobile = sharedPreferences.getString("mobile_no", "");
        //String username = sharedPreferences.getString("username", "");
        binding.edtmobile.setText(mobile);
       // binding.edtName.setText(username);

    }



    public class ClickHandler {
        Context context;

        public ClickHandler(Context context) {
            this.context = context;
        }

        public ClickHandler() {

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

                SharedPreferences sharedPreferences = getSharedPreferences("userdata", MODE_PRIVATE);
                String user_id = sharedPreferences.getString("user_id", "");
               /* String usermobile = sharedPreferences.getString("mobile_no", "");
                String username = sharedPreferences.getString("username", "");
*/
               // binding.userid.setText(user_id);

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
            if (dialog != null && dialog.isShowing()) ;
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
                        String body = jsonObject.getString("body");

                       /* String username=jsonObject.getString("username");
                        String password=jsonObject.getString("password");
                        String device_token=jsonObject.getString("device_token");
                        String mobile_no=jsonObject.getString("mobile_no");
*/

                        if (status.equals("1") && body.equals("1")) {
                            Toast.makeText(EditProfileActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            // String otp = jsonObject.getString("otp");

                            Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);

                            startActivity(intent);
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

}

