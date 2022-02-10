package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.adapter.ProductAdapter;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivitySearchBinding;
import com.pepdeal.in.databinding.ItemHomeShopsListBinding;
import com.pepdeal.in.fragment.HomeFragment;
import com.pepdeal.in.fragment.SuperShopFragment;
import com.pepdeal.in.model.homemodel.HomeShopDataModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.searchmodel.SearchProductModel;
import com.pepdeal.in.model.searchmodel.SearchShopModel;
import com.pepdeal.in.model.supershopmodel.SuperShopDataModel;

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

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    String key = "";
    ProgressDialog dialog;
    List<SearchShopModel> searchShopModelArrayList = new ArrayList<>();
    List<SearchProductModel> searchProductModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.setHandler(new ClickHandler());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        key = getIntent().getStringExtra("key");

        if (Utils.isNetwork(SearchActivity.this)) {
            getSearchData(true);
        } else {
            binding.lnrMainLayout.setVisibility(View.GONE);
            Utils.InternetAlertDialog(SearchActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
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
//        binding.lnrMainLayout.setVisibility(View.VISIBLE);
        binding.shimmerLayout.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void getSearchData(boolean isLoading) {
        if (isLoading)
            showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(SearchActivity.this, SharedPref.user_id));
        model.setSearch_key(key);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.searchTags(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {

                        JSONObject jsonObject1 = jsonObject.getJSONObject("Data");
                        /*Shop array json*/
                        Gson gson1 = new Gson();
                        Type listType = new TypeToken<List<SearchShopModel>>() {
                        }.getType();
                        searchShopModelArrayList = new ArrayList<>();
                        searchShopModelArrayList.addAll(gson1.fromJson(jsonObject1.getString("shopData"), listType));

                        if (searchShopModelArrayList.size() > 0) {
                            binding.recShoplist.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                            binding.recShoplist.setAdapter(new ShopAdapter());

                            binding.recShoplist.setVisibility(View.VISIBLE);
                            binding.lnrMainLayout.setVisibility(View.VISIBLE);
                        } else {
                            binding.recShoplist.setVisibility(View.GONE);
//                            binding.lnrNoData.setVisibility(View.VISIBLE);
                        }

                        /*Product array json*/
                        Gson gson2 = new Gson();
                        Type listType1 = new TypeToken<List<SearchProductModel>>() {
                        }.getType();
                        searchProductModelArrayList = new ArrayList<>();
                        searchProductModelArrayList.addAll(gson2.fromJson(jsonObject1.getString("productData"), listType1));

                        if (searchProductModelArrayList.size() > 0) {
                            binding.recProductlist.setLayoutManager(new GridLayoutManager(SearchActivity.this, 3));
                            binding.recProductlist.setAdapter(new ProductAdapter(SearchActivity.this, searchProductModelArrayList, "search", "", ""));

                            binding.recProductlist.setVisibility(View.VISIBLE);
                            binding.lnrMainLayout.setVisibility(View.VISIBLE);
//                            binding.lnrNoData.setVisibility(View.GONE);
                        } else {
                            binding.recProductlist.setVisibility(View.GONE);
//                            binding.lnrNoData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.lnrMainLayout.setVisibility(View.GONE);
                        binding.lnrNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    binding.lnrMainLayout.setVisibility(View.GONE);
//                    binding.lnrNoData.setVisibility(View.VISIBLE);
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
                            Toast.makeText(SearchActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(SearchActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(SearchActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(SearchActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(SearchActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemHomeShopsListBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(SearchActivity.this), R.layout.item_home_shops_list, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SearchShopModel model = searchShopModelArrayList.get(position);
            holder.bind(model, position);
        }

        @Override
        public int getItemCount() {
            return searchShopModelArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemHomeShopsListBinding layoutBinding;

            public ViewHolder(@NonNull ItemHomeShopsListBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
                layoutBinding.recProduct.setVisibility(View.GONE);
                layoutBinding.imgDelete.setVisibility(View.GONE);
            }

            public void bind(SearchShopModel model, int position) {
                layoutBinding.txtName.setText(model.getShopName());
                if (model.getFontSizeName().contains("px")) {
                    layoutBinding.txtName.setTextSize(Float.parseFloat(model.getFontSizeName().replace("px", "")));
                }

                layoutBinding.txtAddress.setText(model.getShopAddress());
                layoutBinding.txtMobile.setText(model.getShopMobileNo());

                layoutBinding.lnrBack.setBackgroundColor(Color.parseColor(model.getBgColor()));

                layoutBinding.txtName.setOnClickListener(view -> {
                    startActivity(new Intent(SearchActivity.this, ShopDetailsActivity.class).putExtra("shop_id", model.getShopId()));
                });

                layoutBinding.imgSuperShop.setVisibility(View.GONE);

                layoutBinding.imgSuperShop.setOnClickListener(view -> {
                    if (Utils.isNetwork(SearchActivity.this)) {
                        if (model.getShopStatus() == 1) {
                            removeSuperShop(model.getShopStatusId());
                        } else {
                            addSuperShop(model.getShopId());
                        }
                    } else {
                        binding.lnrMainLayout.setVisibility(View.GONE);
                        Utils.InternetAlertDialog(SearchActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                    }
                });
                layoutBinding.imgMessage.setOnClickListener(view -> startActivity(new Intent(SearchActivity.this, MessageChatActivity.class).putExtra("shop_id", model.getShopId())
                        .putExtra("name", model.getShopName()).putExtra("user_id", SharedPref.getVal(SearchActivity.this, SharedPref.user_id))));

                layoutBinding.lnrMobile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);

                        intent.setData(Uri.parse("tel:" + model.getShopMobileNo()));
                        startActivity(intent);
                    }
                });
            }

            private void addSuperShop(String shopId) {
                dialog.show();
                UserProfileRequestModel model = new UserProfileRequestModel();
                model.setUserId(SharedPref.getVal(SearchActivity.this, SharedPref.user_id));
                model.setShop_id(shopId);

                ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
                client.addSupershop(model).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                Toast.makeText(SearchActivity.this, "Shop added in super shop", Toast.LENGTH_SHORT).show();
                                layoutBinding.imgSuperShop.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_selected));
//                                binding.txtFav.setText("Remove Favourite");
                                getSearchData(false);
                            } else {
                                Toast.makeText(SearchActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SearchActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_FORBIDDEN:
                                    Toast.makeText(SearchActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                    Toast.makeText(SearchActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_BAD_REQUEST:
                                    Toast.makeText(SearchActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(SearchActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SearchActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            private void removeSuperShop(String superShopId) {
                UserProfileRequestModel model = new UserProfileRequestModel();
                model.setUserId(SharedPref.getVal(SearchActivity.this, SharedPref.user_id));
                model.setSuper_id(superShopId);

                ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
                client.removeSupershop(model).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                Toast.makeText(SearchActivity.this, "Shop Removed from Super shop", Toast.LENGTH_SHORT).show();
                                layoutBinding.imgSuperShop.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_super_shop));
                                getSearchData(false);

                            } else {
                                Toast.makeText(SearchActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SearchActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_FORBIDDEN:
                                    Toast.makeText(SearchActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                    Toast.makeText(SearchActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_BAD_REQUEST:
                                    Toast.makeText(SearchActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(SearchActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SearchActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}