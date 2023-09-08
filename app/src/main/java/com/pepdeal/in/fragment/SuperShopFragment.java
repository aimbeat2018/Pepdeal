package com.pepdeal.in.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.activity.HomeActivity;
import com.pepdeal.in.activity.MessageChatActivity;
import com.pepdeal.in.activity.SellerTicketListActivity;
import com.pepdeal.in.activity.ShopDetailsActivity;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.FragmentSuperShopBinding;
import com.pepdeal.in.databinding.ItemHomeShopsListBinding;
import com.pepdeal.in.databinding.ItemSuperShopsListBinding;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.supershopmodel.SuperShopDataModel;

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


public class SuperShopFragment extends Fragment {


    Activity activity;
    FragmentSuperShopBinding binding;

    /*public SuperShopFragment(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
    }
*/

    List<SuperShopDataModel> superShopDataModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_super_shop, container, false);
        activity = (HomeActivity) getActivity();

      /*  binding.recList.setLayoutManager(new LinearLayoutManager(activity));
        binding.recList.setAdapter(new ShopAdapter());*/

        if (Utils.isNetwork(activity)) {
            getSuperShopList(true);
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

    private void getSuperShopList(boolean isLoading) {
        if (isLoading)
            showShimmer();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(activity, SharedPref.user_id));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.supershopList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<SuperShopDataModel>>() {
                        }.getType();
                        superShopDataModelList = new ArrayList<>();
                        superShopDataModelList.addAll(gson.fromJson(jsonObject.getString("data"), listType));

                        if (superShopDataModelList.size() > 0) {
                            Collections.reverse(superShopDataModelList);
                            binding.recList.setLayoutManager(new LinearLayoutManager(activity));
                            binding.recList.setAdapter(new ShopAdapter());

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

    public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemSuperShopsListBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.item_super_shops_list, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SuperShopDataModel model = superShopDataModelList.get(position);
            holder.bind(model, position);
        }

        @Override
        public int getItemCount() {
            return superShopDataModelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemSuperShopsListBinding layoutBinding;

            public ViewHolder(@NonNull ItemSuperShopsListBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
                layoutBinding.recProduct.setVisibility(View.GONE);
                layoutBinding.imgDelete.setVisibility(View.VISIBLE);
            }

            public void bind(SuperShopDataModel model, int position) {
                layoutBinding.txtName.setText(model.getShopName());
                if(model.getFontColorName().equals(""))
                {
                    layoutBinding.txtName.setTextColor(Color.parseColor("#000000"));
                }
                else{
                    layoutBinding.txtName.setTextColor(Color.parseColor(model.getFontColorName()));
                }


                Typeface typeface = null;
                if (model.getFontStyleId().equals("1")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.anton_regular);
                } else if (model.getFontStyleId().equals("2")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.berkshireswash_regular);
                } else if (model.getFontStyleId().equals("3")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.brasika_display);
                } else if (model.getFontStyleId().equals("4")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.carterone_regular);
                } else if (model.getFontStyleId().equals("5")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.fredokaone_regular);
                } else if (model.getFontStyleId().equals("6")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.gagalin_regular);
                } else if (model.getFontStyleId().equals("7")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.lato_regular);
                } else if (model.getFontStyleId().equals("8")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.leaguespartan_bold);
                } else if (model.getFontStyleId().equals("9")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.lovelo_black);
                } else if (model.getFontStyleId().equals("10")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.opensans_bold);
                } else if (model.getFontStyleId().equals("11")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.quicksand_bold);
                } else if (model.getFontStyleId().equals("12")) {
                    typeface = ResourcesCompat.getFont(activity, R.font.yesevaone_regular);
                } /*else if (model.getFontStyleId().equals("13")) {
                            typeface = ResourcesCompat.getFont(activity, R.font.roboto_mediumitalic);
                        } else if (model.getFontStyleId().equals("14")) {
                            typeface = ResourcesCompat.getFont(activity, R.font.roboto_regular);
                        } else if (model.getFontStyleId().equals("15")) {
                            typeface = ResourcesCompat.getFont(activity, R.font.roboto_thin);
                        } else if (model.getFontStyleId().equals("16")) {
                            typeface = ResourcesCompat.getFont(activity, R.font.roboto_thinitalic);
                        }*/
                if (!model.getFontStyleId().equals("") || !model.getFontStyleId().equals("0"))
                    layoutBinding.txtName.setTypeface(typeface);
//                layoutBinding.txtAddress.setText(model.getCity() + ", " + model.getState());

                layoutBinding.txtAddress.setText(model.getShopAddress2());
                layoutBinding.txtMobile.setText(model.getShopMobileNo());

                layoutBinding.lnrBack.setBackgroundColor(Color.parseColor(model.getBgColorName()));

                layoutBinding.txtName.setOnClickListener(view -> {
                    startActivity(new Intent(activity, ShopDetailsActivity.class).putExtra("shop_id", model.getShopId()));
                });

                layoutBinding.imgSuperShop.setVisibility(View.GONE);
                layoutBinding.imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.delete_popup);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        TextView txt_title=dialog.findViewById(R.id.txt_title);
                        Button yes = dialog.findViewById(R.id.yes);
                        Button no = dialog.findViewById(R.id.no);
                        txt_title.setText("Are you sure you want to remove shop from super shop?");

                        yes.setOnClickListener(v -> {
                            if (Utils.isNetwork(activity)) {
                                removeSuperShop(model.getSuperId());

//                                            getFavList(true);
                            } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                Utils.InternetAlertDialog(activity, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                            }
                            dialog.dismiss();
                        });

                        no.setOnClickListener(v -> dialog.dismiss());

                        dialog.show();
                    }
                });
             /*   layoutBinding.imgDelete.setOnClickListener(view -> new AlertDialog.Builder(activity,R.style.MyDialogTheme)
                        .setTitle("Alert!!!")
                        .setMessage("Are you sure you want to remove shop from super shop?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                if (Utils.isNetwork(activity)) {
                                    removeSuperShop(model.getSuperId());
//                                            getFavList(true);
                                } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                                    Utils.InternetAlertDialog(activity, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                                }
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show());*/

                layoutBinding.imgMessage.setOnClickListener(view -> startActivity(new Intent(activity, MessageChatActivity.class).putExtra("shop_id", model.getShopId())
                        .putExtra("name", model.getShopName()).putExtra("user_id", SharedPref.getVal(activity, SharedPref.user_id))));

                layoutBinding.lnrMobile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);

                        intent.setData(Uri.parse("tel:" + model.getShopMobileNo()));
                        activity.startActivity(intent);
                    }
                });
            }

            private void removeSuperShop(String shopId) {
                UserProfileRequestModel model = new UserProfileRequestModel();
                model.setUserId(SharedPref.getVal(activity, SharedPref.user_id));
                model.setSuper_id(shopId);

                ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
                client.removeSupershop(model).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                Toast.makeText(activity, "Shop Removed from Super shop", Toast.LENGTH_SHORT).show();
                                getSuperShopList(false);
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