package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.adapter.ProductAdapter;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityMessageChatBinding;
import com.pepdeal.in.databinding.ActivitySellerTicketListBinding;
import com.pepdeal.in.databinding.ItemProductListLayoutBinding;
import com.pepdeal.in.databinding.MessageItemLayoutBinding;
import com.pepdeal.in.firebaseservice.Config;
import com.pepdeal.in.firebaseservice.util.NotificationUtils;
import com.pepdeal.in.model.homemodel.HomeShopDataModel;
import com.pepdeal.in.model.messagemodel.MessageChatModel;
import com.pepdeal.in.model.messagemodel.MessageUsersListModel;
import com.pepdeal.in.model.requestModel.SendMessageRequestModel;
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

public class MessageChatActivity extends AppCompatActivity {

    ActivityMessageChatBinding binding;
    LinearLayoutManager layoutManager;
    String shopId = "", name = "", userId = "";
    List<MessageChatModel> messageChatModelList = new ArrayList<>();
    String msgFlag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message_chat);
        binding.setHandler(new ClickHandler());
        shopId = getIntent().getStringExtra("shop_id");
        name = getIntent().getStringExtra("name");
        userId = getIntent().getStringExtra("user_id");

        binding.txtUserName.setText(name);

        if (Utils.isNetwork(MessageChatActivity.this)) {
            messageCountStatus();
        } else {
            Utils.InternetAlertDialog(MessageChatActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }

    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }

        public void onSendMessage(View view) {
            if (binding.edtMessage.getText().toString().equals("")) {
                Toast.makeText(MessageChatActivity.this, "Enter message", Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.isNetwork(MessageChatActivity.this)) {
                    sendMessage();
                } else {
                    Utils.InternetAlertDialog(MessageChatActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mrefresh, new IntentFilter(Config.msgBroadCast));
        if (Utils.isNetwork(MessageChatActivity.this)) {
            getMessageList(true);
        } else {
//            binding.lnrMainLayout.setVisibility(View.GONE);
            Utils.InternetAlertDialog(MessageChatActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }
    }

    private BroadcastReceiver mrefresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // checking for type intent filter
            if (intent.getAction().equals(Config.msgBroadCast)) {
                NotificationUtils.clearNotifications(MessageChatActivity.this);
                if (Utils.isNetwork(MessageChatActivity.this)) {
                    getMessageList(false);
                } else {
//            binding.lnrMainLayout.setVisibility(View.GONE);
                    Utils.InternetAlertDialog(MessageChatActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mrefresh);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mrefresh);
    }

    private void messageCountStatus() {
        SendMessageRequestModel model = new SendMessageRequestModel();
        if (userId.equals(SharedPref.getVal(MessageChatActivity.this, SharedPref.user_id))) {
            msgFlag = "0";
        } else {
            msgFlag = SharedPref.getVal(MessageChatActivity.this, SharedPref.msgFlag);
        }
        if (msgFlag.equals("0")) {
            model.setUserId(SharedPref.getVal(MessageChatActivity.this, SharedPref.user_id));
            model.setShopId(shopId);
        } else {
            model.setShopId(SharedPref.getVal(MessageChatActivity.this, SharedPref.shop_id));
            model.setUserId(userId);
        }
        model.setMsgFlag(SharedPref.getVal(MessageChatActivity.this, SharedPref.msgFlag));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.msgsCheckingstatus(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                       /* binding.edtMessage.setText("");

                        if (Utils.isNetwork(MessageChatActivity.this)) {
                            getMessageList(false);
                        } else {
//            binding.lnrMainLayout.setVisibility(View.GONE);
                            Utils.InternetAlertDialog(MessageChatActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                        }*/
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(MessageChatActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(MessageChatActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(MessageChatActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(MessageChatActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(MessageChatActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MessageChatActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getMessageList(boolean loading) {
       /* if (loading) {
            showShimmer();
        }*/
        UserProfileRequestModel model = new UserProfileRequestModel();
        if (userId.equals(SharedPref.getVal(MessageChatActivity.this, SharedPref.user_id))) {
            msgFlag = "0";
        } else {
            msgFlag = SharedPref.getVal(MessageChatActivity.this, SharedPref.msgFlag);
        }
        if (msgFlag.equals("0")) {
            model.setUserId(SharedPref.getVal(MessageChatActivity.this, SharedPref.user_id));
            model.setShop_id(shopId);
        } else {
            model.setShop_id(SharedPref.getVal(MessageChatActivity.this, SharedPref.shop_id));
            model.setUserId(userId);
        }
//        model.setUserId(SharedPref.getVal(MessageChatActivity.this, SharedPref.user_id));
        model.setMsg_flag(SharedPref.getVal(MessageChatActivity.this, SharedPref.msgFlag));
//        model.setShop_id(shopId);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.msgsList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<MessageChatModel>>() {
                        }.getType();

                        messageChatModelList = new ArrayList<>();
                        messageChatModelList.addAll(gson.fromJson(jsonObject.getString("message"), listType));

                        if (messageChatModelList.size() > 0) {
                            binding.recList.setLayoutManager(new LinearLayoutManager(MessageChatActivity.this));
                            binding.recList.setAdapter(new MessageChatActivity.MessageAdapter());

                            layoutManager = new LinearLayoutManager(MessageChatActivity.this);
                            layoutManager.setStackFromEnd(true);
                            binding.recList.setLayoutManager(layoutManager);
                            binding.recList.setAdapter(new MessageAdapter());
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

//                hideShimmer();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
//                hideShimmer();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(MessageChatActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(MessageChatActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(MessageChatActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(MessageChatActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(MessageChatActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MessageChatActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage() {
        SendMessageRequestModel model = new SendMessageRequestModel();
        if (userId.equals(SharedPref.getVal(MessageChatActivity.this, SharedPref.user_id))) {
            msgFlag = "0";
        } else {
            msgFlag = SharedPref.getVal(MessageChatActivity.this, SharedPref.msgFlag);
        }
        if (msgFlag.equals("0")) {
            model.setUserId(SharedPref.getVal(MessageChatActivity.this, SharedPref.user_id));
            model.setShopId(shopId);
        } else {
            model.setShopId(SharedPref.getVal(MessageChatActivity.this, SharedPref.shop_id));
            model.setUserId(userId);
        }
//        model.setUserId(SharedPref.getVal(MessageChatActivity.this, SharedPref.user_id));
        model.setMsgFlag(SharedPref.getVal(MessageChatActivity.this, SharedPref.msgFlag));
//        model.setShopId(shopId);
        model.setMsgs(binding.edtMessage.getText().toString());

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.sendMsgs(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        binding.edtMessage.setText("");

                        if (Utils.isNetwork(MessageChatActivity.this)) {
                            getMessageList(false);
                        } else {
//            binding.lnrMainLayout.setVisibility(View.GONE);
                            Utils.InternetAlertDialog(MessageChatActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                        }
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(MessageChatActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(MessageChatActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(MessageChatActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(MessageChatActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(MessageChatActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MessageChatActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
        String dateFormatted = "", timeFormatted = "";

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MessageItemLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(MessageChatActivity.this), R.layout.message_item_layout, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MessageChatModel model = messageChatModelList.get(position);
            holder.bind(model, position);
        }

        @Override
        public int getItemCount() {
            return messageChatModelList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            MessageItemLayoutBinding layoutBinding;

            public ViewHolder(@NonNull MessageItemLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind(MessageChatModel model, int position) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                SimpleDateFormat formatted = new SimpleDateFormat("dd-MM-yyyy hh:ss aa");
                SimpleDateFormat formatted = new SimpleDateFormat("hh:ss aa");
                try {
                    Date date = simpleDateFormat.parse(model.getCreatedAt());
                    dateFormatted = formatted.format(date);
//                    timeFormatted = dateFormatted.substring(11, dateFormatted.length());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (model.getMsgFlag().equals("0")) {
                    layoutBinding.lnrMsgReceived.setVisibility(View.VISIBLE);
                    layoutBinding.lnrMsgSend.setVisibility(View.GONE);
                    layoutBinding.txtMsgReceived.setText(model.getMsgs());
                    layoutBinding.txtDateReceived.setText(dateFormatted);
                } else {
                    layoutBinding.lnrMsgSend.setVisibility(View.VISIBLE);
                    layoutBinding.lnrMsgReceived.setVisibility(View.GONE);
                    layoutBinding.txtMsgSend.setText(model.getMsgs());
                    layoutBinding.txtDateSend.setText(dateFormatted);
                }
            }
        }
    }
}