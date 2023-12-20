package com.pepdeal.in.activity;

import static android.view.Gravity.CENTER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityLeadsBinding;
import com.pepdeal.in.databinding.InterestItemLayoutBinding;
import com.pepdeal.in.databinding.ItemMessageUsersListLayoutBinding;
import com.pepdeal.in.model.messagemodel.MessageUsersListModel;
import com.pepdeal.in.model.messagemodel.ShopLeadModel;
import com.pepdeal.in.model.messagemodel.UserLeadModel;
import com.pepdeal.in.model.requestModel.SellerTicketStatusModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.sellerwiseticketmodel.SellerWiseTicketDataModel;
import com.pepdeal.in.model.ticketmodel.TicketDataModel;

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

public class LeadsActivity extends AppCompatActivity {

    ActivityLeadsBinding binding;
    List<UserLeadModel> userLeadModelList = new ArrayList<>();
    List<ShopLeadModel> sellerLeadModelList = new ArrayList<>();
    String from = "";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_leads);
        binding.setHandler(new ClickHandler());
        from = getIntent().getStringExtra("from");

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");

        if (Utils.isNetwork(LeadsActivity.this)) {
            userFlagUpdate(false);
           /* if (from.equals("user"))
                getLeadsForSeller(true);
            else
                getLeadsForUser(true);*/

            //   updateLeadCount(false);
        } else {
            binding.lnrData.setVisibility(View.GONE);
            Utils.InternetAlertDialog(LeadsActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
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


    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isNetwork(LeadsActivity.this)) {
            userFlagUpdate(false);
           /* if (from.equals("user"))
                getLeadsForSeller(true);
            else
                getLeadsForUser(true);*/

            //   updateLeadCount(false);
        } else {
            binding.lnrData.setVisibility(View.GONE);
            Utils.InternetAlertDialog(LeadsActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }
    }

    private void getLeadsForUser(boolean isLoading) {
        if (isLoading)
            showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId("");
        model.setShop_id(SharedPref.getVal(LeadsActivity.this, SharedPref.shop_id));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.chatListsponse(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<UserLeadModel>>() {
                        }.getType();
                        userLeadModelList = new ArrayList<>();
                        userLeadModelList.addAll(gson.fromJson(jsonObject.getString("data"), listType));
                        //  Collections.reverse(userLeadModelList);
                        if (userLeadModelList.size() > 0) {
                            binding.recList.setLayoutManager(new LinearLayoutManager(LeadsActivity.this));
                            binding.recList.setAdapter(new LeadsAdapter("shop"));

                            binding.lnrData.setVisibility(View.VISIBLE);
                            binding.lnrNoData.setVisibility(View.GONE);
                        } else {
                            binding.lnrData.setVisibility(View.GONE);
                            binding.lnrNoData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.lnrData.setVisibility(View.GONE);
                        binding.lnrNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    binding.lnrData.setVisibility(View.GONE);
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
                            Toast.makeText(LeadsActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(LeadsActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(LeadsActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(LeadsActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(LeadsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LeadsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateLeadCount(boolean isLoading) {
        if (isLoading)
            showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        if (from.equals("user")) {
            model.setUserId(SharedPref.getVal(LeadsActivity.this, SharedPref.user_id));
            model.setShop_id("");
        } else {
            model.setUserId("");
            model.setShop_id(SharedPref.getVal(LeadsActivity.this, SharedPref.shop_id));
        }

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.msgscountStatuschange(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");

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
                            Toast.makeText(LeadsActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(LeadsActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(LeadsActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(LeadsActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(LeadsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LeadsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLeadsForSeller(boolean isLoading) {
        if (isLoading)
            showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(LeadsActivity.this, SharedPref.user_id));
        model.setShop_id("");

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.chatListsponse(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<ShopLeadModel>>() {
                        }.getType();
                        sellerLeadModelList = new ArrayList<>();
                        sellerLeadModelList.addAll(gson.fromJson(jsonObject.getString("data"), listType));
                        // Collections.reverse(sellerLeadModelList);
                        if (sellerLeadModelList.size() > 0) {
                            binding.recList.setLayoutManager(new LinearLayoutManager(LeadsActivity.this));
                            binding.recList.setAdapter(new LeadsAdapter("user"));

                            binding.lnrData.setVisibility(View.VISIBLE);
                            binding.lnrNoData.setVisibility(View.GONE);
                        } else {
                            binding.lnrData.setVisibility(View.GONE);
                            binding.lnrNoData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.lnrData.setVisibility(View.GONE);
                        binding.lnrNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    binding.lnrData.setVisibility(View.GONE);
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
                            Toast.makeText(LeadsActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(LeadsActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(LeadsActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(LeadsActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(LeadsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LeadsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void userFlagUpdate(boolean isLoading) {
        if (isLoading)
            showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        if (from.equals("user")) {
            model.setUserId(SharedPref.getVal(LeadsActivity.this, SharedPref.user_id));
            model.setShop_id("");
        } else {
            model.setUserId("");
            model.setShop_id(SharedPref.getVal(LeadsActivity.this, SharedPref.shop_id));
        }

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.userFlagUpdate(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (Utils.isNetwork(LeadsActivity.this)) {
                        if (from.equals("user"))
                            getLeadsForSeller(true);
                        else
                            getLeadsForUser(true);
                    } else {
                        binding.lnrData.setVisibility(View.GONE);
                        Utils.InternetAlertDialog(LeadsActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
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
                            Toast.makeText(LeadsActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(LeadsActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(LeadsActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(LeadsActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(LeadsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LeadsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public class LeadsAdapter extends RecyclerView.Adapter<LeadsAdapter.ViewHolder> {
        String from;

        public LeadsAdapter(String from) {
            this.from = from;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            InterestItemLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(LeadsActivity.this),
                    R.layout.interest_item_layout, parent, false);
            return new ViewHolder(layoutBinding);
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (from.equals("user")) {
                ShopLeadModel model = sellerLeadModelList.get(position);
                holder.sellerBind(model, position);
            } else {
                UserLeadModel model = userLeadModelList.get(position);
                holder.userBind(model, position);
            }
        }

        @Override
        public int getItemCount() {
            if (from.equals("user")) {
                return sellerLeadModelList.size();
            } else {
                return userLeadModelList.size();

            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            InterestItemLayoutBinding layoutBinding;

            public ViewHolder(@NonNull InterestItemLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void sellerBind(ShopLeadModel model, int position) {
                layoutBinding.lnrResponse.setVisibility(View.GONE);
                layoutBinding.imgMore.setVisibility(View.GONE);

                layoutBinding.txtName.setText(model.getShopName());
                layoutBinding.txtMsg.setText("Inquire to learn more about the " + model.getShopName() + "'s products. Please send details.");

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat formatted = new SimpleDateFormat("dd MMM yyyy");
                try {
                    Date date = simpleDateFormat.parse(model.getCreated());
                    String dateFormatted = formatted.format(date);
                    layoutBinding.txtDate.setText(model.getShopAddress2() + "-" + dateFormatted);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (model.getFlag().equals("1")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.blue1), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("New");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.blue1));
                    updateStatus(model.getId(), "2", SharedPref.getVal(LeadsActivity.this, SharedPref.user_id), "", false);
                } else if (model.getSeller_flag().equals("1")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.purple_200), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Read");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.purple_200));
                } else if (model.getSeller_flag().equals("2")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.purple_200), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Read");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.purple_200));
                } else if (model.getSeller_flag().equals("3")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.green_light), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Interested");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.green_light));
                } else if (model.getSeller_flag().equals("4")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.errorColor), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Not Interested");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.errorColor));
                } else if (model.getSeller_flag().equals("0")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Completed");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.green));
                }
                layoutBinding.cardMsg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showDeleteDialog(model.getId());
                        return true;
                    }
                });

            }

            public void userBind(UserLeadModel model, int position) {

                layoutBinding.txtName.setText(model.getUsername());
                layoutBinding.txtMsg.setText("Inquire to know more about the your products. Please send details.");

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat formatted = new SimpleDateFormat("dd MMM yyyy");
                try {
                    Date date = simpleDateFormat.parse(model.getCreated());
                    String dateFormatted = formatted.format(date);
                    layoutBinding.txtDate.setText(dateFormatted);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (model.getSeller_flag().equals("1")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.blue1), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("New");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.blue1));
                    layoutBinding.imgMore.setVisibility(View.VISIBLE);
                    layoutBinding.lnrResponse.setVisibility(View.VISIBLE);
                    updateStatus(model.getId(), "2", "", SharedPref.getVal(LeadsActivity.this, SharedPref.shop_id), false);

                } else if (model.getSeller_flag().equals("2")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.purple_200), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Read");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.purple_200));
                    layoutBinding.imgMore.setVisibility(View.VISIBLE);
                    layoutBinding.lnrResponse.setVisibility(View.VISIBLE);
                } else if (model.getSeller_flag().equals("3")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.green_light), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Interested");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.green_light));
                    layoutBinding.imgMore.setVisibility(View.VISIBLE);
                    layoutBinding.lnrResponse.setVisibility(View.VISIBLE);
                } else if (model.getSeller_flag().equals("4")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.errorColor), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Not Interested");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.errorColor));
                    layoutBinding.imgMore.setVisibility(View.VISIBLE);
                    layoutBinding.lnrResponse.setVisibility(View.VISIBLE);
                } else if (model.getSeller_flag().equals("0")) {
                    layoutBinding.viewStatus.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
                    layoutBinding.txtStatus.setText("Completed");
                    layoutBinding.txtStatus.setTextColor(getResources().getColor(R.color.green));
                    layoutBinding.imgMore.setVisibility(View.GONE);
                    layoutBinding.lnrResponse.setVisibility(View.GONE);
                }

                layoutBinding.imgMore.setOnClickListener(view -> {
                    PopupMenu popup = new PopupMenu(LeadsActivity.this, layoutBinding.imgMore);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.popup_menu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.interested:
                                    updateStatus(model.getId(), "3", "", SharedPref.getVal(LeadsActivity.this, SharedPref.shop_id), true);
                                    break;
                                case R.id.notInterested:
                                    updateStatus(model.getId(), "4", "", SharedPref.getVal(LeadsActivity.this, SharedPref.shop_id), true);
                                    break;
                                case R.id.dealCompleted:
                                    updateStatus(model.getId(), "0", "", SharedPref.getVal(LeadsActivity.this, SharedPref.shop_id), true);
                                    break;
                            }
                            return true;
                        }
                    });

                    popup.show();
                });

                layoutBinding.lnrCall.setOnClickListener(view -> {
                    updateStatus(model.getId(), "2", "", SharedPref.getVal(LeadsActivity.this, SharedPref.shop_id), false);

                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getMobileNo()));
                    startActivity(intent);
                });
                layoutBinding.lnrMessage.setOnClickListener(view -> showMessageDialog(model.getId(), model.getMobileNo()));
                layoutBinding.lnrEmail.setOnClickListener(view -> {
                    updateStatus(model.getId(), "2", "", SharedPref.getVal(LeadsActivity.this, SharedPref.shop_id), false);

                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{model.getEmailId()});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    intent.setData(Uri.parse("mailto:"));

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(LeadsActivity.this, "There is no application that support this action",
                                Toast.LENGTH_SHORT).show();
                    }

                });


            }

            private void updateStatus(String id, String status, String userid, String shopid, boolean isLoading) {
                if (isLoading)
                    dialog.show();
                SellerTicketStatusModel model = new SellerTicketStatusModel();
                model.setId(id);
                model.setFlag(status);
                model.setUserId(userid);
                model.setShopId(shopid);


                ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
                client.userstatusChange(model).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                if (isLoading)
                                    Toast.makeText(LeadsActivity.this, "Status changed successfully", Toast.LENGTH_SHORT).show();
                                if (from.equals("user"))
                                    getLeadsForSeller(false);
                                else
                                    getLeadsForUser(false);
                            } else {
                                if (isLoading)
                                    Toast.makeText(LeadsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(LeadsActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_FORBIDDEN:
                                    Toast.makeText(LeadsActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                    Toast.makeText(LeadsActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpsURLConnection.HTTP_BAD_REQUEST:
                                    Toast.makeText(LeadsActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(LeadsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LeadsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            private void showDeleteDialog(String id) {
                Dialog dialog = new Dialog(LeadsActivity.this);
                dialog.setContentView(R.layout.delete_popup);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


                Button yes = dialog.findViewById(R.id.yes);
                Button no = dialog.findViewById(R.id.no);

                yes.setOnClickListener(v -> {
                    deleteMessage(id);
                    dialog.dismiss();
                });

                no.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
            }

            private void showMessageDialog(String id, String number) {
                Dialog dialog1 = new Dialog(LeadsActivity.this);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.setContentView(R.layout.send_message_dialog_layout);
                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog1.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                dialog1.getWindow().setGravity(CENTER);
                dialog1.getWindow().setDimAmount((float) 0.8);
                dialog1.setCancelable(true);
                dialog1.setCanceledOnTouchOutside(true);

                LinearLayoutCompat lnr_sms, lnr_whatsapp;
                lnr_sms = dialog1.findViewById(R.id.lnr_sms);
                lnr_whatsapp = dialog1.findViewById(R.id.lnr_whatsapp);

                lnr_sms.setOnClickListener(view -> {
                    updateStatus(id, "2", "", SharedPref.getVal(LeadsActivity.this, SharedPref.shop_id), false);
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:" + number));
//                    sendIntent.putExtra("address", number);
                    startActivity(sendIntent);
                });

                lnr_whatsapp.setOnClickListener(view -> {
                    try {
                        updateStatus(id, "2", "", SharedPref.getVal(LeadsActivity.this, SharedPref.shop_id), false);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //  intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=+91" + usernumber + "&text=" + wpmsg));
                        intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=+91" + number));

                        startActivity(intent);

                    } catch (Exception e) {

                        e.printStackTrace();

                        Toast.makeText(getApplicationContext(), "it may be you don't have whatsapp", Toast.LENGTH_LONG).show();
                    }
                });

                dialog1.show();

            }
        }

        private void deleteMessage(String msgid) {
            showShimmer();

            UserLeadModel model = new UserLeadModel();
            //   model.setUserId(SharedPref.getVal(LeadsActivity.this, SharedPref.user_id));
            model.setId(msgid);

            ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
            client.deleteMessage(model).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String code = jsonObject.getString("code");
                        if (code.equals("200")) {
                            Toast.makeText(LeadsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                            /*  if (from.equals("user"))
                                getLeadsForSeller(true);
                            else
                                getLeadsForUser(true);*/
                        } else {
                            Toast.makeText(LeadsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(LeadsActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_FORBIDDEN:
                                Toast.makeText(LeadsActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                                Toast.makeText(LeadsActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                                break;
                            case HttpsURLConnection.HTTP_BAD_REQUEST:
                                Toast.makeText(LeadsActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(LeadsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LeadsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}