package org.hqu.lly.factory;

import javafx.scene.control.Tab;

/**
 * <p>
 * 标签页工厂,根据实现类创建不同的标签页
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/7 14:51
 */
public interface TabFactory {
    /**
     * <p>
     *     创建标签页
     * </p>
     * @param
     * @return {@link Tab} 标签页
     * @date 2022-08-07 14:51:48 <br>
     * @author hqully <br>
     */
    public Tab create();
}
