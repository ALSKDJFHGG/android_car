package com.example.myapplication.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ViewHolder> {

    // 数据模型：选项 (例如 Label="A", Text="违反交通信号灯")
    public static class OptionItem {
        public String label;
        public String text;

        public OptionItem(String label, String text) {
            this.label = label;
            this.text = text;
        }
    }

    private List<OptionItem> mList = new ArrayList<>();

    // 记录用户选中的 Label，例如 ["A", "C"]
    private List<String> mSelectedLabels = new ArrayList<>();

    // 是否多选模式
    private boolean isMultiSelect = false;

    /**
     * 设置新的题目选项数据
     * @param list 选项列表
     * @param isMulti 是否是多选题
     */
    public void setNewData(List<OptionItem> list, boolean isMulti) {
        this.mList = list;
        this.isMultiSelect = isMulti;
        this.mSelectedLabels.clear(); // 换题时清空选中状态
        notifyDataSetChanged();
    }

    /**
     * 设置已选答案 (用于翻页回显)
     * @param answerStr 例如 "AB"
     */
    public void setSelectedAnswer(String answerStr) {
        mSelectedLabels.clear();
        if (answerStr != null && !answerStr.isEmpty()) {
            // 解析字符串，把 "AB" 拆成 "A" 和 "B"
            for (int i = 0; i < answerStr.length(); i++) {
                String label = String.valueOf(answerStr.charAt(i));
                mSelectedLabels.add(label);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 获取用户当前选的答案
     * @return 例如 "AB" (自动排序)
     */
    public String getUserAnswer() {
        if (mSelectedLabels.isEmpty()) return "";

        // 排序，保证先选B后选A，返回的也是 "AB"
        Collections.sort(mSelectedLabels);

        StringBuilder sb = new StringBuilder();
        for (String s : mSelectedLabels) {
            sb.append(s);
        }
        return sb.toString();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 绑定 item_option.xml 布局
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OptionItem item = mList.get(position);

        holder.tvLabel.setText(item.label);
        holder.tvText.setText(item.text);

        // 判断当前这个选项是否被选中
        boolean isSelected = mSelectedLabels.contains(item.label);

        // 根据选中状态改变 UI (背景色、字体颜色)
        if (isSelected) {
            // 选中状态：背景变蓝，圆圈变蓝
            holder.root.setBackgroundResource(R.drawable.bg_option_selected);
            holder.tvLabel.setBackgroundResource(R.drawable.shape_circle_blue);
            holder.tvLabel.setTextColor(Color.WHITE);
        } else {
            // 未选中状态：背景白色，圆圈灰色
            holder.root.setBackgroundResource(R.drawable.bg_option_normal);
            holder.tvLabel.setBackgroundResource(R.drawable.shape_circle_gray);
            holder.tvLabel.setTextColor(Color.WHITE);
        }

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            String label = item.label;

            if (isMultiSelect) {
                // --- 多选逻辑 ---
                if (mSelectedLabels.contains(label)) {
                    mSelectedLabels.remove(label); // 已选 -> 取消
                } else {
                    mSelectedLabels.add(label);    // 未选 -> 选中
                }
            } else {
                // --- 单选逻辑 ---
                mSelectedLabels.clear();       // 清空其他的
                mSelectedLabels.add(label);    // 选中当前的
            }

            // 刷新列表显示
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    // ViewHolder 类
    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout root;
        TextView tvLabel, tvText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 绑定 item_option.xml 里的 ID
            root = itemView.findViewById(R.id.layout_item_root);
            tvLabel = itemView.findViewById(R.id.tv_option_label);
            tvText = itemView.findViewById(R.id.tv_option_text);
        }
    }
}