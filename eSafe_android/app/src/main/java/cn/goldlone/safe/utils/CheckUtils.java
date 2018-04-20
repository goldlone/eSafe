package cn.goldlone.safe.utils;

/**
 * 检查工具类
 * @author : Created by CN on 2018/4/10 20:04
 */
public class CheckUtils {
    /**
     * 判断是否是一个有效的字符串
     * @param str
     * @return
     */
    public static boolean isEffectiveStr(String str) {
        if(str!=null && !"".equals(str))
            return true;
        else
            return false;
    }

    /**
     * 检查一个字符串数组是否是有效的字符串
     * @param arr
     * @return
     */
    public static boolean isEffectiveStr(String[] arr) {
        for(String str: arr)
            if(str==null || "".equals(str))
                return false;
        return true;
    }
}
