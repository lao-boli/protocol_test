package org.hqu.lly.domain.config;

import lombok.Data;
import org.hqu.lly.utils.ConfUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 总配置类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/1/30 19:58
 */
@Data
public class TopConfig {

    private List<TabPaneConfig> tabPaneConfigs;

    private List<String> contentPaneNames;

    public void addTabPaneConfig(TabPaneConfig tabPaneConfig) {
        tabPaneConfigs.add(tabPaneConfig);
    }

    public void addContentPaneNames(String contentPaneName) {
        contentPaneNames.add(contentPaneName);
    }

    public void save(){
        ConfUtil.saveConf(this);
    }

    public static void load(){
        TopConfigHolder.load();
    }


    private TopConfig() {
        tabPaneConfigs = new ArrayList<>();
        contentPaneNames = new ArrayList<>();
    }

    public static TopConfig getInstance() {
        return TopConfigHolder.instance;
    }

    /**
     * 保证顶级配置类单例
     */
    private static class TopConfigHolder {
        public static TopConfig instance = new TopConfig();
        public static void load(){
            instance = ConfUtil.loadTopConf();
        }
    }

}
