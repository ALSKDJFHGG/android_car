package com.example.myapplication.util;

/**
 * 应用常量定义类
 * 集中管理项目中使用的所有常量值
 *
 * @author 开发者
 * @version 1.0
 */
public class Constants {

    /**
     * 网络相关常量
     */
    public static final class Network {
        /** 网络请求超时时间（秒） */
        public static final int TIMEOUT_SECONDS = 90;
        /** 服务器基础URL */
        public static final String BASE_URL = "http://192.168.43.212:8080/";
        /** 图片基础URL */
        public static final String IMAGE_BASE_URL = "http://t7sxw4srx.hd-bkt.clouddn.com/images/";
    }

    /**
     * 考试相关常量
     */
    public static final class Exam {
        /** 考试模式：顺序练习 */
        public static final String MODE_ORDER = "order";
        /** 考试模式：随机练习 */
        public static final String MODE_RANDOM = "random";
        /** 考试模式：模拟考试 */
        public static final String MODE_EXAM = "exam";

        /** 科目一 */
        public static final String SUBJECT_ONE = "科目一";
        /** 科目四 */
        public static final String SUBJECT_FOUR = "科目四";

        /** 题目类型：单选题 */
        public static final String QUESTION_TYPE_SINGLE = "1";
        /** 题目类型：判断题 */
        public static final String QUESTION_TYPE_JUDGE = "2";
        /** 题目类型：多选题 */
        public static final String QUESTION_TYPE_MULTI = "3";

        /** 模拟考试时长（毫秒）：45分钟 */
        public static final long EXAM_DURATION_MS = 45 * 60 * 1000;
        /** 选项标签数组 */
        public static final String[] OPTION_LABELS = {"A", "B", "C", "D"};
    }

    /**
     * 用户相关常量
     */
    public static final class User {
        /** 用户角色：管理员 */
        public static final String ROLE_ADMIN = "admin";
        /** 用户角色：普通用户 */
        public static final String ROLE_USER = "user";
    }

    /**
     * API响应常量
     */
    public static final class ApiResponse {
        /** 成功状态码 */
        public static final int CODE_SUCCESS = 200;
        /** 空值标识字符串 */
        public static final String NULL_VALUE = "NULL";
    }

    /**
     * UI相关常量
     */
    public static final class UI {
        /** Toast显示时长：短 */
        public static final int TOAST_SHORT = android.widget.Toast.LENGTH_SHORT;
        /** Toast显示时长：长 */
        public static final int TOAST_LONG = android.widget.Toast.LENGTH_LONG;
    }

    /**
     * 时间格式常量
     */
    public static final class TimeFormat {
        /** 标准日期时间格式 */
        public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        /** 时区：中国 */
        public static final String TIME_ZONE_CHINA = "Asia/Shanghai";
    }
}
