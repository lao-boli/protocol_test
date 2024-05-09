package org.hqu.lly.page;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

    }

}
