package com.pepdeal.in.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pepdeal.in.R;
import com.pepdeal.in.activity.AddShopActivity;
import com.pepdeal.in.activity.HomeActivity;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.FragmentHelpBinding;
import com.pepdeal.in.model.requestModel.AddShopRequestModel;
import com.pepdeal.in.model.requestModel.HelpRequestModel;

import org.json.JSONObject;

import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class HelpFragment extends Fragment {

    Activity activity;
    FragmentHelpBinding binding;
    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_help, container, false);
        activity = (HomeActivity) getActivity();

        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        binding.cardSubmit.setOnClickListener(view -> {
            if (binding.edtName.getText().toString().equals("")) {
                Toast.makeText(activity, "Enter Name", Toast.LENGTH_SHORT).show();
            } else if (binding.edtEmail.getText().toString().equals("") || !Utils.isValidEmail(binding.edtEmail.getText().toString())) {
                Toast.makeText(activity, "Enter Email", Toast.LENGTH_SHORT).show();
            } else if (binding.edtMobile.getText().toString().equals("") || binding.edtMobile.getText().length() != 10) {
                Toast.makeText(activity, "Enter Mobile number", Toast.LENGTH_SHORT).show();
            } else if (binding.edtQuery.getText().toString().equals("")) {
                Toast.makeText(activity, "Enter Query", Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.isNetwork(activity)) {
                    addQuery();
                } else {
                    Utils.InternetAlertDialog(activity, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }
        });

        return binding.getRoot();
    }

    private void addQuery() {
        dialog.show();
        HelpRequestModel model = new HelpRequestModel();
//        model.setUserId(SharedPref.getVal(activity, SharedPref.user_id));
        model.setEnquiryName(Objects.requireNonNull(binding.edtName.getText()).toString());
        model.setEmailId(Objects.requireNonNull(binding.edtEmail.getText()).toString());
        model.setQuery(Objects.requireNonNull(binding.edtQuery.getText()).toString());
        model.setMobileNo(Objects.requireNonNull(binding.edtMobile.getText()).toString());

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.enquiryForm(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(activity, "Query Added Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(activity, HomeActivity.class));
                        HomeActivity.pos = 1;
                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismissDialog();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
                dismissDialog();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(activity, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(activity, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(activity, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(activity, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(activity, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

}