package org.hqu.lly.utils;

import io.netty.channel.ChannelHandlerContext;
import org.hqu.lly.protocol.tcp.group.AppChannelGroup;

/**
 * <p>
 * 连接通道工具类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/16 20:31
 */
public class ChannelUtil {

    /**
     * <p>
     * 判断channel是否是本应用产生的channel
     * </p>
     *
     * @param ctx ctx
     * @return {@link boolean} 是则返回true,不是则返回false
     * @date 2022-09-16 20:39:40 <br>
     * @author hqully <br>
     */
    public static boolean isAppChannel(ChannelHandlerContext ctx) {
        return isAppClientChannel(ctx) || isAppServerChannel(ctx);
    }

    public static boolean isAppClientChannel(ChannelHandlerContext ctx) {
        return AppChannelGroup.clientChannelSet.contains(ctx.channel().remoteAddress().toString());
    }

    public static boolean isAppServerChannel(ChannelHandlerContext ctx) {
        return AppChannelGroup.serverChannelSet.contains(ctx.channel().remoteAddress().toString());
    }
}
