package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.pepdeal.in.R;
import com.pepdeal.in.databinding.ActivityShopDetailsBinding;

public class ShopDetailsActivity extends AppCompatActivity {

    ActivityShopDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_details);
    }
}