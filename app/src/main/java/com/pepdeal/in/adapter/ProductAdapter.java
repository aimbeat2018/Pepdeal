package com.pepdeal.in.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.pepdeal.in.R;
import com.pepdeal.in.activity.AddShopActivity;
import com.pepdeal.in.activity.ProductDetailsActivity;
import com.pepdeal.in.activity.SellerProductListingActivity;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.databinding.ItemProductListLayoutBinding;
import com.pepdeal.in.fragment.HomeFragment;
import com.pepdeal.in.model.UsersHomeTabModel;
import com.pepdeal.in.model.homemodel.HomeProductDataModel;
import com.pepdeal.in.model.homemodel.HomeShopDataModel;
import com.pepdeal.in.model.productdetailsmodel.ProductDetailsDataModel;
import com.pepdeal.in.model.requestModel.AddShopRequestModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.searchmodel.SearchProductModel;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

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
        ItemProductListLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.item_product_list_layout, parent, false);
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
        ItemProductListLayoutBinding layoutBinding;

        public ViewHolder(@NonNull ItemProductListLayoutBinding itemView) {
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


            if(model.getOnCall().equalsIgnoreCase("1")) {
                layoutBinding.cardOffer.setVisibility(View.GONE);
                layoutBinding.txtActualPrice.setText("On call");

                //layoutBinding.txtActualPrice.setVisibility(View.GONE);
                layoutBinding.txtDiscountPrice.setVisibility(View.GONE);
                layoutBinding.txtOff.setVisibility(View.GONE);

            }
            else {
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

            if (model.getFavouriteStatus().equals("1")) {
                layoutBinding.imgAddFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_selected));
            } else {
                layoutBinding.imgAddFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite));
            }


            layoutBinding.imgAddFav.setOnClickListener(view -> {
                if (model.getFavouriteStatus().equals("0")) {
                    model.setFavouriteStatus("1");
                    addFav(model.getProductId());
                } else {
                    model.setFavouriteStatus("0");
                    removeFav(model.getProductId());
                }
            });

        }

        public void bindShopData(ProductDetailsDataModel model, int position) {
            Glide.with(activity).load(model.getProductImages().get(0).getProductImage())
                    .error(R.drawable.loader).placeholder(R.drawable.loader).into(layoutBinding.imgProductImage);

        /*    Glide.with(activity)
                    .load(model.getProductImages().get(0).getProductImage())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                    .error(R.drawable.loader).placeholder(R.drawable.loader)
                    .into(new DrawableImageViewTarget(layoutBinding.imgProductImage));*/

            layoutBinding.txtProductName.setText(model.getProductName());
            if(model.getOnCall().equalsIgnoreCase("1")) {
                layoutBinding.cardOffer.setVisibility(View.GONE);
                layoutBinding.txtActualPrice.setText("On call");

//                layoutBinding.txtActualPrice.setVisibility(View.GONE);
                layoutBinding.txtDiscountPrice.setVisibility(View.GONE);
                layoutBinding.txtOff.setVisibility(View.GONE);

            }
            else {
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

            if (model.getFavouriteStatus().equals("1")) {
                layoutBinding.imgAddFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_selected));
            } else {
                layoutBinding.imgAddFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite));
            }


            layoutBinding.imgAddFav.setOnClickListener(view -> {
                if (model.getFavouriteStatus().equals("0")) {
                    model.setFavouriteStatus("1");
                    addFav(model.getProductId());
                } else {
                    model.setFavouriteStatus("0");
                    removeFav(model.getProductId());
                }
            });

        }

        public void bindSearchData(SearchProductModel model, int position) {
            Glide.with(activity).load(model.getProductImage())
                    .error(R.drawable.loader).placeholder(R.drawable.loader).into(layoutBinding.imgProductImage);
            layoutBinding.txtProductName.setText(model.getProductName());

            if(model.getOnCall().equalsIgnoreCase("1")) {
                layoutBinding.cardOffer.setVisibility(View.GONE);
                layoutBinding.txtActualPrice.setText("On call");

                // layoutBinding.txtActualPrice.setVisibility(View.GONE);
                layoutBinding.txtDiscountPrice.setVisibility(View.GONE);
                layoutBinding.txtOff.setVisibility(View.GONE);

            }
            else {
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

            if (model.getFavStatus()==1) {
                layoutBinding.imgAddFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_selected));
            } else {
                layoutBinding.imgAddFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite));
            }


            layoutBinding.imgAddFav.setOnClickListener(view -> {
                if (model.getFavStatus()==0) {
                    model.setFavStatus(1);
                    addFav(model.getProductId());
                } else {
                    model.setFavStatus(0);
                    removeFav(model.getProductId());
                }
            });

        }

        private void addFav(String productId) {
            layoutBinding.imgAddFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_loader));
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

                            layoutBinding.imgAddFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_selected));


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
            layoutBinding.imgAddFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_loader));
            UserProfileRequestModel model = new UserProfileRequestModel();
            model.setUserId(SharedPref.getVal(activity, SharedPref.user_id));
            model.setProduct_id(favId);

            ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
            client.favouriteProductidRemove(model).enqueue(new Callback<ResponseBody>() {
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
                            layoutBinding.imgAddFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite));
                            // getHomeData(false);

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

/*
        private void removeFav(String favId) {
            layoutBinding.imgAddFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_loader));
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
                          */
/*  final VibrationEffect vibrationEffect2;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                final Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                                // create vibrator effect with the constant EFFECT_TICK
                                vibrationEffect2 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK);

                                // it is safe to cancel other vibrations currently taking place
                                vibrator.cancel();

                                vibrator.vibrate(vibrationEffect2);
                            }*//*

                            Toast.makeText(activity, "Product Removed from favourite", Toast.LENGTH_SHORT).show();

                            layoutBinding.imgAddFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite));


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
*/
    }
}
