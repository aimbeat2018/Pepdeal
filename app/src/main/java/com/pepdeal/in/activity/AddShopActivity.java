package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.pepdeal.in.R;
import com.pepdeal.in.databinding.ActivityAddProductBinding;
import com.pepdeal.in.databinding.ActivityAddShopBinding;

public class AddShopActivity extends AppCompatActivity {

    ActivityAddShopBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_shop);
        binding.setHandler(new ClickHandler());
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }
}