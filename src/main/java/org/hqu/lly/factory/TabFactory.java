package org.hqu.lly.factory;

import javafx.scene.control.Tab;
import org.hqu.lly.domain.config.Config;
import org.hqu.lly.view.controller.BaseController;

/**
 * <p>
 * 标签页工厂,根据实现类创建不同的标签页
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/7 14:51
 */
public interface TabFactory<T extends BaseController> {

    /**
     * <p>
     * 创建标签页
     * </p>
     *
     * @return {@link Tab} 标签页
     * @date 2022-08-07 14:51:48 <br>
     */
    Tab create();

    /**
     * <p>
     * 创建标签页
     * </p>
     *
     * @param config 创建标签页所需配置类<br>
     *               应为 {@link org.hqu.lly.domain.config.ClientConfig}<br>
     *               或 {@link org.hqu.lly.domain.config.ServerConfig}
     * @return {@link Tab} 标签页
     * @date 2023-02-03 19:43:33 <br>
     */
    Tab create(Config config);

    /**
     * <p>
     * 获取标签页的controller
     * </p>
     *
     * @return {@link T}
     * @date 2023-02-02 20:07:11 <br>
     * @author hqully <br>
     */
    T getController();

}
