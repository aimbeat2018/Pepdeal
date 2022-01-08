package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pepdeal.in.R;
import com.pepdeal.in.databinding.ActivityLogalBinding;

public class LegalActivity extends AppCompatActivity {

    ActivityLogalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_logal);
        binding.setHandler(new ClickHandler());
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }

        public void onPrivacyClick(View view) {
            startActivity(new Intent(LegalActivity.this, AboutUsActivity.class).putExtra("from", "privacy"));
        }

        public void onTermsClick(View view) {
            startActivity(new Intent(LegalActivity.this, AboutUsActivity.class).putExtra("from", "terms"));
        }
    }
}