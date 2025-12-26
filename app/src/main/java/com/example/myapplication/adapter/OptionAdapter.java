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

/**
 * 题目选项适配器
 * 用于显示单选题、多选题和判断题的选项
 * 支持选项的选中状态管理和UI更新
 *
 * @author 开发者
 * @version 1.0
 */
public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ViewHolder> {

    /**
     * 选项数据模型
     * 表示一个题目选项，包含选项标签（如"A"、"B"）和选项内容
     */
    public static class OptionItem {
        /** 选项标签，如"A"、"B"、"C"、"D" */
        public String label;
        /** 选项内容文本 */
        public String text;

        /**
         * 构造选项项
         * @param label 选项标签
         * @param text 选项内容
         */
        public OptionItem(String label, String text) {
            this.label = label;
            this.text = text;
        }
    }

    /** 选项数据列表 */
    private List<OptionItem> mList = new ArrayList<>();

    /** 用户选中的选项标签列表，支持多选 */
    private List<String> mSelectedLabels = new ArrayList<>();

    /** 是否为多选模式 */
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

        // 设置选项标签和内容
        holder.tvLabel.setText(item.label);
        holder.tvText.setText(item.text);

        // 检查当前选项是否被选中
        boolean isSelected = mSelectedLabels.contains(item.label);

        // 根据选中状态更新UI
        updateOptionAppearance(holder, isSelected);

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> handleOptionClick(item.label));
    }

    /**
     * 根据选中状态更新选项的外观
     * @param holder ViewHolder
     * @param isSelected 是否选中
     */
    private void updateOptionAppearance(ViewHolder holder, boolean isSelected) {
        if (isSelected) {
            // 选中状态：蓝色背景，蓝色圆圈
            holder.root.setBackgroundResource(R.drawable.bg_option_selected);
            holder.tvLabel.setBackgroundResource(R.drawable.shape_circle_blue);
            holder.tvLabel.setTextColor(Color.WHITE);
        } else {
            // 未选中状态：白色背景，灰色圆圈
            holder.root.setBackgroundResource(R.drawable.bg_option_normal);
            holder.tvLabel.setBackgroundResource(R.drawable.shape_circle_gray);
            holder.tvLabel.setTextColor(Color.WHITE);
        }
    }

    /**
     * 处理选项点击事件
     * @param label 被点击的选项标签
     */
    private void handleOptionClick(String label) {
        if (isMultiSelect) {
            // 多选模式：切换选中状态
            if (mSelectedLabels.contains(label)) {
                mSelectedLabels.remove(label); // 取消选中
            } else {
                mSelectedLabels.add(label);    // 设为选中
            }
        } else {
            // 单选模式：清除其他选择，只选中当前项
            mSelectedLabels.clear();
            mSelectedLabels.add(label);
        }

        // 刷新列表显示
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    /**
     * ViewHolder类
     * 持有选项项的视图引用，提高RecyclerView性能
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        /** 选项根布局 */
        LinearLayout root;
        /** 选项标签文本视图 */
        TextView tvLabel;
        /** 选项内容文本视图 */
        TextView tvText;

        /**
         * 构造函数
         * @param itemView 选项项的视图
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 绑定布局中的视图组件
            root = itemView.findViewById(R.id.layout_item_root);
            tvLabel = itemView.findViewById(R.id.tv_option_label);
            tvText = itemView.findViewById(R.id.tv_option_text);
        }
    }
}