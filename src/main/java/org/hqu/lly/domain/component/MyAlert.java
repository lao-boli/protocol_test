package org.hqu.lly.domain.component;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import lombok.SneakyThrows;
import lombok.val;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.utils.DragUtil;
import org.hqu.lly.utils.UIUtil;

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

    private Stage stage = new Stage();
    private BorderPane pane = new BorderPane();

    private final ObjectProperty<ButtonType> resultProperty = new SimpleObjectProperty<ButtonType>() {
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
        setResultAndClose(ButtonType.CLOSE, false);
    }


    @SneakyThrows
    public MyAlert(Alert.AlertType alertType) {
        pane.setTop(new TitleBar(stage, "title"));
        pane.setCenter(new ContentPane(this));
        pane.getStylesheets().add(ResLoc.MY_ALERT_CSS.toExternalForm());

        val scene = UIUtil.getShadowScene(pane, 200, 200);
        // Scene scene = new Scene(pane, 200, 200);
        DragUtil.setDrag(stage,scene.getRoot());
        DarculaFX.applyDarculaStyle(scene);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);


    }

    public Optional<ButtonType> showAndWait() {
        stage.showAndWait();
        return Optional.empty();
    }

    public void initOwner(Window owner) {
        stage.initOwner(owner);
    }

    private void setResultAndClose(ButtonType cmd, boolean close) {
        setResult(cmd);
        stage.close();

    }


    class TitleBar extends BorderPane {

        Label titleLabel;


        StackPane close;

        Stage stage;

        public TitleBar(Stage stage, String title) {
            this.stage = stage;
            this.getStyleClass().add("alert-title-bar");
            this.getStylesheets().add(ResLoc.ALERT_TITLE_BAR_CSS.toExternalForm());

            setupClose();
            this.setRight(close);
            setupTitle(title);
            this.setLeft(titleLabel);

        }

        private void setupTitle(String title) {
            titleLabel = new Label(title);
            titleLabel.getStyleClass().add("title");
        }

        void setupClose() {
            close = new StackPane(new Region());
            close.getStyleClass().add("close");
            close.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                stage.close();
            });

        }

    }

    class ContentPane extends VBox {

        MyAlert alert;

        HBox btnGroup = new HBox();
        HBox content = new HBox();

        public ContentPane(MyAlert alert) {
            this.alert = alert;
            this.getStyleClass().add("my-alert-content");
            setupContent();
            setupBtnGroup();
            this.getChildren().addAll(content,btnGroup);

        }

        void setupBtnGroup() {
            btnGroup.setSpacing(5.0);
            btnGroup.setAlignment(Pos.BOTTOM_RIGHT);
            // btnGroup.setPadding(new Insets(0, 5.0, 0, 0));
            val okBtn = createButton(ButtonType.OK);
            val cancelBtn = createButton(ButtonType.CANCEL);
            btnGroup.getChildren().addAll(okBtn,cancelBtn);
        }

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
                    alert.setResultAndClose(buttonType, true);
                }
            });

            return button;
        }

        void setupContent() {
            VBox.setVgrow(content,Priority.ALWAYS);
            content.setAlignment(Pos.CENTER);
            content.getChildren().add(new Label("content"));

        }

    }

}
