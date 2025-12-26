package com.example.myapplication.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ExamPaper;

import java.util.List;

/**
 * 考试列表适配器
 * 用于显示考试试卷列表
 *
 * @author 开发者
 * @version 1.0
 */
public class ExamListAdapter extends RecyclerView.Adapter<ExamListAdapter.ViewHolder> {

    private List<ExamPaper> examList;
    private OnItemClickListener onItemClickListener;

    /**
     * 点击监听器接口
     */
    public interface OnItemClickListener {
        void onItemClick(ExamPaper examPaper);
    }

    public ExamListAdapter(List<ExamPaper> examList) {
        this.examList = examList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_paper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < 0 || position >= examList.size()) {
            Log.e("ExamListAdapter", "Position out of bounds: " + position + ", list size: " + examList.size());
            return;
        }

        ExamPaper examPaper = examList.get(position);
        if (examPaper == null) {
            Log.e("ExamListAdapter", "ExamPaper is null at position: " + position);
            return;
        }

        holder.bind(examPaper);
        holder.setExamPaper(examPaper);

        // 设置点击监听器 - 使用getAdapterPosition()确保获取正确的position
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            Log.d("ExamListAdapter", "Item clicked at adapter position: " + adapterPosition);
            
            if (adapterPosition == RecyclerView.NO_POSITION) {
                Log.e("ExamListAdapter", "Invalid adapter position");
                return;
            }

            if (adapterPosition < 0 || adapterPosition >= examList.size()) {
                Log.e("ExamListAdapter", "Adapter position out of bounds: " + adapterPosition);
                return;
            }

            ExamPaper clickedPaper = examList.get(adapterPosition);
            Log.d("ExamListAdapter", "Clicked paper: " + (clickedPaper != null ? clickedPaper.getPaperName() : "null"));
            
            if (onItemClickListener != null && clickedPaper != null) {
                onItemClickListener.onItemClick(clickedPaper);
            } else {
                Log.e("ExamListAdapter", "onItemClickListener is null or clickedPaper is null");
            }
        });
    }

    @Override
    public int getItemCount() {
        return examList != null ? examList.size() : 0;
    }

    /**
     * ViewHolder类
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPaperName;
        private TextView tvSubject;
        private TextView tvQuestionCount;
        private TextView tvExamTime;
        private ExamPaper examPaper;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaperName = itemView.findViewById(R.id.tv_paper_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvQuestionCount = itemView.findViewById(R.id.tv_question_count);
            tvExamTime = itemView.findViewById(R.id.tv_exam_time);
        }

        public void bind(ExamPaper examPaper) {
            if (examPaper == null) {
                Log.e("ExamListAdapter", "bind: examPaper is null");
                return;
            }
            tvPaperName.setText(examPaper.getPaperName() != null ? examPaper.getPaperName() : "");
            tvSubject.setText(examPaper.getSubject() != null ? examPaper.getSubject() : "");
            tvQuestionCount.setText(examPaper.getFormattedQuestionCount());
            tvExamTime.setText(examPaper.getFormattedExamTime());
        }

        public void setExamPaper(ExamPaper examPaper) {
            this.examPaper = examPaper;
        }

        public ExamPaper getExamPaper() {
            return examPaper;
        }
    }
}
