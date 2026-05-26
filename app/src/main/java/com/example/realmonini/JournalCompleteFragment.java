package com.example.realmonini;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.realmonini.network.ApiClient;
import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.KeywordItem;
import com.example.realmonini.network.dto.ReplyData;
import com.example.realmonini.util.ApiErrorUtil;
import com.example.realmonini.util.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JournalCompleteFragment extends Fragment {

    private boolean timerDone = false;
    private boolean apiDone = false;
    private boolean replyCalled = false;
    private Bundle replyArgs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            replyCalled = savedInstanceState.getBoolean("replyCalled", false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("replyCalled", replyCalled);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal_complete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        long journalId = requireArguments().getLong("journalId");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            timerDone = true;
            navigateIfReady();
        }, 2000);

        if (!replyCalled) {
            replyCalled = true;
            fetchReply(journalId);
        }
    }

    private void fetchReply(long journalId) {
        String token = "Bearer " + new TokenManager(requireContext()).getAccessToken();

        ApiClient.getJournalService()
                .generateReply(token, journalId)
                .enqueue(new Callback<ApiResponse<ReplyData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ReplyData>> call,
                                           Response<ApiResponse<ReplyData>> response) {
                        if (!isAdded()) return;
                        replyArgs = new Bundle();
                        if (!response.isSuccessful() || response.body() == null) {
                            if (isAdded()) {
                                Toast.makeText(requireContext(),
                                        ApiErrorUtil.parseError(response), Toast.LENGTH_SHORT).show();
                            }
                        } else if (response.isSuccessful() && response.body() != null) {
                            ReplyData data = response.body().getData();
                            replyArgs.putString("reply", data.getReply());
                            replyArgs.putString("keywords", joinKeywords(data.getKeywords()));
                        }
                        apiDone = true;
                        navigateIfReady();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ReplyData>> call, Throwable t) {
                        if (!isAdded()) return;
                        replyArgs = new Bundle();
                        apiDone = true;
                        navigateIfReady();
                    }
                });
    }

    private String joinKeywords(List<KeywordItem> keywords) {
        if (keywords == null || keywords.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keywords.size(); i++) {
            if (i > 0) sb.append(" , ");
            sb.append(keywords.get(i).getKeyword());
        }
        return sb.toString();
    }

    private void navigateIfReady() {
        if (!timerDone || !apiDone || !isAdded()) return;
        JournalReplyFragment fragment = new JournalReplyFragment();
        fragment.setArguments(replyArgs);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit();
    }
}
