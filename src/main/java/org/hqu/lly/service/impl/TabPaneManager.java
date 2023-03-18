package org.hqu.lly.service.impl;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.hqu.lly.constant.ContentPaneConsts;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.config.TabConfig;
import org.hqu.lly.domain.config.TabPaneConfig;
import org.hqu.lly.enums.TabFactoryEnum;
import org.hqu.lly.service.SwitchPaneService;
import org.hqu.lly.view.controller.TabPaneController;
import org.hqu.lly.view.group.ContentPaneGroup;
import org.hqu.lly.view.group.ControllerGroup;

/**
 * <p>
 * 标签面板管理器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/8 19:22
 */
public class TabPaneManager implements SwitchPaneService {

    /**
     * 父面板
     */
    private final Pane rootPane;

    /**
     * 标签面板名称,应为 {@link ContentPaneConsts}中的值。
     */
    private final String tabPaneName;

    /**
     * 标签面板控制器
     */
    private TabPaneController controller;

    /**
     * <p>
     * 创建标签面板管理器
     * </p>
     *
     * @param rootPane        根面板节点
     * @param contentPaneName 标签面板名称,应为 {@link ContentPaneConsts}中的值。
     * @date 2023-02-06 15:44:14 <br>
     */
    public TabPaneManager(Pane rootPane, String contentPaneName) {
        this.rootPane = rootPane;
        this.tabPaneName = contentPaneName;
    }

    @Override
    public void switchPane() {
        // 如果当前存在面板,则先移除(此处面板的size只会是0或1)
        if (rootPane.getChildren().size() > 0) {
            rootPane.getChildren().remove(0);
        }
        // 根据面板名获取要切换的面板实例，若不存在则创建
        Parent contentPane = ContentPaneGroup.contentPaneMap.get(tabPaneName);
        if (contentPane != null) {
            rootPane.getChildren().add(contentPane);
        } else {
            Parent child = createContentPane();
            rootPane.getChildren().add(child);
            // 新创建面板后默认创建一个标签页
            controller.createNewTab();
        }
    }

    /**
     * <p>
     * 根据配置类创建标签面板
     * </p>
     *
     * @param config 标签面板配置类
     * @date 2023-02-06 15:37:45 <br>
     */
    @SneakyThrows
    public void createContentPane(TabPaneConfig config) {
        // 创建面板
        createContentPane();

        controller.setTabPaneConfig(config);
        // 根据配置创建标签页
        for (TabConfig subTabConfig : config.getSubTabConfigs()) {
            controller.createNewTab(subTabConfig);
        }
    }

    /**
     * <p>
     * 创建标签面板
     * </p>
     *
     * @return 标签面板
     * @date 2023-02-06 15:37:45 <br>
     */
    @SneakyThrows
    private Parent createContentPane() {
        // 加载资源文件
        FXMLLoader loader = new FXMLLoader(ResLoc.TAB_PANE);
        Parent contentPane = loader.load();
        controller = loader.getController();

        // 根据面板名称获取相应的标签页工厂
        controller.setTabFactory(TabFactoryEnum.getByPaneType(tabPaneName).getTabFactory());
        controller.setTabPaneName(tabPaneName);

        // 添加到全局controller集合，便于在退出整个程序前保存配置
        ControllerGroup.tabPaneControllerSet.add(controller);

        VBox.setVgrow(contentPane, Priority.ALWAYS);
        // 添加到全局contentPane集合，便于切换各个功能面板
        ContentPaneGroup.contentPaneMap.put(tabPaneName, contentPane);
        return contentPane;
    }


}
