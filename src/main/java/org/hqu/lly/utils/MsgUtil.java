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

    public static String formatReceiveMsg(String msg, String from) {
        return "<--- " + from + " : " + msg;
    }

    public static String formatSendMsg(String msg, String to) {
        return "---> " + to + " : " + msg;
    }
}
