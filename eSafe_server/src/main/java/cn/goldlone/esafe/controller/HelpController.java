package cn.goldlone.esafe.controller;

import cn.goldlone.esafe.Configs;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;


/**
 * @author Created by CN on 2018/4/9 21:39 .
 */
@RestController
public class HelpController extends BaseController {

    @PostMapping("/help/video/upload")
    public String receiveVideo(HttpServletRequest request) {
        long currentTime = System.currentTimeMillis();
        File file = new File(Configs.VIDEO_FILE_PATH+"/"+currentTime+".mp4");
        String getUrl = "http://115.24.13.189:8080/esafe/help?id="+currentTime;
//        String getUrl = file.getAbsolutePath();
        System.out.println(getUrl);
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = request.getInputStream();
            fileOutputStream = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int len;
            while((len=inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, len);
                System.out.println("write...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return getUrl;
    }

//    @PostMapping("/upload")
//    public String receiveVideo2() {
//        return null;
//    }


    /**
     * 下载求救视频
     * @param response
     * @param id
     * @return
     */
    @GetMapping("/help")
    public String downloadVideoFile(HttpServletResponse response, Long id) {
        if(id == null)
            return "{\"code\":\"2003\", \"msg\":\"该视频不存在\", \"data\":null}";
        File file = new File(Configs.VIDEO_FILE_PATH+id+".mp4");
        if(!file.exists())
            return "{\"code\":\"2003\", \"msg\":\"该视频不存在\", \"data\":null}";
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + id+".mp4");
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
//            file = new File(Configs.VIDEO_FILE_PATH+id+".mp4");
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
