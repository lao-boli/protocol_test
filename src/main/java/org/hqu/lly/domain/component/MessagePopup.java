package org.hqu.lly.domain.component;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.utils.UIUtil;

import static org.hqu.lly.utils.UIUtil.copyToClipboard;

/**
 * <p>
 * 消息提示悬浮框(now only warn)
 * <p>
 *
 * @author hqully
 * @since 0.2.0
 * @date 2023/4/4 19:40
 */
public class MessagePopup extends Popup {

    private ContextMenu menu;

    enum Type {
        /**
         * 信息提示
         */
       INFO,
        /**
         * 警告消息
         */
       WARN,
        /**
         * 错误消息
         */
       ERROR,
        /**
         * 成功消息
         */
       SUCCESS;
    }

    private HBox content;

    private Pane contentWrapper;

    private Label icon;

    private Label msg;

    private Type type;

    public MessagePopup() {
        super();
        init("");
    }

    public MessagePopup(String msgText) {
        super();
        init(msgText);
    }

    private void init(String msgText) {
        setupIcon();
        msg = new Label(msgText);
        content = new HBox(icon,msg);

        // setup css
        content.getStyleClass().add("msg-content");
        content.getStylesheets().add(ResLoc.MESSAGE_POPUP_CSS.toString());

        // wrapper 让消息框显示前就能获取到宽度,
        // 以计算消息框的显示位置
        contentWrapper = new Pane(content);
        this.getContent().add(contentWrapper);
        contentWrapper.applyCss();
        contentWrapper.layout();

        setupMenu();
    }

    /**
     * 设置消息复制菜单
     */
    private void setupMenu() {
        menu = new ContextMenu();
        // 复制整条消息
        MenuItem copyItem = new MenuItem("复制");
        copyItem.setOnAction( e -> copyToClipboard(msg.getText()));
        menu.getItems().add(copyItem);

        content.addEventFilter(MouseEvent.MOUSE_CLICKED,e -> {
            if (e.getButton().equals(MouseButton.SECONDARY)){
                menu.show(content,e.getScreenX(),e.getScreenY());
            }else {
                menu.hide();
            }
        });
    }


    public void setMsg(String msg) {
        this.msg.setText(msg);
    }

    public void showPopup() {
        Platform.runLater(() -> {
            Stage primaryStage = UIUtil.getPrimaryStage();
            double windowX = primaryStage.getX();
            double windowY = primaryStage.getY();
            double windowWidth = primaryStage.getWidth();

            // 计算提示框应该显示的位置
            double popupX = windowX + (windowWidth - content.getWidth()) / 2;
            double popupY = windowY + 80;

            setupAnimation(this, primaryStage, popupX, popupY);
        });


    }

    /**
     * 为消息提示框设置动画效果
     * @param popup 本对象
     * @param primaryStage 应用程序主窗口
     * @param popupX 显示x坐标
     * @param popupY 显示y坐标
     */
    private void setupAnimation(MessagePopup popup, Stage primaryStage, double popupX, double popupY) {
        // 从下向上移动显示
        TranslateTransition transIn = new TranslateTransition(Duration.seconds(0.2), content);
        // 初始位置向下偏移 30px
        transIn.setFromY(30);
        // 移动到初始位置
        transIn.setToY(0);
        transIn.play();

        // 淡入
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), content);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        this.show(primaryStage, popupX, popupY);

        // 悬浮框显示3s
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(3));

        // 鼠标悬浮在popup的时候暂停动画
        content.addEventFilter(MouseEvent.MOUSE_ENTERED,e -> pauseTransition.pause());
        content.addEventFilter(MouseEvent.MOUSE_EXITED,e -> pauseTransition.play());

        pauseTransition.setOnFinished(event -> {
            // 淡出
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), content);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> popup.hide());
            fadeOut.play();
        });
        pauseTransition.play();
    }

    private void setupIcon(){
        icon = new Label();
        icon.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        ImageView warnIcon = new ImageView(new Image(ResLoc.WARN_ICON.toString()));
        warnIcon.setFitHeight(15);
        warnIcon.setFitWidth(15);
        warnIcon.setPreserveRatio(true);
        warnIcon.setPickOnBounds(true);

        icon.setGraphic(warnIcon);
    }

}
