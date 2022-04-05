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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.adapter.ChatAdapter;
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
import com.pepdeal.in.model.messagemodel.ChatModel;
import com.pepdeal.in.model.messagemodel.MessageChatModel;
import com.pepdeal.in.model.messagemodel.MessageUsersListModel;
import com.pepdeal.in.model.requestModel.SendMessageRequestModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.ticketmodel.TicketDataModel;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class MessageChatActivity extends AppCompatActivity {

    ActivityMessageChatBinding binding;
    LinearLayoutManager layoutManager;
    String shopId = "", name = "", userId = "", from = "";
    List<MessageChatModel> messageChatModelList = new ArrayList<>();
    String msgFlag = "";
    private DatabaseReference adduserToInbox;

    private DatabaseReference mChatRefReteriving;
    private DatabaseReference sendTypingIndication;
    private DatabaseReference receiveTypingIndication;
    DatabaseReference rootref;
    public static SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);
    ChatAdapter mAdapter;
    List<ChatModel> mChats = new ArrayList<>();
    Query queryGetChat;
    Query myBlockStatusQuery;
    Query otherBlockStatusQuery;
    boolean isUserAlreadyBlock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message_chat);
        binding.setHandler(new ClickHandler());
        shopId = getIntent().getStringExtra("shop_id");
        name = getIntent().getStringExtra("name");
        userId = getIntent().getStringExtra("user_id");
        from = getIntent().getStringExtra("from");

        binding.txtUserName.setText(name);

        rootref = FirebaseDatabase.getInstance().getReference();
        adduserToInbox = FirebaseDatabase.getInstance().getReference();

        if (Utils.isNetwork(MessageChatActivity.this)) {
            messageCountStatus();
        } else {
            Utils.InternetAlertDialog(MessageChatActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }

        final LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setStackFromEnd(true);
        binding.recList.setLayoutManager(layout);
        binding.recList.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(binding.recList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        if (from.equals("home")) {
            mAdapter = new ChatAdapter(mChats, userId, MessageChatActivity.this, new ChatAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ChatModel item, View v) {
                }
            }, new ChatAdapter.OnLongClickListener() {
                @Override
                public void onLongClick(ChatModel item, View view) {
              /*  if (view.getId() == R.id.msgtxt) {
                    if (senderid.equals(item.getSender_id()) && isTodayMessage(item.getTimestamp())) {
                        deleteMessage(item);
                    }
                } */
                }
            });
        } else {
            mAdapter = new ChatAdapter(mChats, shopId, MessageChatActivity.this, new ChatAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ChatModel item, View v) {
                }
            }, new ChatAdapter.OnLongClickListener() {
                @Override
                public void onLongClick(ChatModel item, View view) {
              /*  if (view.getId() == R.id.msgtxt) {
                    if (senderid.equals(item.getSender_id()) && isTodayMessage(item.getTimestamp())) {
                        deleteMessage(item);
                    }
                } */
                }
            });
        }

        binding.recList.setAdapter(mAdapter);
        binding.recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutItems;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollOutItems = layout.findFirstCompletelyVisibleItemPosition();

                if (userScrolled && (scrollOutItems == 0 && mChats.size() > 9)) {
                    userScrolled = false;
                    binding.progressBar.setVisibility(View.VISIBLE);
                    rootref.child("chat").child(userId + "-" + shopId).orderByChild("chat_id")
                            .endAt(mChats.get(0).getChat_id()).limitToLast(20)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    binding.progressBar.setVisibility(View.GONE);
                                    ArrayList<ChatModel> arrayList = new ArrayList<>();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ChatModel item = parseData(snapshot);
                                        arrayList.add(item);
                                    }

                                    for (int i = arrayList.size() - 2; i >= 0; i--) {
                                        mChats.add(0, arrayList.get(i));
                                    }

                                    mAdapter.notifyDataSetChanged();
                                    if (arrayList.size() > 8) {
                                        binding.recList.scrollToPosition(arrayList.size());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });

        getChatData();
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
//                    sendMessage();
                    sendMessage(binding.edtMessage.getText().toString());
                    binding.edtMessage.setText(null);
                } else {
                    Utils.InternetAlertDialog(MessageChatActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
            }
        }
    }

    public ChatModel parseData(DataSnapshot dataSnapshot) {
        ChatModel model = new ChatModel();
        model.chat_id = dataSnapshot.child("chat_id").getValue().toString();
        model.receiver_id = dataSnapshot.child("receiver_id").getValue().toString();
        model.sender_id = dataSnapshot.child("sender_id").getValue().toString();
        model.sender_name = dataSnapshot.child("sender_name").getValue().toString();
        model.text = dataSnapshot.child("text").getValue().toString();
        model.status = dataSnapshot.child("status").getValue().toString();
        model.timestamp = dataSnapshot.child("timestamp").getValue().toString();
        model.type = dataSnapshot.child("type").getValue().toString();
        return model;
    }

    ValueEventListener valueEventListener;
    ChildEventListener eventListener;
    ValueEventListener myInboxListener;
    ValueEventListener otherInboxListener;

    public void getChatData() {
        mChats.clear();
        mChatRefReteriving = FirebaseDatabase.getInstance().getReference();
        queryGetChat = mChatRefReteriving.child("chat").child(userId + "-" + shopId);

        myBlockStatusQuery = mChatRefReteriving.child("Inbox")
                .child(userId)
                .child(shopId);

        otherBlockStatusQuery = mChatRefReteriving.child("Inbox")
                .child(shopId)
                .child(userId);


        // this will get all the messages between two users
        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    ChatModel model = dataSnapshot.getValue(ChatModel.class);//Parse_data(dataSnapshot);

                    mChats.add(model);
                    mAdapter.notifyItemInserted(mChats.size());
                    binding.recList.scrollToPosition(mChats.size() - 1);

                    binding.progressBar.setVisibility(View.GONE);
                } catch (Exception ex) {
                    Log.e("Chat List", "Exception" + ex.toString());
                }

                changeStatus();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    try {
                        ChatModel model = dataSnapshot.getValue(ChatModel.class);

                        for (int i = mChats.size() - 1; i >= 0; i--) {
                            if (mChats.get(i).getTimestamp().equals(dataSnapshot.child("timestamp").getValue())) {
                                mChats.remove(i);
                                mChats.add(i, model);
                                break;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        Log.e("Chat List", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Chat List", databaseError.getMessage());
            }
        };


        // this will check the two user are do chat before or not
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                binding.progressBar.setVisibility(View.GONE);
                if (dataSnapshot.hasChild(userId + "-" + shopId)) {
                    queryGetChat.removeEventListener(valueEventListener);
                } else {
                    queryGetChat.removeEventListener(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        queryGetChat.limitToLast(20).addChildEventListener(eventListener);
        mChatRefReteriving.child("chat").addValueEventListener(valueEventListener);

       /* myBlockStatusQuery.addValueEventListener(myInboxListener);
        otherBlockStatusQuery.addValueEventListener(otherInboxListener);*/
    }

    // this method will change the status to ensure that
    // user is seen all the message or not (in both chat node and Chatinbox node)
    public void changeStatus() {
        final Date c = Calendar.getInstance().getTime();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final Query query1, query2;
//        if (from.equals("home")) {
        query1 = reference.child("chat").child(shopId + "-" + userId).orderByChild("status").equalTo("0");
        query2 = reference.child("chat").child(userId + "-" + shopId).orderByChild("status").equalTo("0");
       /* } else {
            query1 = reference.child("chat").child(userId + "-" + shopId).orderByChild("status").equalTo("0");
            query2 = reference.child("chat").child(shopId + "-" + userId).orderByChild("status").equalTo("0");
        }
*/
        final DatabaseReference inboxChangeStatus1, inboxChangeStatus2;
//        if (from.equals("home")) {
        inboxChangeStatus1 = reference.child("Inbox").child(userId + "/" + shopId);
        inboxChangeStatus2 = reference.child("Inbox").child(shopId + "/" + userId);
//        }

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if (from.equals("home")) {
                        if (!nodeDataSnapshot.child("sender_id").getValue().equals(userId)) {
                            String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                            String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("status", "1");
                            result.put("time", Utils.df2.format(c));
                            reference.child(path).updateChildren(result);
                        }
                    } else {
                        if (!nodeDataSnapshot.child("sender_id").getValue().equals(shopId)) {
                            String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                            String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("status", "1");
                            result.put("time", Utils.df2.format(c));
                            reference.child(path).updateChildren(result);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if (from.equals("home")) {
                        if (!nodeDataSnapshot.child("sender_id").getValue().equals(shopId)) {
                            String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                            String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("status", "1");
                            result.put("time", Utils.df2.format(c));
                            reference.child(path).updateChildren(result);
                        }
                    } else {
                        if (!nodeDataSnapshot.child("sender_id").getValue().equals(userId)) {
                            String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                            String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("status", "1");
                            result.put("time", Utils.df2.format(c));
                            reference.child(path).updateChildren(result);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        inboxChangeStatus1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (from.equals("home")) {
                        if (dataSnapshot.child("rid").getValue().equals(shopId)) {
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("status", "1");
                            inboxChangeStatus1.updateChildren(result);
                        }
                    } else {
                        if (dataSnapshot.child("rid").getValue().equals(userId)) {
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("status", "1");
                            inboxChangeStatus1.updateChildren(result);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        inboxChangeStatus2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (from.equals("home")) {
                        if (dataSnapshot.child("rid").getValue().equals(shopId)) {
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("status", "1");
                            inboxChangeStatus2.updateChildren(result);
                        }
                    } else {
                        if (dataSnapshot.child("rid").getValue().equals(userId)) {
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("status", "1");
                            inboxChangeStatus2.updateChildren(result);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // this will add the new message in chat node and update the ChatInbox by new message by present date
    public void sendMessage(final String message) {
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = df.format(c);

        final String current_user_ref = "chat" + "/" + userId + "-" + shopId;
        final String chat_user_ref = "chat" + "/" + userId + "-" + shopId;

        DatabaseReference reference = rootref.child("chat").child(userId + "-" + shopId).push();
        final String pushid = reference.getKey();

        final HashMap message_user_map = new HashMap<>();
        if (from.equals("home")) {
            message_user_map.put("receiver_id", shopId);
            message_user_map.put("sender_id", userId);
        } else {
            message_user_map.put("receiver_id", userId);
            message_user_map.put("sender_id", shopId);
        }

        if (from.equals("home")) {
            message_user_map.put("sender_name", SharedPref.getVal(MessageChatActivity.this, SharedPref.userName));
        } else {
            message_user_map.put("sender_name", SharedPref.getVal(MessageChatActivity.this, SharedPref.shopName));
        }
        message_user_map.put("chat_id", pushid);
        message_user_map.put("text", message);
        message_user_map.put("type", "text");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("timestamp", formattedDate);

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + pushid, message_user_map);
        user_map.put(chat_user_ref + "/" + pushid, message_user_map);

        rootref.updateChildren(user_map, (databaseError, databaseReference) -> {
            //if first message then set the visibility of whoops layout gone
            String inbox_sender_ref = "Inbox" + "/" + userId + "/" + shopId;
            String inbox_receiver_ref = "Inbox" + "/" + shopId + "/" + userId;

            HashMap sendermap = new HashMap<>();
            if (from.equals("home")) {
                sendermap.put("rid", shopId);
//                sendermap.put("name", SharedPref.getVal(MessageChatActivity.this, SharedPref.userName));
            } else {
                sendermap.put("rid", userId);
//                sendermap.put("name", name);
            }

            if (from.equals("home")) {
                sendermap.put("name", SharedPref.getVal(MessageChatActivity.this, SharedPref.userName));
            } else {
                sendermap.put("name", SharedPref.getVal(MessageChatActivity.this, SharedPref.shopName));
            }
            sendermap.put("msg", message);
            sendermap.put("status", "0");
            sendermap.put("timestamp", -1 * System.currentTimeMillis());
            sendermap.put("date", formattedDate);

            HashMap receivermap = new HashMap<>();
            if (from.equals("home")) {
                receivermap.put("rid", userId);
//                receivermap.put("name", SharedPref.getVal(MessageChatActivity.this, SharedPref.userName));
            } else {
                receivermap.put("rid", shopId);
//                receivermap.put("name", name);
            }
//            if (from.equals("home")) {
                receivermap.put("name",name);
//            } else {
//                receivermap.put("name", SharedPref.getVal(MessageChatActivity.this, SharedPref.shopName));
//            }
//            receivermap.put("rid", shopId);
            receivermap.put("msg", message);
            receivermap.put("status", "1");
            receivermap.put("timestamp", -1 * System.currentTimeMillis());
            receivermap.put("date", formattedDate);

            HashMap both_user_map = new HashMap<>();
            both_user_map.put(inbox_sender_ref, receivermap);
            both_user_map.put(inbox_receiver_ref, sendermap);

            adduserToInbox.updateChildren(both_user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    /*if (getActivity() != null) {
                        ChatActivity.sendPushNotification(getActivity(), message,
                                receiverid, senderid);
                    }*/
                    Toast.makeText(MessageChatActivity.this, "inserted", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mrefresh, new IntentFilter(Config.msgBroadCast));
       /* if (Utils.isNetwork(MessageChatActivity.this)) {
            getMessageList(true);
        } else {
//            binding.lnrMainLayout.setVisibility(View.GONE);
            Utils.InternetAlertDialog(MessageChatActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }*/
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