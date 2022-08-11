package com.hqu.lly.common;

import com.hqu.lly.service.UIService;
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
public abstract class BaseClient implements Callable<Channel> {

   public abstract void init();

   public abstract void destroy();

   public abstract void setAddress(String host, String port);

   public abstract void setService(UIService uiService);

   public abstract void sendMessage(String message);

    @Override
    public Channel call() throws Exception {
        return null;
    }
}
