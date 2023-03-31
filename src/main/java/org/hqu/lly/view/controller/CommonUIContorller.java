package org.hqu.lly.view.controller;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.hqu.lly.domain.component.MsgLabel;

/**
 * <p>
 * 共有UI控件控制器
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/3/27 21:31
 */
public abstract class CommonUIContorller extends BaseController {

    // region msg sidebar

    @FXML
    protected ToggleButton softWrapBtn;
    @FXML
    protected Button clearBtn;
    @FXML
    protected Button displaySettingBtn;

    // endregion

    @FXML
    protected ListView<MsgLabel> msgList;

    // region send relative

    @FXML
    protected Button sendSettingBtn;
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
     *  @date 2023-03-27 19:25
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

        ContextMenu contextMenu = new ContextMenu(time,host,length,msg);
        displaySettingBtn.setContextMenu(contextMenu);
        displaySettingBtn.addEventHandler(MouseEvent.MOUSE_CLICKED , e -> {
            contextMenu.show(displaySettingBtn, Side.BOTTOM, 0, 0);
        });

        msgList.getItems().addListener((ListChangeListener<MsgLabel>) c -> {
            while (c.next()){
                if (c.wasAdded()){
                    c.getAddedSubList().forEach(label -> {
                        label.showTime(time.isSelected());
                        label.showHost(host.isSelected());
                        label.showLength(length.isSelected());
                        label.showMsg(msg.isSelected());
                    });
                }
            }
        });
    }

}
