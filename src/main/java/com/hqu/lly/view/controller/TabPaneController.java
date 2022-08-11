package com.hqu.lly.view.controller;

import com.hqu.lly.factory.TCPClientTabFactory;
import com.hqu.lly.factory.TabFactory;
import com.hqu.lly.view.group.ContentPaneGroup;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/8 9:51
 * @Version 1.0
 */
public class TabPaneController implements Initializable {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab createTab;

    private TabFactory TabFactory;

    public void setTabFactory(com.hqu.lly.factory.TabFactory tabFactory) {
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

        mainTabPane.getTabs().add(mainTabPane.getTabs().size() - 1,tab );

        mainTabPane.getSelectionModel().select(tab);


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


//        ContentPaneGroup.tabPaneMap.put("tcpClient",mainTabPane);

    }
}
