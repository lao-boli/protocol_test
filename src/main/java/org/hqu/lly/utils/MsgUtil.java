package org.hqu.lly.utils;

/**
 * <p>
 * 字符串消息工具
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/12 15:32
 */
public class MsgUtil {


    /**
     * <p>
     * 格式化接收消息字符串
     * </p>
     *
     * @param msg  收到的消息
     * @param from 消息来源
     * @return {@link String} 格式化后的消息字符串
     * @date 2022-09-24 16:37:40 <br>
     * @author hqully <br>
     */
    public static String formatReceiveMsg(String msg, String from) {
        StringBuilder sb = new StringBuilder();
        sb.append("<--- ")
                .append(from)
                .append(" :")
                .append(" [")
                .append(msg.length())
                .append("字节")
                .append("] ")
                .append(msg);
        return sb.toString();
    }

    /**
     * <p>
     * 格式化发送消息字符串
     * </p>
     *
     * @param msg 收到的消息
     * @param to  消息去向
     * @return {@link String} 格式化后的消息字符串
     * @date 2022-09-24 16:37:40 <br>
     * @author hqully <br>
     */
    public static String formatSendMsg(String msg, String to) {
        StringBuilder sb = new StringBuilder();
        sb.append("---> ")
                .append(to)
                .append(" :")
                .append(" [")
                .append(msg.length())
                .append("字节")
                .append("] ")
                .append(msg);
        return sb.toString();
    }

}
