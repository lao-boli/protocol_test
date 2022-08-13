package com.hqu.lly.service;

import io.netty.channel.Channel;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/9 21:03
 * @Version 1.0
 */
public interface ChannelService {

    public void addChannel(Channel channel);

    public void removeChannel(Channel channel);

}
