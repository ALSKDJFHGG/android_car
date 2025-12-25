package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // 导入 ImageView
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // 导入 Glide
import com.example.myapplication.R;
import com.example.myapplication.model.Question;
import java.util.List;

public class MistakeAdapter extends RecyclerView.Adapter<MistakeAdapter.ViewHolder> {

    private List<Question> list;
    private OnItemClickListener listener;

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

        holder.tvContent.setText(q.content);

        // 设置题型
        if ("1".equals(q.type)) holder.tvType.setText("单选");
        else if ("2".equals(q.type)) holder.tvType.setText("判断");
        else holder.tvType.setText("多选");

        // 显示用户选错的答案
        if (q.userAnswer != null && !q.userAnswer.isEmpty()) {
            holder.tvMyAnswer.setText("我的误选：" + q.userAnswer);
            holder.tvMyAnswer.setVisibility(View.VISIBLE);
        } else {
            holder.tvMyAnswer.setVisibility(View.GONE);
        }

        // ★★★★★ 核心：加载图片逻辑 ★★★★★
        if (q.imageUrl != null && !q.imageUrl.isEmpty() && !"NULL".equalsIgnoreCase(q.imageUrl)) {

            holder.ivImg.setVisibility(View.VISIBLE); // 有图，显示

            // 1. 处理 URL (和 ExamActivity 逻辑一致)
            String fileName = q.imageUrl;
            // 2. 拼接服务器地址 (记得换成你队友的真实IP)
            // 建议把这个 BaseUrl 提取成常量放在 RetrofitClient 里
            String fullUrl = "http://t7sxw4srx.hd-bkt.clouddn.com/" +"images/" +fileName;

            // 3. Glide 加载
            Glide.with(holder.itemView.getContext())
                    .load(fullUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivImg);

        } else {
            // 没有图片，必须设为 GONE，否则 RecyclerView 复用时会显示错乱
            holder.ivImg.setVisibility(View.GONE);
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
        TextView tvContent, tvType, tvMyAnswer;
        ImageView ivImg; // ★★★ 新增 ImageView ★★★

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvType = itemView.findViewById(R.id.tv_type_tag);
            tvMyAnswer = itemView.findViewById(R.id.tv_my_wrong_answer);

            // ★★★ 绑定图片 ID ★★★
            ivImg = itemView.findViewById(R.id.iv_mistake_img);
        }
    }
}