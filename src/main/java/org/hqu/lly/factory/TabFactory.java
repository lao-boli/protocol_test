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
     * @param config 创建标签页所需配置类<br>
     *               应为 {@link org.hqu.lly.domain.config.ClientSessionConfig}<br>
     *               或 {@link org.hqu.lly.domain.config.ServerSessionConfig}<br>
     *               或 null
     * @return {@link Tab} 标签页
     * @date 2023-07-04 20:11
     */
    Tab create(Config config);

}
