package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.adapter.ProductAdapter;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivitySellerShopServicesListBinding;
import com.pepdeal.in.databinding.ItemCategoryHomeLayoutBinding;
import com.pepdeal.in.databinding.ItemSellerShopServiceLayoutBinding;
import com.pepdeal.in.fragment.FavoriteFragment;
import com.pepdeal.in.fragment.SuperShopFragment;
import com.pepdeal.in.fragment.TicketFragment;
import com.pepdeal.in.model.UsersHomeTabModel;
import com.pepdeal.in.model.productdetailsmodel.ProductDetailsDataModel;
import com.pepdeal.in.model.requestModel.AddShopRequestModel;
import com.pepdeal.in.model.requestModel.SellerServiceRequestModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopDetailsDataModel;
import com.pepdeal.in.model.shopdetailsmodel.ShopServiceAvailableModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class SellerShopServicesListActivity extends AppCompatActivity {

    ActivitySellerShopServicesListBinding binding;
    String codService = "0", doorStepService = "0", homeDeliveryService = "0", liveDemoService = "0", offersService = "0", bargainService = "0";
    ProgressDialog dialog;
    ShopServiceAvailableModel serviceAvailableModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_shop_services_list);
        binding.setHandler(new ClickHandler());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        if (Utils.isNetwork(SellerShopServicesListActivity.this)) {
            getServiceList(true);
        } else {
            Utils.InternetAlertDialog(SellerShopServicesListActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }
        switchHandler();
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }

        public void onSaveService(View view) {
            if (Utils.isNetwork(SellerShopServicesListActivity.this)) {
                updateService();
            } else {
                Utils.InternetAlertDialog(SellerShopServicesListActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
            }
        }
    }

    private void switchHandler() {
        binding.switchCod.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                codService = "1";
            } else {
                codService = "0";
            }
        });

        binding.switchDoorStep.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                doorStepService = "1";
            } else {
                doorStepService = "0";
            }
        });

        binding.switchHomeDelivery.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                homeDeliveryService = "1";
            } else {
                homeDeliveryService = "0";

            }
        });

        binding.switchLiveDemo.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                liveDemoService = "1";
            } else {
                liveDemoService = "0";
            }
        });

        binding.switchOffers.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                offersService = "1";
            } else {
                offersService = "0";
            }
        });

        binding.switchBargain.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                bargainService = "1";
            } else {
                bargainService = "0";
            }
        });

    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void updateService() {
        dialog.show();
        SellerServiceRequestModel model = new SellerServiceRequestModel();
        model.setUserId(SharedPref.getVal(SellerShopServicesListActivity.this, SharedPref.user_id));
        model.setShopId(SharedPref.getVal(SellerShopServicesListActivity.this, SharedPref.shop_id));
        model.setCashOnDelivery(codService);
        model.setDoorStep(doorStepService);
        model.setHomeDelivery(homeDeliveryService);
        model.setLiveDemo(liveDemoService);
        model.setOffers(offersService);
        model.setBargain(bargainService);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.sellerServices(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(SellerShopServicesListActivity.this, "Services Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
//                        getServiceList(false);
                    } else {
                        Toast.makeText(SellerShopServicesListActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SellerShopServicesListActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(SellerShopServicesListActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(SellerShopServicesListActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(SellerShopServicesListActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(SellerShopServicesListActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SellerShopServicesListActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


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

    private void getServiceList(boolean isLoading) {
        if (isLoading)
            showShimmer();

        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(SellerShopServicesListActivity.this, SharedPref.user_id));
        model.setShop_id(SharedPref.getVal(SellerShopServicesListActivity.this, SharedPref.shop_id));

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

                        if (serviceAvailableModel.getCashOnDelivery().equals("1")) {
                            binding.switchCod.setChecked(true);
                            codService = "1";
                        } else {
                            binding.switchCod.setChecked(false);
                            codService = "0";
                        }
                        if (serviceAvailableModel.getDoorStep().equals("1")) {
                            binding.switchDoorStep.setChecked(true);
                            doorStepService = "1";
                        } else {
                            binding.switchDoorStep.setChecked(false);
                            doorStepService = "0";
                        }
                        if (serviceAvailableModel.getHomeDelivery().equals("1")) {
                            binding.switchHomeDelivery.setChecked(true);
                            homeDeliveryService = "1";
                        } else {
                            binding.switchHomeDelivery.setChecked(false);
                            homeDeliveryService = "0";
                        }
                        if (serviceAvailableModel.getLiveDemo().equals("1")) {
                            binding.switchLiveDemo.setChecked(true);
                            liveDemoService = "1";
                        } else {
                            binding.switchLiveDemo.setChecked(false);
                            liveDemoService = "0";
                        }
                        if (serviceAvailableModel.getOffers().equals("1")) {
                            binding.switchOffers.setChecked(true);
                            offersService = "1";
                        } else {
                            binding.switchOffers.setChecked(false);
                            offersService = "0";
                        }
                        if (serviceAvailableModel.getBargain().equals("1")) {
                            binding.switchBargain.setChecked(true);
                            bargainService = "1";
                        } else {
                            binding.switchBargain.setChecked(false);
                            bargainService = "0";
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
                            Toast.makeText(SellerShopServicesListActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(SellerShopServicesListActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(SellerShopServicesListActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(SellerShopServicesListActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(SellerShopServicesListActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SellerShopServicesListActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}