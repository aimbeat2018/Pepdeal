package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.adapter.ProductAdapter;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivitySellerProductListingBinding;
import com.pepdeal.in.databinding.ActivityShopDetailsBinding;
import com.pepdeal.in.databinding.ItemSellerProductListingLayoutBinding;
import com.pepdeal.in.model.UsersHomeTabModel;
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

public class SellerProductListingActivity extends AppCompatActivity {

    ActivitySellerProductListingBinding binding;
    List<ProductDataModel> productDataModelList = new ArrayList<>();
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_product_listing);
        binding.setHandler(new ClickHandler());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isNetwork(SellerProductListingActivity.this)) {
            getProductList(true);
        } else {
            binding.lnrMainLayout.setVisibility(View.GONE);
            Utils.InternetAlertDialog(SellerProductListingActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
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

    private void getProductList(boolean loading) {
        if (loading) {
            showShimmer();
        }
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(SellerProductListingActivity.this, SharedPref.user_id));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.productList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<ProductDataModel>>() {
                        }.getType();
                        productDataModelList = new ArrayList<>();
                        productDataModelList.addAll(gson.fromJson(jsonObject.getString("data"), listType));

                        if (productDataModelList.size() > 0) {
                            binding.recList.setLayoutManager(new LinearLayoutManager(SellerProductListingActivity.this));
                            binding.recList.setAdapter(new ProductAdapter());

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
                            Toast.makeText(SellerProductListingActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(SellerProductListingActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(SellerProductListingActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(SellerProductListingActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(SellerProductListingActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SellerProductListingActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemSellerProductListingLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(SellerProductListingActivity.this),
                    R.layout.item_seller_product_listing_layout, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ProductDataModel model = productDataModelList.get(position);
            holder.bind(model, position);
        }

        @Override
        public int getItemCount() {
            return productDataModelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemSellerProductListingLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemSellerProductListingLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind(ProductDataModel model, int position) {
                Glide.with(SellerProductListingActivity.this).load(model.getIMges().get(0).getImage())
                        .error(R.drawable.loader).placeholder(R.drawable.loader).into(layoutBinding.imgProductImage);
                layoutBinding.txtProductName.setText(model.getProductName());

                if (model.getIsActive().equals("1")) {
                    layoutBinding.switchLiveStatus.setOn(false);
                } else {
                    layoutBinding.switchLiveStatus.setOn(true);
                }
                if (model.getFlag().equals("1")) {
                    layoutBinding.relWaiting.setVisibility(View.VISIBLE);
                    layoutBinding.switchLiveStatus.setVisibility(View.GONE);
                    layoutBinding.cardDelete.setEnabled(false);
                    layoutBinding.cardUpdate.setEnabled(false);
                } else {
                    layoutBinding.relWaiting.setVisibility(View.GONE);
                    layoutBinding.switchLiveStatus.setVisibility(View.VISIBLE);
                    layoutBinding.cardDelete.setEnabled(true);
                    layoutBinding.cardUpdate.setEnabled(true);
                }
                if(model.getOnCall().equalsIgnoreCase("1")) {
                    layoutBinding.lnrOffer.setVisibility(View.GONE);
                    layoutBinding.txtActualPrice.setText("On call");
//                    layoutBinding.txtActualPrice.setVisibility(View.GONE);
                    layoutBinding.txtDiscountPrice.setVisibility(View.GONE);
                    layoutBinding.txtOff.setVisibility(View.GONE);

                }
                else {
                    if (model.getDiscountMrp().equals("0") || model.getDiscountMrp().equals("") || model.getDiscountMrp() == null) {
                        layoutBinding.lnrOffer.setVisibility(View.GONE);
                        layoutBinding.txtActualPrice.setVisibility(View.GONE);
                        layoutBinding.txtDiscountPrice.setText("₹ " + model.getMrp());
                    } else {
                        layoutBinding.lnrOffer.setVisibility(View.VISIBLE);
                        layoutBinding.txtActualPrice.setVisibility(View.VISIBLE);

                        layoutBinding.txtActualPrice.setText("₹ " + model.getMrp());
                        layoutBinding.txtActualPrice.setPaintFlags(layoutBinding.txtActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        layoutBinding.txtDiscountPrice.setText("₹ " + model.getSellingPrice());

                        layoutBinding.txtOff.setText(model.getDiscountMrp() + "% OFF");
                    }
                }
                layoutBinding.cardUpdate.setOnClickListener(view -> {
                    startActivity(new Intent(SellerProductListingActivity.this, AddProductActivity.class).
                            putExtra("from", "edit").putExtra("product_id", model.getProductId()));
                });

                layoutBinding.switchLiveStatus.setOnToggledListener(new OnToggledListener() {
                    @Override
                    public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                        if (isOn) {
                            Dialog dialog = new Dialog(SellerProductListingActivity.this);
                            dialog.setContentView(R.layout.delete_popup);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            TextView txt_title=dialog.findViewById(R.id.txt_title);
                            TextView txt_alert=dialog.findViewById(R.id.txt_alert);
                            ImageView img_delete=dialog.findViewById(R.id.img_delete);
                            img_delete.setVisibility(View.GONE);
                            txt_alert.setVisibility(View.VISIBLE);
                            Button yes = dialog.findViewById(R.id.yes);
                            Button no = dialog.findViewById(R.id.no);
                            txt_alert.setText("Alert!!!");
                            txt_title.setText("Are you sure you want to do live this product?");

                            yes.setOnClickListener(v -> {
                                if (Utils.isNetwork(SellerProductListingActivity.this)) {
                                    liveProduct(model.getProductId(), "0");
                                    dialog.dismiss();
//                                            getFavList(true);
                                } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                    Utils.InternetAlertDialog(SellerProductListingActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                                }
                            });

                            no.setOnClickListener(view -> {
                                layoutBinding.switchLiveStatus.setOn(false);
                                dialog.dismiss();
                            } );

                            dialog.show();
                          /*  new AlertDialog.Builder(SellerProductListingActivity.this)
                                    .setTitle("Alert!!!")
                                    .setMessage("Are you sure you want to do live this product?")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            if (Utils.isNetwork(SellerProductListingActivity.this)) {
                                                liveProduct(model.getProductId(), "0");
                                                dialog.dismiss();
//                                            getFavList(true);
                                            } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                                Utils.InternetAlertDialog(SellerProductListingActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                                            }
                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();*/
                        } else {

                            Dialog dialog = new Dialog(SellerProductListingActivity.this);
                            dialog.setContentView(R.layout.delete_popup);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            TextView txt_title=dialog.findViewById(R.id.txt_title);
                            TextView txt_alert=dialog.findViewById(R.id.txt_alert);
                            ImageView img_delete=dialog.findViewById(R.id.img_delete);
                            img_delete.setVisibility(View.GONE);
                            txt_alert.setVisibility(View.VISIBLE);
                            Button yes = dialog.findViewById(R.id.yes);
                            Button no = dialog.findViewById(R.id.no);
                            txt_alert.setText("Alert!!!");
                            txt_title.setText("Are you sure you want to do non live this product?");

                            yes.setOnClickListener(v -> {
                                // Continue with delete operation
                                if (Utils.isNetwork(SellerProductListingActivity.this)) {
                                    liveProduct(model.getProductId(), "1");
                                    dialog.dismiss();
//                                            getFavList(true);
                                } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                    Utils.InternetAlertDialog(SellerProductListingActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                                }
                            });

                            no.setOnClickListener(view -> {
                                layoutBinding.switchLiveStatus.setOn(true);
                                dialog.dismiss();
                            } );

                            dialog.show();
/*
                            new AlertDialog.Builder(SellerProductListingActivity.this,R.style.MyDialogTheme)
                                    .setTitle("Alert!!!")
                                    .setMessage("Are you sure you want to do non live this product?")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            if (Utils.isNetwork(SellerProductListingActivity.this)) {
                                                liveProduct(model.getProductId(), "1");
//                                            getFavList(true);
                                            } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                                Utils.InternetAlertDialog(SellerProductListingActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                                            }
                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();*/
                        }
                    }
                });
                layoutBinding.cardDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(SellerProductListingActivity.this);
                        dialog.setContentView(R.layout.delete_popup);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        TextView txt_title=dialog.findViewById(R.id.txt_title);
                        TextView txt_alert=dialog.findViewById(R.id.txt_alert);
                        ImageView img_delete=dialog.findViewById(R.id.img_delete);
                        img_delete.setVisibility(View.GONE);
                        txt_alert.setVisibility(View.VISIBLE);
                        Button yes = dialog.findViewById(R.id.yes);
                        Button no = dialog.findViewById(R.id.no);
                        txt_alert.setText("Alert!!!");
                        txt_title.setText("Are you sure you want to delete this product?");

                        yes.setOnClickListener(v -> {
                            if (Utils.isNetwork(SellerProductListingActivity.this)) {
                                deleteProduct(model.getProductId());
                                dialog.dismiss();
//                                            getFavList(true);
                            } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                Utils.InternetAlertDialog(SellerProductListingActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                            }
                        });

                        no.setOnClickListener(view1 -> {
                            dialog.dismiss();
                        } );

                        dialog.show();
                    }
                });
              /*  layoutBinding.cardDelete.setOnClickListener(view ->
                        new AlertDialog.Builder(SellerProductListingActivity.this)
                        .setTitle("Alert!!!")
                        .setMessage("Are you sure you want to delete this product?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                if (Utils.isNetwork(SellerProductListingActivity.this)) {
                                    deleteProduct(model.getProductId());
                                    dialog.dismiss();
//                                            getFavList(true);
                                } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                    Utils.InternetAlertDialog(SellerProductListingActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                                }
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show());*/
            }

            private void deleteProduct(String productId) {
                UserProfileRequestModel model = new UserProfileRequestModel();
                model.setUserId(SharedPref.getVal(SellerProductListingActivity.this, SharedPref.user_id));
                model.setProduct_id(productId);

                ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
                client.deleteproduct(model).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                Toast.makeText(SellerProductListingActivity.this, "Product Deleted Successfully", Toast.LENGTH_SHORT).show();
                                getProductList(false);
                            } else {
                                Toast.makeText(SellerProductListingActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SellerProductListingActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_FORBIDDEN:
                                    Toast.makeText(SellerProductListingActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                    Toast.makeText(SellerProductListingActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_BAD_REQUEST:
                                    Toast.makeText(SellerProductListingActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(SellerProductListingActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SellerProductListingActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            private void liveProduct(String productId, String isActive) {
                dialog.show();
                UserProfileRequestModel model = new UserProfileRequestModel();
                model.setProduct_id(productId);
                model.setIsActive(isActive);

                ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
                client.productstatusChange(model).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                Toast.makeText(SellerProductListingActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                                if (isActive.equals("1")) {
                                    layoutBinding.switchLiveStatus.setOn(false);
                                } else {
                                    layoutBinding.switchLiveStatus.setOn(true);
                                }
                            } else {
                                Toast.makeText(SellerProductListingActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SellerProductListingActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_FORBIDDEN:
                                    Toast.makeText(SellerProductListingActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                    Toast.makeText(SellerProductListingActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_BAD_REQUEST:
                                    Toast.makeText(SellerProductListingActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(SellerProductListingActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SellerProductListingActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}