package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pepdeal.in.R;
import com.pepdeal.in.databinding.ActivityLoginBinding;
import com.pepdeal.in.databinding.ActivityResetPasswordBinding;

import es.dmoral.toasty.Toasty;

public class ResetPasswordActivity extends AppCompatActivity {

    ActivityResetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password);
        binding.setHandler(new ClickHandler());
    }

    public class ClickHandler {
        public void onResetClick(View view) {
            if (binding.edtPassword.getText().toString().equals("")) {
                Toasty.info(ResetPasswordActivity.this, "Enter valid password", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtPassword.getText().toString().equals(binding.edtConfirmPassword.getText().toString())) {
                Toasty.info(ResetPasswordActivity.this, "Both password should match", Toasty.LENGTH_SHORT, true).show();
            } else {
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }
}