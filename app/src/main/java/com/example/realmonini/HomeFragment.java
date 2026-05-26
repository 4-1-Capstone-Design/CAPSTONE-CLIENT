package com.example.realmonini;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.realmonini.network.ApiClient;
import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.JournalDetailData;
import com.example.realmonini.util.TokenManager;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_journal).setOnClickListener(v -> checkTodayAndNavigate());

        view.findViewById(R.id.ic_mypage).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame, new MyPageFragment())
                    .commit();
        });
    }

    private void checkTodayAndNavigate() {
        TokenManager tokenManager = new TokenManager(requireContext());
        String token = "Bearer " + tokenManager.getAccessToken();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        ApiClient.getJournalService().getByDate(token, year, month, day)
                .enqueue(new Callback<ApiResponse<JournalDetailData>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<JournalDetailData>> call,
                                           @NonNull Response<ApiResponse<JournalDetailData>> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            Toast.makeText(requireContext(),
                                    "오늘 저널은 이미 작성했어요.\n내일 다시 만나요 :)",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            goToWrite();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<JournalDetailData>> call,
                                          @NonNull Throwable t) {
                        if (isAdded()) goToWrite();
                    }
                });
    }

    private void goToWrite() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, new WriteJournalFragment())
                .commit();
    }
}
