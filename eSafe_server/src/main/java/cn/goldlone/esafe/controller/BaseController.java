package cn.goldlone.esafe.controller;


import cn.goldlone.esafe.model.Result;
import cn.goldlone.esafe.utils.ResultUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller基类
 * @author Created by CN on 2018/4/10 20:32 .
 */
public class BaseController {

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        return ResultUtils.error(ResultUtils.RESULT_EXCEPTION, "【异常】："+e.getMessage());
    }


}
