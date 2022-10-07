package org.hqu.lly.protocol.tcp.group;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 本应用程序建立的tcp服务端和客户端channel
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/13 18:57
 */
public class AppChannelGroup {

    public static Set<String> TCPServerChannelSet = ConcurrentHashMap.newKeySet();
    public static Set<String> TCPClientChannelSet = ConcurrentHashMap.newKeySet();
    public static Set<String> WebSocketServerChannelSet = ConcurrentHashMap.newKeySet();
    public static Set<String> WebSocketClientChannelSet = ConcurrentHashMap.newKeySet();

}
