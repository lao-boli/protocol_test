package org.hqu.lly.service;

/**
 * <p>
 * 消息服务
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/10 8:26
 */
public interface MessageService extends UIService {

    /**
     * <p>
     * 更新GUI消息列表
     * </p>
     *
     * @param msg 字符串类型的消息
     * @return void
     * @date 2022-08-10 11:04:04 <br>
     * @author hqully <br>
     */
    void updateMsgList(String msg);
}
