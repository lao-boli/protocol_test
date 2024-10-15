package org.hqu.lly.page;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.hqu.lly.constant.DocInstance;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.component.*;
import org.hqu.lly.domain.config.CustomDataConfig;
import org.hqu.lly.domain.config.JSCodeConfig;
import org.hqu.lly.domain.config.ScheduledSendConfig;
import org.hqu.lly.domain.config.SendSettingConfig;
import org.hqu.lly.utils.JSParser;
import org.hqu.lly.utils.MethodTimer;
import org.hqu.lly.utils.UIUtil;
import org.hqu.lly.view.controller.TitleBarController;

import static org.hqu.lly.utils.CommonUtil.intToStr;
import static org.hqu.lly.utils.CommonUtil.strToInt;

/**
 * <p>
 * send setting pane
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2024/2/28 16:04
 */
public class SendSettingPane extends MyDialog<BorderPane> {
    private static final Integer TIMES = 0;
    private static final Integer MANUAL_STOP = 1;
    public static final int TEXT_MODE = 0;
    public static final int CUSTOM_DATA_MODE = 1;
    public static final int JS_MODE = 2;
    private final String[] modeArray = {"普通文本", "自定义数据"};



    public SendSettingPane(SendSettingConfig sendConfig) {

        super();
        titleBar.setTitle("");

        content = new ContentPane(sendConfig);
        pane.setCenter(content);
        pane.setPrefWidth(600);
        pane.setPrefWidth(600);

        pane.getStylesheets().add(ResLoc.SEND_SETTING_PANE_CSS.toExternalForm());
    }

    private class ContentPane extends BorderPane {
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




        private VBox main = new VBox();


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

        public void testScript(MouseEvent event) {
            // System.out.println(sendSettingConfig.getCurEngine());
            MethodTimer.ResultWithTime<Object> cost = JSParser.testScript(jsEngineBox.getSelectionModel().getSelectedItem(), jsTextArea.getText());
            String msg = "脚本执行耗时: " + cost.getTime() + " ms\n脚本执行结果: " + cost.getResult();
            new MyAlert(Alert.AlertType.NONE, "执行结果", msg, (Stage) titleBar.getScene().getWindow()).showAndWait();
        }

        public ContentPane(SendSettingConfig config) {
            setupMain();
            setCenter(main);
        }

        void setupMain() {
            main.setSpacing(10);
            main.setStyle("-fx-background-color: #3c3f41");
            buildFirstRow();
            buildSecondRow();
            buildThirdRow();

        }

        void buildFirstRow() {

            ToggleGroup toggleGroup = new ToggleGroup();

            RadioButton sendByTimesBtn = new RadioButton("");
            Label name = new Label("发送次数");
            TextField sendCountTextField = new TextField("10");
            sendCountTextField.setPrefWidth(66);
            Label times = new Label("次");
            HBox box = new HBox(5, name, sendCountTextField, times);
            box.setAlignment(Pos.CENTER);
            sendByTimesBtn.setGraphic(box);
            sendByTimesBtn.setToggleGroup(toggleGroup);

            RadioButton manualStopBtn = new RadioButton("");
            Label name2 = new Label("手动停止");
            manualStopBtn.setSelected(true);
            manualStopBtn.setGraphic(new HBox(5, name2, manualStopBtn));
            manualStopBtn.setToggleGroup(toggleGroup);

            Label sendLabel = new Label("发送方式");
            sendLabel.setPadding(new Insets(3, 0, 0, 0));
            HBox row = new HBox(5, sendLabel, new VBox(sendByTimesBtn, manualStopBtn));
            main.getChildren().add(row);

        }

        void buildSecondRow() {

            TextField intervalField = new TextField("1000");
            intervalField.setPrefWidth(66);

            Label sendLabel = new Label("发送间隔");
            sendLabel.setPadding(new Insets(3, 0, 0, 0));
            HBox row = new HBox(5, sendLabel, intervalField,new Label("ms"));
            row.setAlignment(Pos.CENTER_LEFT);
            main.getChildren().add(row);
        }

        void buildThirdRow() {


            Label sendLabel = new Label("发送模式");
            sendLabel.setPadding(new Insets(3, 0, 0, 0));

            Tab common = new Tab("普通文本", new AnchorPane(new Label("请直接在输入框中输入文本")));

            TextArea customFormTextArea = new TextArea("\\%.2f %d %o %x ");
            customFormTextArea.setPrefHeight(90);
            customFormTextArea.setPrefWidth(220);
            customFormTextArea.setWrapText(true);
            HBox.setHgrow(customFormTextArea, Priority.SOMETIMES);

            Button setRangeBtn = new Button("设置值域");

            HBox hBox = new HBox(5, customFormTextArea, setRangeBtn);
            VBox vBox = new VBox(hBox);
            vBox.setPrefHeight(200);
            vBox.setPrefWidth(100);

            Tab custom = new Tab("自定义格式", vBox);

            Tab js = new Tab("使用javascript", buildJsPane());

            TabPane sendModeTabPane = new TabPane(common, custom,js);
            sendModeTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            HBox.setHgrow(sendModeTabPane, Priority.SOMETIMES);




            HBox row = new HBox(5, sendLabel, sendModeTabPane);
            row.setAlignment(Pos.CENTER_LEFT);
            main.getChildren().add(row);
        }

        VBox buildJsPane(){
            VBox container = new VBox();
            container.setPrefHeight(200);
            container.setPrefWidth(100);

            HBox hBox = new HBox();
            hBox.setSpacing(5);
            VBox.setVgrow(hBox, Priority.ALWAYS);

            TextArea jsTextArea = new TextArea("return Math.random()");
            jsTextArea.setPrefWidth(216);
            jsTextArea.setWrapText(true);
            HBox.setHgrow(jsTextArea, Priority.ALWAYS);

            Label helpIcon = new Label();
            helpIcon.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            helpIcon.setGraphic(new Region());
            StackPane helpIconPane = new StackPane(helpIcon);
            helpIconPane.getStyleClass().add("icon-help");
            helpIconPane.setAlignment(Pos.TOP_LEFT);

            Tooltip jsTip = UIUtil.getTooltip("帮助文档");
            UIUtil.setTooltip(jsHelpIcon,
                              jsTip,
                              e -> {
                                  Bounds bounds = jsHelpIcon.localToScreen(jsHelpIcon.getBoundsInLocal());
                                  jsTip.show(jsHelpIcon, bounds.getMinX(), bounds.getMinY() - 30);
                              });
            jsHelpIcon.setOnMouseClicked(event -> DocInstance.getJs().show());

            Button storingCodeBtn = new Button("加至暂存区");
            storingCodeBtn.setOnMouseClicked(event -> {
                jsStagingArea.stagingArea.storeText(jsTextArea.getText());
                new MessagePopup("已加入暂存区").showPopup(30, 0.7);
            });
            Button jsStoringAreaBtn = new Button("暂存区");
            jsStoringAreaBtn.setOnMouseClicked(event -> jsStagingArea.show());
            ChoiceBox<String> jsEngineBox = new ChoiceBox<>();
            Button jsTestBtn = new Button("测试脚本");
            jsTestBtn.setOnMouseClicked(this::testScript);

            VBox funArea = new VBox(helpIconPane,storingCodeBtn,jsStoringAreaBtn,jsEngineBox,jsTestBtn);
            funArea.minWidth(30);
            hBox.getChildren().addAll(jsTextArea,funArea);
            container.getChildren().add(hBox);

            return container;
        }

    }

}
