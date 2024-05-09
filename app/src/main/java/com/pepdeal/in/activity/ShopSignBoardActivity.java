package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.adapter.ProductAdapter;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityAddShopBinding;
import com.pepdeal.in.databinding.ActivityShopSignBoardBinding;
import com.pepdeal.in.model.productdetailsmodel.ProductDetailsDataModel;
import com.pepdeal.in.model.requestModel.AddBackgroundColorResponseModel;
import com.pepdeal.in.model.requestModel.AddShopRequestModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopDetailsDataModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopFontColorModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopFontStyleModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopServiceAvailableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ShopSignBoardActivity extends AppCompatActivity {
    ActivityShopSignBoardBinding binding;

    ArrayList<AddBackgroundColorResponseModel> backgroundcolorModelList = new ArrayList<>();
    ArrayList<ShopFontStyleModel> shopFontModelList = new ArrayList<>();
    ArrayList<ShopFontColorModel> shopFontModelListSize = new ArrayList<>();
    String backgroundColor = "", fontStyle = "", fontSize = "", fontColor = "";
    ProgressDialog dialog;
    ShopDetailsDataModel shopDetailsDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_sign_board);
        binding.setHandler(new ClickHandler());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        if (Utils.isNetwork(ShopSignBoardActivity.this)) {
            requestBackgroundColor();
//            requestShopFont();
            setFontStyle();

        } else {
            Utils.InternetAlertDialog(ShopSignBoardActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
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

                if (!backgroundcolorModelList.get(i).getBgColorName().equals("Select Background Color")) {
                    binding.lnrBack.setBackgroundColor(Color.parseColor(backgroundcolorModelList.get(i).getBgColorName()));

                }

                requestShopFontSize(backgroundcolorModelList.get(i).getBgColorId());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinFontStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fontStyle = shopFontModelList.get(i).getFontStyleId();

                Typeface typeface = null;
                if (fontStyle.equals("1")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.gagalin);
                } else if (fontStyle.equals("3")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.quicksand);
                } else if (fontStyle.equals("4")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.open_sans_extra);
                } else if (fontStyle.equals("5")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.lovelo);
                } else if (fontStyle.equals("8")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.yeseva_one);
                } else if (fontStyle.equals("12")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.kollektif);
                }
                if (!shopFontModelList.get(i).getFontStyleId().equals("") || !shopFontModelList.get(i).getFontStyleId().equals("0"))
                    binding.txtName.setTypeface(typeface);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinBoardSignFontColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fontColor = shopFontModelListSize.get(i).getFontColorId();

                if (!shopFontModelListSize.get(i).getFontColorName().equals("Select Font Color")) {
                    binding.txtName.setTextColor(Color.parseColor(shopFontModelListSize.get(i).getFontColorName()));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getShopDetails(true);
    }

    private void setFontStyle() {
        ShopFontStyleModel model0 = new ShopFontStyleModel();
        model0.setFontStyleId("0");
        model0.setFontStyleName(getString(R.string.board_sign_font_style));
        model0.setFontStyle(R.string.board_sign_font_style);
        shopFontModelList.add(model0);

        ShopFontStyleModel model = new ShopFontStyleModel();
        model.setFontStyleId("1");
        model.setFontStyleName("Gagalin");
        model.setFontStyle(R.font.gagalin);
        shopFontModelList.add(model);

        ShopFontStyleModel model2 = new ShopFontStyleModel();
        model2.setFontStyleId("3");
        model2.setFontStyleName("Quicksand");
        model2.setFontStyle(R.font.quicksand);
        shopFontModelList.add(model2);

        ShopFontStyleModel model3 = new ShopFontStyleModel();
        model3.setFontStyleId("4");
        model3.setFontStyleName("Open sans Extra");
        model3.setFontStyle(R.font.open_sans_extra);
        shopFontModelList.add(model3);

        ShopFontStyleModel model4 = new ShopFontStyleModel();
        model4.setFontStyleId("5");
        model4.setFontStyleName("Lovelo");
        model4.setFontStyle(R.font.lovelo);
        shopFontModelList.add(model4);


        ShopFontStyleModel model7 = new ShopFontStyleModel();
        model7.setFontStyleId("8");
        model7.setFontStyleName("Yeseva one");
        model7.setFontStyle(R.font.yeseva_one);
        shopFontModelList.add(model7);



        ShopFontStyleModel model11 = new ShopFontStyleModel();
        model11.setFontStyleId("12");
        model11.setFontStyleName("Kollektif");
        model11.setFontStyle(R.font.kollektif);
        shopFontModelList.add(model11);


        setShopFontStyle(shopFontModelList, binding.spinFontStyle);
    }

    private void setShopFontStyle(ArrayList<ShopFontStyleModel> shopFontModelList, AppCompatSpinner spinnershopfont) {

        ArrayAdapter<ShopFontStyleModel> modelArrayAdapter = new ArrayAdapter<ShopFontStyleModel>(ShopSignBoardActivity.this,
                R.layout.custom_spinner_shopfont, shopFontModelList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_shopfont, null);
                }
                AppCompatTextView colorname = v.findViewById(R.id.fontname);

                colorname.setText(shopFontModelList.get(position).getFontStyleName());

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
                    v = vi.inflate(R.layout.custom_spinner_shopfont1, null);
                }

                AppCompatTextView colorname = v.findViewById(R.id.fontname);
                colorname.setText(shopFontModelList.get(position).getFontStyleName());

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
        spinnershopfont.setAdapter(modelArrayAdapter);
        modelArrayAdapter.notifyDataSetChanged();


    }

    private void updateShop() {
        dialog.show();
        AddShopRequestModel model = new AddShopRequestModel();
        model.setShopId(SharedPref.getVal(ShopSignBoardActivity.this, SharedPref.shop_id));
        model.setBgColorId(backgroundColor);
        model.setFontStyleId(fontStyle);
        model.setFontColorId(fontColor);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.bgcolorupdate(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(ShopSignBoardActivity.this, "Shop Updated Successfully", Toast.LENGTH_SHORT).show();
//                        finish();
                        getShopDetails(false);
                    } else {
                        Toast.makeText(ShopSignBoardActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ShopSignBoardActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShopSignBoardActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void getShopDetails(boolean isLoading) {
        if (isLoading)
            dialog.show();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(ShopSignBoardActivity.this, SharedPref.user_id));
        model.setShop_id(SharedPref.getVal(ShopSignBoardActivity.this, SharedPref.shop_id));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.shopListWithDetail(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {

                        /*Shop Details json*/
                        Gson gson2 = new Gson();
                        shopDetailsDataModel = gson2.fromJson(jsonObject.getString("ShopDetail"), ShopDetailsDataModel.class);

                        binding.txtName.setText(shopDetailsDataModel.getShopName());

                        binding.txtAddress.setText(shopDetailsDataModel.getShopAddress2());
                        binding.txtAddress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String map = "http://maps.google.co.in/maps?q=" + shopDetailsDataModel.getShopAddress2();
                                // String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                                startActivity(intent);
                            }
                        });
                        binding.txtMobile.setText(shopDetailsDataModel.getShopMobileNo());

                        binding.lnrBack.setBackgroundColor(Color.parseColor(shopDetailsDataModel.getBgcolorName()));
                        binding.txtName.setTextColor(Color.parseColor(shopDetailsDataModel.getFontColorName()));

                        Typeface typeface = null;
                        if (shopDetailsDataModel.getFontStyleId().equals("1")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.gagalin);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("3")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.quicksand);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("4")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.open_sans_extra);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("5")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.lovelo);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("8")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.yeseva_one);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("12")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.kollektif);
                        }
                        if (!shopDetailsDataModel.getFontStyleId().equals("") || !shopDetailsDataModel.getFontStyleId().equals("0"))
                            binding.txtName.setTypeface(typeface);

                        fontColor = shopDetailsDataModel.getFontColorId();
                        backgroundColor = shopDetailsDataModel.getBgColorId();
                        fontStyle = shopDetailsDataModel.getFontStyleId();

                        if (shopDetailsDataModel != null) {
                            for (int i = 0; i < shopFontModelListSize.size(); i++) {
                                if (shopFontModelListSize.get(i).getFontColorId().equals(shopDetailsDataModel.getFontColorId()))
                                    binding.spinBoardSignFontColor.setSelection(i);
                            }
                        }

                        if (shopDetailsDataModel != null) {
                            for (int i = 0; i < backgroundcolorModelList.size(); i++) {
                                if (backgroundcolorModelList.get(i).getBgColorId().equals(shopDetailsDataModel.getBgColorId()))
                                    binding.spinbackcolor.setSelection(i);
                            }
                        }

                        if (shopDetailsDataModel != null) {
                            for (int i = 0; i < shopFontModelList.size(); i++) {
                                if (shopFontModelList.get(i).getFontStyleId().equals(shopDetailsDataModel.getFontStyleId()))
                                    binding.spinFontStyle.setSelection(i);
                            }
                        }

                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismissDialog();

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                dismissDialog();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ShopSignBoardActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShopSignBoardActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*private void requestShopFont() {
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
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ShopSignBoardActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShopSignBoardActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }*/

    private void requestShopFontSize(String bgColorId) {
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setBgColorId(bgColorId);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.fontcolorList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    shopFontModelListSize = new ArrayList<>();
                    ShopFontColorModel model1 = new ShopFontColorModel();

                    model1.setFontColorId("");
                    model1.setFontColorName("Select Font Color");
                    model1.setFontColorName2("Select Font Color");
                    shopFontModelListSize.add(model1);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            ShopFontColorModel model = new ShopFontColorModel();

                            model.setFontColorId(jsonObject1.getString("font_color_id"));
                            model.setFontColorName(jsonObject1.getString("font_color_name"));
                            model.setFontColorName2(jsonObject1.getString("font_color_name2"));

                            shopFontModelListSize.add(model);
                        }
                    }
                    setShopFontColor(shopFontModelListSize, binding.spinBoardSignFontColor);

                    if (shopDetailsDataModel != null) {
                        for (int i = 0; i < shopFontModelListSize.size(); i++) {
                            if (shopFontModelListSize.get(i).getFontColorName().equals(shopDetailsDataModel.getFontColorName()))
                                binding.spinBoardSignFontColor.setSelection(i);
                        }
                    }

                } catch (Exception e) {
                    ShopFontColorModel model1 = new ShopFontColorModel();

                    model1.setFontColorId("");
                    model1.setFontColorName("Select Font Color");
                    model1.setFontColorName2("Select Font Color");
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
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ShopSignBoardActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShopSignBoardActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setShopFontColor(ArrayList<ShopFontColorModel> shopFontModelList, AppCompatSpinner spinnershopfont) {


        ArrayAdapter<ShopFontColorModel> modelArrayAdapter = new ArrayAdapter<ShopFontColorModel>(ShopSignBoardActivity.this,
                R.layout.custom_spinner_backgroundcolor, shopFontModelList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_backgroundcolor, null);
                }
                View color = v.findViewById(R.id.bacKcolor);
                AppCompatTextView colorname = v.findViewById(R.id.colorname);
                colorname.setText(shopFontModelList.get(position).getFontColorName2());
                if (!shopFontModelList.get(position).getFontColorId().equals("")) {
                    color.setBackgroundColor(Color.parseColor(shopFontModelList.get(position).getFontColorName()));
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

                View color = v.findViewById(R.id.bacKcolor);
                AppCompatTextView colorname = v.findViewById(R.id.colorname);
                colorname.setText(shopFontModelList.get(position).getFontColorName2());
                if (!shopFontModelList.get(position).getFontColorId().equals("")) {
                    color.setBackgroundColor(Color.parseColor(shopFontModelList.get(position).getFontColorName()));
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
                    model1.setBgColorName2("Select Background Color");
                    model1.setIsActive("");
                    backgroundcolorModelList.add(model1);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            AddBackgroundColorResponseModel model = new AddBackgroundColorResponseModel();

                            model.setBgColorId(jsonObject1.getString("bg_color_id"));
                            model.setBgColorName(jsonObject1.getString("bg_color_name"));
                            model.setBgColorName2(jsonObject1.getString("bg_color_name2"));
                            model.setIsActive(jsonObject1.getString("isActive"));

                            backgroundcolorModelList.add(model);
                        }
                    }
                    setShopBackgroundcolor(backgroundcolorModelList, binding.spinbackcolor);

                    if (shopDetailsDataModel != null) {
                        for (int i = 0; i < backgroundcolorModelList.size(); i++) {
                            if (backgroundcolorModelList.get(i).getBgColorName().equals(shopDetailsDataModel.getBgcolorName()))
                                binding.spinbackcolor.setSelection(i);
                        }
                    }

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
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(ShopSignBoardActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ShopSignBoardActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShopSignBoardActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setShopBackgroundcolor
            (ArrayList<AddBackgroundColorResponseModel> backgroundcolorModelList, AppCompatSpinner
                    spinbackcolor) {

        ArrayAdapter<AddBackgroundColorResponseModel> modelArrayAdapter = new ArrayAdapter<AddBackgroundColorResponseModel>(ShopSignBoardActivity.this,
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
                colorname.setText(backgroundcolorModelList.get(position).getBgColorName2());
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
                colorname.setText(backgroundcolorModelList.get(position).getBgColorName2());
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
            if (backgroundColor.equals("")) {
                Toast.makeText(ShopSignBoardActivity.this, "Select Background Color", Toast.LENGTH_SHORT).show();
            } else if (fontStyle.equals("")) {
                Toast.makeText(ShopSignBoardActivity.this, "Select Font Style", Toast.LENGTH_SHORT).show();
            } else if (fontColor.equals("")) {
                Toast.makeText(ShopSignBoardActivity.this, "Select Font Color", Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.isNetwork(ShopSignBoardActivity.this)) {
                    updateShop();
                } else {
                    Utils.InternetAlertDialog(ShopSignBoardActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }
        }
    }


}