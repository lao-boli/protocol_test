package org.hqu.lly.view.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.Setter;
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
 * 标签页控制器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/8 9:51
 */
public class TabPaneController extends BaseController implements Initializable {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab createTab;

    private TabFactory tabFactory;

    @Setter
    private String tabPaneName;

    private List<BaseController> controllers;

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

    public void createNewTab() {

        Tab tab = tabFactory.create();
        controllers.add(tabFactory.getController());

        mainTabPane.getTabs().add(mainTabPane.getTabs().size() - 1, tab);

        // 切换到新添加的标签页
        mainTabPane.getSelectionModel().select(tab);
    }

    @Override
    public TabPaneConfig saveAndGetConfig() {
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
