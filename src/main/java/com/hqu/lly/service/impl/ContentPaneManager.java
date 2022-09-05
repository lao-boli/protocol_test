package com.hqu.lly.service.impl;

import com.hqu.lly.enums.TabFactoryEnum;
import com.hqu.lly.factory.TCPClientTabFactory;
import com.hqu.lly.service.SwitchPaneService;
import com.hqu.lly.view.controller.TabPaneController;
import com.hqu.lly.view.group.ContentPaneGroup;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import lombok.SneakyThrows;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/8 19:22
 * @Version 1.0
 */
public class ContentPaneManager implements SwitchPaneService {

    private Pane rootPane;

    private String contentPaneName;

    private String contentPanePath;

    public ContentPaneManager(Pane rootPane, String contentPaneName, String contentPanePath) {
        this.rootPane = rootPane;
        this.contentPaneName = contentPaneName;
        this.contentPanePath = contentPanePath;
    }

    @Override
    public void switchPane() {
        if (rootPane.getChildren().size() > 0) {
            rootPane.getChildren().remove(0);
        }
        Parent contentPane = ContentPaneGroup.contentPaneMap.get(contentPaneName);
        if (contentPane != null) {
            rootPane.getChildren().add(contentPane);
        } else {
            rootPane.getChildren().add(createContentPane(contentPanePath));
        }
    }

    @SneakyThrows
    private Parent createContentPane(String contentPanePath) {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(contentPanePath));

        Parent contentPane = loader.load();

        Object controller = loader.getController();

        if (controller instanceof TabPaneController) {
            ((TabPaneController) controller).setTabFactory(TabFactoryEnum.getByPaneType(contentPaneName).getTabFactory());
            ((TabPaneController) controller).createNewTab();
        }

        ContentPaneGroup.contentPaneMap.put(contentPaneName, contentPane);

        return contentPane;
    }

}
