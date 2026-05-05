package com.example.realmonini;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class JoinActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        findViewById(R.id.btn_join).setOnClickListener(v -> {
            //회원가입 처리
        });
    }
}