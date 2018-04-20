package cn.goldlone.esafe.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by CN on 2018/2/22.
 */
public class IOUtil {

    public static String stream2Str(InputStream is) throws IOException {
        int len = -1;
        byte[] buff = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while((len=is.read(buff)) != -1) {
            sb.append(new String(buff, 0, len));
        }
        return sb.toString();
    }

}
