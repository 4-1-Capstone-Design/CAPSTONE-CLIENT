package com.example.realmonini;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realmonini.network.ApiClient;
import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.LoginData;
import com.example.realmonini.network.dto.LoginRequest;
import com.example.realmonini.util.TokenManager;

import android.util.Log;

import com.example.realmonini.util.ApiErrorUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tokenManager = new TokenManager(this);

        findViewById(R.id.tv_join).setOnClickListener(v ->
                startActivity(new Intent(this, JoinActivity.class)));

        findViewById(R.id.btn_login).setOnClickListener(v -> login());
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getAuthService()
                .login(new LoginRequest(email, password))
                .enqueue(new Callback<ApiResponse<LoginData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<LoginData>> call,
                                           Response<ApiResponse<LoginData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginData data = response.body().getData();
                            tokenManager.saveLoginInfo(
                                    data.getUserId(), data.getEmail(), data.getNickname(),
                                    data.getAccessToken(), data.getRefreshToken());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    ApiErrorUtil.parseError(response), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<LoginData>> call, Throwable t) {
                        Toast.makeText(LoginActivity.this,
                                "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
