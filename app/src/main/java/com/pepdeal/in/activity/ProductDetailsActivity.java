package com.pepdeal.in.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
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
import com.pepdeal.in.model.productdetailsmodel.ShopDetailsModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopDetailsDataModel;

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
    String productId = "", shopId = "";
    ProgressDialog dialog;
    ShopDetailsModel shopDetailsDataModel;
    ProductDetailsDataModel productDetailsDataModel;

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
        binding.lnrBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductDetailsActivity.this, ShopDetailsActivity.class).putExtra("shop_id", shopId));
            }
        });
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }

        public void onClickImage1(View view) {
            startActivity(new Intent(ProductDetailsActivity.this, FullImageActivity.class)
                    .putExtra("from", "multiple").putExtra("arraylist", (Serializable) productDataModelList.get(0).getProductImages()).putExtra("position", String.valueOf(0)));
        }

        public void onClickImage2(View view) {
            startActivity(new Intent(ProductDetailsActivity.this, FullImageActivity.class)
                    .putExtra("from", "multiple").putExtra("arraylist", (Serializable) productDataModelList.get(0).getProductImages()).putExtra("position", String.valueOf(1)));
        }

        public void onClickImage3(View view) {
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
                        productDetailsDataModel = productDataModelList.get(0);

                        /*Shop Details json*/
                        Gson gson2 = new Gson();
                        shopDetailsDataModel = gson2.fromJson(jsonObject.getString("shop_detail"), ShopDetailsModel.class);

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
        // ProductDetailsDataModel model = productDataModelList.get(0);
        shopId = productDetailsDataModel.getShopId();

        Glide.with(ProductDetailsActivity.this).load(productDetailsDataModel.getProductImages().get(0).getProductImage())
                .error(R.drawable.loader).placeholder(R.drawable.loader).into(binding.imgImage1);
        binding.txtDesc.setText(productDetailsDataModel.getDescription());

        if (productDetailsDataModel.getProductImages().get(1).getProductImage().equals("") || productDetailsDataModel.getProductImages().get(1).getProductImage() == null) {
            binding.imgImage2.setVisibility(View.GONE);
        } else {
            binding.imgImage2.setVisibility(View.VISIBLE);
            Glide.with(ProductDetailsActivity.this).load(productDetailsDataModel.getProductImages().get(1).getProductImage())
                    .error(R.drawable.loader).placeholder(R.drawable.loader).into(binding.imgImage2);
        }

        if (productDetailsDataModel.getDescription2().equals("") || productDetailsDataModel.getDescription2() == null) {
            binding.lnrDesc1.setVisibility(View.GONE);
        } else {
            binding.lnrDesc1.setVisibility(View.VISIBLE);
            binding.txtDesc1.setText(productDetailsDataModel.getDescription2());
        }

        if (productDetailsDataModel.getProductImages().get(2).getProductImage().equals("") || productDetailsDataModel.getProductImages().get(2).getProductImage() == null) {
            binding.imgImage3.setVisibility(View.GONE);
        } else {
            binding.imgImage3.setVisibility(View.VISIBLE);
            Glide.with(ProductDetailsActivity.this).load(productDetailsDataModel.getProductImages().get(2).getProductImage())
                    .error(R.drawable.loader).placeholder(R.drawable.loader).into(binding.imgImage3);
        }

        binding.txtProductName.setText(productDetailsDataModel.getProductName());
        binding.txtCategory.setText("Category : " + productDetailsDataModel.getCategoryName());

        if (productDetailsDataModel.getBrandName().equals("") || productDetailsDataModel.getBrandName() == null) {
            binding.txtBrand.setVisibility(View.GONE);
        } else {
            binding.txtBrand.setVisibility(View.VISIBLE);
            binding.txtBrand.setText("Brand : " + productDetailsDataModel.getBrandName());
        }

        if (productDetailsDataModel.getColor().equals("") || productDetailsDataModel.getColor() == null) {
            binding.txtColor.setVisibility(View.GONE);
        } else {
            binding.txtColor.setVisibility(View.VISIBLE);
            binding.txtColor.setText("Color : " + productDetailsDataModel.getColor());
        }
        if (productDetailsDataModel.getSizeName().equals("") || productDetailsDataModel.getSizeName() == null) {
            binding.txtSize.setVisibility(View.GONE);
        } else {
            binding.txtSize.setVisibility(View.VISIBLE);
            binding.txtSize.setText("Size : " + productDetailsDataModel.getSizeName());
        }
        if (productDetailsDataModel.getOnCall().equalsIgnoreCase("1")) {
            binding.txtOff.setVisibility(View.GONE);
            binding.txtActualPrice.setText("On call");

            //   binding.txtActualPrice.setVisibility(View.GONE);
            binding.txtDiscountPrice.setVisibility(View.GONE);
        } else {
            if (productDetailsDataModel.getDiscountMrp().equals("0") || productDetailsDataModel.getDiscountMrp().equals("") || productDetailsDataModel.getDiscountMrp() == null) {
                binding.txtOff.setVisibility(View.GONE);
                binding.txtActualPrice.setVisibility(View.GONE);
                binding.txtDiscountPrice.setText("₹ " + productDetailsDataModel.getMrp());
            } else {
                binding.txtOff.setVisibility(View.VISIBLE);
                binding.txtActualPrice.setVisibility(View.VISIBLE);

                binding.txtActualPrice.setText("₹ " + productDetailsDataModel.getMrp());
                binding.txtActualPrice.setPaintFlags(binding.txtActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.txtDiscountPrice.setText("₹ " + productDetailsDataModel.getSellingPrice());

                binding.txtOff.setText(productDetailsDataModel.getDiscountMrp() + "% OFF");
            }
        }

        if (productDetailsDataModel.getSpecification().equals("") || productDetailsDataModel.getSpecification() == null) {
            binding.lnrSpecification.setVisibility(View.GONE);
        } else {
            binding.lnrSpecification.setVisibility(View.VISIBLE);
            binding.txtSpecification.setText(productDetailsDataModel.getSpecification());
        }

        /*Ticket Status 0 = Delivered , 1 = Approved , 2 = Waiting ,3 =Rejected*/
        if (productDetailsDataModel.getTicketStatus().equals("") || productDetailsDataModel.getTicketStatus().equals("0") ||
                productDetailsDataModel.getTicketStatus().equals("1") || productDetailsDataModel.getTicketStatus().equals("3")) {
            //   binding.cardTicket.setCardBackgroundColor(getResources().getColor(R.color.blue));
            binding.cardTicket.setCardBackgroundColor(getResources().getColor(R.color.black));
            binding.txtTicket.setTextColor(getResources().getColor(R.color.yellow));

        } else {
            // binding.cardTicket.setCardBackgroundColor(getResources().getColor(R.color.bluelight));
            binding.cardTicket.setCardBackgroundColor(getResources().getColor(R.color.white));
            binding.txtTicket.setTextColor(getResources().getColor(R.color.black));
        }

        /*Fav Status 0 = Not in fav , 1 = In Fav*/
        if (productDetailsDataModel.getFavouriteStatus().equals("0")) {
//            binding.cardTicket.setCardBackgroundColor(getResources().getColor(R.color.blue));
            binding.txtFav.setText("Add To Favourite");
        } else {
//            binding.cardTicket.setCardBackgroundColor(getResources().getColor(R.color.bluelight));
            binding.txtFav.setText("Remove Favourite");
        }

        binding.cardFav.setOnClickListener(view -> {
            if (Utils.isNetwork(ProductDetailsActivity.this)) {
                if (binding.txtFav.getText().toString().equals("Add To Favourite"))
                    addFav(productDetailsDataModel.getProductId());
                else
                    removeFav(productDetailsDataModel.getFavouriteId());
            } else {
//                    binding.lnrMainLayout.setVisibility(View.GONE);
                Utils.InternetAlertDialog(ProductDetailsActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
            }

        });

        binding.cardTicket.setOnClickListener(view -> {
            if (productDetailsDataModel.getTicketStatus().equals("") || productDetailsDataModel.getTicketStatus().equals("0") ||
                    productDetailsDataModel.getTicketStatus().equals("1") || productDetailsDataModel.getTicketStatus().equals("3")) {
                if (Utils.isNetwork(ProductDetailsActivity.this)) {
                    productDetailsDataModel.setTicketStatus("2");
                    raiseTicket(productDetailsDataModel.getProductId(), productDetailsDataModel.getShopId());
                } else {
//                    binding.lnrMainLayout.setVisibility(View.GONE);
                    Utils.InternetAlertDialog(ProductDetailsActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            } else {
                Toast.makeText(ProductDetailsActivity.this, "Ticket already raised for this product", Toast.LENGTH_SHORT).show();

            }
        });

        binding.txtName.setText(shopDetailsDataModel.getShopName());
//        binding.txtAddress.setText(shopDetailsDataModel.getCity() + "," + shopDetailsDataModel.getState());
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
        String bgColorName = shopDetailsDataModel.getBgColorName();
        binding.lnrBack.setBackgroundColor(Color.parseColor(bgColorName));
        binding.txtName.setTextColor(Color.parseColor(shopDetailsDataModel.getFontColorName()));
        Typeface typeface = null;
        if (shopDetailsDataModel.getFontStyleId().equals("1")) {
            typeface = ResourcesCompat.getFont(ProductDetailsActivity.this, R.font.gagalin);
        } else if (shopDetailsDataModel.getFontStyleId().equals("3")) {
            typeface = ResourcesCompat.getFont(ProductDetailsActivity.this, R.font.quicksand);
        } else if (shopDetailsDataModel.getFontStyleId().equals("4")) {
            typeface = ResourcesCompat.getFont(ProductDetailsActivity.this, R.font.open_sans_extra);
        } else if (shopDetailsDataModel.getFontStyleId().equals("5")) {
            typeface = ResourcesCompat.getFont(ProductDetailsActivity.this, R.font.lovelo);
        } else if (shopDetailsDataModel.getFontStyleId().equals("8")) {
            typeface = ResourcesCompat.getFont(ProductDetailsActivity.this, R.font.yeseva_one);
        } else if (shopDetailsDataModel.getFontStyleId().equals("12")) {
            typeface = ResourcesCompat.getFont(ProductDetailsActivity.this, R.font.kollektif);
        }
        if (!shopDetailsDataModel.getFontStyleId().equals("") || !shopDetailsDataModel.getFontStyleId().equals("0"))
            binding.txtName.setTypeface(typeface);
    }

    private void raiseTicket(String productId, String shopId) {
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
                        productDetailsDataModel.setTicketStatus("2");
                        Toast.makeText(ProductDetailsActivity.this, "Ticket Raised Successfully", Toast.LENGTH_SHORT).show();
                        binding.cardTicket.setCardBackgroundColor(getResources().getColor(R.color.white));
                        binding.txtTicket.setTextColor(getResources().getColor(R.color.black));
                        // getProductDetails(false);
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
}