package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pepdeal.in.R;
import com.pepdeal.in.databinding.ActivityOtpVerificationBinding;

public class OtpVerificationActivity extends AppCompatActivity {

    ActivityOtpVerificationBinding binding;
    String mobileNo = "", from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verification);
        binding.setHandler(new ClickHandler());
        mobileNo = getIntent().getStringExtra("mobile_no");
        from = getIntent().getStringExtra("from");
        binding.txtOtpMsg.setText(getString(R.string.enter_otp_sent_to) + " " + mobileNo);
    }

    public class ClickHandler {
        public void onEditClick(View view) {
            if (from.equals("forgot")) {
                startActivity(new Intent(OtpVerificationActivity.this, ForgotPasswordActivity.class));
            } else {
                startActivity(new Intent(OtpVerificationActivity.this, RegistrationActivity.class));
            }
        }

        public void onVerifyClick(View view) {
            if (from.equals("forgot")) {
                startActivity(new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class));
            } else {
                startActivity(new Intent(OtpVerificationActivity.this, HomeActivity.class));
            }
        }
    }
}