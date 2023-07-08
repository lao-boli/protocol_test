package org.hqu.lly.view.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.DataSettingPane;
import org.hqu.lly.domain.config.CustomDataConfig;
import org.hqu.lly.domain.config.ScheduledSendConfig;
import org.hqu.lly.domain.config.SendSettingConfig;

import static org.hqu.lly.utils.CommonUtil.intToStr;
import static org.hqu.lly.utils.CommonUtil.strToInt;

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
public class SendSettingController {

    private static final Integer TIMES = 0;
    private static final Integer MANUAL_STOP = 1;
    private final String[] modeArray = {"普通文本", "自定义数据"};
    /**
     * 自定义数据设置面板
     */
    protected DataSettingPane dataSettingPane;
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
    private SendSettingConfig sendSettingConfig;
    private ScheduledSendConfig scheduledSendConfig;

    // region 自定义数据相关
    @FXML
    private ChoiceBox<String> modeChoiceBox;
    @FXML
    private TextArea customFormTextArea;
    @FXML
    private Button showBoundPaneBtn;
    private CustomDataConfig customDataConfig;
    // endregion


    public void setConfig(SendSettingConfig config) {
        this.sendSettingConfig = config;
        this.customDataConfig = config.getCustomDataConfig();
        this.scheduledSendConfig = config.getScheduledSendConfig();
    }

    @FXML
    void saveSetting(MouseEvent event) {
        saveSetting();
    }

    /**
     * <p>
     * 保存定时发送设置,即<br>
     * ({@link ScheduledSendConfig#interval})和
     * ({@link ScheduledSendConfig#sendTimes})
     * </p>
     *
     * @date 2023-02-05 18:39:28 <br>
     */
    private void saveSetting() {
        scheduledSendConfig.setInterval(strToInt(intervalTextField.getText()));
        scheduledSendConfig.setSendTimes(strToInt(sendCountTextField.getText()));
    }

    /**
     * <p>
     * 从本地配置文件中加载配置.
     * </p>
     *
     * @date 2023-02-05 18:39:28 <br>
     */
    public void loadConfig() {

        // 加载配置中的定时设置
        intervalTextField.setText(intToStr(scheduledSendConfig.getInterval()));
        sendCountTextField.setText(intToStr(scheduledSendConfig.getSendTimes()));

        // 设置模式为配置中的模式
        if (sendSettingConfig.isTextMode()) {
            modeChoiceBox.setValue(modeArray[0]);
        }

        if (sendSettingConfig.isCustomMode()) {
            modeChoiceBox.setValue(modeArray[1]);
        }
        
        customFormTextArea.setText(customDataConfig.getCustomDataPattern());

        // XXX 当配置文件保存的发送格式为文本时仍然会加载自定义数据面板,待优化.

        // 防止空指针异常
        Platform.runLater(() -> {
            // 若为从本地配置文件中首次加载,则从本地配置中加载[customDataConfig]的数据
            customDataConfig.loadLocalConfig();
            //生成自定义数据面板
            dataSettingPane = new DataSettingPane(customDataConfig, (Stage) titleBar.getScene().getWindow());
        });

    }


    @FXML
    void showDataRangeSettingPane(MouseEvent event) {
        String dataPattern = customFormTextArea.getText();
        String customDataPattern = customDataConfig.getCustomDataPattern();

        // 若配置中的自定义数据格式与当前输入框中的相同
        if (customDataPattern != null
                && dataPattern.equals(customDataPattern)) {
            // 直接显示面板
            dataSettingPane.show();
        } else {
            // 读取输入框中新的数据格式,更新[customDataConfig]的数据
            customDataConfig.updateConfig(dataPattern);
            // 创建新的面板并显示
            dataSettingPane = new DataSettingPane(customDataConfig, (Stage) titleBar.getScene().getWindow());
            dataSettingPane.show();
        }
    }

    @FXML
    public void initialize() {
        // 标题栏初始化
        titleBarController.initOnlyClose("发送设置");
        titleBarController.setOnBeforeClose(() -> {
            saveSetting();
            return true;
        });

        // 定时发送设置初始化
        sendSetting.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == sendByTimesBtn) {
                scheduledSendConfig.setSendType(TIMES);
            }
            if (newValue == manualStopBtn) {
                scheduledSendConfig.setSendType(MANUAL_STOP);
            }
        });

        // 发送模式设置初始化
        modeChoiceBox.getItems().addAll(modeArray);
        // 设置初始模式为"普通文本"模式.
        modeChoiceBox.setValue(modeArray[0]);
        modeChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            // 普通文本模式
            if (newValue.intValue() == 0) {
                sendSettingConfig.setTextMode();
                sendSettingConfig.getOnModeChange().fireTask();
                Platform.runLater(() -> {
                    customFormTextArea.setDisable(true);
                    showBoundPaneBtn.setDisable(true);
                });
            }
            // 自定义数据模式
            if (newValue.intValue() == 1) {
                sendSettingConfig.setCustomMode();
                sendSettingConfig.getOnModeChange().fireTask();
                Platform.runLater(() -> {
                    customFormTextArea.setDisable(false);
                    showBoundPaneBtn.setDisable(false);
                });
            }
        });
    }

}
