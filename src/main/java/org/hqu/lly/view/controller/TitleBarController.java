package org.hqu.lly.view.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.StageConsts;
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

    public static final String MAIN_PANE = "协议测试工具";
    public static final String SEND_SETTING = "发送设置";
    public static final String DATA_SETTING = "数据设置";
    @Setter
    private Integer type;
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


    private void exitApp() {
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

    public void initTitleBar(Integer type){
        this.type = type;
        if (type.equals(StageConsts.MAIN_PANE)){
            titleLabel.setText(MAIN_PANE);
        }
        if (type.equals(StageConsts.SEND_SETTING)){
            minimizeLabel.setVisible(false);
            titleLabel.setText(SEND_SETTING);
        }
        if (type.equals(StageConsts.DATA_SETTING)){
            minimizeLabel.setVisible(false);
            titleLabel.setText(SEND_SETTING);
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

    }
}
