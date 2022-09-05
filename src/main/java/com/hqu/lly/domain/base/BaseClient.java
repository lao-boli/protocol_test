package com.hqu.lly.domain.base;

import com.hqu.lly.service.MessageService;
import com.hqu.lly.service.impl.ClientService;
import io.netty.channel.Channel;

import java.net.URI;
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
public abstract class BaseClient implements Callable<Channel> {

   public abstract void init();

   public abstract void destroy();

   public abstract void setURI(URI uri);

   public abstract void setService(ClientService clientService);

   public abstract void sendMessage(String message);

    @Override
    public Channel call() throws Exception {
        return null;
    }
}
