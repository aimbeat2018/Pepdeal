package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.pepdeal.in.R;
import com.pepdeal.in.adapter.ProductAdapter;
import com.pepdeal.in.databinding.ActivityProductDetailsBinding;
import com.pepdeal.in.databinding.ActivityShopDetailsBinding;

public class ProductDetailsActivity extends AppCompatActivity {

    ActivityProductDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        binding.setHandler(new ClickHandler());
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }

}