package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pepdeal.in.R;
import com.pepdeal.in.databinding.ActivityRegistrationBinding;

import es.dmoral.toasty.Toasty;

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);

    }

    public class ClickHandler {
        public ClickHandler() {
        }

        public void onRegistration(View view) {
            if (binding.edtName.getText().toString().equals("")) {
                Toasty.info(RegistrationActivity.this, "Enter name to continue", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtMobileNo.getText().toString().equals("")||binding.edtMobileNo.getText().length()!=10) {
                Toasty.info(RegistrationActivity.this, "Enter valid mobile number", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtPassword.getText().toString().equals("")) {
                Toasty.info(RegistrationActivity.this, "Enter valid password", Toasty.LENGTH_SHORT, true).show();
            }else if (binding.edtPassword.getText().toString().equals(binding.edtConfirmPassword.getText().toString())) {
                Toasty.info(RegistrationActivity.this, "Both password should match", Toasty.LENGTH_SHORT, true).show();
            } else {
                startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
            }
        }
    }
}