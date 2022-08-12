package com.hqu.lly.utils;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/12 15:32
 * @Version 1.0
 */
public class MsgFormatUtil {

    public static String formatReceiveMsg(String msg , String from) {
       return "<--- " + from + " : " + msg;
    }

    public static String formatSendMsg(String msg , String to) {
        return "---> " + to + " : " + msg;
    }
}
