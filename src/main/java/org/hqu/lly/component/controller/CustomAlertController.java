package org.hqu.lly.component.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.service.TaskService;
import org.hqu.lly.view.controller.TitleBarController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>
 * 自定义样式的弹窗控制器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/14 19:32
 */
@Slf4j
public class CustomAlertController implements Initializable {

    @FXML
    private BorderPane titleBar;
    @FXML
    TitleBarController titleBarController;
    @FXML
    private Label infoLabel;
    @FXML
    private Button confirmBtn;
    @FXML
    private Button cancelBtn;

    @Setter
    private Integer type;
    /**
     * 执行点击确认按钮后的回调任务
     */
    @Setter
    private TaskService onConfirm;


    /**
     * 当前程序窗口舞台
     */
    private Stage stage;


    @FXML
    void onConfirm(MouseEvent mouseEvent) {
        onConfirm.fireTask();
        stage.close();
    }

    @FXML
    void onCancel(MouseEvent event) {
        stage.close();
    }


    /**
     * <p>
     *     初始化弹框
     * </p>
     * @param title 弹框标题
     * @param info 弹框信息
     * @date 2023-02-06 20:12:02 <br>
     */
    public void init(String title, String info){
        getStage();
        titleBarController.initHideMini(title);
        infoLabel.setText(info);
    }

    /**
     * <p>
     * 初始化完成后调用,获取当前窗口的 {@link #stage}实例
     * </p>
     * @date 2023-02-06 19:19:50 <br>
     */
    private void getStage() {
        if (stage == null) {
            stage = (Stage) titleBar.getScene().getWindow();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
