package org.hqu.lly.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.CharsetUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.enums.DataType;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 字符串消息工具
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/12 15:32
 */
@Slf4j
public class MsgUtil {


    private static ObjectMapper mapper = new ObjectMapper();

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

    /**
     * 字符串转换成base64编码字符串(utf8)
     *
     * @param str 要编码的字符串
     * @return base64编码字符串
     * @date 2023-04-02 19:56
     * @since 0.2.0
     */
    public static String encodeBase64(String str) {
        byte[] encode = Base64.getEncoder().encode(str.getBytes(CharsetUtil.UTF_8));
        return new String(encode, CharsetUtil.UTF_8);
    }

    public static String encodeBase64(byte[] bytes) {
        byte[] encode = Base64.getEncoder().encode(bytes);
        return new String(encode, CharsetUtil.UTF_8);
    }


    /**
     * base64编码字符串解码成字符串(utf8)
     *
     * @param str base64编码字符串
     * @return 解码后的字符串
     * @date 2023-04-02 21:34
     * @since 0.2.0
     */
    public static String decodeBase64(String str) {
        byte[] decode = Base64.getDecoder().decode(str);
        return new String(decode, CharsetUtil.UTF_8);
    }

    /**
     * base64字节数组解码成字符串(utf8)
     *
     * @param bytes 字节数组
     * @return 解码后的字符串
     * @date 2023-04-03 21:24
     * @since 0.2.0
     */
    public static String decodeBase64(byte[] bytes) {
        byte[] decode = Base64.getDecoder().decode(bytes);
        return new String(decode, CharsetUtil.UTF_8);
    }

    /**
     * 字符串格式化成json
     *
     * @param str 符合json格式的字符串
     * @return 格式化后的json字符串
     * @date 2023-04-02 21:50
     * @since 0.2.0
     */
    @SneakyThrows
    public static String jsonFormat(String str) {
        JsonNode jsonNode = mapper.readTree(str);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode).replaceAll("\r", "");
    }


    /**
     * 将字符串编码成对应的16进制数字组成的,以空格分隔的字符串(utf8).
     * <pre>
     * e.g.
     *  "123测试test" -> "31 32 33 E6 B5 8B E8 AF 95 74 65 73 74"
     * </pre>
     *
     * @param str 字符串
     * @return 转换后的16进制组成的字符串
     * @date 2023-04-02 21:10
     * @since 0.2.0
     */
    public static String encodeHex(String str) {
        byte[] bytes = str.getBytes(CharsetUtil.UTF_8);
        String s = byteToHex(bytes);
        return s.trim();
    }


    /**
     * 将byte数组转成对应的16进制数字组成的,以空格分隔的字符串(utf8).
     *
     * @param bytes byte数组
     * @return 转换后的字符串
     * @date 2023-04-03 21:25
     * @since 0.2.0
     */
    public static String byteToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // 在字符串中含有中文的情况下,byte可能为负数
            // 将它转为正数进行比较
            if ((b & 0xff) < 16) {
                // 不足2位数,补0
                sb.append(String.format("0%X ", b));
            } else {
                sb.append(String.format("%X ", b));
            }
        }
        return sb.toString();
    }


    /**
     * 16进制组成的字符串解码成对应的字符串,
     * 若字符串包含非16进制字符,将自动舍去非法字符后的字符串。
     * 若字符串长度为奇数,将自动舍去最后一位。
     * <pre>
     * e.g.
     *  "74657374" -> "test"
     *  "74t57374" -> "t"
     *  "7465737" -> "tes"
     * </pre>
     *
     * @param str 16进制组成的字符串
     * @return 转换后的字符串
     * @date 2023-04-02 21:10
     * @since 0.2.0
     */
    public static String decodeHex(String str) {
        String temp = str.replaceAll(" ", "");
        // 遇到非16进制字符,去除非法字符后的字符串
        String pattern = "[^0-9a-fA-F]";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(temp);
        if (m.find()) {
            temp = temp.substring(0, m.start());
        }

        byte[] bytes = hexStringToByteArray(temp);
        return new String(bytes, CharsetUtil.UTF_8);
    }

    /**
     * 16进制组成的字符串转换成对应的byte数组,
     * 若字符串长度为奇数,将自动舍去最后一位。
     * <pre>
     * e.g.
     *  "74657374" -> [74,65,73,74]
     *  "7465737" -> [74,65,73]
     * </pre>
     *
     * @param s 16进制组成的字符串
     * @return 转换后的byte数组
     * @date 2023-04-02 21:10
     * @since 0.2.0
     */
    public static byte[] hexStringToByteArray(String s) {

        int len = s.length();
        byte[] data = new byte[len / 2];
        try {
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }
        } catch (IndexOutOfBoundsException e) {
            // 索引溢出表示字符串的长度为奇数,
            // 舍去最后一位,直接返回已经解码的数据
            return data;
        }
        return data;
    }


    /**
     * 转换文本格式(plainText,hex,base64,json) <br>
     * 注:普通文本转换为json后将无法转换回来.
     * Base64编码若转换失败,会以plainText的形式返回.
     *
     * @param form 文本原格式
     * @param to   要转换的格式
     * @param text 文本
     * @return 转换格式后的文本
     * @date 2023-04-03 14:38
     * @since 0.2.0
     */
    public static String convertText(DataType form, DataType to, String text) {
        String result = switch (form) {
            case PLAIN_TEXT, JSON -> text;
            case HEX -> decodeHex(text);
            case BASE64 -> {
                try {
                    yield decodeBase64(text);
                } catch (IllegalArgumentException e) {
                    // 非法base64字符就将其当做plainText返回
                    log.warn(e.toString());
                    yield text;
                }
            }
        };

        result = switch (to) {
            case PLAIN_TEXT -> result;
            case HEX -> encodeHex(result);
            case BASE64 -> encodeBase64(result);
            case JSON -> jsonFormat(result);
        };
        return result;
    }

    /**
     * 将字节数组转换成 {@link DataType}中的各种格式.
     *
     * @param to    格式类型
     * @param bytes 字节数组
     * @date 2023-04-03 21:20
     * @since 0.2.0
     */
    public static String convertText(DataType to, byte[] bytes) {
        String result = new String(bytes, CharsetUtil.UTF_8);
        result = switch (to) {
            case PLAIN_TEXT -> result;
            case HEX -> byteToHex(bytes);
            case BASE64 -> {
                try {
                    yield decodeBase64(bytes);
                } catch (IllegalArgumentException e) {
                    // 非法base64字符就将其当做plainText返回
                    log.warn(e.toString());
                    yield result;
                }
            }
            case JSON -> {
                try {
                    yield jsonFormat(result);
                } catch (Exception e) {
                    // 非法json字符就将其当做plainText返回
                    log.warn(e.toString());
                    yield result;
                }
            }
        };
        return result;
    }

}
