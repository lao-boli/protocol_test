package org.hqu.lly.component.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.CustomAlert;
import org.hqu.lly.service.TaskService;
import org.hqu.lly.view.controller.TitleBarController;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

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

    //    private enum ButtonType {
    //        OK,
    //        CANCEL;
    //    }

    @FXML
    TitleBarController titleBarController;
    @FXML
    private BorderPane titleBar;
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
    private TaskService onConfirm;

    @Setter
    private Supplier<Boolean> onCancel;


    /**
     * 当前程序窗口舞台
     */
    private Stage stage;

    /**
     * 点击按钮的类型,用于给{@link CustomAlert#showAndWait()}返回。
     */
    private ButtonType buttonType;

    /**
     * &emsp&emsp此变量为真时，取消按钮和确认按钮都将返回 {@link ButtonType#OK}.
     * <p>
     * &emsp&emsp一个使用场景是:你想在程序退出前弹出一个窗口提醒用户是否保存配置文件，
     * 此时，无论点击确认还是取消，都要让程序正常退出；但是当点击弹窗的“X”号时，
     * 应保证弹窗关闭，应用也不退出，因为有可能是用户误点了关闭程序的按钮。
     * </p>
     * <p>
     * &emsp&emsp遇到这种使用场景时，请将本变量置为true.
     * </p>
     */
    @Setter
    private Boolean forceResume = false;

    public ButtonType getButtonType() {
        return buttonType;
    }
    /**
     * <p>
     * 设置确认按钮回调方法
     * </p>
     * @param task 要执行的操作方法
     * @date 2023-02-08 09:36:55 <br>
     */
    public void setOnConfirm(TaskService task){
        this.onConfirm = task;
    }

    @FXML
    void onConfirm(MouseEvent mouseEvent) {
        buttonType = ButtonType.OK;
        onConfirm.fireTask();
        stage.close();
    }

    @FXML
    void onCancel(MouseEvent event) {
        if (forceResume) {
            buttonType = ButtonType.OK;
        } else {
            buttonType = ButtonType.CANCEL;
        }
        stage.close();
    }


    /**
     * <p>
     * 初始化弹框
     * </p>
     *
     * @param title 弹框标题
     * @param info  弹框信息
     * @date 2023-02-06 20:12:02 <br>
     */
    public void init(String title, String info) {
        getStage();
        titleBarController.initOnlyClose(title);
        titleBarController.setOnBeforeClose(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                buttonType = ButtonType.CANCEL;
                return true;
            }
        });
        infoLabel.setText(info);
    }

    /**
     * <p>
     * 初始化完成后调用,获取当前窗口的 {@link #stage}实例
     * </p>
     *
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
