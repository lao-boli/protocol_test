package org.hqu.lly.domain.component;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.val;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.icon.CloseIcon;
import org.hqu.lly.utils.UIUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * <p>
 * 列表条目popup
 * <p>
 *
 * @author hqully
 * @date 2023-08-05 20:09
 * @since 0.2.0
 */
public class ListItemPopup extends Popup {

    public enum Direction {
        /**
         * 由下至上
         */
        DOWN_TO_UP,
        /**
         * 由上至下
         */
        UP_TO_DOWN,
    }


    private ListView<Label> dataListView;

    private List<HBox> dataList;

    private Consumer<String> onItemClicked;

    private Pane contentWrapper;


    public ListItemPopup() {
        super();
        dataListView = new ListView<>();

        // setup css
        dataListView.getStylesheets().add(ResLoc.LIST_ITEM_POPUP_CSS.toString());
        setupListView();


        // wrapper 让消息框显示前就能获取到宽度,
        // 以计算消息框的显示位置
        contentWrapper = new Pane(dataListView);
        this.getContent().add(contentWrapper);
        contentWrapper.applyCss();
        contentWrapper.layout();

    }

    public void addData(String data) {
        CloseIcon closeIcon = new CloseIcon();
        closeIcon.setIconHeight(10);
        closeIcon.setIconWidth(10);
        Label label = new Label(data, closeIcon);
        closeIcon.setOnMousePressed(e -> {
            Optional<ButtonType> result = new MyAlert(Alert.AlertType.CONFIRMATION, "提示", "是否删除本条目?",this).showAndWait();
           if (result.isPresent() && result.get().equals(ButtonType.OK)){
               dataListView.getItems().remove(label);
           }
        });
        dataListView.getItems().add(label);
    }

    public void setOnItemClicked(Consumer<String> onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

    private void setupListView() {
        val popup = this;
        dataListView.getItems().addListener((ListChangeListener<Label>) c -> {
            // 24px is the default item height
            dataListView.setMaxHeight(24 * c.getList().size());
        });

        dataListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setMinWidth(100);
                    setMaxWidth(100);
                    setPrefWidth(100);

                    VBox graphic = new VBox(item.getGraphic());
                    heightProperty().addListener((observable, oldValue, newValue) -> {
                        if (wrapTextProperty().get()) {
                            graphic.setPrefHeight(newValue.doubleValue() - 12);
                        } else {
                            graphic.setPrefHeight(-1);
                        }
                    });
                    setGraphic(graphic);
                    setText(item.getText());
                    val _this = this;
                    setOnMouseClicked(event -> {onItemClicked.accept(item.getText());popup.close();});
                    setOnMouseExited((e) -> {
                        _this.setWrapText(false);
                    });
                }
            }
        });
    }


    public void showPopup() {
        showPopup(80);
    }

    public void showPopup(int yOffset) {
        showPopup(yOffset, Direction.DOWN_TO_UP, 3);
    }

    public void showPopup(int yOffset, double pauseDuration) {
        showPopup(yOffset, Direction.DOWN_TO_UP, pauseDuration);
    }

    public void showPopup(int yOffset, Direction direction, double pauseDuration) {
        Platform.runLater(() -> {
            Stage primaryStage = UIUtil.getPrimaryStage();
            double windowX = primaryStage.getX();
            double windowY = primaryStage.getY();
            double windowWidth = primaryStage.getWidth();

            // 计算提示框应该显示的位置
            double popupX = windowX + (windowWidth - dataListView.getWidth()) / 2;
            double popupY = windowY + yOffset;

            setupAnimation(this, primaryStage, popupX, popupY, direction, pauseDuration);
        });
    }

    private void setupAnimation(ListItemPopup popup, Stage primaryStage, double popupX, double popupY) {
        setupAnimation(popup, primaryStage, popupX, popupY, Direction.UP_TO_DOWN, 3);
    }

    /**
     * 为消息提示框设置动画效果
     *
     * @param popup         本对象
     * @param primaryStage  应用程序主窗口
     * @param popupX        显示x坐标
     * @param popupY        显示y坐标
     * @param direction     消息框出现的方向
     * @param pauseDuration 消息框显示时间
     */
    private void setupAnimation(ListItemPopup popup, Stage primaryStage, double popupX, double popupY, Direction direction, double pauseDuration) {
        // 从下向上移动显示
        TranslateTransition transIn = new TranslateTransition(Duration.seconds(0.2), dataListView);
        switch (direction) {
            case DOWN_TO_UP -> {
                // 初始位置向下偏移 30px
                transIn.setFromY(30);
                // 移动到初始位置
                transIn.setToY(0);
            }
            case UP_TO_DOWN -> {
                transIn.setFromY(0);
                // 移动到初始位置向下偏移30px
                transIn.setToY(30);
                // 修正出现位置
                popupY = popupY - 30;
            }
        }
        transIn.play();

        // 淡入
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), dataListView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        this.show(primaryStage, popupX, popupY);

        // 悬浮框显示3s
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(pauseDuration));

        // 鼠标悬浮在popup的时候暂停动画
        dataListView.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> pauseTransition.pause());
        dataListView.addEventFilter(MouseEvent.MOUSE_EXITED, e -> pauseTransition.play());

    }
    public void close() {
        // 淡出
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), dataListView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> this.hide());
        fadeOut.play();
    }


}
