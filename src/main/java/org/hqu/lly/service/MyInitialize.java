package org.hqu.lly.service;

/**
 * <p>
 *
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/7/4 9:40
 */
public interface MyInitialize<T> {

    void init();
    void init(T obj);

}
