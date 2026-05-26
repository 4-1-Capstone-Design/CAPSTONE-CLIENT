package com.example.realmonini;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class JournalReplyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal_reply, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            String keywords = args.getString("keywords", "");
            String reply    = args.getString("reply", "");

            TextView tvTitle   = view.findViewById(R.id.tv_reply_title);
            TextView tvContent = view.findViewById(R.id.tv_reply_content);

            if (!keywords.isEmpty()) tvTitle.setText(keywords);
            if (!reply.isEmpty())    tvContent.setText(reply);
        }

        view.findViewById(R.id.btn_done).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, new HomeFragment())
                        .commit());
    }
}
