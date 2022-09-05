package org.hqu.lly.protocol.websocket.server.group;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WSChannelGroup {
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static Map<String, Channel> userChannelGroup = new ConcurrentHashMap<>();
    public static Map<Channel, String> channelUserGroup = new ConcurrentHashMap<>();
}
