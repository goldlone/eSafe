package cn.goldlone.esafe.controller;

import cn.goldlone.esafe.model.Result;
import cn.goldlone.esafe.service.GeoService;
import cn.goldlone.esafe.utils.IOUtil;
import cn.goldlone.esafe.utils.ResultUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Created by CN on 2018/3/23 12:06 .
 */
@RestController
public class GeoController {

    @Autowired
    private GeoService gs;


    /**
     * 记录数据
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/rec")
    public String receiveGPS(HttpServletRequest request) throws IOException {
        String jsonString = IOUtil.stream2Str(request.getInputStream());
        System.out.println("记录数据：");
        System.out.println(jsonString);
        System.out.println();
        JSONObject object = new JSONObject(jsonString);
        return gs.receive(object);
    }


    /**
     * 训练
     * @param request
     * @return
     */
    @PostMapping("/train")
    public String train(HttpServletRequest request) throws IOException {
        String jsonString = IOUtil.stream2Str(request.getInputStream());
        System.out.println("训练：");
        System.out.println(jsonString);
        System.out.println();
        JSONObject object = new JSONObject(jsonString);
        return gs.train(object);
    }


    /**
     * 检测轨迹点是否异常
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/detect")
    public String detect(HttpServletRequest request) throws IOException {
        String jsonString = IOUtil.stream2Str(request.getInputStream());
        System.out.println("检测：");
        System.out.println(jsonString);
        System.out.println();
        JSONObject object = new JSONObject(jsonString);
        return gs.detect(object);
    }

    /**
     * 获取聚类数据
     * @param username
     * @return
     */
    @PostMapping("/cluster/data")
    public Result getClusterCenter(String username) {
        System.out.println(username);
        if(username==null || "".equals(username))
            return ResultUtils.error(ResultUtils.RESULT_FAIL, "缺少必要的参数");
        return gs.getClusterCenter(username);
    }

}
