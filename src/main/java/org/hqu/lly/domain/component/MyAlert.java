package org.hqu.lly.domain.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import lombok.val;
import org.hqu.lly.constant.ResLoc;

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
public class MyAlert extends MyDialog<MyAlert.ContentPane>{

    private ContentPane contentPane;

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

    @Override
    public void close() {
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
        super();
        titleBar.setTitle(title);

        contentPane = new ContentPane(this, contentText);
        pane.setCenter(contentPane);

        pane.getStylesheets().add(ResLoc.MY_ALERT_CSS.toExternalForm());
    }

    /**
     *
     * @param alertType todo custom alert type
     * @param title 弹窗标题
     * @param contentText 弹窗内容文本
     * @param owner 所属stage
     */
    public MyAlert(Alert.AlertType alertType, String title, String contentText, Window owner) {
        this(alertType,title,contentText);
        initOwner(owner);
    }

    @Override
    protected Scene initScene() {
        return initScene(300,200);
    }

    public Optional<ButtonType> showAndWait() {
        stage.showAndWait();
        return Optional.ofNullable(getResult());
    }

    private void setResultAndClose(ButtonType cmd) {
        setResult(cmd);
        stage.close();
    }


    class ContentPane extends VBox {

        MyAlert alert;

        HBox btnGroup = new HBox();

        HBox content = new HBox();
        private Label contentText = new Label();

        public ContentPane(MyAlert alert) {
            this.alert = alert;
            this.contentText.setWrapText(true);
            this.getStyleClass().add("alert-content-pane");
            setupContent();
            setupBtnGroup();
            this.getChildren().addAll(content, btnGroup);
        }

        public ContentPane(MyAlert alert, String contentText) {
            this(alert);
            this.contentText.setText(contentText);
        }

        void setupContent() {
            content.getStyleClass().add("alert-content");
            VBox.setVgrow(content, Priority.ALWAYS);
            content.getChildren().add(contentText);
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


    }

}
