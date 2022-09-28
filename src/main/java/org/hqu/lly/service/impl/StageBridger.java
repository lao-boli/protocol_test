package org.hqu.lly.service.impl;

import org.hqu.lly.service.UIService;

/**
 * <p>
 * 多窗口之间的桥接者,负责多窗口之间的交互
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/25 20:29
 */
public abstract class StageBridger implements UIService {


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
     * 执行另一窗口的任务
     * </p>
     *
     * @return void
     * @date 2022-09-26 08:27:30 <br>
     * @author hqully <br>
     */
    public abstract void fireTask();

    /**
     * <p>
     * 所有任务都完成后的回调
     * </p>
     *
     * @return void
     * @date 2022-09-28 15:56:49 <br>
     * @author hqully <br>
     */
    public abstract void allTasksCompleted();


}
