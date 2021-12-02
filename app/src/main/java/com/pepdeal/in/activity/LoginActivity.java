package com.pepdeal.in.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.pepdeal.in.R;
import com.pepdeal.in.databinding.ActivityLoginBinding;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setHandler(new ClickHandler());
    }

    public class ClickHandler {
        public void registerClick(View view) {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        }

        public void ForgotPasswordClick(View view) {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        }

        public void onLogin(View view) {
            if (binding.edtMobileNo.getText().toString().equals("") || binding.edtMobileNo.getText().length() != 10) {
                Toasty.info(LoginActivity.this, "Enter valid mobile number", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtPassword.getText().toString().equals("")) {
                Toasty.info(LoginActivity.this, "Enter valid password", Toasty.LENGTH_SHORT, true).show();
            } else {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
        }
    }
}