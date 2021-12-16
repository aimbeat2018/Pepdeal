package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
import com.pepdeal.in.databinding.ActivitySellerTicketListBinding;
import com.pepdeal.in.databinding.ItemCategoryListLayoutBinding;
import com.pepdeal.in.databinding.ItemSellerTicketLayoutBinding;
import com.pepdeal.in.databinding.ItemTicketLayoutBinding;
import com.pepdeal.in.fragment.TicketFragment;
import com.pepdeal.in.model.requestModel.AddProductCategoryResponseModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.ticketmodel.TicketDataModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class SellerTicketListActivity extends AppCompatActivity {

    ActivitySellerTicketListBinding binding;
    List<TicketDataModel> productDataModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_ticket_list);
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
        if (Utils.isNetwork(SellerTicketListActivity.this)) {
            getTicketsList();
        } else {
            binding.lnrMainLayout.setVisibility(View.GONE);
            Utils.InternetAlertDialog(SellerTicketListActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
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

    private void getTicketsList() {
        showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(SellerTicketListActivity.this, SharedPref.user_id));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.ticketList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<TicketDataModel>>() {
                        }.getType();
                        productDataModelList = new ArrayList<>();
                        productDataModelList.addAll(gson.fromJson(jsonObject.getString("data"), listType));

                        if (productDataModelList.size() > 0) {
                            binding.recList.setLayoutManager(new LinearLayoutManager(SellerTicketListActivity.this));
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
                            Toast.makeText(SellerTicketListActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(SellerTicketListActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(SellerTicketListActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(SellerTicketListActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(SellerTicketListActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SellerTicketListActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemSellerTicketLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(SellerTicketListActivity.this),
                    R.layout.item_seller_ticket_layout, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TicketDataModel model = productDataModelList.get(position);
            holder.bind(model, position);
        }

        @Override
        public int getItemCount() {
            return productDataModelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemSellerTicketLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemSellerTicketLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind(TicketDataModel model, int position) {

                Glide.with(SellerTicketListActivity.this).load(model.getProductImage())
                        .error(R.drawable.loader).placeholder(R.drawable.loader).into(layoutBinding.imgProductImage);
                layoutBinding.txtProductName.setText(model.getProductName());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat formatted = new SimpleDateFormat("dd MMM yyyy");
                try {
                    Date date = simpleDateFormat.parse(model.getTicketCreatedAt());
                    String dateFormatted = formatted.format(date);
                    layoutBinding.txtDate.setText("Date : " + dateFormatted);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

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

                /*Ticket Status 0 = Delivered , 1 = Approved , 2 = Waiting ,3 =Rejected*/
               /* if (model.getTicketStatus().equals("0")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Delivered");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.green));
                } else if (model.getTicketStatus().equals("1")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.purple_200), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Confirmed");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.purple_200));
                } else if (model.getTicketStatus().equals("2")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.errorColor), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Waiting");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.errorColor));
                } else if (model.getTicketStatus().equals("3")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.errorColor), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Rejected");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.errorColor));
                }*/

                layoutBinding.cardDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(new Intent(SellerTicketListActivity.this, AddProductActivity.class));
                        intent.putExtra("product_id", model.getProductId());
                        intent.putExtra("from", "edit");
                        startActivity(intent);
                    }
                });
            }
        }
    }
}