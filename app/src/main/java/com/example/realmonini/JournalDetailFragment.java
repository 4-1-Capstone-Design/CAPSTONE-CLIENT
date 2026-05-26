package com.example.realmonini;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.realmonini.network.ApiClient;
import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.JournalDetailData;
import com.example.realmonini.network.dto.JournalReplyData;
import com.example.realmonini.network.dto.KeywordItem;
import com.example.realmonini.network.dto.QuestionAnswerItem;
import com.example.realmonini.util.ApiErrorUtil;
import com.example.realmonini.util.TokenManager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JournalDetailFragment extends Fragment {

    private static final String TAG = "JournalDetailFragment";

    private long journalId = -1;
    private TextView btnReply;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        String journalDate = args != null ? args.getString("journalDate", "") : "";
        String title       = args != null ? args.getString("title", "") : "";
        journalId = args != null ? args.getLong("journalId", -1) : -1;

        // Bundle 데이터로 즉시 표시 (API 응답 전에도 보임)
        ((TextView) view.findViewById(R.id.tv_date)).setText(journalDate);
        ((TextView) view.findViewById(R.id.tv_title)).setText(title);

        btnReply = view.findViewById(R.id.btn_check_reply);
        btnReply.setEnabled(false);

        if (!journalDate.isEmpty()) {
            loadDetail(view, journalDate);
        } else {
            // journalDate 없으면 journalId로 바로 버튼 활성화
            btnReply.setEnabled(journalId != -1);
        }

        btnReply.setOnClickListener(v -> {
            if (journalId == -1) return;
            v.setEnabled(false);
            fetchReply(journalId, v);
        });
    }

    private void loadDetail(View view, String journalDate) {
        String[] parts = journalDate.split("-");
        if (parts.length != 3) return;

        int year, month, day;
        try {
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            day = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return;
        }

        String token = "Bearer " + new TokenManager(requireContext()).getAccessToken();

        ApiClient.getJournalService().getByDate(token, year, month, day)
                .enqueue(new Callback<ApiResponse<JournalDetailData>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<JournalDetailData>> call,
                                           @NonNull Response<ApiResponse<JournalDetailData>> response) {
                        if (!isAdded()) return;
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e(TAG, "by-date 실패: " + response.code());
                            Toast.makeText(requireContext(),
                                    ApiErrorUtil.parseError(response), Toast.LENGTH_SHORT).show();
                            btnReply.setEnabled(journalId != -1);
                            return;
                        }

                        JournalDetailData data = response.body().getData();
                        if (data == null) return;

                        journalId = data.getJournalId();

                        TextView tvDate = view.findViewById(R.id.tv_date);
                        TextView tvTitle = view.findViewById(R.id.tv_title);
                        LinearLayout container = view.findViewById(R.id.container_qa);

                        tvDate.setText(data.getJournalDate());
                        tvTitle.setText(data.getTitle());

                        List<QuestionAnswerItem> questions = data.getQuestions();
                        if (questions != null) {
                            for (QuestionAnswerItem qa : questions) {
                                addQaView(container, qa.getDisplayOrder(), qa.getQuestion(), qa.getAnswer());
                            }
                        }

                        btnReply.setEnabled(true);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<JournalDetailData>> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Log.e(TAG, "네트워크 오류", t);
                        Toast.makeText(requireContext(), "저널을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addQaView(LinearLayout container, int order, String question, String answer) {
        int dp8 = dp(8);
        int dp12 = dp(12);
        int dp16 = dp(16);
        int dp20 = dp(20);

        TextView tvQuestion = new TextView(requireContext());
        tvQuestion.setText(order + ". " + question);
        tvQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvQuestion.setTextColor(0xFF3D3D3D);
        tvQuestion.setTypeface(android.graphics.Typeface.DEFAULT);
        LinearLayout.LayoutParams qParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        qParams.topMargin = dp20;
        tvQuestion.setLayoutParams(qParams);
        container.addView(tvQuestion);

        EditText etAnswer = new EditText(requireContext());
        etAnswer.setText(answer);
        etAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        etAnswer.setTextColor(0xFF3D3D3D);
        etAnswer.setFocusable(false);
        etAnswer.setBackground(requireContext().getDrawable(R.drawable.et_journal_border));
        etAnswer.setPadding(dp16, dp12, dp16, dp12);
        etAnswer.setGravity(android.view.Gravity.TOP);
        LinearLayout.LayoutParams aParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(86));
        aParams.topMargin = dp8;
        etAnswer.setLayoutParams(aParams);
        container.addView(etAnswer);
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value,
                requireContext().getResources().getDisplayMetrics());
    }

    private void fetchReply(long journalId, View btn) {
        String token = "Bearer " + new TokenManager(requireContext()).getAccessToken();

        ApiClient.getJournalService().getStoredReply(token, journalId)
                .enqueue(new Callback<ApiResponse<JournalReplyData>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<JournalReplyData>> call,
                                           @NonNull Response<ApiResponse<JournalReplyData>> response) {
                        if (!isAdded()) return;

                        if (!response.isSuccessful() || response.body() == null
                                || response.body().getData() == null) {
                            btn.setEnabled(true);
                            Log.e(TAG, "답장 조회 실패: " + response.code());
                            Toast.makeText(requireContext(),
                                    ApiErrorUtil.parseError(response), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String replyText = response.body().getData().getContent() != null
                                ? response.body().getData().getContent() : "";

                        fetchKeywordsThenNavigate(journalId, replyText, btn, token);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<JournalReplyData>> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        btn.setEnabled(true);
                        Log.e(TAG, "답장 네트워크 오류", t);
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchKeywordsThenNavigate(long journalId, String replyText, View btn, String token) {
        ApiClient.getJournalService().getKeywords(token, journalId)
                .enqueue(new Callback<ApiResponse<List<KeywordItem>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<KeywordItem>>> call,
                                           @NonNull Response<ApiResponse<List<KeywordItem>>> response) {
                        if (!isAdded()) return;
                        btn.setEnabled(true);

                        String keywordsText = "";
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            keywordsText = response.body().getData().stream()
                                    .map(KeywordItem::getKeyword)
                                    .collect(Collectors.joining(" , "));
                        }

                        navigateToReply(replyText, keywordsText);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<KeywordItem>>> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        btn.setEnabled(true);
                        Log.e(TAG, "키워드 네트워크 오류", t);
                        // 키워드 실패해도 답장은 보여줌
                        navigateToReply(replyText, "");
                    }
                });
    }

    private void navigateToReply(String replyText, String keywordsText) {
        JournalReplyFragment replyFragment = new JournalReplyFragment();
        Bundle args = new Bundle();
        args.putString("keywords", keywordsText);
        args.putString("reply", replyText);
        replyFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, replyFragment)
                .addToBackStack(null)
                .commit();
    }
}
