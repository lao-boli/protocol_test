package org.hqu.lly.view.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.DataSettingPane;
import org.hqu.lly.domain.component.JSStagingArea;
import org.hqu.lly.domain.component.MyAlert;
import org.hqu.lly.domain.config.CustomDataConfig;
import org.hqu.lly.domain.config.JSCodeConfig;
import org.hqu.lly.domain.config.ScheduledSendConfig;
import org.hqu.lly.domain.config.SendSettingConfig;
import org.hqu.lly.utils.JSParser;
import org.hqu.lly.utils.MethodTimer;
import org.hqu.lly.utils.UIUtil;

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
    public static final int TEXT_MODE = 0;
    public static final int CUSTOM_DATA_MODE = 1;
    public static final int JS_MODE = 2;
    private final String[] modeArray = {"普通文本", "自定义数据"};


    /**
     * 自定义数据设置面板
     */
    protected DataSettingPane dataSettingPane;
    protected JSStagingArea jsStagingArea;
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
    /**
     * 发送模式设置面板
     */
    @FXML
    public TabPane sendModeTabPane;
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
    private TextArea customFormTextArea;
    @FXML
    private Button showBoundPaneBtn;
    private CustomDataConfig customDataConfig;
    // endregion

    // region js数据相关
    @FXML
    public TextArea jsTextArea;
    @FXML
    public Button jsTestBtn;
    @FXML
    public Label jsHelpIcon;
    @FXML
    public ChoiceBox<JSParser.EngineType> jsEngineBox;
    @FXML
    public Button jsStoringAreaBtn;
    private JSCodeConfig jsCodeConfig;
    ;
    // endregion


    public void setConfig(SendSettingConfig config) {
        this.sendSettingConfig = config;
        this.customDataConfig = config.getCustomDataConfig();
        this.scheduledSendConfig = config.getScheduledSendConfig();
        this.jsCodeConfig = config.getJsCodeConfig();
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
        jsCodeConfig.setScript(jsTextArea.getText());
        jsStagingArea.saveConfig(jsCodeConfig);
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

        jsCodeConfig.setEngine(jsCodeConfig.getEngine());
        jsCodeConfig.setScript(jsCodeConfig.getScript());

        // 设置模式为配置中的模式
        if (sendSettingConfig.isTextMode()) {
            sendModeTabPane.getSelectionModel().select(TEXT_MODE);
        }
        if (sendSettingConfig.isCustomMode()) {
            sendModeTabPane.getSelectionModel().select(CUSTOM_DATA_MODE);
        }
        if (sendSettingConfig.isJSMode()) {
            sendModeTabPane.getSelectionModel().select(JS_MODE);
        }

        customFormTextArea.setText(customDataConfig.getCustomDataPattern());

        jsTextArea.setText(jsCodeConfig.getScript());
        jsStagingArea.loadConfig(jsCodeConfig);


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
    public void testScript(MouseEvent event) {
        // System.out.println(sendSettingConfig.getCurEngine());
        MethodTimer.ResultWithTime<Object> cost = JSParser.testScript(jsEngineBox.getSelectionModel().getSelectedItem(), jsTextArea.getText());
        String msg = "脚本执行耗时: " + cost.getTime() + " ms\n脚本执行结果: " + cost.getResult();
        new MyAlert(Alert.AlertType.NONE, "执行结果", msg, (Stage) titleBar.getScene().getWindow()).showAndWait();
    }

    private void initIcon() {
        // TODO 自定义一个tooltip样式组件
        Tooltip jsTip = UIUtil.getTooltip("""
                                                  JS执行时间应小于发送间隔
                                                  可先执行几次JS脚本进行预热
                                                  以减少后续执行时间""");
        UIUtil.setTooltip(jsHelpIcon,
                          jsTip,
                          e -> {
                              Bounds bounds = jsHelpIcon.localToScreen(jsHelpIcon.getBoundsInLocal());
                              jsTip.show(jsHelpIcon, bounds.getMinX(), bounds.getMinY() - 65);
                          });
    }

    public void openStoringArea(MouseEvent event) {
        jsStagingArea.show();

    }

    public void storingCode(MouseEvent event) {
        jsStagingArea.stagingArea.storeText(jsTextArea.getText());
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
        sendModeTabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int i = newValue.intValue();
            if (i == TEXT_MODE) {
                sendSettingConfig.setTextMode();
                sendSettingConfig.getOnModeChange().fireTask();
            }

            if (i == CUSTOM_DATA_MODE) {
                sendSettingConfig.setCustomMode();
                sendSettingConfig.getOnModeChange().fireTask();
                Platform.runLater(() -> {
                    customFormTextArea.setDisable(false);
                    showBoundPaneBtn.setDisable(false);
                });
            }

            if (i == JS_MODE) {
                sendSettingConfig.setJSMode();
                sendSettingConfig.getOnModeChange().fireTask();
            }
        });

        initIcon();
        initEngineBox();

        jsStagingArea = new JSStagingArea(s -> jsTextArea.setText(s));
        Platform.runLater(() -> {
            jsStagingArea.initOwner(titleBar.getScene().getWindow());
        });

    }

    private void initEngineBox() {
        jsEngineBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            jsCodeConfig.setEngine(newValue);
        });
        jsEngineBox.getItems().addAll(JSParser.EngineType.GRAAL, JSParser.EngineType.NASHORN);
        Platform.runLater(() -> {
            if (sendSettingConfig != null && jsCodeConfig.getEngine() != null) {
                jsEngineBox.getSelectionModel().select(jsCodeConfig.getEngine());
            } else {
                jsEngineBox.getSelectionModel().select(JSParser.EngineType.NASHORN);
            }
        });

    }

}
