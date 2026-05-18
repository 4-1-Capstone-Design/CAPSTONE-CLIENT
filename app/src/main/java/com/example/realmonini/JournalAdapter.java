package com.example.realmonini;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private List<String> dateList;
    private List<String> titleList;
    private List<String> keywordList;
    private OnItemClickListener listener;

    public JournalAdapter(List<String> dateList, List<String> titleList, List<String> keywordList, OnItemClickListener listener) {
        this.dateList = dateList;
        this.titleList = titleList;
        this.keywordList = keywordList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_journal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvDate.setText(dateList.get(position));
        holder.tvTitle.setText(titleList.get(position));
        holder.tvKeyword.setText(keywordList.get(position));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTitle, tvKeyword;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_item_date);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvKeyword = itemView.findViewById(R.id.tv_item_keyword);
        }
    }
}