package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.pepdeal.in.R;
import com.pepdeal.in.constants.SharedPref;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String Id = "";

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