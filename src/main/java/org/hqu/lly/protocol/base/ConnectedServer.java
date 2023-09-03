package org.hqu.lly.protocol.base;

import io.netty.channel.Channel;

/**
 * <p>
 * 有连接服务
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/17 19:27
 */
public abstract class ConnectedServer extends BaseServer<Channel> {

    /**
     * <p>
     *     发送消息
     * </p>
     * @param message 要发送的消息
     * @param channel 与客户端建立的channel
     * @return  void
     * @date 2022-09-17 19:34:12 <br>
     * @author hqully <br>
     */
    @Override
    public abstract void sendMessage(String message, Channel channel);

}
