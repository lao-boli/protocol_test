package org.hqu.lly.utils;

import org.hqu.lly.struct.Pair;

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

    public static Pair<Integer, String> checkPortAndPath(String addr) {
        int port = 0;
        String path = "";
        if (addr.contains("/")) {
            String[] split = addr.split("/",2);
            String _port = split[0];
            path = split[1];
            if (checkPort(_port)) {
                port = Integer.parseInt(_port);
                return new Pair<>(port, path);
            }
        } else {
            if (checkPort(addr)) {
                return new Pair<>(Integer.parseInt(addr), "");
            }
        }
        return new Pair<>(port, path);
    }


    public static boolean checkPort(String port) {
        boolean isNumeric = port.matches("\\d+");
        if (!isNumeric) {
            throw new IllegalArgumentException("illegal port text:" + port);
        }
        return checkPort(Integer.parseInt(port));

    }

    public static boolean checkPort(int port) {
        if (port < 0 || port > 0xFFFF)
            throw new IllegalArgumentException("port out of range:" + port);
        return true;
    }

    public static String checkHost(String hostname) {
        if (hostname == null)
            throw new IllegalArgumentException("hostname can't be null");
        return hostname;
    }

}
