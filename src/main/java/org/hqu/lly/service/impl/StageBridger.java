package org.hqu.lly.service.impl;

import org.hqu.lly.service.TaskService;

/**
 * <p>
 * 多窗口之间的桥接者,负责多窗口之间的交互
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/25 20:29
 */
public abstract class StageBridger implements TaskService {


    /**
     * <p>
     * 在不同GUI界面之间传递数据
     * </p>
     *
     * @param data 要传递的数据
     * @return void
     * @date 2022-09-25 20:39:34 <br>
     * @author hqully <br>
     */
    public <T> void transferData(T data) {
        throw new ClassCastException();
    }

    /**
     * <p>
     * 接收其他窗口传递的数据
     * </p>
     *
     * @return {@link T}
     * @date 2022-09-29 10:54:40 <br>
     * @author hqully <br>
     */
    public <T> T receiveData(T data) {
        return data;
    }

    @Override
    public abstract void fireTask();


}
