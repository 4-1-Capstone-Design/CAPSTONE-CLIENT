package com.example.realmonini;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.realmonini.network.ApiClient;
import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.LogoutRequest;
import com.example.realmonini.network.dto.UserData;
import com.example.realmonini.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageFragment extends Fragment {

    private TokenManager tokenManager;
    private EditText etEmail;
    private EditText etNickname;
    private TextView tvCount;
    private TextView tvCloverComment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mypage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tokenManager = new TokenManager(requireContext());

        etEmail = view.findViewById(R.id.et_email);
        etNickname = view.findViewById(R.id.et_nickname);
        tvCount = view.findViewById(R.id.tv_count);
        tvCloverComment = view.findViewById(R.id.tv_clover_comment);

        loadMyPage();

        view.findViewById(R.id.btn_logout).setOnClickListener(v -> logout());
        view.findViewById(R.id.tv_withdraw).setOnClickListener(v -> {
            // 나중에 회원탈퇴 처리
        });
    }

    private void loadMyPage() {
        String token = "Bearer " + tokenManager.getAccessToken();

        ApiClient.getUserService()
                .getMe(token)
                .enqueue(new Callback<ApiResponse<UserData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<UserData>> call,
                                           Response<ApiResponse<UserData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserData data = response.body().getData();
                            etEmail.setText("E-mail : " + data.getEmail());
                            etNickname.setText("Nickname : " + data.getNickname());
                            tvCount.setText(data.getCloverBalance() + "개");
                            tvCloverComment.setText(data.getCloverComment());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<UserData>> call, Throwable t) {
                        // 로컬에 저장된 정보로 fallback
                        String email = tokenManager.getEmail();
                        String nickname = tokenManager.getNickname();
                        if (email != null) etEmail.setText("E-mail : " + email);
                        if (nickname != null) etNickname.setText("Nickname : " + nickname);
                    }
                });
    }

    private void logout() {
        String refreshToken = tokenManager.getRefreshToken();
        if (refreshToken == null) {
            navigateToBeforeLogin();
            return;
        }

        ApiClient.getAuthService()
                .logout(new LogoutRequest(refreshToken))
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call,
                                           Response<ApiResponse<Object>> response) {
                        tokenManager.clear();
                        navigateToBeforeLogin();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        tokenManager.clear();
                        navigateToBeforeLogin();
                    }
                });
    }

    private void navigateToBeforeLogin() {
        Intent intent = new Intent(requireContext(), BeforeLoginMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
