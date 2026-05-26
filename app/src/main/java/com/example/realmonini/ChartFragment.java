package com.example.realmonini;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.realmonini.network.ApiClient;
import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.EmotionDistribution;
import com.example.realmonini.network.dto.MonthlyStatsData;
import com.example.realmonini.util.ApiErrorUtil;
import com.example.realmonini.util.TokenManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartFragment extends Fragment {

    private static final String TAG = "ChartFragment";

    private static final int[] CHART_COLORS = {
        Color.parseColor("#A8C5A0"),
        Color.parseColor("#C5B8A0"),
        Color.parseColor("#A0B8C5"),
        Color.parseColor("#C5A0B8"),
        Color.parseColor("#B8C5A0"),
        Color.parseColor("#C5A8A0"),
        Color.parseColor("#A0C5B8"),
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar now = Calendar.getInstance();
        int year  = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;

        String token = "Bearer " + new TokenManager(requireContext()).getAccessToken();

        ApiClient.getStatsService().getMonthly(token, year, month)
                .enqueue(new Callback<ApiResponse<MonthlyStatsData>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<MonthlyStatsData>> call,
                                           @NonNull Response<ApiResponse<MonthlyStatsData>> response) {
                        if (!isAdded()) return;
                        if (!response.isSuccessful() || response.body() == null
                                || response.body().getData() == null) {
                            Log.e(TAG, "통계 조회 실패: " + response.code());
                            Toast.makeText(requireContext(),
                                    ApiErrorUtil.parseError(response), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        bindStats(view, response.body().getData());
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<MonthlyStatsData>> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Log.e(TAG, "네트워크 오류", t);
                        Toast.makeText(requireContext(), "통계를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void bindStats(View view, MonthlyStatsData data) {
        TextView tvTopEmotion  = view.findViewById(R.id.tv_top_emotion);
        TextView tvJournalCount = view.findViewById(R.id.tv_journal_count);
        PieChart pieChart       = view.findViewById(R.id.pie_chart);
        LinearLayout legend     = view.findViewById(R.id.container_legend);

        // 1등 감정 키워드
        if (data.getTopEmotion() != null) {
            tvTopEmotion.setText(data.getTopEmotion());
        }

        // 저널 수
        tvJournalCount.setText(data.getMonth() + "월 저널 " + data.getJournalCount() + "개");

        // 도넛 차트
        List<EmotionDistribution> dist = data.getEmotionDistribution();
        if (dist == null || dist.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        for (EmotionDistribution e : dist) {
            entries.add(new PieEntry((float) e.getPercentage(), e.getEmotion()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(CHART_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setDrawValues(false);

        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.setHoleRadius(52f);
        pieChart.setTransparentCircleRadius(57f);
        pieChart.setHoleColor(Color.parseColor("#FAFAF8"));
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setTouchEnabled(false);
        pieChart.invalidate();

        // 커스텀 범례
        legend.removeAllViews();
        for (int i = 0; i < dist.size(); i++) {
            EmotionDistribution e = dist.get(i);
            addLegendItem(legend, CHART_COLORS[i % CHART_COLORS.length],
                    e.getEmotion(), e.getPercentage());
        }
    }

    private void addLegendItem(LinearLayout container, int color, String emotion, double percentage) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = dp(6);
        row.setLayoutParams(rowParams);

        View dot = new View(requireContext());
        int size = dp(10);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(size, size);
        dotParams.rightMargin = dp(8);
        dot.setLayoutParams(dotParams);
        dot.setBackgroundColor(color);
        row.addView(dot);

        TextView tv = new TextView(requireContext());
        tv.setText(emotion + "  " + String.format("%.1f", percentage) + "%");
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tv.setTextColor(0xFF3D3D3D);
        row.addView(tv);

        container.addView(row);
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value,
                requireContext().getResources().getDisplayMetrics());
    }
}
