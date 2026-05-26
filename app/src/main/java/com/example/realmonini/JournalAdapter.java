package com.example.realmonini;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realmonini.network.dto.JournalItem;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(JournalItem item);
    }

    private final List<JournalItem> items;
    private final OnItemClickListener listener;

    public JournalAdapter(List<JournalItem> items, OnItemClickListener listener) {
        this.items = items;
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
        JournalItem item = items.get(position);
        holder.tvDate.setText(item.getJournalDate());
        holder.tvTitle.setText(item.getTitle() != null ? item.getTitle() : "");
        holder.tvKeyword.setText(item.getContent() != null ? item.getContent() : "");
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
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
