package com.example.realmonini;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realmonini.network.ApiClient;
import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.SignupData;
import com.example.realmonini.network.dto.SignupRequest;
import com.example.realmonini.util.ApiErrorUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etNickname;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        etEmail = findViewById(R.id.et_email);
        etNickname = findViewById(R.id.et_nickname);
        etPassword = findViewById(R.id.et_password);

        findViewById(R.id.btn_join).setOnClickListener(v -> signup());
    }

    private void signup() {
        String email = etEmail.getText().toString().trim();
        String nickname = etNickname.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || nickname.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getAuthService()
                .signup(new SignupRequest(email, password, nickname))
                .enqueue(new Callback<ApiResponse<SignupData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<SignupData>> call,
                                           Response<ApiResponse<SignupData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(JoinActivity.this,
                                    "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(JoinActivity.this,
                                    ApiErrorUtil.parseError(response), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<SignupData>> call, Throwable t) {
                        Toast.makeText(JoinActivity.this,
                                "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
