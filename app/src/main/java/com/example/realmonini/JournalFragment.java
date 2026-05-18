package com.example.realmonini;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class JournalFragment extends Fragment {

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

        // 임시 더미 데이터
        List<String> dates = Arrays.asList("2026-04-30", "2026-04-30", "2026-04-30", "2026-04-30", "2026-04-30", "2026-04-30");
        List<String> titles = Arrays.asList("모닝저널 제목뜨", "", "", "", "", "");
        List<String> keywords = Arrays.asList("성장욕구 , 의욕 , 내면의 단단함", "성장욕구 , 의욕 , 내면의 단단함", "성장욕구 , 의욕 , 내면의 단단함", "성장욕구 , 의욕 , 내면의 단단함", "성장욕구 , 의욕 , 내면의 단단함", "성장욕구 , 의욕 , 내면의 단단함");

        RecyclerView rv = view.findViewById(R.id.rv_journal);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new JournalAdapter(dates, titles, keywords, position -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame, new JournalDetailFragment())
                    .commit();
        }));
    }
}