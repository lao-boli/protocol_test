package org.hqu.lly.view.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.service.TaskService;
import org.hqu.lly.view.handler.DragWindowHandler;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

/**
 * <p>
 * 标题栏控制器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/14 19:32
 */
@Slf4j
public class TitleBarController implements Initializable {
    @FXML
    private BorderPane titleBar;

    /**
     * 标题栏标题
     */
    @FXML
    private Label titleLabel;
    /**
     * 关闭窗口图标
     */
    @FXML
    private Label closeLabel;
    /**
     * 最小化窗口图标
     */
    @FXML
    private Label minimizeLabel;

    /**
     * 最大化窗口图标
     */
    @FXML
    private Label maxLabel;

    /**
     * 窗口固定图标
     */
    @FXML
    private Label pinLabel;

    /**
     * 执行关闭窗口任务
     */
    @Setter
    private TaskService onClose;

    /**
     * 执行关闭窗口前的任务
     */
    @Setter
    private Callable<Boolean> onBeforeClose;

    /**
     * 当前程序窗口舞台
     */
    private Stage stage;

    /**
     * 鼠标第一次移入标题栏标记
     */
    private boolean firstMoveIn = true;

    /**
     * stage固定标识
     */
    private boolean pined = true;

    /**
     * 窗口最大化标识
     */
    private boolean maxed = true;
    private ImageView restoreIcon;
    private ImageView maxIcon;

    @FXML
    void minimizeWindow(MouseEvent mouseEvent) {
        stage.setIconified(true);
    }

    @FXML
    void handleCloseWindow(MouseEvent event) throws Exception {
        if (onBeforeClose.call()){
            if (onClose != null) {
                onClose.fireTask();
            }else {
                stage.close();
            }
        }
    }

    @FXML
    void pinStage(MouseEvent event) {
        stage.setAlwaysOnTop(pined);
        if (pined){
            pinLabel.setStyle("-fx-background-color: #5c6164");
        }else {
            pinLabel.setStyle("-fx-background-color: #3c3f41");
        }
        pined = !pined;
    }

    @FXML
    void switchSize(MouseEvent event) {
        BorderPane back = (BorderPane) stage.getScene().getRoot();
        if (maxed){
            back.setPadding(new Insets(0));
            stage.setMaximized(maxed);
            correctSize(stage);
            maxLabel.setGraphic(restoreIcon);
        }else {
            back.setPadding(new Insets(5));
            stage.setMaximized(maxed);
            maxLabel.setGraphic(maxIcon);
        }
        maxed = !maxed;
    }

    /**
     * 修正stage的长度和宽度,保证在最大化时不遮挡任务栏
     * @param stage 主stage
     * @date 2023-03-21 19:44:51 <br>
     */
    private void correctSize(Stage stage){
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setHeight(bounds.getHeight());
        stage.setWidth(bounds.getWidth());
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

    /**
     * <p>
     *     初始化标题栏
     * </p>
     * @param title 标题栏上显示的标题
     * @param miniVisible 是否显示 {@link #minimizeLabel}
     * @param closeVisible 是否显示 {@link #closeLabel}
     * @date 2023-02-06 19:56:54 <br>
     */
    public void init(String title,Boolean miniVisible,Boolean closeVisible) {
        titleLabel.setText(title);
        minimizeLabel.setVisible(miniVisible);
        closeLabel.setVisible(closeVisible);
    }

    /**
     * <p>
     *     初始化标题栏,不显示 {@link #minimizeLabel}
     * </p>
     * @param title 标题栏上显示的标题
     * @date 2023-02-06 19:56:54 <br>
     */
    public void initHideMini(String title){
        init(title,false,true);
        titleLabel.setText(title);
    }

    /**
     * 初始化一个只显示关闭按钮的标题栏
     * @param title 标题文本
     * @date 2023-03-21 19:43:35 <br>
     */
    public void initOnlyClose(String title){
        minimizeLabel.setVisible(false);
        maxLabel.setVisible(false);
        pinLabel.setVisible(false);
        titleLabel.setText(title);
    }

    /**
     * <p>
     *     初始化标题栏
     * </p>
     * @param title 标题栏上显示的标题
     * @param miniVisible 是否显示 {@link #minimizeLabel}
     * @date 2023-02-06 19:56:54 <br>
     */
    public void init(String title,Boolean miniVisible){
        init(title,miniVisible,true);
        titleLabel.setText(title);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pinLabel.addEventFilter(MouseEvent.MOUSE_MOVED,e -> {
            if (pined){
                pinLabel.setStyle("-fx-background-color:  #4f5254;");
            }
        });
        pinLabel.addEventFilter(MouseEvent.MOUSE_EXITED,e -> {
            if (pined){
                pinLabel.setStyle("-fx-background-color: #3c3f41;");
            }
        });


        restoreIcon = new ImageView(new Image(ResLoc.RESTORE_ICON.toString()));
        restoreIcon.setFitHeight(15);
        restoreIcon.setFitWidth(15);
        restoreIcon.setPreserveRatio(true);
        restoreIcon.setPickOnBounds(true);

        maxIcon = new ImageView(new Image(ResLoc.MAX_ICON.toString()));
        maxIcon.setFitHeight(15);
        maxIcon.setFitWidth(15);
        maxIcon.setPreserveRatio(true);
        maxIcon.setPickOnBounds(true);
    }
}
