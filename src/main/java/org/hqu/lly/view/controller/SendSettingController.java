package org.hqu.lly.view.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.StageConsts;
import org.hqu.lly.domain.bean.CustomDataConfig;
import org.hqu.lly.domain.bean.ScheduledSendConfig;
import org.hqu.lly.domain.bean.SendSettingConfig;
import org.hqu.lly.factory.DataSettingPaneFactory;
import org.hqu.lly.service.TaskService;
import org.hqu.lly.service.impl.StageBridger;
import org.hqu.lly.utils.ConfUtil;

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
public class SendSettingController implements Initializable {

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
    private SendSettingConfig sendSettingConfig;

    @Setter
    private ScheduledSendConfig scheduledSendConfig;

    @FXML
    private ChoiceBox<String> modeChoiceBox;

    private final String[] modeArray = {"普通文本","自定义数据"};

    // region 自定义数据相关

    @FXML
    private TextArea customFormTextArea;

    @FXML
    private Button showBoundPaneBtn;

    /**
     * 自定义数据设置面板
     */
    protected Stage dataSettingPane;

    @Setter
    private CustomDataConfig customDataConfig;
    // endregion


    @FXML
    void saveSetting(MouseEvent event) {
        scheduledSendConfig.setInterval(Integer.valueOf(intervalTextField.getText()));
        scheduledSendConfig.setSendTimes(Integer.valueOf(sendCountTextField.getText()));
        ConfUtil.saveConf(sendSettingConfig);
//        ConfUtil.loadConf();
    }


    @FXML
    void showDataRangeSettingPane(MouseEvent event) {
        String dataPattern = customFormTextArea.getText();
        String customDataPattern = customDataConfig.getCustomDataPattern();
        if (customDataPattern != null && dataPattern.equals(customDataPattern)){
            dataSettingPane.show();
        }else {
            customDataConfig.updateConfig(dataPattern);
            dataSettingPane = DataSettingPaneFactory.create(customDataConfig);
            dataSettingPane.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 标题栏初始化
        titleBarController.initTitleBar(StageConsts.SEND_SETTING);
        titleBarController.setOnBeforeClose(new TaskService() {
            @Override
            public void fireTask() {
                scheduledSendConfig.setInterval(Integer.valueOf(intervalTextField.getText()));
                scheduledSendConfig.setSendTimes(Integer.valueOf(sendCountTextField.getText()));
            }
        });

        // 定时发送设置初始化
        sendSetting.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == sendByTimesBtn) {
                    scheduledSendConfig.setSendType(TIMES);
                }
                if (newValue == manualStopBtn) {
                    scheduledSendConfig.setSendType(MANUAL_STOP);
                }
            }
        });

        // 发送模式设置初始化

        modeChoiceBox.getItems().addAll(modeArray);
        modeChoiceBox.setValue(modeArray[0]);
        modeChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // 普通文本模式
                if (newValue.intValue() == 0){
                    sendSettingConfig.setTextMode();
                    sendSettingConfig.getOnModeChange().fireTask();
                    Platform.runLater(() -> {
                        customFormTextArea.setDisable(true);
                        showBoundPaneBtn.setDisable(true);
                    });
                }
                // 自定义数据模式
                if (newValue.intValue() == 1){
                    sendSettingConfig.setCustomMode();
                    sendSettingConfig.getOnModeChange().fireTask();
                    Platform.runLater(() -> {
                        customFormTextArea.setDisable(false);
                        showBoundPaneBtn.setDisable(false);
                    });
                }
            }
        });
    }

}
