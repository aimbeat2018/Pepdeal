package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.adapter.AdapterClickListener;
import com.pepdeal.in.adapter.InboxAdapter;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.databinding.ActivityMessageSellerListActivityBinding;
import com.pepdeal.in.databinding.ActivityMessageUsersListBinding;
import com.pepdeal.in.databinding.ItemMessageUsersListLayoutBinding;
import com.pepdeal.in.model.messagemodel.InboxModel;
import com.pepdeal.in.model.messagemodel.MessageUsersListModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;

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

public class MessageSellerListActivityActivity extends AppCompatActivity {

    ActivityMessageSellerListActivityBinding binding;
    List<MessageUsersListModel> messageUsersListModelList = new ArrayList<>();
    DatabaseReference rootRef;
    ArrayList<InboxModel> inboxArrayList;
    InboxAdapter inboxAdapter;
    String from = "";
    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message_seller_list_activity);
        binding.setHandler(new ClickHandler());
        from = getIntent().getStringExtra("from");
        rootRef = FirebaseDatabase.getInstance().getReference();
        // intialize the arraylist and and inboxlist
        inboxArrayList = new ArrayList<>();
//        binding.recList.setLayoutManager(new LinearLayoutManager(MessageSellerListActivityActivity.this));
//        binding.recList.setAdapter(new MessageAdapter());

        LinearLayoutManager layout = new LinearLayoutManager(this);
        binding.recList.setLayoutManager(layout);
        binding.recList.setHasFixedSize(false);
        inboxAdapter = new InboxAdapter(MessageSellerListActivityActivity.this, inboxArrayList, new AdapterClickListener() {
            @Override
            public void onItemClick(int pos, Object object, View view) {
                InboxModel item = (InboxModel) object;
//                chatFragment(userId, item.getId(), item.getName(), false);
                /*if (from.equals("home")) {
                    Intent intent = new Intent(MessageSellerListActivityActivity.this, MessageChatActivity.class);
                    intent.putExtra("shop_id", item.getId());
                    intent.putExtra("name", item.getName());
                    intent.putExtra("user_id", SharedPref.getVal(MessageSellerListActivityActivity.this, SharedPref.user_id));
                    startActivity(intent);
                } else {*/
                Intent intent = new Intent(MessageSellerListActivityActivity.this, MessageChatSellerActivity.class);
                intent.putExtra("shop_id", item.getId());
                intent.putExtra("name", item.getName());
                intent.putExtra("user_id", SharedPref.getVal(MessageSellerListActivityActivity.this, SharedPref.shop_id));
                startActivity(intent);
//                }

            }

            @Override
            public void onLongItemClick(int pos, Object item, View view) {
            }
        });

        binding.recList.setAdapter(inboxAdapter);
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getInbox();
    }

    ValueEventListener eventListener2;
    Query inboxQuery;

    private void getInbox() {

        if (from.equals("home")) {
            userId = SharedPref.getVal(MessageSellerListActivityActivity.this, SharedPref.user_id);
        } else {
            userId = SharedPref.getVal(MessageSellerListActivityActivity.this, SharedPref.shop_id);
        }
        inboxQuery = rootRef.child("InboxSeller").child(userId).orderByChild("date");
        eventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inboxArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    InboxModel model = new InboxModel();
                    model.setId(ds.getKey());
                    model.setName(ds.child("name").getValue().toString());
                    model.setMessage(ds.child("msg").getValue().toString());
                    model.setTimestamp(ds.child("date").getValue().toString());
                    model.setStatus(ds.child("status").getValue().toString());
                    inboxArrayList.add(model);
                }
                Collections.reverse(inboxArrayList);
                inboxAdapter.notifyDataSetChanged();

                if (inboxArrayList.size() > 0) {
                    binding.recList.setVisibility(View.VISIBLE);
                    binding.lnrNoData.setVisibility(View.GONE);
                } else {
                    binding.recList.setVisibility(View.GONE);
                    binding.lnrNoData.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        inboxQuery.addValueEventListener(eventListener2);
    }


    // on stop we will remove the listener
    @Override
    public void onStop() {
        super.onStop();
        if (inboxQuery != null)
            inboxQuery.removeEventListener(eventListener2);
    }

    @Override
    protected void onResume() {
        super.onResume();
      /*  if (Utils.isNetwork(MessageSellerListActivityActivity.this)) {
            getMessageUsersList(true);
        } else {
//            binding.lnrMainLayout.setVisibility(View.GONE);
            Utils.InternetAlertDialog(MessageSellerListActivityActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }*/
    }

    private void getMessageUsersList(boolean loading) {
       /* if (loading) {
            showShimmer();
        }*/
        UserProfileRequestModel model = new UserProfileRequestModel();
        String msgFlag = SharedPref.getVal(MessageSellerListActivityActivity.this, SharedPref.msgFlag);
        if (msgFlag.equals("0")) {
            model.setUserId(SharedPref.getVal(MessageSellerListActivityActivity.this, SharedPref.user_id));
            model.setShop_id("0");
        } else {
            model.setShop_id(SharedPref.getVal(MessageSellerListActivityActivity.this, SharedPref.shop_id));
            model.setUserId("0");
        }

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.shopMsgForUser(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<MessageUsersListModel>>() {
                        }.getType();
                        messageUsersListModelList = new ArrayList<>();
                        messageUsersListModelList.addAll(gson.fromJson(jsonObject.getString("message"), listType));

                        if (messageUsersListModelList.size() > 0) {
                            binding.recList.setLayoutManager(new LinearLayoutManager(MessageSellerListActivityActivity.this));
                            binding.recList.setAdapter(new MessageSellerListActivityActivity.MessageAdapter());

//                            binding.lnrMainLayout.setVisibility(View.VISIBLE);
                            binding.lnrNoData.setVisibility(View.GONE);
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
                            Toast.makeText(MessageSellerListActivityActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(MessageSellerListActivityActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(MessageSellerListActivityActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(MessageSellerListActivityActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(MessageSellerListActivityActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MessageSellerListActivityActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class MessageAdapter extends RecyclerView.Adapter<MessageSellerListActivityActivity.MessageAdapter.ViewHolder> {

        @NonNull
        @Override
        public MessageSellerListActivityActivity.MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemMessageUsersListLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(MessageSellerListActivityActivity.this),
                    R.layout.item_message_users_list_layout, parent, false);
            return new MessageSellerListActivityActivity.MessageAdapter.ViewHolder(layoutBinding);
        }


        @Override
        public void onBindViewHolder(@NonNull MessageSellerListActivityActivity.MessageAdapter.ViewHolder holder, int position) {
            MessageUsersListModel model = messageUsersListModelList.get(position);
            holder.bind(model, position);
//            holder.bind();
        }

        @Override
        public int getItemCount() {
            return messageUsersListModelList.size();
//            return 3;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemMessageUsersListLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemMessageUsersListLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind(MessageUsersListModel model, int position) {
                layoutBinding.txtUserName.setText(model.getShopName());
                layoutBinding.txtLastMessage.setText(model.getMsgs());

                if (model.getMsgCount().equals("") || model.getMsgCount().equals("0")) {
                    layoutBinding.cardCount.setVisibility(View.GONE);
                } else {
                    layoutBinding.cardCount.setVisibility(View.VISIBLE);
                    layoutBinding.txtCount.setText(model.getMsgCount());
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat formatted = new SimpleDateFormat("dd/MM/yy");
                try {
                    Date date = simpleDateFormat.parse(model.getCreatedAt());
                    String dateFormatted = formatted.format(date);
                    layoutBinding.txtTime.setText(dateFormatted);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                layoutBinding.lnrMain.setOnClickListener(view -> {
                    startActivity(new Intent(MessageSellerListActivityActivity.this, MessageChatActivity.class)
                            .putExtra("shop_id", model.getShopId()).putExtra("name", model.getShopName()).putExtra("user_id", model.getUserId()));
                });
            }

            /*public void bind(AddProductCategoryResponseModel model, int position) {
                Glide.with(MessageSellerListActivityActivity.this).load(model.getCategoryImages())
                        .error(R.drawable.loader).placeholder(R.drawable.loader).into(layoutBinding.imgProductImage);
                layoutBinding.txtCategoryName.setText(model.getCategoryName());
            }*/

        }
    }
}