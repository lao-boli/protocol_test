package org.hqu.lly.view.controller;

import org.hqu.lly.view.handler.DragWindowHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>
 * 标题栏controller
 * <p>
 *
 * @author liulingyu
 * @version 1.0
 * @date 2022/8/14 19:32
 */
@Slf4j
public class TitleBarController implements Initializable {
    @FXML
    private AnchorPane titleBar;

    /**
     * 当前程序窗口舞台
     */
    private Stage stage;

    /**
     * 鼠标第一次移入标题栏
     */
    private boolean firstMoveIn = true;

    @FXML
    void minimizeWindow(MouseEvent mouseEvent) {
        stage.setIconified(true);
    }

    @FXML
    void closeWindow(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    void initDrag(MouseEvent event) {
        // 鼠标第一次移入标题栏时，获取stage并初始化窗口拖拽方法
        if (firstMoveIn) {
            getStage();
            setWindowDrag(stage);
            firstMoveIn = false;
        }
    }

    private void setWindowDrag(Stage stage) {
        DragWindowHandler dragWindowHandler = new DragWindowHandler(stage);
        titleBar.setOnMousePressed(dragWindowHandler);
        titleBar.setOnMouseDragged(dragWindowHandler);
    }

    private void getStage() {
        if (stage == null) {
            stage = (Stage) titleBar.getScene().getWindow();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
