package org.hqu.lly.domain.component;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import lombok.val;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.utils.DragUtil;
import org.hqu.lly.utils.UIUtil;
import org.hqu.lly.view.handler.DragWindowHandler;

import java.util.Optional;

/**
 * <p>
 * 自定义弹框
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/5/30 21:12
 */
public class MyAlert {

    private TitleBar titleBar;
    private ContentPane contentPane;
    private Stage stage = new Stage();
    private BorderPane pane = new BorderPane();

    private final ObjectProperty<ButtonType> resultProperty = new SimpleObjectProperty<>() {
        protected void invalidated() {
            close();
        }
    };

    public final ObjectProperty<ButtonType> resultProperty() {
        return resultProperty;
    }

    public final ButtonType getResult() {
        return resultProperty().get();
    }

    public final void setResult(ButtonType value) {
        this.resultProperty().set(value);
    }

    void close() {
        if (getResult() == null) {
            setResultAndClose(ButtonType.CLOSE);
        }
        stage.close();
    }

    /**
     *
     * @param alertType todo custom alert type
     * @param title 弹窗标题
     * @param contentText 弹窗内容文本
     */
    public MyAlert(Alert.AlertType alertType, String title, String contentText) {
        init(title, contentText);
    }

    /**
     *
     * @param alertType todo custom alert type
     * @param title 弹窗标题
     * @param contentText 弹窗内容文本
     * @param owner 所属stage
     */
    public MyAlert(Alert.AlertType alertType, String title, String contentText,Stage owner) {
        init(title, contentText);
        stage.initOwner(owner);
    }

    private void init(String title, String contentText) {
        titleBar = new TitleBar(this, title);
        pane.setTop(titleBar);
        contentPane = new ContentPane(this, contentText);
        pane.setCenter(contentPane);
        pane.getStylesheets().add(ResLoc.MY_ALERT_CSS.toExternalForm());

        val scene = UIUtil.getShadowScene(pane, 300, 200);
        DragUtil.setDrag(stage, scene.getRoot());
        DarculaFX.applyDarculaStyle(scene);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    public Optional<ButtonType> showAndWait() {
        stage.showAndWait();
        return Optional.ofNullable(getResult());
    }

    public void initOwner(Window owner) {
        stage.initOwner(owner);
    }

    private void setResultAndClose(ButtonType cmd) {
        setResult(cmd);
        stage.close();

    }


    class TitleBar extends BorderPane {

        Label titleLabel;

        StackPane close;

        MyAlert alert;

        public TitleBar(MyAlert myAlert, String title) {
            this.alert = myAlert;
            this.getStyleClass().add("alert-title-bar");
            this.getStylesheets().add(ResLoc.ALERT_TITLE_BAR_CSS.toExternalForm());

            // todo perf ugly
            DragWindowHandler dragWindowHandler = new DragWindowHandler(myAlert.stage);
            setOnMousePressed(dragWindowHandler);
            setOnMouseDragged(dragWindowHandler);

            setupClose();
            setupTitle(title);

        }

        private void setupTitle(String title) {
            titleLabel = new Label(title);
            titleLabel.getStyleClass().add("title");
            setLeft(titleLabel);
        }

        void setupClose() {
            close = new StackPane(new Region());
            close.getStyleClass().add("close");
            close.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> alert.close());
            setRight(close);

        }

    }

    class ContentPane extends VBox {

        MyAlert alert;

        HBox btnGroup = new HBox();

        HBox content = new HBox();
        private Label contentText = new Label();

        public ContentPane(MyAlert alert) {
            init(alert);
        }

        public ContentPane(MyAlert alert, String contentText) {
            init(alert);
            this.contentText.setText(contentText);
        }

        void init(MyAlert alert) {
            this.alert = alert;
            this.contentText.setWrapText(true);
            this.getStyleClass().add("alert-content-pane");
            setupContent();
            setupBtnGroup();
            this.getChildren().addAll(content, btnGroup);
        }

        void setupBtnGroup() {
            btnGroup.getStyleClass().add("alert-btn-group");
            val okBtn = createButton(ButtonType.OK);
            val cancelBtn = createButton(ButtonType.CANCEL);
            btnGroup.getChildren().addAll(okBtn, cancelBtn);
        }

        /**
         *
         * @param buttonType 按钮类型
         * @return button
         *  @date 2023-06-04 21:38
         * @see DialogPane#createButton(ButtonType)
         */
        Node createButton(ButtonType buttonType) {
            final Button button = new Button(buttonType.getText());
            final ButtonBar.ButtonData buttonData = buttonType.getButtonData();
            ButtonBar.setButtonData(button, buttonData);
            button.setDefaultButton(buttonData.isDefaultButton());
            button.setCancelButton(buttonData.isCancelButton());
            button.addEventHandler(ActionEvent.ACTION, ae -> {
                if (ae.isConsumed()) {
                    return;
                }
                if (alert != null) {
                    alert.setResultAndClose(buttonType);
                }
            });

            return button;
        }

        void setupContent() {
            content.getStyleClass().add("alert-content");
            VBox.setVgrow(content, Priority.ALWAYS);
            content.getChildren().add(contentText);
        }

    }

}
