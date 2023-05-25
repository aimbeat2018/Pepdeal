package com.pepdeal.in.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.activity.HomeActivity;
import com.pepdeal.in.activity.LeadsActivity;
import com.pepdeal.in.activity.ProductDetailsActivity;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.FragmentHomeBinding;
import com.pepdeal.in.databinding.FragmentTicketBinding;
import com.pepdeal.in.databinding.ItemHomeShopsListBinding;
import com.pepdeal.in.databinding.ItemProductListLayoutBinding;
import com.pepdeal.in.databinding.ItemTicketLayoutBinding;
import com.pepdeal.in.model.UsersHomeTabModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.ticketmodel.TicketDataModel;

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

public class TicketFragment extends Fragment {

    Activity activity;
    FragmentTicketBinding binding;
    List<TicketDataModel> productDataModelList = new ArrayList<>();

 /*   public TicketFragment(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ticket, container, false);
        activity = (HomeActivity) getActivity();
      /*  binding.recList.setLayoutManager(new LinearLayoutManager(activity));
        binding.recList.setAdapter(new ProductAdapter());*/

        if (Utils.isNetwork(activity)) {
            getTicketsList();
        } else {
            binding.lnrMainLayout.setVisibility(View.GONE);
            Utils.InternetAlertDialog(activity, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }
        return binding.getRoot();
    }

    private void showShimmer() {
        binding.lnrData.setVisibility(View.GONE);
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void hideShimmer() {
        binding.lnrData.setVisibility(View.VISIBLE);
        binding.shimmerLayout.setVisibility(View.GONE);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void getTicketsList() {
        showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(activity, SharedPref.user_id));

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
                            binding.recList.setLayoutManager(new LinearLayoutManager(activity));
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

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemTicketLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.item_ticket_layout, parent, false);
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
            ItemTicketLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemTicketLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind(TicketDataModel model, int position) {

                Glide.with(activity).load(model.getProductImage())
                        .error(R.drawable.loader).placeholder(R.drawable.loader).into(layoutBinding.imgProductImage);
                layoutBinding.txtProductName.setText(model.getProductName());
                layoutBinding.txtShopName.setText(model.getShopName());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat formatted = new SimpleDateFormat("dd MMM yyyy");
                try {
                    Date date = simpleDateFormat.parse(model.getCreated());
                    String dateFormatted = formatted.format(date);
                    layoutBinding.txtDate.setText("Date : " + dateFormatted);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(model.getOnCall().equalsIgnoreCase("1")) {
                    layoutBinding.lnrOffer.setVisibility(View.GONE);
                    layoutBinding.txtActualPrice.setVisibility(View.GONE);
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
                }

                layoutBinding.cardDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(new Intent(activity, ProductDetailsActivity.class));
                        intent.putExtra("product_id", model.getProductId());
                        activity.startActivity(intent);
                    }
                });

                layoutBinding.cardDetails.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showDeleteDialog(model.getTicketId());
                        return true;
                    }
                });
            }
            private void showDeleteDialog(String id) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.delete_popup);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


                Button yes = dialog.findViewById(R.id.yes);
                Button no = dialog.findViewById(R.id.no);


                yes.setOnClickListener(v -> {
                    Toast.makeText(getActivity(),"Call API here",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

                no.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
            }
        }
    }
}