package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityCitywiseShopListBinding;
import com.pepdeal.in.databinding.ItemHomeProductListLayoutBinding;
import com.pepdeal.in.databinding.ItemHomeShopsListBinding;
import com.pepdeal.in.fragment.HomeFragment;
import com.pepdeal.in.model.homemodel.HomeProductDataModel;
import com.pepdeal.in.model.homemodel.HomeShopDataModel;
import com.pepdeal.in.model.productdetailsmodel.ProductDetailsDataModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.searchmodel.SearchProductModel;

import org.json.JSONArray;
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

public class CitywiseShopListActivity extends AppCompatActivity {

    ActivityCitywiseShopListBinding binding;
    String cityId = "";
    List<HomeShopDataModel> homeShopDataModelList = new ArrayList<>();
    ProgressDialog dialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_citywise_shop_list);
        binding.setHandler(new ClickHandler());
        cityId = getIntent().getStringExtra("city_id");

        dialog = new ProgressDialog(CitywiseShopListActivity.this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        if (Utils.isNetwork(CitywiseShopListActivity.this)) {
            getHomeData();
        } else {
            binding.lnrMainLayout.setVisibility(View.GONE);
            Utils.InternetAlertDialog(CitywiseShopListActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
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

    private void getHomeData() {
        showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(CitywiseShopListActivity.this, SharedPref.user_id));
        model.setCity_id(cityId);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.citySearch(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<HomeShopDataModel>>() {
                        }.getType();
                        homeShopDataModelList = new ArrayList<>();
                        homeShopDataModelList.addAll(gson.fromJson(jsonObject.getString("shop List"), listType));

                        if (homeShopDataModelList.size() > 0) {
                            binding.recList.setLayoutManager(new LinearLayoutManager(CitywiseShopListActivity.this));
                            binding.recList.setAdapter(new ShopAdapter());

                            binding.lnrMainLayout.setVisibility(View.VISIBLE);
                            binding.lnrNoData.setVisibility(View.GONE);
                        } else {
                            binding.lnrMainLayout.setVisibility(View.GONE);
                            binding.lnrNoData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.lnrMainLayout.setVisibility(View.GONE);
                        binding.lnrNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    binding.lnrMainLayout.setVisibility(View.GONE);
                    binding.lnrNoData.setVisibility(View.VISIBLE);
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
                            Toast.makeText(CitywiseShopListActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(CitywiseShopListActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(CitywiseShopListActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(CitywiseShopListActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(CitywiseShopListActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CitywiseShopListActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemHomeShopsListBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(CitywiseShopListActivity.this), R.layout.item_home_shops_list, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HomeShopDataModel model = homeShopDataModelList.get(position);
            holder.bind(model, position);
        }

        @Override
        public int getItemCount() {
            return homeShopDataModelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemHomeShopsListBinding layoutBinding;

            public ViewHolder(@NonNull ItemHomeShopsListBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind(HomeShopDataModel model, int position) {
                layoutBinding.txtName.setText(model.getShopName());
                if (!model.getFontcolorName().equals(""))
                    layoutBinding.txtName.setTextColor(Color.parseColor(model.getFontcolorName()));

                Typeface typeface = null;
                if (model.getFontStyleId().equals("1")) {
                    typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.anton_regular);
                } else if (model.getFontStyleId().equals("2")) {
                    typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.berkshireswash_regular);
                } else if (model.getFontStyleId().equals("3")) {
                    typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.brasika_display);
                } else if (model.getFontStyleId().equals("4")) {
                    typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.carterone_regular);
                } else if (model.getFontStyleId().equals("5")) {
                    typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.fredokaone_regular);
                } else if (model.getFontStyleId().equals("6")) {
                    typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.gagalin_regular);
                } else if (model.getFontStyleId().equals("7")) {
                    typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.lato_regular);
                } else if (model.getFontStyleId().equals("8")) {
                    typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.leaguespartan_bold);
                } else if (model.getFontStyleId().equals("9")) {
                    typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.lovelo_black);
                } else if (model.getFontStyleId().equals("10")) {
                    typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.opensans_bold);
                } else if (model.getFontStyleId().equals("11")) {
                    typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.quicksand_bold);
                } else if (model.getFontStyleId().equals("12")) {
                    typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.yesevaone_regular);
                } /*else if (model.getFontStyleId().equals("13")) {
                            typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.roboto_mediumitalic);
                        } else if (model.getFontStyleId().equals("14")) {
                            typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.roboto_regular);
                        } else if (model.getFontStyleId().equals("15")) {
                            typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.roboto_thin);
                        } else if (model.getFontStyleId().equals("16")) {
                            typeface = ResourcesCompat.getFont(CitywiseShopListActivity.this, R.font.roboto_thinitalic);
                        }*/
                if (!model.getFontStyleId().equals("") || !model.getFontStyleId().equals("0"))
                    layoutBinding.txtName.setTypeface(typeface);

                String address = model.getCity() + ", " + model.getState();
                layoutBinding.txtAddress.setText(address);
                layoutBinding.txtMobile.setText(model.getShopMobileNo());

                layoutBinding.lnrBack.setBackgroundColor(Color.parseColor(model.getBgcolorName()));

                layoutBinding.lnrBack.setOnClickListener(view -> {
                    startActivity(new Intent(CitywiseShopListActivity.this, ShopDetailsActivity.class).putExtra("shop_id", model.getShopId()));
                });
                if (model.getProductsList().isEmpty()) {
                    layoutBinding.recProduct.setVisibility(View.GONE);
                } else {
                    layoutBinding.recProduct.setVisibility(View.VISIBLE);
                    layoutBinding.recProduct.setLayoutManager(new LinearLayoutManager(CitywiseShopListActivity.this, RecyclerView.HORIZONTAL, false));
                    layoutBinding.recProduct.setAdapter(new ProductAdapter(model.getProductsList()));
                }
            }

        }
    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
        List<HomeProductDataModel> homeProductDataModelList;

        public ProductAdapter(List<HomeProductDataModel> homeProductDataModelList) {
            this.homeProductDataModelList = homeProductDataModelList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemHomeProductListLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(CitywiseShopListActivity.this), R.layout.item_home_product_list_layout, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HomeProductDataModel model = homeProductDataModelList.get(position);
            holder.bindHomeData(model, position);
        }

        @Override
        public int getItemCount() {
            return homeProductDataModelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemHomeProductListLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemHomeProductListLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bindHomeData(HomeProductDataModel model, int position) {
                Glide.with(CitywiseShopListActivity.this).load(model.getProductImages())
                        .error(R.drawable.loader).placeholder(R.drawable.loader).into(layoutBinding.imgProductImage);
                layoutBinding.txtProductName.setText(model.getProductName());

                if (model.getDiscountMrp().equals("0") || model.getDiscountMrp().equals("") || model.getDiscountMrp() == null) {
                    layoutBinding.cardOffer.setVisibility(View.GONE);
                    layoutBinding.txtActualPrice.setVisibility(View.GONE);
                    layoutBinding.txtDiscountPrice.setText("₹ " + model.getMrp());
                } else {
                    layoutBinding.cardOffer.setVisibility(View.VISIBLE);
                    layoutBinding.txtActualPrice.setVisibility(View.VISIBLE);

                    layoutBinding.txtActualPrice.setText("₹ " + model.getMrp());
                    layoutBinding.txtActualPrice.setPaintFlags(layoutBinding.txtActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    layoutBinding.txtDiscountPrice.setText("₹ " + model.getSellingPrice());

                    layoutBinding.txtOff.setText(model.getDiscountMrp() + "% OFF");
                }

                layoutBinding.lnrDetails.setOnClickListener(view -> {
                    Intent intent = new Intent(new Intent(CitywiseShopListActivity.this, ProductDetailsActivity.class));
                    intent.putExtra("product_id", model.getProductId());
                    CitywiseShopListActivity.this.startActivity(intent);
                });
                layoutBinding.relImage.setOnClickListener(view -> {
                    Intent intent = new Intent(new Intent(CitywiseShopListActivity.this, ProductDetailsActivity.class));
                    intent.putExtra("product_id", model.getProductId());
                    CitywiseShopListActivity.this.startActivity(intent);
                });

//                if (model.getFavouriteStatus().equals("0")) {
//                    layoutBinding.imgAddFav.setColorFilter(ContextCompat.getColor(CitywiseShopListActivity.this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
//                } else {
//                    layoutBinding.imgAddFav.setColorFilter(ContextCompat.getColor(CitywiseShopListActivity.this, R.color.errorColor), android.graphics.PorterDuff.Mode.SRC_IN);
//                }

                if (model.getFavouriteStatus().equals("1")) {
                    layoutBinding.imgAddFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_selected));
                } else {
                    layoutBinding.imgAddFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                }


                layoutBinding.imgAddFav.setOnClickListener(view -> {
                    if (model.getFavouriteStatus().equals("0")) {
                        addFav(model.getProductId());
                    } else {
                        removeFav(model.getFavouriteId());
                    }
                });

            }

            private void addFav(String productId) {
                UserProfileRequestModel model = new UserProfileRequestModel();
                model.setUserId(SharedPref.getVal(CitywiseShopListActivity.this, SharedPref.user_id));
                model.setProduct_id(productId);

                ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
                client.addFavourite(model).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                          /*  final VibrationEffect vibrationEffect2;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                final Vibrator vibrator = (Vibrator) CitywiseShopListActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                                // create vibrator effect with the constant EFFECT_TICK
                                vibrationEffect2 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK);

                                // it is safe to cancel other vibrations currently taking place
                                vibrator.cancel();

                                vibrator.vibrate(vibrationEffect2);
                            }*/
                                Toast.makeText(CitywiseShopListActivity.this, "Product Added to favourite", Toast.LENGTH_SHORT).show();
                                layoutBinding.imgAddFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_selected));
                            } else {
                                Toast.makeText(CitywiseShopListActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                    dismissDialog();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                        // binding.recProductlist.hideShimmer();
//                    dismissDialog();
                        error.printStackTrace();
                        if (error instanceof HttpException) {
                            switch (((HttpException) error).code()) {
                                case HttpsURLConnection.HTTP_UNAUTHORIZED:
                                    Toast.makeText(CitywiseShopListActivity.this, CitywiseShopListActivity.this.getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_FORBIDDEN:
                                    Toast.makeText(CitywiseShopListActivity.this, CitywiseShopListActivity.this.getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                    Toast.makeText(CitywiseShopListActivity.this, CitywiseShopListActivity.this.getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_BAD_REQUEST:
                                    Toast.makeText(CitywiseShopListActivity.this, CitywiseShopListActivity.this.getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(CitywiseShopListActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CitywiseShopListActivity.this, CitywiseShopListActivity.this.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            private void removeFav(String favId) {
                UserProfileRequestModel model = new UserProfileRequestModel();
                model.setUserId(SharedPref.getVal(CitywiseShopListActivity.this, SharedPref.user_id));
                model.setFav_id(favId);

                ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
                client.favouriteRemove(model).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                          /*  final VibrationEffect vibrationEffect2;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                final Vibrator vibrator = (Vibrator) CitywiseShopListActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                                // create vibrator effect with the constant EFFECT_TICK
                                vibrationEffect2 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK);

                                // it is safe to cancel other vibrations currently taking place
                                vibrator.cancel();

                                vibrator.vibrate(vibrationEffect2);
                            }*/
                                Toast.makeText(CitywiseShopListActivity.this, "Product Removed from favourite", Toast.LENGTH_SHORT).show();
                                layoutBinding.imgAddFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                            } else {
                                Toast.makeText(CitywiseShopListActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                    dismissDialog();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                        // binding.recProductlist.hideShimmer();
//                    dismissDialog();
                        error.printStackTrace();
                        if (error instanceof HttpException) {
                            switch (((HttpException) error).code()) {
                                case HttpsURLConnection.HTTP_UNAUTHORIZED:
                                    Toast.makeText(CitywiseShopListActivity.this, CitywiseShopListActivity.this.getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_FORBIDDEN:
                                    Toast.makeText(CitywiseShopListActivity.this, CitywiseShopListActivity.this.getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                    Toast.makeText(CitywiseShopListActivity.this, CitywiseShopListActivity.this.getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_BAD_REQUEST:
                                    Toast.makeText(CitywiseShopListActivity.this, CitywiseShopListActivity.this.getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(CitywiseShopListActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CitywiseShopListActivity.this, CitywiseShopListActivity.this.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}