package com.pepdeal.in.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityProductDetailsBinding;
import com.pepdeal.in.model.productdetailsmodel.ProductDetailsDataModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    ActivityProductDetailsBinding binding;
    List<ProductDetailsDataModel> productDataModelList = new ArrayList<>();
    String productId = "";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        binding.setHandler(new ClickHandler());
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
        productId = getIntent().getStringExtra("product_id");

        if (Utils.isNetwork(ProductDetailsActivity.this)) {
            getProductDetails(true);
        } else {
            binding.lnrMainLayout.setVisibility(View.GONE);
            Utils.InternetAlertDialog(ProductDetailsActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }

        public void onClickImage1(View view){
            startActivity(new Intent(ProductDetailsActivity.this, FullImageActivity.class)
                    .putExtra("from", "multiple").putExtra("arraylist", (Serializable) productDataModelList.get(0).getProductImages()).putExtra("position", String.valueOf(0)));
        }

        public void onClickImage2(View view){
            startActivity(new Intent(ProductDetailsActivity.this, FullImageActivity.class)
                    .putExtra("from", "multiple").putExtra("arraylist", (Serializable) productDataModelList.get(0).getProductImages()).putExtra("position", String.valueOf(1)));
        }

        public void onClickImage3(View view){
            startActivity(new Intent(ProductDetailsActivity.this, FullImageActivity.class)
                    .putExtra("from", "multiple").putExtra("arraylist", (Serializable) productDataModelList.get(0).getProductImages()).putExtra("position", String.valueOf(2)));
        }
    }

    private void showShimmer() {
        binding.lnrMainLayout.setVisibility(View.GONE);
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void hideShimmer() {
        binding.lnrMainLayout.setVisibility(View.VISIBLE);
        binding.shimmerLayout.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void getProductDetails(boolean isLoading) {
        if (isLoading)
            showShimmer();

        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(ProductDetailsActivity.this, SharedPref.user_id));
        model.setProduct_id(productId);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.productDetail(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<ProductDetailsDataModel>>() {
                        }.getType();
                        productDataModelList = new ArrayList<>();
                        productDataModelList.addAll(gson.fromJson(jsonObject.getString("product Detail"), listType));

                        setData();
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                hideShimmer();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                hideShimmer();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(ProductDetailsActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(ProductDetailsActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(ProductDetailsActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(ProductDetailsActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ProductDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setData() {
        ProductDetailsDataModel model = productDataModelList.get(0);

        Glide.with(ProductDetailsActivity.this).load(model.getProductImages().get(0).getProductImage())
                .error(R.drawable.loader).placeholder(R.drawable.loader).into(binding.imgImage1);
        binding.txtDesc.setText(model.getDescription());

        Glide.with(ProductDetailsActivity.this).load(model.getProductImages().get(1).getProductImage())
                .error(R.drawable.loader).placeholder(R.drawable.loader).into(binding.imgImage2);
        binding.txtDesc1.setText(model.getDescription2());

        Glide.with(ProductDetailsActivity.this).load(model.getProductImages().get(2).getProductImage())
                .error(R.drawable.loader).placeholder(R.drawable.loader).into(binding.imgImage3);

        binding.txtProductName.setText(model.getProductName());
        binding.txtCategory.setText("Category : " + model.getCategoryName());
        binding.txtBrand.setText("Brand : " + model.getBrandName());

        if (model.getColor().equals("") || model.getColor() == null) {
            binding.txtColor.setVisibility(View.GONE);
        } else {
            binding.txtColor.setText("Color : " + model.getColor());
        }
        if (model.getSizeName().equals("") || model.getSizeName() == null) {
            binding.txtSize.setVisibility(View.GONE);
        } else {
            binding.txtSize.setText("Size : " + model.getSizeName());
        }

        if (model.getDiscountMrp().equals("0") || model.getDiscountMrp().equals("") || model.getDiscountMrp() == null) {
            binding.txtOff.setVisibility(View.GONE);
            binding.txtActualPrice.setVisibility(View.GONE);
            binding.txtDiscountPrice.setText("₹ " + model.getMrp());
        } else {
            binding.txtOff.setVisibility(View.VISIBLE);
            binding.txtActualPrice.setVisibility(View.VISIBLE);

            binding.txtActualPrice.setText("₹ " + model.getMrp());
            binding.txtActualPrice.setPaintFlags(binding.txtActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.txtDiscountPrice.setText("₹ " + model.getSellingPrice());

            binding.txtOff.setText(model.getDiscountMrp() + "% OFF");
        }

        binding.txtSpecification.setText(model.getSpecification());

        /*Ticket Status 0 = Delivered , 1 = Approved , 2 = Waiting ,3 =Rejected*/
        if (model.getTicketStatus().equals("") || model.getTicketStatus().equals("0") ||
                model.getTicketStatus().equals("1") || model.getTicketStatus().equals("3")) {
            binding.cardTicket.setCardBackgroundColor(getResources().getColor(R.color.blue));
        } else {
            binding.cardTicket.setCardBackgroundColor(getResources().getColor(R.color.bluelight));
        }

        /*Fav Status 0 = Not in fav , 1 = In Fav*/
        if (model.getFavouriteStatus().equals("0")) {
//            binding.cardTicket.setCardBackgroundColor(getResources().getColor(R.color.blue));
            binding.txtFav.setText("Add To Favourite");
        } else {
//            binding.cardTicket.setCardBackgroundColor(getResources().getColor(R.color.bluelight));
            binding.txtFav.setText("Remove Favourite");
        }

        binding.cardFav.setOnClickListener(view -> {
            if (Utils.isNetwork(ProductDetailsActivity.this)) {
                if (binding.txtFav.getText().toString().equals("Add To Favourite"))
                    removeFav(model.getProductId());
                else
                    addFav(model.getProductId());
            } else {
//                    binding.lnrMainLayout.setVisibility(View.GONE);
                Utils.InternetAlertDialog(ProductDetailsActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
            }

        });

        binding.cardTicket.setOnClickListener(view -> {
            if (model.getTicketStatus().equals("") || model.getTicketStatus().equals("0") ||
                    model.getTicketStatus().equals("1") || model.getTicketStatus().equals("3"))
                if (Utils.isNetwork(ProductDetailsActivity.this)) {
                    raiseTicket(model.getProductId(), model.getShopId());
                } else {
//                    binding.lnrMainLayout.setVisibility(View.GONE);
                    Utils.InternetAlertDialog(ProductDetailsActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            else
                Toast.makeText(ProductDetailsActivity.this, "Ticket already raised for this product", Toast.LENGTH_SHORT).show();
        });
    }

    private void raiseTicket(String productId,String shopId) {
        dialog.show();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(ProductDetailsActivity.this, SharedPref.user_id));
        model.setProduct_id(productId);
        model.setShop_id(shopId);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.addTicket(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(ProductDetailsActivity.this, "Ticket Raised Successfully", Toast.LENGTH_SHORT).show();
                        getProductDetails(false);
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ProductDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addFav(String productId) {
        dialog.show();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(ProductDetailsActivity.this, SharedPref.user_id));
        model.setProduct_id(productId);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.addFavourite(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(ProductDetailsActivity.this, "Product Added to favourite", Toast.LENGTH_SHORT).show();
                        binding.txtFav.setText("Remove Favourite");
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ProductDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removeFav(String favId) {
        dialog.show();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(ProductDetailsActivity.this, SharedPref.user_id));
        model.setFav_id(favId);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.favouriteRemove(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {

                        Toast.makeText(ProductDetailsActivity.this, "Product Removed from favourite", Toast.LENGTH_SHORT).show();
                        binding.txtFav.setText("Add To Favourite");
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ProductDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductDetailsActivity.this, ProductDetailsActivity.this.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}