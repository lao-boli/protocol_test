package org.hqu.lly.utils;

/**
 * <p>
 * 通用工具类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/2/4 16:55
 */
public class CommonUtil {

    /**
     * <p>
     *     将String转为Integer,
     *     与{@link Integer#valueOf}的区别在于,
     *     当string为null时,本方法也返回null而不是抛出{@link NumberFormatException}异常.
     * </p>
     * @param s 要转换的字符串
     * @return {@link Integer}
     * @date 2023-02-04 17:01:17 <br>
     */
    public static Integer strToInt(String s) {
        if (s == null) {
            return null;
        }
        return Integer.valueOf(s);
    }

    /**
     * <p>
     *     将Integer转为String,
     *     与{@link Integer#toString()}的区别在于,
     *     当string为null时,本方法也返回null而不是抛出{@link NullPointerException}异常.
     * </p>
     * @param i 要转换的整型数字
     * @return {@link Integer}
     * @date 2023-02-04 17:01:17 <br>
     */
    public static String intToStr(Integer i) {
        if (i == null) {
            return null;
        }
        return i.toString();
    }

}
