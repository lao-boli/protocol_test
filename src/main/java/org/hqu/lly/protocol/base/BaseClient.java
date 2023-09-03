package org.hqu.lly.protocol.base;

import io.netty.channel.Channel;
import org.hqu.lly.enums.DataType;
import org.hqu.lly.exception.ChannelInactiveException;
import org.hqu.lly.service.impl.ClientService;

import java.net.URI;
import java.util.concurrent.Callable;

/**
 * <p>
 * 客户端抽象父类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/11 14:15
 */
public abstract class BaseClient implements Callable<Channel> {
    protected Channel channel;

    public abstract void init();

    public abstract void destroy();

    public abstract void setURI(URI uri);

    public abstract void setService(ClientService clientService);

    public void sendMessage(String message){
        if (!channel.isActive()){
            throw new ChannelInactiveException();
        }
    }

    public void sendMessage(String message, DataType type){
        if (!channel.isActive()){
            throw new ChannelInactiveException();
        }
    }

    public boolean isActive(){
        return channel != null && channel.isActive();
    }
    @Override
    public Channel call() throws Exception {
        return null;
    }
}
