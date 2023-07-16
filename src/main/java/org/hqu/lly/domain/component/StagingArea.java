package org.hqu.lly.domain.component;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.config.StoringAreaConfig;

import java.util.List;

/**
 * <p>
 * 文本暂存区
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/7/15 20:24
 */
public class StagingArea {
    TabPane root;

    public StagingArea() {
        root = new TabPane();
        root.setPrefHeight(600);
        root.setPrefWidth(400);
        root.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        root.getStylesheets().add(ResLoc.STAGING_AREA_CSS.toExternalForm());
    }

    public void storeText(String text) {
        TitleTab tab = new TitleTab("title", root);
        tab.setContent(new Content(text));
        root.getTabs().add(tab);
    }

    public void loadConfig(StoringAreaConfig config) {
        TitleTab tab = new TitleTab(config.getTitle(), root);
        tab.setContent(new Content(config.getText()));
        root.getTabs().add(tab);
    }

    public void loadConfig(List<StoringAreaConfig> configs) {
        configs.forEach(this::loadConfig);
    }

    public class Content extends VBox {

        HBox container;

        TextArea textArea;

        Button chooseBtn;

        public Content(String text) {
            textArea = new TextArea(text);
            textArea.setEditable(false);
            chooseBtn = new Button("选择");
            chooseBtn.setMinWidth(50);

            HBox.setHgrow(textArea, Priority.ALWAYS);
            container = new HBox(textArea,chooseBtn);
            container.setSpacing(5);
            container.setAlignment(Pos.BOTTOM_LEFT);

            VBox.setVgrow(container,Priority.ALWAYS);
            getChildren().add(container);
        }

        public Content() {
            this("");
        }

        public String getCode(){
            return textArea.getText();
        }
    }

}
