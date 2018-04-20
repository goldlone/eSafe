package cn.goldlone.esafe.utils;


import cn.goldlone.esafe.model.Result;

/**
 * @author Created by CN on 2018/4/10 12:42 .
 */
public class ResultUtils {
    // 请求成功
    public static final int RESULT_SUCCESS = 1001;

    // 请求失败
    public static final int RESULT_FAIL = 2001;
    // 重复或已存在
    public static final int RESULT_HAD_EXIST = 2002;
    // 结果不存在
    public static final int RESULT_NOT_EXIST = 2003;

    // 抛出异常
    public static final int RESULT_EXCEPTION = 5001;

    public static Result success(Object data, String msg) {
        return new Result(RESULT_SUCCESS, msg, data);
    }

    public static Result success(String msg) {
        return new Result(RESULT_SUCCESS, null, msg);
    }

    public static Result error(int code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result error(int code, String msg, Object data) {
        return new Result(code, msg, data);
    }

}
