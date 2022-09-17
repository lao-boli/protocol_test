package org.hqu.lly.view.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.hqu.lly.factory.TabFactory;

import java.net.URL;
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
public class TabPaneController implements Initializable {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab createTab;

    private TabFactory TabFactory;

    public void setTabFactory(TabFactory tabFactory) {
        TabFactory = tabFactory;
    }


    @FXML
    void createNewTab(Event event) {
        if (TabFactory != null) {
            createNewTab();
        }
    }

    public void createNewTab() {

        Tab tab = TabFactory.create();
        mainTabPane.getTabs().add(mainTabPane.getTabs().size() - 1, tab);

        // 切换到新添加的标签页
        mainTabPane.getSelectionModel().select(tab);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
