package org.hqu.lly.domain.component;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.config.StoringAreaConfig;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
    TabPane tabPane;

    /**
     * {@link Content#chooseBtn}的点击回调 <br>
     * 传入参数为 {@link Content#textArea} 内的文本
     */
    Consumer<String> onChoose;

    public StagingArea() {
        tabPane = new TabPane();
        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        tabPane.getStylesheets().add(ResLoc.STAGING_AREA_CSS.toExternalForm());
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }

    public StagingArea(Consumer<String> onChoose) {
        this();
        this.onChoose = onChoose;
    }

    public void storeText(String text) {
        TitleTab tab = new TitleTab("title", tabPane);
        tab.setContent(new Content(text,onChoose));
        tab.setOnCloseRequest(() -> {
            Optional<ButtonType> result = new MyAlert(Alert.AlertType.WARNING, "", "关闭后将删除暂存区内容,是否继续?").showAndWait();
            return result.get().equals(ButtonType.OK);
        });
        tabPane.getTabs().add(tab);
    }

    public void loadConfig(StoringAreaConfig config) {
        TitleTab tab = new TitleTab(config.getTitle(), tabPane);
        tab.setContent(new Content(config.getText(),onChoose));
        tab.setOnCloseRequest(() -> {
            Optional<ButtonType> result = new MyAlert(Alert.AlertType.WARNING, "", "关闭后将删除暂存区内容,是否继续?").showAndWait();
            return result.get().equals(ButtonType.OK);
        });
        tabPane.getTabs().add(tab);
    }

    public void loadConfig(List<StoringAreaConfig> configs) {
        configs.forEach(this::loadConfig);
    }

    public class Content extends VBox {

        HBox container;

        TextArea textArea;

        Button chooseBtn;

        public Content(String text, Consumer<String> onChoose) {
            textArea = new TextArea(text);
            textArea.setEditable(false);

            chooseBtn = new Button("选择");
            chooseBtn.setMinWidth(50);
            chooseBtn.setOnMouseClicked(e->onChoose.accept(textArea.getText()));

            HBox.setHgrow(textArea, Priority.ALWAYS);
            container = new HBox(textArea,chooseBtn);
            container.setSpacing(5);
            container.setAlignment(Pos.BOTTOM_LEFT);

            VBox.setVgrow(container,Priority.ALWAYS);
            getChildren().add(container);
        }


        public String getCode(){
            return textArea.getText();
        }
    }

}
