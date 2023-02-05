package org.hqu.lly.domain.config;

import lombok.Data;
import org.hqu.lly.constant.ContentPaneConsts;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用于保存标签面板的配置类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/1/29 13:15
 */
@Data
public class TabPaneConfig extends Config {

    /**
     * 标签页面板名称<br>
     * 应为 {@link ContentPaneConsts}中的一种.
     */
    private String name;

    /**
     * 客户端配置或服务端配置列表
     */
    private List<TabConfig> subTabConfigs;

    /**
     * <p>
     *     根据 {@link #name}创建标签面板配置类。
     * </p>
     * @param name 标签页面板名称,
     *             应为 {@link ContentPaneConsts}中的一种.
     * @return {@link }
     * @date 2023-02-06 14:04:00 <br>
     * @author hqully <br>
     */
    public TabPaneConfig(String name) {
        this.name = name;
        subTabConfigs = new ArrayList<>();
    }

    /**
     * <p>
     * 添加本 {@link org.hqu.lly.view.controller.TabPaneController}
     * 所控制下的所有标签页的配置类
     * </p>
     * @param subTabConfig 标签页配置类,应为 {@link ClientConfig}或 {@link ServerConfig}
     * @date 2023-02-05 20:51:24 <br>
     */
    public void addSubConfig(TabConfig subTabConfig) {
        subTabConfigs.add(subTabConfig);
    }

}
