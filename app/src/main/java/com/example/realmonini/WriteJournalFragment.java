package com.example.realmonini;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.realmonini.network.ApiClient;
import com.example.realmonini.network.dto.AnswerRequest;
import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.QuestionItem;
import com.example.realmonini.network.dto.SubmitData;
import com.example.realmonini.network.dto.SubmitRequest;
import com.example.realmonini.network.dto.TodayQuestionsData;
import com.example.realmonini.util.ApiErrorUtil;
import com.example.realmonini.util.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteJournalFragment extends Fragment {

    private TokenManager tokenManager;
    private TextView[] qViews;
    private EditText[] etViews;
    private long[] questionIds;
    private int validCount = 0;
    private boolean submitted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_write_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tokenManager = new TokenManager(requireContext());

        qViews = new TextView[]{
                view.findViewById(R.id.q1),
                view.findViewById(R.id.q2),
                view.findViewById(R.id.q3),
                view.findViewById(R.id.q4)
        };
        etViews = new EditText[]{
                view.findViewById(R.id.et_q1),
                view.findViewById(R.id.et_q2),
                view.findViewById(R.id.et_q3),
                view.findViewById(R.id.et_q4)
        };
        questionIds = new long[4];

        loadTodayQuestions(view);

        view.findViewById(R.id.btn_done).setOnClickListener(v -> {
            if (submitted) return;
            submitted = true;
            v.setEnabled(false);
            submitJournal(view);
        });
    }

    private void loadTodayQuestions(View view) {
        String token = "Bearer " + tokenManager.getAccessToken();

        ApiClient.getQuestionService()
                .getToday(token)
                .enqueue(new Callback<ApiResponse<TodayQuestionsData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<TodayQuestionsData>> call,
                                           Response<ApiResponse<TodayQuestionsData>> response) {
                        if (!isAdded() || response.body() == null || !response.isSuccessful()) return;

                        TodayQuestionsData data = response.body().getData();
                        List<QuestionItem> questions = data.getQuestions();

                        TextView tvDate = view.findViewById(R.id.tv_date);
                        if (data.getQuestionDate() != null) {
                            tvDate.setText(data.getQuestionDate().replace("-", "."));
                        }

                        validCount = Math.min(questions.size(), 4);
                        for (int i = 0; i < validCount; i++) {
                            QuestionItem q = questions.get(i);
                            qViews[i].setText((i + 1) + ". " + q.getContent());
                            questionIds[i] = q.getDailyQuestionId();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<TodayQuestionsData>> call, Throwable t) {
                        if (isAdded()) {
                            Toast.makeText(requireContext(),
                                    "질문을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void submitJournal(View view) {
        EditText etTitle = view.findViewById(R.id.et_title);
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            view.findViewById(R.id.btn_done).setEnabled(true);
            return;
        }
        if (validCount == 0) {
            Toast.makeText(requireContext(), "질문을 불러오는 중입니다.", Toast.LENGTH_SHORT).show();
            view.findViewById(R.id.btn_done).setEnabled(true);
            return;
        }

        List<AnswerRequest> answers = new ArrayList<>();
        for (int i = 0; i < validCount; i++) {
            String content = etViews[i].getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "모든 답변을 입력해주세요.", Toast.LENGTH_SHORT).show();
                view.findViewById(R.id.btn_done).setEnabled(true);
                return;
            }
            answers.add(new AnswerRequest(questionIds[i], content));
        }

        String token = "Bearer " + tokenManager.getAccessToken();
        ApiClient.getQuestionService()
                .submit(token, new SubmitRequest(title, answers))
                .enqueue(new Callback<ApiResponse<SubmitData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<SubmitData>> call,
                                           Response<ApiResponse<SubmitData>> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null) {
                            long journalId = response.body().getData().getJournalId();
                            JournalCompleteFragment fragment = new JournalCompleteFragment();
                            Bundle args = new Bundle();
                            args.putLong("journalId", journalId);
                            fragment.setArguments(args);
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_frame, fragment)
                                    .commit();
                        } else {
                            Toast.makeText(requireContext(),
                                    ApiErrorUtil.parseError(response), Toast.LENGTH_SHORT).show();
                            submitted = false;
                            view.findViewById(R.id.btn_done).setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<SubmitData>> call, Throwable t) {
                        if (isAdded()) {
                            submitted = false;
                            view.findViewById(R.id.btn_done).setEnabled(true);
                            Toast.makeText(requireContext(),
                                    "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
