package org.hqu.lly.domain.config;

import lombok.Data;
import org.hqu.lly.utils.ConfUtil;

import java.util.HashSet;
import java.util.Set;

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

    /**
     * 标签页面板的配置列表
     */
    private Set<TabPaneConfig> tabPaneConfigs;

    /**
     * 是否从文件加载的flag
     */
    private static Boolean isLoad = false;

    /**
     * 向{@link #tabPaneConfigs}中添加 {@link TabPaneConfig}.
     * @param tabPaneConfig 标签面板配置类
     */
    public void addTabPaneConfig(TabPaneConfig tabPaneConfig) {
        tabPaneConfigs.add(tabPaneConfig);
    }

    /**
     * <p>
     *     保存配置到本地
     * </p>
     * @date 2023-02-05 19:13:04 <br>
     */
    public void save(){
        ConfUtil.saveConf(this);
    }

    /**
     * <p>
     * 从本地配置文件中加载配置.
     * </p>
     * @date 2023-02-05 19:11:46 <br>
     */
    public static void load(){
        TopConfigHolder.load();
        isLoad = true;
    }


    /**
     * <p>
     * 根据加载的配置文件生成需要的面板和数据后,
     * 必须调用,标志已生成所需面板和数据.
     * </p>
     * @date 2023-02-05 19:11:46 <br>
     */
    public static void initComplete(){
        isLoad = false;
    }

    /**
     * 是否从文件中加载配置
     * @return 是从文件中加载返回true,否则返回false.
     */
    public static Boolean isLoad(){
        return isLoad;
    }


    private TopConfig() {
        tabPaneConfigs = new HashSet<>();
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
