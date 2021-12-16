package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.pepdeal.in.R;
import com.pepdeal.in.databinding.ActivityMessageChatBinding;
import com.pepdeal.in.databinding.ActivitySellerTicketListBinding;
import com.pepdeal.in.model.ticketmodel.TicketDataModel;

import java.util.ArrayList;
import java.util.List;

public class MessageChatActivity extends AppCompatActivity {

    ActivityMessageChatBinding binding;
    List<TicketDataModel> productDataModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message_chat);
        binding.setHandler(new ClickHandler());

    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }
}