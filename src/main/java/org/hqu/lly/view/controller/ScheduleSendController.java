package org.hqu.lly.view.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.StageConsts;
import org.hqu.lly.domain.bean.ScheduledSendConfig;
import org.hqu.lly.service.impl.StageBridger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>
 * 定时发送消息弹出框控制器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/25 17:37
 */
@Slf4j
public class ScheduleSendController implements Initializable {

    private static final Integer TIMES = 0;
    private static final Integer MANUAL_STOP = 1;
    @Setter
    private StageBridger bridger;
    @FXML
    private TextField intervalTextField;
    @FXML
    private TextField sendCountTextField;
    @FXML
    private ToggleGroup sendSetting;
    @FXML
    private RadioButton sendByTimesBtn;
    @FXML
    private RadioButton manualStopBtn;
    @FXML
    private Button saveSettingBtn;
    @FXML
    private BorderPane titleBar;
    @FXML
    private TitleBarController titleBarController;
    @Setter
    private ScheduledSendConfig sendConfig;

    @FXML
    void saveSetting(MouseEvent event) {
        sendConfig.setInterval(Integer.valueOf(intervalTextField.getText()));
        sendConfig.setSendTimes(Integer.valueOf(sendCountTextField.getText()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sendSetting.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == sendByTimesBtn) {
                    sendConfig.setSendType(TIMES);
                }
                if (newValue == manualStopBtn) {
                    sendConfig.setSendType(MANUAL_STOP);
                }
            }
        });
        titleBarController.initTitleBar(StageConsts.SEND_SETTING);
    }

}
