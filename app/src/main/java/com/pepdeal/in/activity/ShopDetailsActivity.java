package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.pepdeal.in.databinding.ActivityShopDetailsBinding;
import com.pepdeal.in.databinding.ItemCategoryHomeLayoutBinding;
import com.pepdeal.in.databinding.ItemServiceAvaliableLayoutBinding;
import com.pepdeal.in.fragment.FavoriteFragment;
import com.pepdeal.in.fragment.SuperShopFragment;
import com.pepdeal.in.fragment.TicketFragment;
import com.pepdeal.in.model.UsersHomeTabModel;
import com.pepdeal.in.model.productdetailsmodel.ProductDetailsDataModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopDetailsDataModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopServiceAvailableModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ShopDetailsActivity extends AppCompatActivity {

    ActivityShopDetailsBinding binding;
    List<ProductDetailsDataModel> productDataModelList = new ArrayList<>();
    ShopServiceAvailableModel serviceAvailableModel;
    ShopDetailsDataModel shopDetailsDataModel;
    String shop_id = "";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_details);
        binding.setHandler(new ClickHandler());
        shop_id = getIntent().getStringExtra("shop_id");

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }

        public void onAddSuperShopClick(View view) {
            if (Utils.isNetwork(ShopDetailsActivity.this)) {
                addSuperShop(shop_id);
            } else {
                binding.lnrMainLayout.setVisibility(View.GONE);
                Utils.InternetAlertDialog(ShopDetailsActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
            }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isNetwork(ShopDetailsActivity.this)) {
            getShopDetails(true);
        } else {
            binding.lnrMainLayout.setVisibility(View.GONE);
            Utils.InternetAlertDialog(ShopDetailsActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }
    }

    private void getShopDetails(boolean isLoading) {
        if (isLoading)
            showShimmer();

        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(ShopDetailsActivity.this, SharedPref.user_id));
        model.setShop_id(shop_id);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.shopListWithDetail(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {

                        /*Service Available json*/
                        Gson gson1 = new Gson();
                        serviceAvailableModel = gson1.fromJson(jsonObject.getString("shopDeliveryStatus"), ShopServiceAvailableModel.class);

                        if (serviceAvailableModel.getCashOnDelivery().equals("0")) {
                            binding.lnrCod.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.txtCod.setTextColor(getResources().getColor(R.color.gray));
                        } else {
                            binding.lnrCod.setBackgroundColor(getResources().getColor(R.color.purple_500));
                            binding.txtCod.setTextColor(getResources().getColor(R.color.white));
                        }
                        if (serviceAvailableModel.getDoorStep().equals("0")) {
                            binding.lnrDoorStep.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.txtDoorStep.setTextColor(getResources().getColor(R.color.gray));
                        } else {
                            binding.lnrDoorStep.setBackgroundColor(getResources().getColor(R.color.purple_500));
                            binding.txtDoorStep.setTextColor(getResources().getColor(R.color.white));
                        }
                        if (serviceAvailableModel.getHomeDelivery().equals("0")) {
                            binding.lnrHomeDelivery.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.txtHomeDelivery.setTextColor(getResources().getColor(R.color.gray));
                        } else {
                            binding.lnrHomeDelivery.setBackgroundColor(getResources().getColor(R.color.purple_500));
                            binding.txtHomeDelivery.setTextColor(getResources().getColor(R.color.white));
                        }
                        if (serviceAvailableModel.getLiveDemo().equals("0")) {
                            binding.lnrLiveDemo.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.txtLiveDemo.setTextColor(getResources().getColor(R.color.gray));
                        } else {
                            binding.lnrLiveDemo.setBackgroundColor(getResources().getColor(R.color.purple_500));
                            binding.txtLiveDemo.setTextColor(getResources().getColor(R.color.white));
                        }
                        if (serviceAvailableModel.getOffers().equals("0")) {
                            binding.lnrOffers.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.txtOffers.setTextColor(getResources().getColor(R.color.gray));
                        } else {
                            binding.lnrOffers.setBackgroundColor(getResources().getColor(R.color.purple_500));
                            binding.txtOffers.setTextColor(getResources().getColor(R.color.white));
                        }
                        if (serviceAvailableModel.getBargain().equals("0")) {
                            binding.lnrBargain.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.txtBargain.setTextColor(getResources().getColor(R.color.gray));
                        } else {
                            binding.lnrBargain.setBackgroundColor(getResources().getColor(R.color.purple_500));
                            binding.txtBargain.setTextColor(getResources().getColor(R.color.white));
                        }


                        /*Shop Details json*/
                        Gson gson2 = new Gson();
                        shopDetailsDataModel = gson2.fromJson(jsonObject.getString("ShopDetail"), ShopDetailsDataModel.class);

                        binding.txtName.setText(shopDetailsDataModel.getShopName());
                        if (shopDetailsDataModel.getFontsizeName().contains("px")) {
                            binding.txtName.setTextSize(Float.parseFloat(shopDetailsDataModel.getFontsizeName().replace("px", "")));
                        }

                        binding.txtAddress.setText(shopDetailsDataModel.getShopAddress());
                        binding.txtMobile.setText(shopDetailsDataModel.getShopMobileNo());

                        binding.lnrBack.setBackgroundColor(Color.parseColor(shopDetailsDataModel.getBgcolorName()));

                        if (shopDetailsDataModel.getSuperShopTatus().equals("1")) {
                            Glide.with(ShopDetailsActivity.this).load(R.drawable.ic_fav_selected).into(binding.imgSuperShop);
                        } else {
                            Glide.with(ShopDetailsActivity.this).load(R.drawable.ic_add_super_shop).into(binding.imgSuperShop);
                        }

                        binding.imgMessage.setOnClickListener(view -> startActivity(new Intent(ShopDetailsActivity.this, MessageChatActivity.class)
                                .putExtra("shop_id", shopDetailsDataModel.getShopId())
                                .putExtra("name", shopDetailsDataModel.getShopName())
                                .putExtra("user_id", SharedPref.getVal(ShopDetailsActivity.this, SharedPref.user_id))));

                        /*Product list*/
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<ProductDetailsDataModel>>() {
                        }.getType();
                        productDataModelList = new ArrayList<>();
                        productDataModelList.addAll(gson.fromJson(jsonObject.getString("productDetail"), listType));

                        if (productDataModelList.size() < 0) {
                            binding.recList.setVisibility(View.GONE);
                        } else {
                            binding.recList.setVisibility(View.VISIBLE);

                            binding.recList.setLayoutManager(new GridLayoutManager(ShopDetailsActivity.this, 3));
                            binding.recList.setAdapter(new ProductAdapter(ShopDetailsActivity.this, productDataModelList, "shop", ""));
                        }

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
                            Toast.makeText(ShopDetailsActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(ShopDetailsActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(ShopDetailsActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(ShopDetailsActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ShopDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShopDetailsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void addSuperShop(String shopId) {
        dialog.show();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(ShopDetailsActivity.this, SharedPref.user_id));
        model.setShop_id(shopId);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.addSupershop(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(ShopDetailsActivity.this, "Shop added in super shop", Toast.LENGTH_SHORT).show();
//                                binding.txtFav.setText("Remove Favourite");
                    } else {
                        Toast.makeText(ShopDetailsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ShopDetailsActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(ShopDetailsActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(ShopDetailsActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(ShopDetailsActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ShopDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShopDetailsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}