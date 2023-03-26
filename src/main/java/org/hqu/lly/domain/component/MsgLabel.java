package org.hqu.lly.domain.component;

import javafx.beans.property.BooleanProperty;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.utils.CommonUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 网络消息文本
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/3/25 16:03
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class MsgLabel extends TextFlow {

    /**
     * 消息类型枚举
     */
 public enum Type {
        /**
         * 接收消息类型
         */
        RECEIVE,
        /**
         * 发送消息类型
         */
        SEND,
        /**
         * 普通消息类型
         */
        COMMON
    }

    private BooleanProperty wrapText;

    private Text timeText;

    private Text hostText;

    private Text lengthText;

    private Text msgText;

    private Type type;

    /**
     * 普通消息构造器
     *
     * @param msg 消息
     * @date 2023-03-25 21:27
     */
    public MsgLabel(String msg) {
        super();
        initTimeText();
        this.hostText = new Text();
        this.lengthText = new Text();
        this.msgText = new Text(msg);
        this.getChildren().addAll(this.timeText, this.hostText, this.lengthText, this.msgText);
    }

    /**
     * 网络消息构造器
     * @param type 消息类型 应为 {@link Type}中的值
     * @param host 消息收发地址
     * @param msg 消息内容
     */
    public MsgLabel(Type type, String host, String msg) {
        super();
        this.type = type;
        initTimeText();
        initHostText(host);
        initLengthText(msg);
        this.msgText = new Text(msg);
        this.getChildren().addAll(this.timeText, this.hostText, this.lengthText, this.msgText);
    }

    private void initTimeText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = LocalDateTime.now().format(formatter) + " ";
        this.timeText = new Text(time);
    }

    private void initHostText(String host) {
        StringBuilder sb = new StringBuilder();
        if (Type.SEND.equals(type)) {
            sb.append("---> ");
        }
        if (Type.RECEIVE.equals(type)) {
            sb.append("<--- ");
        }
        sb.append(host).append(" :");
        this.hostText = new Text(sb.toString());
    }

    private void initLengthText(String msg) {
        this.lengthText = new Text("[" + CommonUtil.getRealLength(msg) + "字节]");
    }
    public void setDisplayOpt(){

    }

    public void showTime(boolean showTime) {
        timeText.setVisible(showTime);
        timeText.setManaged(showTime);
    }
    public void showLength(boolean showLength) {
        lengthText.setVisible(showLength);
        lengthText.setManaged(showLength);
    }
    public void showHost(boolean showHost) {
        hostText.setVisible(showHost);
        hostText.setManaged(showHost);
    }
    public void showMsg(boolean showMsg) {
        msgText.setVisible(showMsg);
        msgText.setManaged(showMsg);
    }

}
