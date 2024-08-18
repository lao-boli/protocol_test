package org.hqu.lly.utils;

import com.sun.javafx.scene.control.skin.Utils;
import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.tk.Toolkit;
import javafx.geometry.Bounds;
import javafx.scene.control.Labeled;
import javafx.scene.text.Font;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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

    static final TextLayout layout = Toolkit.getToolkit().getTextLayoutFactory().createLayout();

    /**
     * copy from {@link Utils#computeTextWidth(Font, String, double)}
     * @param font labeled font
     * @param text labeled text
     * @param wrappingWidth use 0 is ok
     * @return labeled text width
     */
    public static double computeTextWidth(Font font, String text, double wrappingWidth) {
        layout.setContent(text != null ? text : "", FontHelper.getNativeFont(font));
        layout.setWrapWidth((float)wrappingWidth);
        return layout.getBounds().getWidth();
    }

    /**
     * judge a {@link Labeled} text if overflow
     * @param labeled labeled
     * @return if overflow return true otherwise false
     */
    public static Boolean isLabeledTextOverflow(Labeled labeled) {
        // 获取 Label 的布局边界
        Bounds labelBounds = labeled.getLayoutBounds();
        // 获取纯文本显示区域宽度
        double textLayoutWidth = labelBounds.getWidth() - labeled.getPadding().getLeft() - labeled.getPadding().getRight();
        // 若graphic存在,需要减去graphic的宽度
        if (labeled.getGraphic() != null){
            double graphicW = (labeled.getGraphic().getLayoutBounds().getWidth() + labeled.getGraphicTextGap());
            textLayoutWidth -= graphicW;
        }
        double textWidth = computeTextWidth(labeled.getFont(), labeled.getText(), 0);
        boolean isOverflow = textWidth > textLayoutWidth;
        return isOverflow;
    }

    /**
     * <p>
     * 将String转为Integer,
     * 与{@link Integer#valueOf}的区别在于,
     * 当string为null时,本方法也返回null而不是抛出{@link NumberFormatException}异常.
     * </p>
     *
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
     * 将Integer转为String,
     * 与{@link Integer#toString()}的区别在于,
     * 当string为null时,本方法也返回null而不是抛出{@link NullPointerException}异常.
     * </p>
     *
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

    /**
     * <p>
     * 获取字符串的半角字符长度(英文、数字1个字符，汉字2个字符)
     * </p>
     *
     * @param str 字符串
     * @return 字符串真实长度, 若字符串为null则返回0.
     * @date 2023-02-26 13:52:24 <br>
     * @author hqully <br>
     */
    @SneakyThrows
    public static int getRealLength(String str) {
        // 若编码为unicode,则汉字将占3字节，不符合要求。
        // 故指定编码为gbk。
        return str == null ? 0 : str.getBytes("gbk").length;
    }

    /**
     * 获取本地IP地址
     *
     * @return IP地址列表
     * @date 2023-05-16 08:55
     */
    @SneakyThrows
    public static List<String> getLocalAddrs() {
        List<String> addrs = new ArrayList<>();
        Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = allNetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (!address.isLinkLocalAddress() && !address.isLoopbackAddress()
                        && !address.getHostAddress().contains(":")) {
                    addrs.add(address.getHostAddress());
                }
            }
        }
        return addrs;
    }

    public static Field[] getAllFields(Class<?> clazz,Class<?> topClazz) {
        // 获取当前类的所有字段
        Field[] fields = clazz.getDeclaredFields();
        if (!clazz.equals(topClazz)) {
            Field[] superFields = getAllFields(clazz.getSuperclass(), topClazz);
             // 合并当前类的字段与超类的字段
            Field[] combinedFields = new Field[fields.length + superFields.length];
            System.arraycopy(fields, 0, combinedFields, 0, fields.length);
            System.arraycopy(superFields, 0, combinedFields, fields.length, superFields.length);
            return combinedFields;
        }
        return fields;
    }

}
