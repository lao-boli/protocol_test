package org.hqu.lly.page;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.component.MyDialog;
import org.hqu.lly.domain.config.SendSettingConfig;

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


    public SendSettingPane(SendSettingConfig sendConfig) {

        super();
        titleBar.setTitle("");

        content = new ContentPane();
        pane.setCenter(content);
        pane.setPrefWidth(600);
        pane.setPrefWidth(600);

        pane.getStylesheets().add(ResLoc.SEND_SETTING_PANE_CSS.toExternalForm());
    }

    private class ContentPane extends BorderPane {

        private VBox main = new VBox();

        public ContentPane() {
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

            Button storingCodeBtn = new Button("加至暂存区");
            Button jsStoringAreaBtn = new Button("暂存区");
            ChoiceBox<String> jsEngineBox = new ChoiceBox<>();
            Button jsTestBtn = new Button("测试脚本");

            VBox funArea = new VBox(helpIconPane,storingCodeBtn,jsStoringAreaBtn,jsEngineBox,jsTestBtn);
            funArea.minWidth(30);
            hBox.getChildren().addAll(jsTextArea,funArea);
            container.getChildren().add(hBox);

            return container;
        }

    }

}
