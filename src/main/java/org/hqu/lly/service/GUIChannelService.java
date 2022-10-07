package org.hqu.lly.service;

import io.netty.channel.Channel;

/**
 * <p>
 * GUI界面的channel管理服务
 * <p>
 *
 * @author hqully
 * @date 2022/8/9 21:03
 * @version 1.0
 */
public interface GUIChannelService extends UIService{

    /**
     * <p>
     *     添加Channel
     * </p>
     * @param channel channel
     * @return void
     * @date 2022-08-10 11:13:13 <br>
     * @author hqully <br>
     */
    public void addChannel(Channel channel);

    /**
     * <p>
     *     移除channel
     * </p>
     * @param channel channel
     * @return void
     * @date 2022-08-10 11:13:13 <br>
     * @author hqully <br>
     */
    public void removeChannel(Channel channel);

}
