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
            requestShopFontSize();
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
                if (shopFontModelList.get(i).getFontStyleId().equals("1")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_black);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("2")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_blackitalic);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("3")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_bold);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("4")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_boldcondensed);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("5")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_boldcondenseditalic);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("6")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_bolditalic);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("7")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_condensed);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("8")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_condenseditalic);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("9")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_italic);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("10")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_light);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("11")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_lightitalic);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("12")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_medium);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("13")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_mediumitalic);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("14")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_regular);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("15")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_thin);
                } else if (shopFontModelList.get(i).getFontStyleId().equals("16")) {
                    typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_thinitalic);
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
        model.setFontStyleName("Roboto Black");
        model.setFontStyle(R.font.roboto_black);
        shopFontModelList.add(model);
        ShopFontStyleModel model1 = new ShopFontStyleModel();
        model1.setFontStyleId("2");
        model1.setFontStyleName("Roboto Black Italic");
        model1.setFontStyle(R.font.roboto_blackitalic);
        shopFontModelList.add(model1);
        ShopFontStyleModel model2 = new ShopFontStyleModel();
        model2.setFontStyleId("3");
        model2.setFontStyleName("Roboto Bold");
        model2.setFontStyle(R.font.roboto_bold);
        shopFontModelList.add(model2);
        ShopFontStyleModel model3 = new ShopFontStyleModel();
        model3.setFontStyleId("4");
        model3.setFontStyleName("Roboto Bold Condensed");
        model3.setFontStyle(R.font.roboto_boldcondensed);
        shopFontModelList.add(model3);
        ShopFontStyleModel model4 = new ShopFontStyleModel();
        model4.setFontStyleId("5");
        model4.setFontStyleName("Roboto Bold Condensed Italic");
        model4.setFontStyle(R.font.roboto_boldcondenseditalic);
        shopFontModelList.add(model4);
        ShopFontStyleModel model5 = new ShopFontStyleModel();
        model5.setFontStyleId("6");
        model5.setFontStyleName("Roboto Bold Italic");
        model5.setFontStyle(R.font.roboto_bolditalic);
        shopFontModelList.add(model5);
        ShopFontStyleModel model6 = new ShopFontStyleModel();
        model6.setFontStyleId("7");
        model6.setFontStyleName("Roboto Condensed");
        model6.setFontStyle(R.font.roboto_condensed);
        shopFontModelList.add(model6);
        ShopFontStyleModel model7 = new ShopFontStyleModel();
        model7.setFontStyleId("8");
        model7.setFontStyleName("Roboto Condensed Italic");
        model7.setFontStyle(R.font.roboto_condenseditalic);
        shopFontModelList.add(model7);
        ShopFontStyleModel model8 = new ShopFontStyleModel();
        model8.setFontStyleId("9");
        model8.setFontStyleName("Roboto Italic");
        model8.setFontStyle(R.font.roboto_italic);
        shopFontModelList.add(model8);
        ShopFontStyleModel model9 = new ShopFontStyleModel();
        model9.setFontStyleId("10");
        model9.setFontStyleName("Roboto Light");
        model9.setFontStyle(R.font.roboto_light);
        shopFontModelList.add(model9);
        ShopFontStyleModel model10 = new ShopFontStyleModel();
        model10.setFontStyleId("11");
        model10.setFontStyleName("Roboto Light Italic");
        model10.setFontStyle(R.font.roboto_lightitalic);
        shopFontModelList.add(model10);
        ShopFontStyleModel model11 = new ShopFontStyleModel();
        model11.setFontStyleId("12");
        model11.setFontStyleName("Roboto Medium");
        model11.setFontStyle(R.font.roboto_medium);
        shopFontModelList.add(model11);
        ShopFontStyleModel model12 = new ShopFontStyleModel();
        model12.setFontStyleId("13");
        model12.setFontStyleName("Roboto Medium Italic");
        model12.setFontStyle(R.font.roboto_mediumitalic);
        shopFontModelList.add(model12);
        ShopFontStyleModel model13 = new ShopFontStyleModel();
        model13.setFontStyleId("14");
        model13.setFontStyleName("Roboto Regular");
        model13.setFontStyle(R.font.roboto_regular);
        shopFontModelList.add(model13);
        ShopFontStyleModel model14 = new ShopFontStyleModel();
        model14.setFontStyleId("15");
        model14.setFontStyleName("Roboto Thin");
        model14.setFontStyle(R.font.roboto_thin);
        shopFontModelList.add(model14);
        ShopFontStyleModel model15 = new ShopFontStyleModel();
        model15.setFontStyleId("16");
        model15.setFontStyleName("Roboto Thin Italic");
        model15.setFontStyle(R.font.roboto_thinitalic);
        shopFontModelList.add(model15);

        setShopFontStyle(shopFontModelList, binding.spinFontStyle);

        if (shopDetailsDataModel != null) {
            for (int i = 0; i < shopFontModelList.size(); i++) {
                if (shopFontModelList.get(i).getFontStyleId().equals(shopDetailsDataModel.getFontStyleId()))
                    binding.spinFontStyle.setSelection(i);
            }
        }
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

                        binding.txtAddress.setText(shopDetailsDataModel.getCity() + "," + shopDetailsDataModel.getState());
                        binding.txtMobile.setText(shopDetailsDataModel.getShopMobileNo());

                        binding.lnrBack.setBackgroundColor(Color.parseColor(shopDetailsDataModel.getBgcolorName()));
                        binding.txtName.setTextColor(Color.parseColor(shopDetailsDataModel.getFontColorName()));

                        Typeface typeface = null;
                        if (shopDetailsDataModel.getFontStyleId().equals("1")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_black);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("2")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_blackitalic);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("3")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_bold);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("4")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_boldcondensed);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("5")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_boldcondenseditalic);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("6")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_bolditalic);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("7")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_condensed);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("8")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_condenseditalic);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("9")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_italic);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("10")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_light);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("11")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_lightitalic);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("12")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_medium);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("13")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_mediumitalic);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("14")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_regular);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("15")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_thin);
                        } else if (shopDetailsDataModel.getFontStyleId().equals("16")) {
                            typeface = ResourcesCompat.getFont(ShopSignBoardActivity.this, R.font.roboto_thinitalic);
                        }
                        if (!shopDetailsDataModel.getFontStyleId().equals("") || !shopDetailsDataModel.getFontStyleId().equals("0"))
                            binding.txtName.setTypeface(typeface);

                        fontColor = shopDetailsDataModel.getFontColorName2();
                        backgroundColor = shopDetailsDataModel.getBgcolorName2();
                        fontStyle = shopDetailsDataModel.getFontStyleId();

                        if (shopDetailsDataModel != null) {
                            for (int i = 0; i < shopFontModelListSize.size(); i++) {
                                if (shopFontModelListSize.get(i).getFontColorName2().equals(shopDetailsDataModel.getFontColorName2()))
                                    binding.spinBoardSignFontColor.setSelection(i);
                            }
                        }

                        if (shopDetailsDataModel != null) {
                            for (int i = 0; i < backgroundcolorModelList.size(); i++) {
                                if (backgroundcolorModelList.get(i).getBgColorName2().equals(shopDetailsDataModel.getBgcolorName2()))
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

    private void requestShopFontSize() {
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId("");

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