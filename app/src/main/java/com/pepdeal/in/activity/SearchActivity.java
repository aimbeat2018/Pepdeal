package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
import com.pepdeal.in.databinding.ItemSearchListBinding;
import com.pepdeal.in.fragment.HomeFragment;
import com.pepdeal.in.fragment.SuperShopFragment;
import com.pepdeal.in.model.homemodel.HomeShopDataModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.searchmodel.SearchHistoryModel;
import com.pepdeal.in.model.searchmodel.SearchProductModel;
import com.pepdeal.in.model.searchmodel.SearchShopModel;
import com.pepdeal.in.model.supershopmodel.SuperShopDataModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
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
    String search_by="";
    ProgressDialog dialog;
    List<SearchShopModel> searchShopModelArrayList = new ArrayList<>();
    List<SearchProductModel> searchProductModelArrayList = new ArrayList<>();
    List<SearchHistoryModel> searchHistoryModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.setHandler(new ClickHandler());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
        search_by = getIntent().getStringExtra("searchby");
        if(search_by.equalsIgnoreCase("shop"))
        {
            binding.searchView.setHint("Enter Shop Name");
        }
        else {
            binding.searchView.setHint("Enter Product Name");
        }

      /*  key = getIntent().getStringExtra("key");
        binding.searchView.setText(key);
*/

        getSearchHistoryList(false);
        binding.txtSearch.setOnClickListener(view -> {
            if (Utils.isNetwork(SearchActivity.this)) {
                if (binding.searchView.getText().toString().equals("")) {
                    Toast.makeText(SearchActivity.this, "Enter search keyword", Toast.LENGTH_SHORT).show();
                }
                else {
                    insertSearchData(false);
                }
            } else {
                binding.lnrMainLayout.setVisibility(View.GONE);
                Utils.InternetAlertDialog(SearchActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
            }

        });
      /*  binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Utils.isNetwork(SearchActivity.this)) {
                    getSearchData(false);
                } else {
                    binding.lnrMainLayout.setVisibility(View.GONE);
                    Utils.InternetAlertDialog(SearchActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.searchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                new Handler().postDelayed(() -> {
                    key = binding.searchView.getText().toString().trim();
                    getSearchData(false);
                }, 1000);
                return true;
            }
            return false;
        });*/
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

    private void insertSearchData(boolean isLoading) {
        if (isLoading)
            showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(SearchActivity.this, SharedPref.user_id));
        model.setSearch_key(binding.searchView.getText().toString());
    //    model.setSearch_type(search_by);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.insertSearchHistory(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                      //  Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                        getSearchData(false);
                    } else {
                    //    binding.lnrMainLayout.setVisibility(View.GONE);
                     //   binding.lnrNoData.setVisibility(View.VISIBLE);
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


    private void getSearchHistoryList(boolean isLoading) {
        if (isLoading)
            showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(SearchActivity.this, SharedPref.user_id));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.getSearchHistory(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                            Gson gson1 = new Gson();
                            Type listType = new TypeToken<List<SearchHistoryModel>>() {
                            }.getType();
                            searchHistoryModelArrayList = new ArrayList<>();
                            searchHistoryModelArrayList.addAll(gson1.fromJson(jsonObject.getString("data"), listType));
                            
                            if (searchHistoryModelArrayList.size() > 0) {
                                Collections.reverse(searchHistoryModelArrayList);
                                binding.recHistoryList.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                                binding.recHistoryList.setAdapter(new HistoryAdapter());

                                binding.recHistoryList.setVisibility(View.VISIBLE);
                                binding.lnrMainLayout.setVisibility(View.VISIBLE);

                            } else {
                                binding.recHistoryList.setVisibility(View.GONE);
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


    private void getSearchData(boolean isLoading) {
        if (isLoading)
            showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(SearchActivity.this, SharedPref.user_id));
        model.setSearch_key(binding.searchView.getText().toString());
        model.setSearch_type(search_by);

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
                        if(jsonObject1.has("shopData")) {
                            Gson gson1 = new Gson();
                            Type listType = new TypeToken<List<SearchShopModel>>() {
                            }.getType();
                            searchShopModelArrayList = new ArrayList<>();
                            searchShopModelArrayList.addAll(gson1.fromJson(jsonObject1.getString("shopData"), listType));

                            if (searchShopModelArrayList.size() > 0) {
                                binding.recShoplist.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                                binding.recShoplist.setAdapter(new ShopAdapter());

                                binding.recShoplist.setVisibility(View.VISIBLE);
                                binding.recHistoryList.setVisibility(View.GONE);
                                binding.lnrNoData.setVisibility(View.GONE);
                                binding.lnrMainLayout.setVisibility(View.VISIBLE);
                            } else {
                                binding.recShoplist.setVisibility(View.GONE);
//                            binding.lnrNoData.setVisibility(View.VISIBLE);
                            }
                        }

                        /*Product array json*/
                        if(jsonObject1.has("productData")) {
                            Gson gson2 = new Gson();
                            Type listType1 = new TypeToken<List<SearchProductModel>>() {
                            }.getType();
                            searchProductModelArrayList = new ArrayList<>();
                            searchProductModelArrayList.addAll(gson2.fromJson(jsonObject1.getString("productData"), listType1));

                            if (searchProductModelArrayList.size() > 0) {
                                binding.recProductlist.setLayoutManager(new GridLayoutManager(SearchActivity.this, 3));
                                binding.recProductlist.setAdapter(new ProductAdapter(SearchActivity.this, searchProductModelArrayList, "search", "", ""));

                                binding.recProductlist.setVisibility(View.VISIBLE);
                                binding.recHistoryList.setVisibility(View.GONE);
                                binding.lnrNoData.setVisibility(View.GONE);
                                binding.lnrMainLayout.setVisibility(View.VISIBLE);
//                            binding.lnrNoData.setVisibility(View.GONE);
                            } else {
                                binding.recProductlist.setVisibility(View.GONE);
//                            binding.lnrNoData.setVisibility(View.VISIBLE);
                            }
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

    private void removeSearchData(boolean isLoading,String search_id) {
        if (isLoading)
            showShimmer();
        SearchHistoryModel model = new SearchHistoryModel();
     //   model.setUserId(SharedPref.getVal(SearchActivity.this, SharedPref.user_id));
        model.setSearchId(search_id);
        //    model.setSearch_type(search_by);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.removeHistory(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        //  Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                        getSearchHistoryList(false);
                    } else {
                        //    binding.lnrMainLayout.setVisibility(View.GONE);
                        //   binding.lnrNoData.setVisibility(View.VISIBLE);
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
               /* layoutBinding.txtName.setText(model.getShopName());
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
                });*/

                layoutBinding.txtName.setText(model.getShopName());
                if (!model.getFontcolorName().equals(""))
                    layoutBinding.txtName.setTextColor(Color.parseColor(model.getFontcolorName()));

                Typeface typeface = null;
                if (model.getFontStyleId().equals("1")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.gagalin);
                } else if (model.getFontStyleId().equals("2")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.anahaw);
                } else if (model.getFontStyleId().equals("3")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.quicksand);
                } else if (model.getFontStyleId().equals("4")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.open_sans_extra);
                } else if (model.getFontStyleId().equals("5")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.lovelo);
                } else if (model.getFontStyleId().equals("6")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.league_spartan);
                } else if (model.getFontStyleId().equals("7")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.lato);
                } else if (model.getFontStyleId().equals("8")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.yeseva_one);
                } else if (model.getFontStyleId().equals("9")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.quando);
                } else if (model.getFontStyleId().equals("10")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.roboto);
                } else if (model.getFontStyleId().equals("11")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.muli);
                } else if (model.getFontStyleId().equals("12")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.kollektif);
                } else if (model.getFontStyleId().equals("13")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.sailors);
                } else if (model.getFontStyleId().equals("14")) {
                    typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.fredoka_one);
                }
                /*else if (model.getFontStyleId().equals("13")) {
                            typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.roboto_mediumitalic);
                        } else if (model.getFontStyleId().equals("14")) {
                            typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.roboto_regular);
                        } else if (model.getFontStyleId().equals("15")) {
                            typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.roboto_thin);
                        } else if (model.getFontStyleId().equals("16")) {
                            typeface = ResourcesCompat.getFont(SearchActivity.this, R.font.roboto_thinitalic);
                        }*/
                if (!model.getFontStyleId().equals("") || !model.getFontStyleId().equals("0")) {
                    layoutBinding.txtName.setTypeface(typeface);
                }
                String address = model.getShopAddress2();
                layoutBinding.txtAddress.setText(address);
                layoutBinding.txtMobile.setText(model.getShopMobileNo());

                try {
                    layoutBinding.lnrBack.setBackgroundColor(Color.parseColor(model.getBgColor()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                layoutBinding.lnrBack.setOnClickListener(view -> {
                    startActivity(new Intent(SearchActivity.this, ShopDetailsActivity.class).putExtra("shop_id", model.getShopId()));
                });
            }

        }
    }

    public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemSearchListBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(SearchActivity.this), R.layout.item_search_list, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SearchHistoryModel model = searchHistoryModelArrayList.get(position);
            holder.bind(model, position);
        }

        @Override
        public int getItemCount() {
            int display_size=0;
            if(searchHistoryModelArrayList.size()>=10)
            {
                display_size=10;
            }
            else {
                display_size=searchHistoryModelArrayList.size();
            }
            return display_size;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemSearchListBinding layoutBinding;

            public ViewHolder(@NonNull ItemSearchListBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            //    layoutBinding.recProduct.setVisibility(View.GONE);
               // layoutBinding.imgDelete.setVisibility(View.GONE);
            }

            public void bind(SearchHistoryModel model, int position) {

                layoutBinding.txtSearchkey.setText(model.getSearchKey());
                layoutBinding.txtSearchkey.setOnClickListener(view -> {
                    binding.searchView.setText(layoutBinding.txtSearchkey.getText().toString());
                });
                layoutBinding.btnRemove.setOnClickListener(view -> {
                    removeSearchData(false,model.getSearchId());
                   // startActivity(new Intent(SearchActivity.this, ShopDetailsActivity.class).putExtra("shop_id", model.getShopId()));
                });
            }

        }
    }

}