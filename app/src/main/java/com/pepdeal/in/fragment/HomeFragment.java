package com.pepdeal.in.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.activity.HomeActivity;
import com.pepdeal.in.activity.MessageChatActivity;
import com.pepdeal.in.activity.ProductDetailsActivity;
import com.pepdeal.in.activity.SearchActivity;
import com.pepdeal.in.activity.SellerProductListingActivity;
import com.pepdeal.in.activity.ShopDetailsActivity;
import com.pepdeal.in.activity.ShopSignBoardActivity;
import com.pepdeal.in.adapter.ProductAdapter;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.FragmentHomeBinding;
import com.pepdeal.in.databinding.ItemCategoryHomeLayoutBinding;
import com.pepdeal.in.databinding.ItemHomeProductListLayoutBinding;
import com.pepdeal.in.databinding.ItemHomeShopsListBinding;
import com.pepdeal.in.databinding.ItemProductListLayoutBinding;
import com.pepdeal.in.model.UsersHomeTabModel;
import com.pepdeal.in.model.homemodel.HomeProductDataModel;
import com.pepdeal.in.model.homemodel.HomeShopDataModel;
import com.pepdeal.in.model.productdetailsmodel.ProductDetailsDataModel;
import com.pepdeal.in.model.productlistmodel.ProductDataModel;
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

public class HomeFragment extends Fragment {
    HomeActivity activity;
    FragmentHomeBinding binding;
    List<HomeShopDataModel> homeShopDataModelList = new ArrayList<>();
    List<String> bannerList = new ArrayList<>();
    ProgressDialog dialog;
   /* public HomeFragment(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        activity = (HomeActivity) getActivity();

        dialog = new ProgressDialog(activity);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
        /*   *//*Swipe refresh color scheme*//*
        binding.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.blue),
                getResources().getColor(R.color.purple),
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.orange));*/

        if (Utils.isNetwork(activity)) {
            getHomeData();
        } else {
            binding.lnrMainLayout.setVisibility(View.GONE);
            Utils.InternetAlertDialog(activity, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }

       /* binding.swipeRefresh.setOnRefreshListener(() -> {
            if (Utils.isNetwork(activity)) {
                getHomeData();
            } else {
                binding.lnrMainLayout.setVisibility(View.GONE);
                Utils.InternetAlertDialog(activity, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
            }

        });*/

        return binding.getRoot();
    }

    private void showShimmer() {
        binding.lnrMainLayout.setVisibility(View.GONE);
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void hideShimmer() {
        binding.lnrMainLayout.setVisibility(View.VISIBLE);
        binding.shimmerLayout.setVisibility(View.GONE);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void getHomeData() {
        showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(activity, SharedPref.user_id));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.homePage(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {

                        bannerList = new ArrayList<>();
                        JSONArray bannerArray = jsonObject.getJSONArray("banner_list");
                        if (bannerArray.length() > 0) {
                            for (int i = 0; i < bannerArray.length(); i++) {
                                JSONObject jsonObject1 = bannerArray.getJSONObject(i);
                                bannerList.add(jsonObject1.getString("banner_image"));
                            }
                            binding.viewPager2.setAdapter(new ViewPagerAdapter());
                            binding.viewPager2.startAutoScroll();

                            binding.viewPager2.setAdapter(new ViewPagerAdapter());
                            binding.viewPager2.startAutoScroll();
                            binding.viewPager2.setInterval(3000);
                            binding.viewPager2.setCycle(true);
                            binding.viewPager2.setStopScrollWhenTouch(true);
                            binding.viewPager2.setVisibility(View.VISIBLE);
                            binding.dotsIndicator.setViewPager(binding.viewPager2);
                            binding.dotsIndicator.setVisibility(View.VISIBLE);
                        } else {
                            binding.viewPager2.setVisibility(View.GONE);
                        }
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<HomeShopDataModel>>() {
                        }.getType();
                        homeShopDataModelList = new ArrayList<>();
                        homeShopDataModelList.addAll(gson.fromJson(jsonObject.getString("shop List"), listType));

                        if (homeShopDataModelList.size() > 0) {
                            binding.recList.setLayoutManager(new LinearLayoutManager(activity));
                            binding.recList.setAdapter(new ShopAdapter());

//                            binding.lnrMainLayout.setVisibility(View.VISIBLE);
//                            binding.lnrNoData.setVisibility(View.GONE);
                        } else {
//                            binding.lnrMainLayout.setVisibility(View.GONE);
//                            binding.lnrNoData.setVisibility(View.VISIBLE);
                        }
                    } else {
//                        binding.lnrMainLayout.setVisibility(View.GONE);
//                        binding.lnrNoData.setVisibility(View.VISIBLE);
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
                            Toast.makeText(activity, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(activity, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(activity, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(activity, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(activity, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemHomeShopsListBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.item_home_shops_list, parent, false);
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
                layoutBinding.txtName.setTextColor(Color.parseColor(model.getFontcolorName()));

                Typeface typeface = null;
                if (model.getFontStyleId().equals("1")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_black);
                } else if (model.getFontStyleId().equals("2")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_blackitalic);
                } else if (model.getFontStyleId().equals("3")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_bold);
                } else if (model.getFontStyleId().equals("4")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_boldcondensed);
                } else if (model.getFontStyleId().equals("5")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_boldcondenseditalic);
                } else if (model.getFontStyleId().equals("6")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_bolditalic);
                } else if (model.getFontStyleId().equals("7")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_condensed);
                } else if (model.getFontStyleId().equals("8")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_condenseditalic);
                } else if (model.getFontStyleId().equals("9")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_italic);
                } else if (model.getFontStyleId().equals("10")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_light);
                } else if (model.getFontStyleId().equals("11")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_lightitalic);
                } else if (model.getFontStyleId().equals("12")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_medium);
                } else if (model.getFontStyleId().equals("13")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_mediumitalic);
                } else if (model.getFontStyleId().equals("14")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_regular);
                } else if (model.getFontStyleId().equals("15")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_thin);
                } else if (model.getFontStyleId().equals("16")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.roboto_thinitalic);
                }
                if (!model.getFontStyleId().equals("") || !model.getFontStyleId().equals("0"))
                    layoutBinding.txtName.setTypeface(typeface);
                layoutBinding.txtAddress.setText(model.getCity() + ", " + model.getState());
                layoutBinding.txtMobile.setText(model.getShopMobileNo());

                layoutBinding.lnrBack.setBackgroundColor(Color.parseColor(model.getBgcolorName()));

                layoutBinding.lnrBack.setOnClickListener(view -> {
                    startActivity(new Intent(activity, ShopDetailsActivity.class).putExtra("shop_id", model.getShopId()));
                });
                if (model.getProductsList().isEmpty()) {
                    layoutBinding.recProduct.setVisibility(View.GONE);
                } else {
                    layoutBinding.recProduct.setVisibility(View.VISIBLE);
                    layoutBinding.recProduct.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
                    layoutBinding.recProduct.setAdapter(new ProductAdapter(activity, model.getProductsList(), "home"));
                }

                if (model.getSuperShopStatus().equals("1")) {
                    layoutBinding.imgSuperShop.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_selected));
                } else {
                    layoutBinding.imgSuperShop.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_super_shop));
                }

                layoutBinding.imgSuperShop.setOnClickListener(view -> {
                    if (Utils.isNetwork(activity)) {
                        if (model.getSuperShopStatus().equals("1")) {
                            removeSuperShop(model.getSuperShopId());
                        } else {
                            addSuperShop(model.getShopId());
                        }
                    } else {
                        binding.lnrMainLayout.setVisibility(View.GONE);
                        Utils.InternetAlertDialog(activity, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                    }
                });

                layoutBinding.imgMessage.setOnClickListener(view -> {
                    startActivity(new Intent(activity, MessageChatActivity.class).putExtra("shop_id", model.getShopId())
                            .putExtra("name", model.getShopName()).putExtra("user_id", SharedPref.getVal(activity, SharedPref.user_id)));
                });

                layoutBinding.lnrMobile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);

                        intent.setData(Uri.parse("tel:" + model.getShopMobileNo()));
                        activity.startActivity(intent);
                    }
                });
            }

            private void addSuperShop(String shopId) {
                dialog.show();
                UserProfileRequestModel model = new UserProfileRequestModel();
                model.setUserId(SharedPref.getVal(activity, SharedPref.user_id));
                model.setShop_id(shopId);

                ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
                client.addSupershop(model).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                Toast.makeText(activity, "Shop added in super shop", Toast.LENGTH_SHORT).show();
                                layoutBinding.imgSuperShop.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_selected));
//                                binding.txtFav.setText("Remove Favourite");
                                getHomeData();
                            } else {
                                Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(activity, activity.getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_FORBIDDEN:
                                    Toast.makeText(activity, activity.getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                    Toast.makeText(activity, activity.getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_BAD_REQUEST:
                                    Toast.makeText(activity, activity.getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(activity, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            private void removeSuperShop(String superShopId) {
                UserProfileRequestModel model = new UserProfileRequestModel();
                model.setUserId(SharedPref.getVal(activity, SharedPref.user_id));
                model.setSuper_id(superShopId);

                ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
                client.removeSupershop(model).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                Toast.makeText(activity, "Shop Removed from Super shop", Toast.LENGTH_SHORT).show();
                                layoutBinding.imgSuperShop.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_super_shop));
                                getHomeData();

                            } else {
                                Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(activity, activity.getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_FORBIDDEN:
                                    Toast.makeText(activity, activity.getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                    Toast.makeText(activity, activity.getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_BAD_REQUEST:
                                    Toast.makeText(activity, activity.getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(activity, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    /*Slider Adapter*/
    public class ViewPagerAdapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.viewpager_item_layout, container, false);
            ImageView img_banner = layout.findViewById(R.id.img_banner);

            Glide.with(activity).load(bannerList.get(position))
                    .error(R.drawable.loader).placeholder(R.drawable.loader).into(img_banner);

            /*img_banner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bannerModelArrayList.get(position).getBanner_id().equals("")) {
                        Intent intent = new Intent(HomeActivity2.this, CategoryWiseProductListActivity.class);
                        intent.putExtra("sub_cat_id", bannerModelArrayList.get(position).getCategory_id());
                        intent.putExtra("sub_cat_name", "");
                        intent.putExtra("from", "cat");
                        startActivity(intent);
                    }
                }
            });*/

            container.addView(layout);
            return layout;
        }

        @Override
        public int getCount() {
            return bannerList.size();
        }


        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }


        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
        Context activity;
        List<HomeProductDataModel> homeProductDataModelList;
        List<ProductDetailsDataModel> productDetailsDataModelList;
        List<SearchProductModel> searchProductDataModelList;
        String from = "";
        String from1 = "";

        public ProductAdapter(Context activity) {
            this.activity = activity;
        }

        public ProductAdapter(Context activity, List<HomeProductDataModel> homeProductDataModelList, String from) {
            this.activity = activity;
            this.homeProductDataModelList = homeProductDataModelList;
            this.from = from;
        }

        public ProductAdapter(Context activity, List<ProductDetailsDataModel> productDetailsDataModelList, String from, String from1) {
            this.activity = activity;
            this.productDetailsDataModelList = productDetailsDataModelList;
            this.from = from;
            this.from1 = from1;
        }

        public ProductAdapter(Context activity, List<SearchProductModel> productDetailsDataModelList, String from, String from1, String from2) {
            this.activity = activity;
            this.searchProductDataModelList = productDetailsDataModelList;
            this.from = from;
            this.from1 = from1;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemHomeProductListLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.item_home_product_list_layout, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (from.equals("home")) {
                HomeProductDataModel model = homeProductDataModelList.get(position);
                holder.bindHomeData(model, position);

            } else if (from.equals("shop")) {
                ProductDetailsDataModel model = productDetailsDataModelList.get(position);
                holder.bindShopData(model, position);

            } else if (from.equals("search")) {
                SearchProductModel model = searchProductDataModelList.get(position);
                holder.bindSearchData(model, position);

            } else {
          /*  UsersHomeTabModel model = homeTabModelArrayList.get(position);
            holder.bind(model, position);*/
                holder.bind();
            }
        }

        @Override
        public int getItemCount() {
            if (from.equals("home"))
                return homeProductDataModelList.size();
            else if (from.equals("shop"))
                return productDetailsDataModelList.size();
            else if (from.equals("search"))
                return searchProductDataModelList.size();
            else
                return 3;

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemHomeProductListLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemHomeProductListLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind() {
                layoutBinding.lnrDetails.setOnClickListener(view -> {
                    Intent intent = new Intent(new Intent(activity, ProductDetailsActivity.class));
                    intent.putExtra("product_id", "");
                    activity.startActivity(intent);
                });
                layoutBinding.relImage.setOnClickListener(view -> {
                    Intent intent = new Intent(new Intent(activity, ProductDetailsActivity.class));
                    intent.putExtra("product_id", "");
                    activity.startActivity(intent);
                });

            }

            public void bindHomeData(HomeProductDataModel model, int position) {
                Glide.with(activity).load(model.getProductImages())
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
                    Intent intent = new Intent(new Intent(activity, ProductDetailsActivity.class));
                    intent.putExtra("product_id", model.getProductId());
                    activity.startActivity(intent);
                });
                layoutBinding.relImage.setOnClickListener(view -> {
                    Intent intent = new Intent(new Intent(activity, ProductDetailsActivity.class));
                    intent.putExtra("product_id", model.getProductId());
                    activity.startActivity(intent);
                });

//                if (model.getFavouriteStatus().equals("0")) {
//                    layoutBinding.imgAddFav.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
//                } else {
//                    layoutBinding.imgAddFav.setColorFilter(ContextCompat.getColor(activity, R.color.errorColor), android.graphics.PorterDuff.Mode.SRC_IN);
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

            public void bindShopData(ProductDetailsDataModel model, int position) {
                Glide.with(activity).load(model.getProductImages())
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
                    Intent intent = new Intent(new Intent(activity, ProductDetailsActivity.class));
                    intent.putExtra("product_id", model.getProductId());
                    activity.startActivity(intent);
                });
                layoutBinding.relImage.setOnClickListener(view -> {
                    Intent intent = new Intent(new Intent(activity, ProductDetailsActivity.class));
                    intent.putExtra("product_id", model.getProductId());
                    activity.startActivity(intent);
                });

//                if (model.getFavouriteStatus().equals("0")) {
//                    layoutBinding.imgAddFav.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
//                } else {
//                    layoutBinding.imgAddFav.setColorFilter(ContextCompat.getColor(activity, R.color.errorColor), android.graphics.PorterDuff.Mode.SRC_IN);
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

            public void bindSearchData(SearchProductModel model, int position) {
                Glide.with(activity).load(model.getProductImage())
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
                    Intent intent = new Intent(new Intent(activity, ProductDetailsActivity.class));
                    intent.putExtra("product_id", model.getProductId());
                    activity.startActivity(intent);
                });
                layoutBinding.relImage.setOnClickListener(view -> {
                    Intent intent = new Intent(new Intent(activity, ProductDetailsActivity.class));
                    intent.putExtra("product_id", model.getProductId());
                    activity.startActivity(intent);
                });

//                if (model.getFavStatus().equals("0")) {
//                    layoutBinding.imgAddFav.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
//                } else {
//                    layoutBinding.imgAddFav.setColorFilter(ContextCompat.getColor(activity, R.color.errorColor), android.graphics.PorterDuff.Mode.SRC_IN);
//                }

                if (model.getFavStatus().equals("1")) {
                    layoutBinding.imgAddFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_selected));
                } else {
                    layoutBinding.imgAddFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                }

                layoutBinding.imgAddFav.setOnClickListener(view -> {
                    if (model.getFavStatus().equals("0")) {
                        addFav(model.getProductId());
                    } else {
                        removeFav(model.getFavId());
                    }
                });

            }

            private void addFav(String productId) {
                UserProfileRequestModel model = new UserProfileRequestModel();
                model.setUserId(SharedPref.getVal(activity, SharedPref.user_id));
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
                                final Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                                // create vibrator effect with the constant EFFECT_TICK
                                vibrationEffect2 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK);

                                // it is safe to cancel other vibrations currently taking place
                                vibrator.cancel();

                                vibrator.vibrate(vibrationEffect2);
                            }*/
                                Toast.makeText(activity, "Product Added to favourite", Toast.LENGTH_SHORT).show();
                                layoutBinding.imgAddFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_selected));
                            } else {
                                Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(activity, activity.getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_FORBIDDEN:
                                    Toast.makeText(activity, activity.getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                    Toast.makeText(activity, activity.getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_BAD_REQUEST:
                                    Toast.makeText(activity, activity.getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(activity, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            private void removeFav(String favId) {
                UserProfileRequestModel model = new UserProfileRequestModel();
                model.setUserId(SharedPref.getVal(activity, SharedPref.user_id));
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
                                final Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                                // create vibrator effect with the constant EFFECT_TICK
                                vibrationEffect2 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK);

                                // it is safe to cancel other vibrations currently taking place
                                vibrator.cancel();

                                vibrator.vibrate(vibrationEffect2);
                            }*/
                                Toast.makeText(activity, "Product Removed from favourite", Toast.LENGTH_SHORT).show();
                                layoutBinding.imgAddFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                            } else {
                                Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(activity, activity.getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_FORBIDDEN:
                                    Toast.makeText(activity, activity.getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                    Toast.makeText(activity, activity.getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_BAD_REQUEST:
                                    Toast.makeText(activity, activity.getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(activity, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

}