package com.example.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.OptionAdapter;
import com.example.myapplication.model.BaseResponse;
import com.example.myapplication.model.ExamRecord;
import com.example.myapplication.model.ExamRecordDetail;
import com.example.myapplication.model.Question;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.util.UserManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamActivity extends AppCompatActivity {

    // UI 控件
    private TextView tvProgress, tvTimer, tvContent, tvType, tvAnalysisResult, tvTitle;
    private RecyclerView rvOptions;
    private Button btnPrev, btnNext, btnSubmit;
    private View layoutAnalysis;

    private ProgressDialog loadingDialog;
    private android.widget.ImageView ivQuestionImage;

    // 数据变量
    private List<Question> questionList = new ArrayList<>();
    private int currentPosition = 0; // 当前题目下标
    private OptionAdapter optionAdapter;

    // 用于暂存用户每一题的答案 Map<题目下标, 答案字符串>
    private Map<Integer, String> userAnswers = new HashMap<>();

    // 模式相关
    private String examType = "order";
    private String subject = "科目一"; // ★★★ 新增：当前科目 ★★★
    private CountDownTimer timer;

    // 记录考试开始时间
    private String startTimeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        // 1. 获取传递过来的参数
        examType = getIntent().getStringExtra("exam_type");
        if (examType == null) examType = "order";

        // ★★★ 获取科目参数 (默认科目一) ★★★
        subject = getIntent().getStringExtra("subject");
        if (subject == null) subject = "科目一";

        // 2. 记录开始时间
        startTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());

        initViews();
        loadQuestions();
    }

    private void initViews() {
        tvProgress = findViewById(R.id.tv_exam_progress);
        tvTimer = findViewById(R.id.tv_timer);
        tvContent = findViewById(R.id.tv_question_content);
        tvType = findViewById(R.id.tv_question_type);
        tvAnalysisResult = findViewById(R.id.tv_analysis_result);
        ivQuestionImage = findViewById(R.id.iv_question_image);

        // 绑定解析相关控件

        layoutAnalysis = findViewById(R.id.layout_analysis);

        // 绑定标题 (显示：科目一 - 模拟考试)
        tvTitle = findViewById(R.id.tv_exam_title);

        rvOptions = findViewById(R.id.rv_options);
        rvOptions.setLayoutManager(new LinearLayoutManager(this));
        optionAdapter = new OptionAdapter();
        rvOptions.setAdapter(optionAdapter);

        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        btnSubmit = findViewById(R.id.btn_submit);

        // 构建标题文字
        String typeName = "顺序练习";
        if ("random".equals(examType)) typeName = "随机练习";
        else if ("exam".equals(examType)) typeName = "模拟考试";

        if (tvTitle != null) {
            tvTitle.setText(subject + " · " + typeName);
        }

        // 根据模式调整 UI
        if ("exam".equals(examType)) {
            startTimer();
            btnSubmit.setText("交 卷");
        } else {
            tvTimer.setVisibility(View.GONE);
            btnSubmit.setText("查看解析");
        }

        // --- 点击事件监听 ---

        btnPrev.setOnClickListener(v -> {
            if (currentPosition > 0) {
                saveCurrentAnswer();
                currentPosition--;
                showQuestion();
            } else {
                Toast.makeText(this, "已经是第一题了", Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (questionList.isEmpty()) return;
            if (currentPosition < questionList.size() - 1) {
                saveCurrentAnswer();
                currentPosition++;
                showQuestion();
            } else {
                Toast.makeText(this, "已经是最后一题了", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (questionList.isEmpty()) return;
            saveCurrentAnswer();

            if ("exam".equals(examType)) {
                showSubmitDialog();
            } else {
                checkCurrentAnswer();
            }
        });
    }

    // --- 网络请求部分 ---

    private void loadQuestions() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("正在加载 " + subject + " 题库...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        Call<BaseResponse<List<Question>>> call;

        // ★★★ 在这里把 subject 传给后端 ★★★
        if ("random".equals(examType) || "exam".equals(examType)) {
            call = RetrofitClient.getInstance().getApi().getRandomQuestions(subject);
        } else {
            call = RetrofitClient.getInstance().getApi().getAllQuestions(subject);
        }

        call.enqueue(new Callback<BaseResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Question>>> call, Response<BaseResponse<List<Question>>> response) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                if (response.body() != null && response.body().isSuccess()) {
                    questionList = response.body().data;

                    if (questionList != null && !questionList.isEmpty()) {
                        for (Question q : questionList) {
                            q.flattenOptions();
                        }
                        showQuestion();
                    } else {
                        Toast.makeText(ExamActivity.this, "暂无题目数据", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ExamActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Question>>> call, Throwable t) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Toast.makeText(ExamActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ExamActivity", "网络错误详情", t);
            }
        });
    }

    // --- 核心逻辑部分 ---

    private void showQuestion() {
        Question q = questionList.get(currentPosition);

        tvProgress.setText("第 " + (currentPosition + 1) + " / " + questionList.size() + " 题");
        tvContent.setText(q.content);

        // 设置题型标签
        if ("1".equals(q.type)) {
            tvType.setText("单选题");
        } else if ("2".equals(q.type)) {
            tvType.setText("判断题");
        } else {
            tvType.setText("多选题");
        }

        if (q.imageUrl != null && !q.imageUrl.isEmpty() && !"NULL".equalsIgnoreCase(q.imageUrl)) {

            ivQuestionImage.setVisibility(View.VISIBLE); // 显示图片区域


            String fileName =q.imageUrl;
            String fullUrl = "http://t7sxw4srx.hd-bkt.clouddn.com/" +"images/"+fileName;

            Log.i("图片", "showQuestion: "+fullUrl);


            // 使用 Glide 加载
            com.bumptech.glide.Glide.with(this)
                    .load(fullUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery) // 加载中显示的图
                    .error(android.R.drawable.stat_notify_error)     // 加载失败显示的图
                    .into(ivQuestionImage);

        } else {
            // 没有图片，隐藏 ImageView，防止占用空间
            ivQuestionImage.setVisibility(View.GONE);
        }
        // 构造选项
        List<OptionAdapter.OptionItem> options = new ArrayList<>();
        if (!TextUtils.isEmpty(q.optionA)) options.add(new OptionAdapter.OptionItem("A", q.optionA));
        if (!TextUtils.isEmpty(q.optionB)) options.add(new OptionAdapter.OptionItem("B", q.optionB));
        if (!TextUtils.isEmpty(q.optionC) && !"NULL".equalsIgnoreCase(q.optionC)) {
            options.add(new OptionAdapter.OptionItem("C", q.optionC));
        }
        if (!TextUtils.isEmpty(q.optionD) && !"NULL".equalsIgnoreCase(q.optionD)) {
            options.add(new OptionAdapter.OptionItem("D", q.optionD));
        }

        // type=3 为多选，其他单选
        boolean isMulti = "3".equals(q.type);
        optionAdapter.setNewData(options, isMulti);

        // 恢复选中状态
        String savedAns = userAnswers.get(currentPosition);
        optionAdapter.setSelectedAnswer(savedAns);

        layoutAnalysis.setVisibility(View.GONE);
    }

    private void saveCurrentAnswer() {
        String myChoice = optionAdapter.getUserAnswer();
        userAnswers.put(currentPosition, myChoice);
    }

    private void checkCurrentAnswer() {
        Question q = questionList.get(currentPosition);
        String myChoice = userAnswers.get(currentPosition);

        layoutAnalysis.setVisibility(View.VISIBLE);

        if (myChoice != null && myChoice.equals(q.answer)) {
            tvAnalysisResult.setText("回答正确！");
            tvAnalysisResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvAnalysisResult.setText("回答错误，正确答案：" + q.answer);
            tvAnalysisResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

    }

    private void showSubmitDialog() {
        int answeredCount = 0;
        for (String ans : userAnswers.values()) {
            if (!TextUtils.isEmpty(ans)) answeredCount++;
        }

        new AlertDialog.Builder(this)
                .setTitle("确认交卷")
                .setMessage("共 " + questionList.size() + " 道题，已做 " + answeredCount + " 道。\n确定要提交试卷吗？")
                .setPositiveButton("交卷", (dialog, which) -> calculateAndUploadScore())
                .setNegativeButton("继续做题", null)
                .show();
    }

    private void calculateAndUploadScore() {
        int correctCount = 0;

        // 1. 准备一个列表，用来装每一道题的答题详情
        List<ExamRecordDetail> details = new ArrayList<>();

        for (int i = 0; i < questionList.size(); i++) {
            Question q = questionList.get(i);
            String myAns = userAnswers.get(i); // 用户选的答案 (可能为 null)

            // 处理未作答的情况
            String finalUserAns = (myAns == null) ? "" : myAns;

            // 判断对错 (true=对, false=错)
            boolean isCorrect = false;
            if (myAns != null && myAns.equals(q.answer)) {
                correctCount++;
                isCorrect = true;
            }

            // ★★★ 创建明细对象并加入列表 ★★★
            // 无论对错都要记录，这样后端才能完整还原考试场景
            details.add(new ExamRecordDetail(q.id, finalUserAns, isCorrect));
        }

        // 计算总分
        double totalScore = 0.0;
        if (questionList.size() > 0) {
            totalScore = (double) correctCount * 100 / questionList.size();
        }

        if (timer != null) timer.cancel();

        // 2. 把 details 列表传给上传方法
        uploadScore(totalScore, details);
    }

    // 增加参数 List<ExamRecordDetail> details
    private void uploadScore(double scoreDouble, List<ExamRecordDetail> details) {
        Long currentUserId = UserManager.getInstance(this).getUserId();
        // 实际开发从 SharedPreferences 取
        String endTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());

        // 随机练习没有 paperId，传 0L
        Long paperId = null;

        // 构造 ExamRecord 对象
        // 注意：ExamRecord 构造函数里如果不包含 detailList，我们需要手动赋值
        ExamRecord record = new ExamRecord(
                currentUserId,
                scoreDouble,
                startTimeStr,
                endTimeStr,
                subject
        );

        // 手动补全字段
        record.paperId = paperId;
        record.subject = subject; // 确保 subject 也传过去

        // ★★★ 关键：把明细列表塞进去 ★★★
        record.detailList = details;

        // 发送请求
        RetrofitClient.getInstance().getApi().submitScore(record).enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                showResultDialog((int) scoreDouble);
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                showResultDialog((int) scoreDouble);
                Toast.makeText(ExamActivity.this, "成绩上传失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResultDialog(int score) {
        new AlertDialog.Builder(this)
                .setTitle("考试结束")
                .setMessage("科目：" + subject + "\n你的最终得分是：" + score + " 分")
                .setPositiveButton("退出", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void startTimer() {
        long totalMillis = 45 * 60 * 1000;
        timer = new CountDownTimer(totalMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long min = millisUntilFinished / 60000;
                long sec = (millisUntilFinished % 60000) / 1000;
                tvTimer.setText(String.format("%02d:%02d", min, sec));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                Toast.makeText(ExamActivity.this, "考试时间到，自动交卷！", Toast.LENGTH_LONG).show();
                calculateAndUploadScore();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}