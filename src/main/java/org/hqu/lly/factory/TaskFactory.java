package org.hqu.lly.factory;

import javafx.concurrent.Task;

/**
 * <p>
 * 异步任务工厂接口
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/28 20:37
 */
public interface TaskFactory<T> {

    /**
     * <p>
     * 创建一个异步任务
     * </p>
     *
     * @param
     * @return {@link Task<T>} 异步任务
     * @date 2022-09-28 20:40:11 <br>
     * @author hqully <br>
     */
    Task<T> create();

}
