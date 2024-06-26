package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
import com.pepdeal.in.model.requestModel.SellerTicketStatusModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.sellerwiseticketmodel.SellerWiseTicketDataModel;
import com.pepdeal.in.model.ticketmodel.TicketDataModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
    List<SellerWiseTicketDataModel> productDataModelList = new ArrayList<>();
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_ticket_list);
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
        if (Utils.isNetwork(SellerTicketListActivity.this)) {
            getTicketsList(true);
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

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void getTicketsList(boolean isLoading) {
        if (isLoading)
            showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(SellerTicketListActivity.this, SharedPref.user_id));
        model.setShop_id(SharedPref.getVal(SellerTicketListActivity.this, SharedPref.shop_id));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.sellerwiseTicket(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<SellerWiseTicketDataModel>>() {
                        }.getType();
                        productDataModelList = new ArrayList<>();
                        productDataModelList.addAll(gson.fromJson(jsonObject.getString("Data"), listType));

                        if (productDataModelList.size() > 0) {
                            Collections.reverse(productDataModelList);
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
            SellerWiseTicketDataModel model = productDataModelList.get(position);
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

            public void bind(SellerWiseTicketDataModel model, int position) {

                Glide.with(SellerTicketListActivity.this).load(model.getProductImage())
                        .error(R.drawable.loader).placeholder(R.drawable.loader).into(layoutBinding.imgProductImage);
                layoutBinding.txtProductName.setText(model.getProductName());
                layoutBinding.txtUserName.setText(model.getCustName());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat formatted = new SimpleDateFormat("dd MMM yyyy");
                try {
                    Date date = simpleDateFormat.parse(model.getCreatedAt());
                    String dateFormatted = formatted.format(date);
                    layoutBinding.txtDate.setText("Date : " + dateFormatted);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(model.getOnCall().equalsIgnoreCase("1")) {
                    layoutBinding.lnrOffer.setVisibility(View.GONE);
                  //  layoutBinding.txtActualPrice.setVisibility(View.GONE);
                    layoutBinding.txtActualPrice.setText("On call");
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
                /*Ticket Status 0 = Delivered , 1 = Approved , 2 = Waiting ,3 =Rejected*/
                if (model.getTicketStatus().equals("0")) {
                    layoutBinding.txtStatus.setText("Status : Delivered");
                    layoutBinding.txtStatus.setVisibility(View.VISIBLE);
                    layoutBinding.cardReject.setVisibility(View.GONE);
                    layoutBinding.cardConfirm.setVisibility(View.GONE);
                } else if (model.getTicketStatus().equals("1")) {
                    layoutBinding.txtStatus.setText("Status : Confirmed");
                    layoutBinding.txtStatus.setVisibility(View.VISIBLE);
                    layoutBinding.cardReject.setVisibility(View.GONE);
                    layoutBinding.cardConfirm.setVisibility(View.VISIBLE);
                    layoutBinding.txtConfirm.setText("Delivered");
                } else if (model.getTicketStatus().equals("2")) {
                    layoutBinding.txtStatus.setVisibility(View.GONE);
                    layoutBinding.cardReject.setVisibility(View.VISIBLE);
                    layoutBinding.cardConfirm.setVisibility(View.VISIBLE);
                    layoutBinding.txtConfirm.setText("Confirm");
                } else if (model.getTicketStatus().equals("3")) {
                    layoutBinding.txtStatus.setText("Status : Rejected");
                    layoutBinding.cardReject.setVisibility(View.GONE);
                    layoutBinding.cardConfirm.setVisibility(View.GONE);
                }

                /*layoutBinding.cardDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(new Intent(SellerTicketListActivity.this, AddProductActivity.class));
                        intent.putExtra("product_id", model.getProductId());
                        intent.putExtra("from", "edit");
                        startActivity(intent);
                    }
                });*/
                /*layoutBinding.cardDetails.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showDeleteDialog(model.getTicketId());
                        return true;
                    }
                });*/
                layoutBinding.cardConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getTicketStatus().equals("2")) {
                            Dialog dialog = new Dialog(SellerTicketListActivity.this);
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
                            txt_title.setText("Are you sure you want to confirm this ticket?");

                            yes.setOnClickListener(v -> {
                                // Continue with delete operation
                                if (Utils.isNetwork(SellerTicketListActivity.this)) {
                                    updateTicketStatus(model.getTicketId(), "1");
                                    dialog.dismiss();
//                                            getFavList(true);
                                } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                    Utils.InternetAlertDialog(SellerTicketListActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                                }
                            });

                            no.setOnClickListener(v -> dialog.dismiss());

                            dialog.show();
/*
                            new AlertDialog.Builder(SellerTicketListActivity.this,R.style.MyDialogTheme)
                                    .setTitle("Alert!!!")
                                    .setMessage("Are you sure you want to confirm this ticket?")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            if (Utils.isNetwork(SellerTicketListActivity.this)) {
                                                updateTicketStatus(model.getTicketId(), "1");
//                                            getFavList(true);
                                            } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                                Utils.InternetAlertDialog(SellerTicketListActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                                            }
                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();*/
                        } else if (model.getTicketStatus().equals("1")) {

                            Dialog dialog = new Dialog(SellerTicketListActivity.this);
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
                            txt_title.setText("Are you sure you want to delivered this ticket?");

                            yes.setOnClickListener(v -> {
                                // Continue with delete operation
                                if (Utils.isNetwork(SellerTicketListActivity.this)) {
                                    updateTicketStatus(model.getTicketId(), "0");
                                    dialog.dismiss();
//                                            getFavList(true);
                                } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                    Utils.InternetAlertDialog(SellerTicketListActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                                }
                            });

                            no.setOnClickListener(v -> dialog.dismiss());

                            dialog.show();



                           /* new AlertDialog.Builder(SellerTicketListActivity.this,R.style.MyDialogTheme)
                                    .setTitle("Alert!!!")
                                    .setMessage("Are you sure you want to delivered this ticket?")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            if (Utils.isNetwork(SellerTicketListActivity.this)) {
                                                updateTicketStatus(model.getTicketId(), "0");
//                                            getFavList(true);
                                            } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                                Utils.InternetAlertDialog(SellerTicketListActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                                            }
                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();*/
                        }
                    }
                });

                layoutBinding.cardReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(SellerTicketListActivity.this);
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
                        txt_title.setText("Are you sure you want to reject this ticket?");

                        yes.setOnClickListener(v -> {
                            // Continue with delete operation
                            if (Utils.isNetwork(SellerTicketListActivity.this)) {
                                updateTicketStatus(model.getTicketId(), "3");
                                dialog.dismiss();
//                                            getFavList(true);
                            } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                Utils.InternetAlertDialog(SellerTicketListActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                            }
                        });

                        no.setOnClickListener(v -> dialog.dismiss());

                        dialog.show();


                       /* new AlertDialog.Builder(SellerTicketListActivity.this,R.style.MyDialogTheme)
                                .setTitle("Alert!!!")
                                .setMessage("Are you sure you want to reject this ticket?")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        if (Utils.isNetwork(SellerTicketListActivity.this)) {
                                            updateTicketStatus(model.getTicketId(), "3");
//                                            getFavList(true);
                                        } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                            Utils.InternetAlertDialog(SellerTicketListActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                                        }
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .show();*/
                    }
                });
            }

            private void updateTicketStatus(String ticketId, String ticketStatus) {
                dialog.show();
                SellerTicketStatusModel model = new SellerTicketStatusModel();
                model.setUserId(SharedPref.getVal(SellerTicketListActivity.this, SharedPref.user_id));
                model.setTicketId(ticketId);
                model.setTicketStatus(ticketStatus);

                ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
                client.sellerticketStatus(model).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                Toast.makeText(SellerTicketListActivity.this, "Status changed successfully", Toast.LENGTH_SHORT).show();
                                getTicketsList(false);
                            } else {
                                Toast.makeText(SellerTicketListActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
        }
    }
    private void showDeleteDialog(String id) {
        Dialog dialog = new Dialog(SellerTicketListActivity.this);
        dialog.setContentView(R.layout.delete_popup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        Button yes = dialog.findViewById(R.id.yes);
        Button no = dialog.findViewById(R.id.no);


        yes.setOnClickListener(v -> {
            deleteTicketAPI(id);
            dialog.dismiss();
        });

        no.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void deleteTicketAPI(String ticketid) {
        showShimmer();
        String userIdStr = SharedPref.getVal(SellerTicketListActivity.this, SharedPref.user_id);

        TicketDataModel model = new TicketDataModel();
        model.setUserid(SharedPref.getVal(SellerTicketListActivity.this, SharedPref.user_id));
        model.setTicketId(ticketid);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.deleteTicket(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(SellerTicketListActivity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                        getTicketsList(true);
                    } else {
                        Toast.makeText(SellerTicketListActivity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    hideShimmer();
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


}