package org.hqu.lly.domain.base;

import org.hqu.lly.service.impl.ServerService;
import io.netty.channel.Channel;

import java.util.concurrent.Callable;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/11 14:15
 * @Version 1.0
 */
public abstract class BaseServer implements Callable<Channel> {

   public abstract void init();

   public abstract void destroy();

   public abstract void setPort(String port);

   public abstract void setService(ServerService serverService);

   public abstract void sendMessage(String message,Channel channel);

    @Override
    public Channel call() throws Exception {
        return null;
    }
}
