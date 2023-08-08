package org.hqu.lly.domain.component;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;
import lombok.Getter;
import lombok.val;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.icon.CloseIcon;
import org.hqu.lly.utils.CommonUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    private Timeline inAnim;
    private Timeline outAnim;

    /**
     * 24px is the default single item height
     */
    public static final int ITEM_HEIGHT = 24;
    public static final int MAX_ROW = 5;
    /**
     * data listView max height
     */
    public static final int MAX_HEIGHT = MAX_ROW * ITEM_HEIGHT;

    /**
     * as a reference height when play in and out anim.
     * only can be set when {@link #dataListView} items change.
     */
    private double curListViewPerfHeight = 0;

    @Getter
    private ListView<Label> dataListView;


    private Consumer<String> onItemClicked;

    private Pane contentWrapper;

    public BooleanProperty onWorking = new SimpleBooleanProperty(false);


    public ListItemPopup() {
        super();
        dataListView = new ListView<>();

        // setup css
        dataListView.getStylesheets().add(ResLoc.LIST_ITEM_POPUP_CSS.toString());
        setupListView();
        setupInAnim();
        setupOutAnim();


        // wrapper 让消息框显示前就能获取到宽度,
        // 以计算消息框的显示位置
        contentWrapper = new Pane(dataListView);
        this.getContent().add(contentWrapper);
        contentWrapper.applyCss();
        contentWrapper.layout();

    }

    public void addDataList(List<String> items) {
        if (items == null || items.size() == 0) {
            return;
        }
        dataListView.getItems().addAll(items.stream().map(this::createDataLabel).collect(Collectors.toList()));
    }


    public void addData(String data) {
        Label label = createDataLabel(data);
        dataListView.getItems().add(label);
    }

    private Label createDataLabel(String data) {
        CloseIcon closeIcon = new CloseIcon();
        closeIcon.setIconHeight(10);
        closeIcon.setIconWidth(10);
        Label label = new Label(data, closeIcon);
        closeIcon.setOnMousePressed(e -> {
            onWorking.setValue(true);
            Optional<ButtonType> result = new MyAlert(Alert.AlertType.CONFIRMATION, "提示", "是否删除本条目?", this).showAndWait();
            if (result.isPresent() && result.get().equals(ButtonType.OK)) {
                dataListView.getItems().remove(label);
            }
            onWorking.setValue(false);
        });
        return label;
    }

    public List<String> getDataList() {
        return dataListView.getItems().stream().map(Labeled::getText).collect(Collectors.toList());
    }

    public void setOnItemClicked(Consumer<String> onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

    private void setupListView() {
        dataListView.setPrefHeight(ITEM_HEIGHT);

        dataListView.getItems().addListener((ListChangeListener<Label>) c -> {
            if (c.getList().size() <= MAX_ROW) {
                curListViewPerfHeight = ITEM_HEIGHT * c.getList().size();
                dataListView.setPrefHeight(curListViewPerfHeight);

            } else if (dataListView.getPrefHeight() < MAX_HEIGHT) {
                // handle height computation when call addDataList(List<String> items) method
                curListViewPerfHeight = MAX_HEIGHT;
                dataListView.setPrefHeight(MAX_HEIGHT);
            }
        });

        // cell factory
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

                    setText(item.getText());

                    // keep the close icon always on top
                    VBox graphic = new VBox(item.getGraphic());
                    heightProperty().addListener((observable, oldValue, newValue) -> {
                        if (wrapTextProperty().get()) {
                            graphic.setPrefHeight(newValue.doubleValue() - 12);
                        } else {
                            // use compute size
                            graphic.setPrefHeight(-1);
                        }
                    });
                    setGraphic(graphic);

                    // event callbacks
                    val _this = this;
                    setOnMouseClicked(event -> {
                        onItemClicked.accept(item.getText());
                        close();
                    });
                    setOnMouseEntered(event -> {
                        if (CommonUtil.isLabeledTextOverflow(_this)) {
                            setWrapText(true);
                        }
                    });
                    setOnMouseExited(event -> {
                        if (wrapTextProperty().get()) {
                            setWrapText(false);
                        }
                    });

                }
            }
        });
    }


    public void showPopup(double yOffset, Node node) {
        Platform.runLater(() -> {
            double anchorX = node.localToScreen(node.getBoundsInLocal()).getMinX();
            double anchorY = yOffset + node.localToScreen(node.getBoundsInLocal()).getMaxY();

            dataListView.setPrefHeight(0);
            this.show(node, anchorX, anchorY);
            outAnim.stop();
            inAnim.play();
        });
    }

    public void close() {
        inAnim.stop();
        outAnim.play();
    }

    private void setupInAnim() {
        // XXX optimize anim implementation
        Duration animDuration = Duration.seconds(0.1);
        inAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(dataListView.translateYProperty(), -curListViewPerfHeight)),
                new KeyFrame(animDuration, new KeyValue(dataListView.translateYProperty(), 0))
        );

        inAnim.setAutoReverse(false);
        inAnim.setCycleCount(1);
        // 动态地改变 Popup 内容的高度
        inAnim.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double progress = newValue.toMillis() / animDuration.toMillis();
            dataListView.setPrefHeight(progress * curListViewPerfHeight);
        });
    }


    private Timeline setupOutAnim() {
        // XXX optimize anim implementation
        Duration animDuration = Duration.seconds(0.1);
        outAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(dataListView.translateYProperty(), 0)),
                new KeyFrame(animDuration, new KeyValue(dataListView.translateYProperty(), -curListViewPerfHeight))
        );

        outAnim.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double progress = newValue.toMillis() / animDuration.toMillis();
            dataListView.setPrefHeight(curListViewPerfHeight - progress * curListViewPerfHeight);
        });
        outAnim.setAutoReverse(false);
        outAnim.setCycleCount(1);
        outAnim.setOnFinished(e -> hide());
        return outAnim;
    }


}
