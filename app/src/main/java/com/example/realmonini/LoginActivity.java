package com.example.realmonini;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.tv_join).setOnClickListener(v -> {
            startActivity(new Intent(this, JoinActivity.class));
        });

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            //로그인 처리 필요
        });
    }
}