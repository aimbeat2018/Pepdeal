package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pepdeal.in.R;
import com.pepdeal.in.constants.SharedPref;

public class SplashActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String Id = "";

        imageView = findViewById(R.id.img_view);

        // Adding the gif here using glide library
        Glide.with(this).load(R.drawable.artboard).into(imageView);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
               if (SharedPref.getBol(SplashActivity.this, SharedPref.isLogin)) {
                   if (Id.equals("")) {
                       startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                       finish();
                   } } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
               /* startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();*/
            }
        }, 2000);
    }
}