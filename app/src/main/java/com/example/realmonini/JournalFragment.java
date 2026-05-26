package com.example.realmonini;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realmonini.network.ApiClient;
import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.JournalDetailData;
import com.example.realmonini.network.dto.JournalItem;
import com.example.realmonini.network.dto.JournalListData;
import com.example.realmonini.util.ApiErrorUtil;
import com.example.realmonini.util.TokenManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JournalFragment extends Fragment {

    private static final String TAG = "JournalFragment";
    private static final int PAGE_SIZE = 10;

    private RecyclerView rv;
    private JournalAdapter adapter;
    private final List<JournalItem> journalList = new ArrayList<>();

    private Long nextCursor = null;
    private boolean hasNext = true;
    private boolean isLoading = false;

    private TextView tvDateFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDateFilter = view.findViewById(R.id.tv_date_filter);

        Calendar today = Calendar.getInstance();
        updateDateLabel(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH));

        tvDateFilter.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    R.style.DatePickerTheme,
                    (datePicker, year, month, day) -> searchByDate(year, month + 1, day),
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        rv = view.findViewById(R.id.rv_journal);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);

        adapter = new JournalAdapter(journalList, item -> {
            JournalDetailFragment detail = new JournalDetailFragment();
            Bundle args = new Bundle();
            args.putLong("journalId", item.getJournalId());
            args.putString("title", item.getTitle() != null ? item.getTitle() : "");
            args.putString("content", item.getContent() != null ? item.getContent() : "");
            args.putString("journalDate", item.getJournalDate() != null ? item.getJournalDate() : "");
            detail.setArguments(args);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame, detail)
                    .addToBackStack(null)
                    .commit();
        });
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1) && hasNext && !isLoading) {
                    loadJournals();
                }
            }
        });

        loadJournals();
    }

    private void updateDateLabel(int year, int month, int day) {
        tvDateFilter.setText(String.format("%d.%02d.%02d", year, month, day));
    }

    private void searchByDate(int year, int month, int day) {
        updateDateLabel(year, month, day);

        TokenManager tokenManager = new TokenManager(requireContext());
        String token = "Bearer " + tokenManager.getAccessToken();

        ApiClient.getJournalService().getByDate(token, year, month, day)
                .enqueue(new Callback<ApiResponse<JournalDetailData>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<JournalDetailData>> call,
                                           @NonNull Response<ApiResponse<JournalDetailData>> response) {
                        if (!isAdded()) return;
                        if (!response.isSuccessful() || response.body() == null
                                || response.body().getData() == null) {
                            Toast.makeText(requireContext(),
                                    "해당 날짜의 저널이 없어요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JournalDetailData data = response.body().getData();
                        JournalDetailFragment detail = new JournalDetailFragment();
                        Bundle args = new Bundle();
                        args.putLong("journalId", data.getJournalId());
                        args.putString("title", data.getTitle() != null ? data.getTitle() : "");
                        args.putString("journalDate", data.getJournalDate() != null ? data.getJournalDate() : "");
                        detail.setArguments(args);
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_frame, detail)
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<JournalDetailData>> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Log.e(TAG, "날짜 검색 오류", t);
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했어요.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadJournals() {
        if (isLoading || !hasNext) return;
        isLoading = true;

        TokenManager tokenManager = new TokenManager(requireContext());
        String token = "Bearer " + tokenManager.getAccessToken();

        ApiClient.getJournalService().getJournals(token, nextCursor, PAGE_SIZE)
                .enqueue(new Callback<ApiResponse<JournalListData>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<JournalListData>> call,
                                           @NonNull Response<ApiResponse<JournalListData>> response) {
                        isLoading = false;
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e(TAG, "응답 실패: " + response.code());
                            if (getContext() != null) {
                                Toast.makeText(getContext(),
                                        ApiErrorUtil.parseError(response), Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                        JournalListData data = response.body().getData();
                        if (data == null) return;

                        int insertStart = journalList.size();
                        journalList.addAll(data.getJournals());
                        adapter.notifyItemRangeInserted(insertStart, data.getJournals().size());

                        nextCursor = data.getNextCursor();
                        hasNext = data.isHasNext();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<JournalListData>> call,
                                          @NonNull Throwable t) {
                        isLoading = false;
                        Log.e(TAG, "네트워크 오류", t);
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
