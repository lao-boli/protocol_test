package org.hqu.lly.protocol.base;

import io.netty.channel.Channel;
import org.hqu.lly.service.impl.ServerService;

import java.util.concurrent.Callable;

/**
 * <p>
 * 服务端抽象父类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/11 14:15
 */
public abstract class BaseServer<T> implements Callable<Channel> {

    protected Channel channel;

    public abstract void init();

    public abstract void destroy();

    public abstract void setPort(int port);

    public abstract void setService(ServerService serverService);

    public abstract void sendMessage(String message, T dstAddr);

    public boolean isActive(){
        return channel != null && channel.isActive();
    }

    @Override
    public Channel call() throws Exception {
        return null;
    }
}
