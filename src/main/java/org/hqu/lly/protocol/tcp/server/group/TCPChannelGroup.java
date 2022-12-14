package org.hqu.lly.protocol.tcp.server.group;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * channel管理组
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/9 15:57
 */
public class TCPChannelGroup {

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static Map<String, Channel> clientChannelGroup = new ConcurrentHashMap<>();
    public static Map<String, Channel> serverChannelGroup = new ConcurrentHashMap<>();
}
