package org.hqu.lly.domain.component;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.utils.DragUtil;
import org.hqu.lly.utils.ThemeUtil;
import org.hqu.lly.utils.UIUtil;

/**
 * <p>
 * my dialog
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023-07-15 09:32
 */
@Slf4j
public class MyDialog<T extends Node> {

    protected TitleBar titleBar;
    public Stage stage = new Stage();

    protected T content;
    protected BorderPane pane = new BorderPane();

    protected double xCache = -1;
    protected double yCache = -1;

    protected boolean moved = false;

    protected DoubleProperty prefWidth = new SimpleDoubleProperty();
    protected DoubleProperty prefHeight = new SimpleDoubleProperty();


    protected void defaultPos() {
        Stage primaryStage = UIUtil.getPrimaryStage();
        if (primaryStage == null) {
            return;
        }
        double windowX = primaryStage.getX();
        double windowY = primaryStage.getY();
        double windowWidth = primaryStage.getWidth();
        double windowHeight = primaryStage.getHeight();

        // 计算提示框应该显示的位置
        double popupX = windowX + (windowWidth - prefWidth.get()) / 2;
        double popupY = windowY + (windowHeight - prefHeight.get()) / 2;
        xCache = popupX;
        yCache = popupY;

    }

    public void close() {
        stage.close();
    }

    public void show() {
        if (!moved) {
            defaultPos();
            if (xCache != -1 || yCache != -1) {
                stage.setX(xCache);
                stage.setY(yCache);
            }
        }
        stage.show();
    }

    public MyDialog() {

        // title bar
        initTitleBar();

        // scene and stage
        final Scene scene = initScene();
        DragUtil.setDrag(stage, scene.getRoot());
        ThemeUtil.applyStyle(scene);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    /**
     * init owner stage
     */
    public MyDialog(Stage parent) {
        this();
        initOwner(parent);
    }

    protected Scene initScene() {
        return initScene(450, 300);
    }

    protected Scene initScene(double width, double height) {
        prefWidth.set(width);
        prefHeight.set(height);
        val scene = UIUtil.getShadowScene(pane, width, height);
        return scene;
    }

    protected void initTitleBar() {
        initTitleBar("title");
    }

    protected TitleBar initTitleBar(String title) {
        titleBar = new TitleBar(this, title);
        pane.setTop(titleBar);
        return titleBar;
    }


    public void initOwner(Window owner) {
        stage.initOwner(owner);
    }

    protected class TitleBar extends BorderPane {

        Label titleLabel;

        StackPane close;

        MyDialog dialog;

        private double dragOffsetX;
        private double dragOffsetY;

        public TitleBar(MyDialog dialog, String title) {
            this.dialog = dialog;
            this.getStyleClass().add("alert-title-bar");
            this.getStylesheets().add(ResLoc.ALERT_TITLE_BAR_CSS.toExternalForm());


            setOnMousePressed(e -> {
                dragOffsetX = e.getScreenX() - stage.getX();
                dragOffsetY = e.getScreenY() - stage.getY();
            });
            setOnMouseDragged(e -> {
                dialog.moved = true;
                stage.setX(e.getScreenX() - dragOffsetX);
                stage.setY(e.getScreenY() - dragOffsetY);
            });

            setupClose();
            setupTitle(title);

        }

        public void setTitle(String title) {
            titleLabel.setText(title);
        }

        private void setupTitle(String title) {
            titleLabel = new Label(title);
            titleLabel.getStyleClass().add("title");
            setLeft(titleLabel);
        }

        void setupClose() {
            close = new StackPane(new Region());
            close.getStyleClass().add("close");
            close.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> dialog.close());
            setRight(close);

        }

    }


}
