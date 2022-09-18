package org.hqu.lly.domain.bean;

import org.hqu.lly.domain.base.BaseServer;

import java.net.InetSocketAddress;

/**
 * <p>
 * 无连接服务
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/17 19:27
 */
public abstract class ConnectionlessServer extends BaseServer {

    /**
     * <p>
     *     发送消息
     * </p>
     * @param message 要发送的消息
     * @param dstAddr 目标地址
     * @return void
     * @date 2022-09-17 19:33:02 <br>
     * @author hqully <br>
     */
    public abstract void sendMessage(String message, InetSocketAddress dstAddr);

}
