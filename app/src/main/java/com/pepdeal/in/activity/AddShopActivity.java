package com.pepdeal.in.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;

import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityAddShopBinding;
import com.pepdeal.in.model.requestModel.AddBackgroundColorResponseModel;
import com.pepdeal.in.model.requestModel.AddShopFontResponseModel;
import com.pepdeal.in.model.requestModel.AddShopRequestModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class AddShopActivity extends AppCompatActivity {

    ActivityAddShopBinding binding;

    ArrayList<AddBackgroundColorResponseModel> backgroundcolorModelList = new ArrayList<>();
    ArrayList<AddShopFontResponseModel> shopFontModelList = new ArrayList<>();
    ArrayList<AddShopFontResponseModel> shopFontModelListSize = new ArrayList<>();
    String backgroundColor = "", fontStyle = "", fontSize = "";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_shop);
        binding.setHandler(new ClickHandler());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        if (Utils.isNetwork(AddShopActivity.this)) {
            requestBackgroundColor();
            requestShopFont();
            requestShopFontSize();
        } else {
            Utils.InternetAlertDialog(AddShopActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }

        onSpinnerSelected();
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void onSpinnerSelected() {
        binding.spinbackcolor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                backgroundColor = backgroundcolorModelList.get(i).getBgColorId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinfont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                fontStyle = shopFontModelList.get(i).getFontStyleId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinBoardSignFontSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                fontSize = shopFontModelListSize.get(i).getFontStyleId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void addShop() {
        dialog.show();
        AddShopRequestModel model = new AddShopRequestModel();
        model.setUserId(SharedPref.getVal(AddShopActivity.this, SharedPref.user_id));
        model.setShopName(Objects.requireNonNull(binding.edtShopName.getText()).toString());
        model.setShopAddress(Objects.requireNonNull(binding.edtShopAddress.getText()).toString());
        model.setShopMobileNo(Objects.requireNonNull(binding.edtMobileNumber.getText()).toString());
        model.setBgColorId(backgroundColor);
        model.setFontStyleId(fontStyle);
        model.setFontSizeId(fontSize);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.shopAdd(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(AddShopActivity.this, "Shop Added Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddShopActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AddShopActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AddShopActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AddShopActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AddShopActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AddShopActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddShopActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void requestShopFont() {
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId("");

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.fontstyleList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    shopFontModelList = new ArrayList<>();
                    AddShopFontResponseModel model1 = new AddShopFontResponseModel();

                    model1.setFontStyleId("");
                    model1.setFontStyleName("Select Font Style");
                    model1.setIsActive("");
                    shopFontModelList.add(model1);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            AddShopFontResponseModel model = new AddShopFontResponseModel();

                            model.setFontStyleId(jsonObject1.getString("font_style_id"));
                            model.setFontStyleName(jsonObject1.getString("font_style_name"));
                            model.setIsActive(jsonObject1.getString("isActive"));

                            shopFontModelList.add(model);
                        }
                    }
                    setShopFontStyle(shopFontModelList, binding.spinfont);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(AddShopActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AddShopActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AddShopActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AddShopActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AddShopActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddShopActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void requestShopFontSize() {
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId("");

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.fontsizeList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    shopFontModelListSize = new ArrayList<>();
                    AddShopFontResponseModel model1 = new AddShopFontResponseModel();

                    model1.setFontStyleId("");
                    model1.setFontStyleName("Select Font Size");
                    model1.setIsActive("");
                    shopFontModelListSize.add(model1);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            AddShopFontResponseModel model = new AddShopFontResponseModel();

                            model.setFontStyleId(jsonObject1.getString("font_size_id"));
                            model.setFontStyleName(jsonObject1.getString("font_size_name"));
                            model.setIsActive(jsonObject1.getString("isActive"));

                            shopFontModelListSize.add(model);
                        }
                    }
                    setShopFontStyle(shopFontModelListSize, binding.spinBoardSignFontSize);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(AddShopActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AddShopActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AddShopActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AddShopActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AddShopActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddShopActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setShopFontStyle(ArrayList<AddShopFontResponseModel> shopFontModelList, AppCompatSpinner spinnershopfont) {


        ArrayAdapter<AddShopFontResponseModel> modelArrayAdapter = new ArrayAdapter<AddShopFontResponseModel>(AddShopActivity.this,
                R.layout.custom_spinner_shopfont, shopFontModelList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_shopfont, null);
                }

                TextView font_name = (TextView) v.findViewById(R.id.fontname);
                //  colorid.setText(backgroundcolorModelList.get(position).getIsActive());
                font_name.setText(shopFontModelList.get(position).getFontStyleName());
                //font_name.setBackgroundColor(Color.parseColor(shopFontModelList.get(position).getBgColorName()));


//                image.setImageResource(paymentModeArrayList.get(position).getImage());

                switch (position) {
                    case 0:
                        font_name.setTextColor(Color.GRAY);
                        break;
                    default:
                        font_name.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_shopfont1, null);
                }

                TextView font_name = (TextView) v.findViewById(R.id.fontname);

                font_name.setText(shopFontModelList.get(position).getFontStyleName());


//                image.setImageResource(paymentModeArrayList.get(position).getImage());

                switch (position) {
                    case 0:
                        font_name.setTextColor(Color.GRAY);
                        break;
                    default:
                        font_name.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }
        };
        spinnershopfont.setAdapter(modelArrayAdapter);
        modelArrayAdapter.notifyDataSetChanged();


    }

    private void requestBackgroundColor() {
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId("");

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.bgcolorList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    backgroundcolorModelList = new ArrayList<>();
                    AddBackgroundColorResponseModel model1 = new AddBackgroundColorResponseModel();

                    model1.setBgColorId("");
                    model1.setBgColorName("Select Background Color");
                    model1.setIsActive("");
                    backgroundcolorModelList.add(model1);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            AddBackgroundColorResponseModel model = new AddBackgroundColorResponseModel();

                            model.setBgColorId(jsonObject1.getString("bg_color_id"));
                            model.setBgColorName(jsonObject1.getString("bg_color_name"));
                            model.setIsActive(jsonObject1.getString("isActive"));

                            backgroundcolorModelList.add(model);
                        }
                    }
                    setShopBackgroundcolor(backgroundcolorModelList, binding.spinbackcolor);

//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(AddShopActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AddShopActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AddShopActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AddShopActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AddShopActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddShopActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setShopBackgroundcolor(ArrayList<AddBackgroundColorResponseModel> backgroundcolorModelList, AppCompatSpinner spinbackcolor) {

        ArrayAdapter<AddBackgroundColorResponseModel> modelArrayAdapter = new ArrayAdapter<AddBackgroundColorResponseModel>(AddShopActivity.this,
                R.layout.custom_spinner_backgroundcolor, backgroundcolorModelList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_backgroundcolor, null);
                }
                View color = (View) v.findViewById(R.id.bacKcolor);
                AppCompatTextView colorname = (AppCompatTextView) v.findViewById(R.id.colorname);
                colorname.setText(backgroundcolorModelList.get(position).getBgColorName());
                if (!backgroundcolorModelList.get(position).getBgColorId().equals("")) {
                    color.setBackgroundColor(Color.parseColor(backgroundcolorModelList.get(position).getBgColorName()));
                }

                switch (position) {
                    case 0:
                        colorname.setTextColor(Color.GRAY);
                        break;
                    default:
                        colorname.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_backgroundcolor1, null);
                }

                View color = (View) v.findViewById(R.id.bacKcolor);
                AppCompatTextView colorname = (AppCompatTextView) v.findViewById(R.id.colorname);
                colorname.setText(backgroundcolorModelList.get(position).getBgColorName());
                if (!backgroundcolorModelList.get(position).getBgColorId().equals("")) {
                    color.setBackgroundColor(Color.parseColor(backgroundcolorModelList.get(position).getBgColorName()));
                }

                switch (position) {
                    case 0:
                        colorname.setTextColor(Color.GRAY);
                        break;
                    default:
                        colorname.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }
        };
        spinbackcolor.setAdapter(modelArrayAdapter);
        modelArrayAdapter.notifyDataSetChanged();


    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }

        public void onAddShop(View view) {
            if (binding.edtShopName.getText().toString().equals("")) {
                Toast.makeText(AddShopActivity.this, "Enter Shop Name", Toast.LENGTH_SHORT).show();
            } else if (binding.edtShopAddress.getText().toString().equals("")) {
                Toast.makeText(AddShopActivity.this, "Enter Shop Address", Toast.LENGTH_SHORT).show();
            } else if (binding.edtMobileNumber.getText().toString().equals("")) {
                Toast.makeText(AddShopActivity.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
            } else if (binding.edtMobileNumber.getText().length() != 10) {
                Toast.makeText(AddShopActivity.this, "Enter valid Mobile Number", Toast.LENGTH_SHORT).show();
            } else if (backgroundColor.equals("")) {
                Toast.makeText(AddShopActivity.this, "Select Background Color", Toast.LENGTH_SHORT).show();
            } else if (fontStyle.equals("")) {
                Toast.makeText(AddShopActivity.this, "Select Font Style", Toast.LENGTH_SHORT).show();
            } else if (fontSize.equals("")) {
                Toast.makeText(AddShopActivity.this, "Select Font Size", Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.isNetwork(AddShopActivity.this)) {
                    addShop();
                } else {
                    Utils.InternetAlertDialog(AddShopActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }
        }
    }


}