package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Question;
import java.util.List;

public class MistakeAdapter extends RecyclerView.Adapter<MistakeAdapter.ViewHolder> {

    private List<Question> list;
    private OnItemClickListener listener;

    // 点击事件接口
    public interface OnItemClickListener {
        void onClick(Question question);
    }

    public MistakeAdapter(List<Question> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mistake, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question q = list.get(position);

        // 1. 显示题干
        holder.tvContent.setText(q.content);

        // 2. 设置题型标签
        if ("1".equals(q.type)) holder.tvType.setText("单选");
        else if ("2".equals(q.type)) holder.tvType.setText("判断");
        else holder.tvType.setText("多选");

        // ★★★ 优化点：如果有错题记录，显示在题目下方 (可选) ★★★
        // 你的 item_mistake.xml 里如果没地方放，可以追加到 content 后面，或者利用 TextView 的 append
        if (q.userAnswer != null && !q.userAnswer.isEmpty()) {
            String text = q.content + "\n\n(我误选了: " + q.userAnswer + ")";
            holder.tvContent.setText(text);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(q);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvType;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvType = itemView.findViewById(R.id.tv_type_tag);
        }
    }
}