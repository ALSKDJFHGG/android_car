package com.example.myapplication.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.ExamRecord;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private List<ExamRecord> list;

    public RecordAdapter(List<ExamRecord> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExamRecord record = list.get(position);

        // 显示开始时间，如果没有则显示结束时间
        String time = record.startTime != null ? record.startTime : record.endTime;
        holder.tvTime.setText(time);

        // 显示分数
        holder.tvScore.setText(record.score + "");

        // 判断及格状态 (>=90分及格)
        if (record.score != null && record.score >= 90) {
            // 合格：绿色
            holder.tvScore.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvStatus.setText("合格");
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            // 不合格：红色
            holder.tvScore.setTextColor(Color.parseColor("#F44336"));
            holder.tvStatus.setText("不合格");
            holder.tvStatus.setTextColor(Color.parseColor("#F44336"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvScore, tvStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_exam_time);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvStatus = itemView.findViewById(R.id.tv_pass_status);
        }
    }
}