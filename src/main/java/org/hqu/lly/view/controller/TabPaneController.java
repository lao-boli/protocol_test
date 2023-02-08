package org.hqu.lly.view.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.Setter;
import org.hqu.lly.constant.ContentPaneConsts;
import org.hqu.lly.domain.config.Config;
import org.hqu.lly.domain.config.TabConfig;
import org.hqu.lly.domain.config.TabPaneConfig;
import org.hqu.lly.factory.TabFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * <p>
 * 标签面板控制器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/8 9:51
 */
public class TabPaneController extends BaseController implements Initializable {

    /**
     * 本 {@link #mainTabPane}下的标签页 {@link Tab}的控制器列表 <br>
     * 用于遍历来获取并保存每一个标签页的配置类。
     */
    private final List<BaseController> controllers;
    /**
     * fxml的tabPane节点
     */
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab createTab;
    private TabFactory tabFactory;
    /**
     * 标签页面板名称<br>
     * 应为 {@link ContentPaneConsts}中的一种.
     */
    @Setter
    private String tabPaneName;
    /**
     * 本页面配置类
     */
    @Setter
    private TabPaneConfig tabPaneConfig;

    public TabPaneController() {
        controllers = new ArrayList<>();
    }

    public void setTabFactory(TabFactory tabFactory) {
        this.tabFactory = tabFactory;
    }


    @FXML
    void createNewTab(Event event) {
        if (tabFactory != null) {
            createNewTab();
        }
    }

    // XXX 待优化两个创建新标签页的方法实现

    /**
     * <p>
     * 创建一个默认的新标签页
     * </p>
     *
     * @date 2023-02-05 20:09:11 <br>
     */
    public void createNewTab() {
        Tab tab = tabFactory.create();
        controllers.add(tabFactory.getController());

        mainTabPane.getTabs().add(mainTabPane.getTabs().size() - 1, tab);

        // 切换到新添加的标签页
        mainTabPane.getSelectionModel().select(tab);
    }

    /**
     * <p>
     * 以配置类创建一个新标签页
     * </p>
     *
     * @date 2023-02-05 20:09:11 <br>
     */
    public void createNewTab(TabConfig config) {
        Tab tab = tabFactory.create(config);
        controllers.add(tabFactory.getController());

        mainTabPane.getTabs().add(mainTabPane.getTabs().size() - 1, tab);

        // 切换到新添加的标签页
        mainTabPane.getSelectionModel().select(tab);
    }

    @Override
    public TabPaneConfig saveAndGetConfig() {
//        TopConfig.getInstance().removeTabPaneConfig(tabPaneConfig);
        TabPaneConfig tabPaneConfig = new TabPaneConfig(tabPaneName);
        for (BaseController controller : controllers) {
            Config subTabConfig = controller.saveAndGetConfig();
            tabPaneConfig.addSubConfig((TabConfig) subTabConfig);
        }
        return tabPaneConfig;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

}
