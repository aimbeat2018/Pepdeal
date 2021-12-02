package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pepdeal.in.R;
import com.pepdeal.in.databinding.ActivityForgotPasswordBinding;
import com.pepdeal.in.databinding.ActivityLoginBinding;

import es.dmoral.toasty.Toasty;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        binding.setHandler(new ClickHandler());
    }

    public class ClickHandler {
        public void registerClick(View view) {
            startActivity(new Intent(ForgotPasswordActivity.this, RegistrationActivity.class));
        }

        public void onSendOtp(View view) {
            if (binding.edtMobileNo.getText().toString().equals("") || binding.edtMobileNo.getText().length() != 10) {
                Toasty.info(ForgotPasswordActivity.this, "Enter valid mobile number", Toasty.LENGTH_SHORT, true).show();
            }  else {
                Intent intent = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
                intent.putExtra("mobile_no", binding.edtMobileNo.getText().toString());
                intent.putExtra("from", "forgot");
                startActivity(intent);
            }
        }
    }
}