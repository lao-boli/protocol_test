package org.hqu.lly.utils;

/**
 * <p>
 * 校验工具
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/7/12 9:10
 */
public class ValidateUtil {

    public static int checkPort(String port) {
       boolean isNumeric =  port.matches("\\d+");
       if (!isNumeric){
           throw new IllegalArgumentException("illegal port text:" + port);
       }
       return checkPort(Integer.parseInt(port));

    }
    public static int checkPort(int port) {
        if (port < 0 || port > 0xFFFF)
            throw new IllegalArgumentException("port out of range:" + port);
        return port;
    }

    public static String checkHost(String hostname) {
        if (hostname == null)
            throw new IllegalArgumentException("hostname can't be null");
        return hostname;
    }

}
