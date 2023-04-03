package org.hqu.lly.view.controller;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.enums.DataType;
import org.hqu.lly.utils.MsgUtil;

import static org.hqu.lly.enums.DataType.*;
import static org.hqu.lly.utils.UIUtil.getFixMsgLabelWidth;

/**
 * <p>
 * 共有UI控件控制器
 * <p>
 *
 * @author hqully
 * @date 2023/3/27 21:31
 * @since 0.1.0
 */
@Slf4j
public abstract class CommonUIContorller extends BaseController {

    /**
     * 接收消息的格式类型
     */
    protected DataType recvMsgType = PLAIN_TEXT;

    // region msg sidebar

    @FXML
    protected ToggleButton softWrapBtn;
    @FXML
    protected Button clearBtn;
    @FXML
    protected Button displaySettingBtn;

    // endregion

    //region msg list relative

    /**
     * 长文本是否换行flag
     */
    protected boolean softWrap = false;
    @FXML
    protected Button recvFormatBtn;
    @FXML
    protected ListView<MsgLabel> msgList;
    //endregion

    // region send relative

    @FXML
    protected Button sendSettingBtn;
    @FXML
    protected Button sendFormatBtn;
    @FXML
    protected TextArea msgInput;
    @FXML
    protected Button sendMsgButton;
    @FXML
    protected ToggleButton scheduleSendBtn;
    //endregion


    /**
     * 设置消息显示设置按钮 {@link #displaySettingBtn} 的contextMenu.
     * 包括时间、主机、消息长度、消息内容的显示和隐藏.
     *
     * @date 2023-03-27 19:25
     */
    protected void setupDisplaySetting() {
        RadioMenuItem time = new RadioMenuItem("时间");
        time.setSelected(true);
        time.setOnAction(event -> msgList.getItems().forEach(item -> item.showTime(time.isSelected())));

        RadioMenuItem host = new RadioMenuItem("主机");
        host.setSelected(true);
        host.setOnAction(event -> msgList.getItems().forEach(item -> item.showHost(host.isSelected())));

        RadioMenuItem length = new RadioMenuItem("消息长度");
        length.setSelected(true);
        length.setOnAction(event -> msgList.getItems().forEach(item -> item.showLength(length.isSelected())));

        RadioMenuItem msg = new RadioMenuItem("消息内容");
        msg.setSelected(true);
        msg.setOnAction(event -> msgList.getItems().forEach(item -> item.showMsg(msg.isSelected())));

        ContextMenu contextMenu = new ContextMenu(time, host, length, msg);
        displaySettingBtn.setContextMenu(contextMenu);
        displaySettingBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            contextMenu.show(displaySettingBtn, Side.BOTTOM, 0, 0);
        });

        msgList.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (softWrap) {
                Platform.runLater(() -> msgList.getItems().forEach(msgLabel -> msgLabel.setPrefWidth(getFixMsgLabelWidth(newValue.doubleValue()))));
            }
        });

        msgList.getItems().addListener((ListChangeListener<MsgLabel>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(label -> {
                        label.showTime(time.isSelected());
                        label.showHost(host.isSelected());
                        label.showLength(length.isSelected());
                        label.showMsg(msg.isSelected());

                        if (softWrap) {
                            label.setPrefWidth(getFixMsgLabelWidth(msgList.getWidth()));
                        }

                        label.changeFormat(PLAIN_TEXT, recvMsgType);
                    });
                }
            }
        });
    }


    /**
     * 初始化接收消息格式化按钮
     *
     * @date 2023-04-03 19:01
     * @since 0.2.0
     */
    protected void setupRecvFormatBtn() {
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null || newValue == null) {
                return;
            }
            DataType from = (DataType) oldValue.getUserData();
            DataType to = (DataType) newValue.getUserData();
            msgList.getItems().forEach(msgLabel -> msgLabel.changeFormat(from, to));
            recvMsgType = to;
        });
        setupFormatBtn(toggleGroup, recvFormatBtn);
    }

    /**
     * 初始化发送格式化按钮
     *
     * @date 2023-04-03 19:01
     * @since 0.2.0
     */
    protected void setupSendFormatBtn() {
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null || newValue == null) {
                return;
            }
            String converted;
            try {
                converted = MsgUtil.convertText((DataType) oldValue.getUserData(), (DataType) newValue.getUserData(), msgInput.getText());
            } catch (Exception e) {
                log.warn(e.toString());
                // json格式转换出错时,保存原本的选项
                converted = msgInput.getText();
                toggleGroup.selectToggle(oldValue);
            }
            msgInput.setText(converted);
        });
        setupFormatBtn(toggleGroup, sendFormatBtn);
    }


    /**
     * 初始化文本格式化按钮
     *
     * @param toggleGroup 单选菜单组
     * @param formatBtn   {@link #sendFormatBtn} 或 {@link #recvFormatBtn}
     * @date 2023-04-03 19:00
     * @since 0.2.0
     */
    private void setupFormatBtn(ToggleGroup toggleGroup, Button formatBtn) {
        RadioMenuItem plainText = new RadioMenuItem("plain text");
        plainText.setUserData(PLAIN_TEXT);
        plainText.setSelected(true);

        RadioMenuItem hex = new RadioMenuItem("Hex");
        hex.setUserData(HEX);

        RadioMenuItem base64 = new RadioMenuItem("Base64");
        base64.setUserData(BASE64);

        RadioMenuItem json = new RadioMenuItem("JSON");
        json.setUserData(JSON);

        toggleGroup.getToggles().addAll(plainText, hex, base64, json);

        ContextMenu contextMenu = new ContextMenu(plainText, hex, base64, json);
        formatBtn.setContextMenu(contextMenu);
        formatBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            contextMenu.show(formatBtn, Side.RIGHT, 0, 0);
        });
    }

    @FXML
    protected void handleSoftWrap(MouseEvent event) {
        softWrap = !softWrap;
        double labelWidth = softWrap ? getFixMsgLabelWidth(msgList.getWidth()) : Region.USE_COMPUTED_SIZE;
        Platform.runLater(() -> msgList.getItems().forEach(msgLabel -> msgLabel.setPrefWidth(labelWidth)));
    }

}
