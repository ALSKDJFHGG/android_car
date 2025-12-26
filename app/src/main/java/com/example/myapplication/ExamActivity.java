package com.example.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.myapplication.util.Constants;
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

/**
 * 考试Activity
 * 支持三种考试模式：
 * - 顺序练习：按题目顺序逐题练习
 * - 随机练习：随机抽取题目进行练习
 * - 模拟考试：限时考试，完成后提交成绩
 *
 * 功能特性：
 * - 支持科目一和科目四的选择
 * - 题目类型：单选题、多选题、判断题
 * - 图片题支持
 * - 考试计时功能
 * - 答案保存和恢复
 * - 成绩上传和记录
 *
 * @author 开发者
 * @version 1.0
 */
public class ExamActivity extends AppCompatActivity {

    // 导入常量引用，方便使用

    // UI 控件
    private TextView tvProgress, tvTimer, tvContent, tvType, tvAnalysisResult, tvTitle;
    private RecyclerView rvOptions;
    private Button btnPrev, btnNext, btnSubmit;
    private View layoutAnalysis;
    private ImageView ivQuestionImage;

    private ProgressDialog loadingDialog;

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
        if (examType == null) examType = Constants.Exam.MODE_ORDER;

        // 获取科目参数 (默认科目一)
        subject = getIntent().getStringExtra("subject");
        if (subject == null) subject = Constants.Exam.SUBJECT_ONE;

        // 2. 记录开始时间
        startTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());

        initViews();
        loadQuestions();
    }

    /**
     * 初始化UI控件和事件监听器
     */
    private void initViews() {
        // 初始化文本视图控件
        tvProgress = findViewById(R.id.tv_exam_progress);
        tvTimer = findViewById(R.id.tv_timer);
        tvContent = findViewById(R.id.tv_question_content);
        tvType = findViewById(R.id.tv_question_type);
        tvAnalysisResult = findViewById(R.id.tv_analysis_result);
        tvTitle = findViewById(R.id.tv_exam_title);

        // 初始化图片控件
        ivQuestionImage = findViewById(R.id.iv_question_image);

        // 初始化布局控件
        layoutAnalysis = findViewById(R.id.layout_analysis);

        // 初始化选项列表
        rvOptions = findViewById(R.id.rv_options);
        rvOptions.setLayoutManager(new LinearLayoutManager(this));
        optionAdapter = new OptionAdapter();
        rvOptions.setAdapter(optionAdapter);

        // 初始化按钮控件
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        btnSubmit = findViewById(R.id.btn_submit);

        // 设置页面标题
        setupTitle();

        // 根据考试模式调整UI
        setupUIMode();

        // 设置按钮点击事件
        setupClickListeners();
    }

    /**
     * 设置页面标题
     */
    private void setupTitle() {
        String typeName = getExamTypeDisplayName(examType);
        if (tvTitle != null) {
            tvTitle.setText(subject + " · " + typeName);
        }
    }

    /**
     * 获取考试类型的显示名称
     */
    private String getExamTypeDisplayName(String examType) {
        switch (examType) {
            case Constants.Exam.MODE_RANDOM:
                return "随机练习";
            case Constants.Exam.MODE_EXAM:
                return "模拟考试";
            case Constants.Exam.MODE_ORDER:
            default:
                return "顺序练习";
        }
    }

    /**
     * 根据考试模式调整UI显示
     */
    private void setupUIMode() {
        if (Constants.Exam.MODE_EXAM.equals(examType)) {
            // 模拟考试模式：显示计时器，提交按钮显示"交卷"
            startTimer();
            btnSubmit.setText("交 卷");
        } else {
            // 练习模式：隐藏计时器，提交按钮显示"查看解析"
            tvTimer.setVisibility(View.GONE);
            btnSubmit.setText("查看解析");
        }
    }

    /**
     * 设置所有按钮的点击事件监听器
     */
    private void setupClickListeners() {
        // 上一题按钮
        btnPrev.setOnClickListener(v -> navigateToPreviousQuestion());

        // 下一题按钮
        btnNext.setOnClickListener(v -> navigateToNextQuestion());

        // 提交/查看解析按钮
        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    /**
     * 导航到上一题
     */
    private void navigateToPreviousQuestion() {
        if (currentPosition > 0) {
            saveCurrentAnswer();
            currentPosition--;
            showQuestion();
        } else {
            Toast.makeText(this, "已经是第一题了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 导航到下一题
     */
    private void navigateToNextQuestion() {
        if (questionList.isEmpty()) return;

        if (currentPosition < questionList.size() - 1) {
            saveCurrentAnswer();
            currentPosition++;
            showQuestion();
        } else {
            Toast.makeText(this, "已经是最后一题了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理提交按钮点击事件
     */
    private void handleSubmit() {
        if (questionList.isEmpty()) return;

        saveCurrentAnswer();

        if (Constants.Exam.MODE_EXAM.equals(examType)) {
            showSubmitDialog();
        } else {
            checkCurrentAnswer();
        }
    }

    // --- 网络请求部分 ---

    private void loadQuestions() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("正在加载 " + subject + " 题库...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        Call<BaseResponse<List<Question>>> call;

        // 根据考试模式选择不同的API接口
        if (Constants.Exam.MODE_RANDOM.equals(examType) || Constants.Exam.MODE_EXAM.equals(examType)) {
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

    /**
     * 显示当前题目
     */
    private void showQuestion() {
        if (questionList == null || questionList.isEmpty() || currentPosition >= questionList.size()) {
            Log.e("ExamActivity", "题目数据异常或索引超出范围");
            return;
        }

        Question q = questionList.get(currentPosition);

        // 更新进度显示
        tvProgress.setText("第 " + (currentPosition + 1) + " / " + questionList.size() + " 题");

        // 设置题目内容
        tvContent.setText(q.content != null ? q.content : "");

        // 设置题型标签
        setQuestionTypeLabel(q.type);

        // 处理题目图片
        handleQuestionImage(q.imageUrl);
        // 设置题目选项
        setupQuestionOptions(q);

        // 恢复用户之前的选择
        restoreUserSelection();

        // 隐藏解析区域
        layoutAnalysis.setVisibility(View.GONE);
    }

    /**
     * 设置题目类型标签
     */
    private void setQuestionTypeLabel(String type) {
        String typeLabel;
        switch (type) {
            case Constants.Exam.QUESTION_TYPE_SINGLE:
                typeLabel = "单选题";
                break;
            case Constants.Exam.QUESTION_TYPE_JUDGE:
                typeLabel = "判断题";
                break;
            case Constants.Exam.QUESTION_TYPE_MULTI:
                typeLabel = "多选题";
                break;
            default:
                typeLabel = "未知题型";
                break;
        }
        tvType.setText(typeLabel);
    }

    /**
     * 处理题目图片显示
     */
    private void handleQuestionImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty() && !Constants.ApiResponse.NULL_VALUE.equalsIgnoreCase(imageUrl)) {
            ivQuestionImage.setVisibility(View.VISIBLE);

            String fullUrl = Constants.Network.IMAGE_BASE_URL + imageUrl;
            Log.i("图片", "加载题目图片: " + fullUrl);

            // 使用 Glide 加载图片
            com.bumptech.glide.Glide.with(this)
                    .load(fullUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .into(ivQuestionImage);
        } else {
            // 没有图片时隐藏ImageView
            ivQuestionImage.setVisibility(View.GONE);
        }
    }

    /**
     * 设置题目选项
     */
    private void setupQuestionOptions(Question q) {
        List<OptionAdapter.OptionItem> options = new ArrayList<>();

        // 添加选项（最多支持4个选项）
        if (!TextUtils.isEmpty(q.optionA)) options.add(new OptionAdapter.OptionItem("A", q.optionA));
        if (!TextUtils.isEmpty(q.optionB)) options.add(new OptionAdapter.OptionItem("B", q.optionB));
        if (!TextUtils.isEmpty(q.optionC) && !"NULL".equalsIgnoreCase(q.optionC)) {
            options.add(new OptionAdapter.OptionItem("C", q.optionC));
        }
        if (!TextUtils.isEmpty(q.optionD) && !"NULL".equalsIgnoreCase(q.optionD)) {
            options.add(new OptionAdapter.OptionItem("D", q.optionD));
        }

        // 判断是否为多选题
        boolean isMultiSelect = Constants.Exam.QUESTION_TYPE_MULTI.equals(q.type);
        optionAdapter.setNewData(options, isMultiSelect);
    }

    /**
     * 恢复用户之前的选择
     */
    private void restoreUserSelection() {
        String savedAnswer = userAnswers.get(currentPosition);
        optionAdapter.setSelectedAnswer(savedAnswer);
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

    /**
     * 开始考试计时器
     */
    private void startTimer() {
        timer = new CountDownTimer(Constants.Exam.EXAM_DURATION_MS, 1000) {
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