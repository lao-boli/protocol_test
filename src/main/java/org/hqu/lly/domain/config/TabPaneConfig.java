package org.hqu.lly.domain.config;

import lombok.Data;

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
public class TabPaneConfig extends Config{

    /**
     * 标签页面板名称
     * []
     */
    private String name;

    /**
     * 客户端配置或服务端配置
     */
    private List<TabConfig> subTabConfigs;

    public TabPaneConfig(String name){
        this.name = name;
        subTabConfigs = new ArrayList<>();
    }

    public void addSubConfig(TabConfig subTabConfig) {
        subTabConfigs.add(subTabConfig);
    }
}
