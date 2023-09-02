package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityAllCategoryListBinding;
import com.pepdeal.in.databinding.ActivitySellerProductListingBinding;
import com.pepdeal.in.databinding.ItemCategoryListLayoutBinding;
import com.pepdeal.in.databinding.ItemSellerProductListingLayoutBinding;
import com.pepdeal.in.model.productlistmodel.ProductDataModel;
import com.pepdeal.in.model.requestModel.AddProductCategoryResponseModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;

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

public class AllCategoryListActivity extends AppCompatActivity {

    ActivityAllCategoryListBinding binding;
    ArrayList<AddProductCategoryResponseModel> productCategoryModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_category_list);
        binding.setHandler(new ClickHandler());

    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isNetwork(AllCategoryListActivity.this)) {
            getCategoryList(true);
        } else {
            binding.lnrMainLayout.setVisibility(View.GONE);
            Utils.InternetAlertDialog(AllCategoryListActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }
    }

    private void showShimmer() {
        binding.lnrData.setVisibility(View.GONE);
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void hideShimmer() {
        binding.lnrData.setVisibility(View.VISIBLE);
        binding.shimmerLayout.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void getCategoryList(boolean loading) {
        if (loading) {
            showShimmer();
        }
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(AllCategoryListActivity.this, SharedPref.user_id));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.categoryList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    productCategoryModelList = new ArrayList<>();

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            AddProductCategoryResponseModel model = new AddProductCategoryResponseModel();

                            model.setCategoryId(jsonObject1.getString("category_id"));
                            model.setCategoryName(jsonObject1.getString("category_name"));
                            model.setCategoryImages(jsonObject1.getString("category_images"));
                            productCategoryModelList.add(model);
                        }
                    }

                    if (productCategoryModelList.size() > 0) {
                        binding.recList.setLayoutManager(new LinearLayoutManager(AllCategoryListActivity.this));
                        binding.recList.setAdapter(new CategoryAdapter());

                        binding.lnrMainLayout.setVisibility(View.VISIBLE);
                        binding.lnrNoData.setVisibility(View.GONE);
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
                            Toast.makeText(AllCategoryListActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AllCategoryListActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AllCategoryListActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AllCategoryListActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AllCategoryListActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AllCategoryListActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemCategoryListLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(AllCategoryListActivity.this),
                    R.layout.item_category_list_layout, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AddProductCategoryResponseModel model = productCategoryModelList.get(position);
            holder.bind(model, position);
        }

        @Override
        public int getItemCount() {
            return productCategoryModelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemCategoryListLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemCategoryListLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind(AddProductCategoryResponseModel model, int position) {
                Glide.with(AllCategoryListActivity.this).load(model.getCategoryImages())
                        .error(R.drawable.loader).placeholder(R.drawable.loader).into(layoutBinding.imgProductImage);
                layoutBinding.txtCategoryName.setText(model.getCategoryName());
            }
        }
    }
}