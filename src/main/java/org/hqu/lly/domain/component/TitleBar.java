package org.hqu.lly.domain.component;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.view.handler.DragWindowHandler;

/**
 * <p>
 * titleTar
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/10/11 15:45
 */
public class TitleBar extends BorderPane {

    private Stage stage;
    TitleLabel titleLabel;

    Node pane;

    public TitleBar(Node pane, String title) {
        this.pane = pane;
        this.getStyleClass().add("title-bar");
        this.getStylesheets().addAll(ResLoc.NEW_TITLE_BAR_CSS.toExternalForm(), ResLoc.ICON.toExternalForm());

        Platform.runLater(() -> {
            stage = (Stage) pane.getScene().getWindow();
            DragWindowHandler dragWindowHandler = new DragWindowHandler(stage);
            setOnMousePressed(dragWindowHandler);
            setOnMouseDragged(dragWindowHandler);

            setupTitle(title);
            setupBtnGroup();
        });


    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    private void setupTitle(String title) {
        titleLabel = new TitleLabel(title);
        setLeft(titleLabel);
    }

    void setupBtnGroup() {
        HBox btnGroup = new HBox(new PinPane(), new MiniPane(), new WindowStatePane(), new ClosePane());
        setRight(btnGroup);
    }

    class TitleLabel extends Label {

        public TitleLabel(String title) {
            super(title);
            getStyleClass().add("title-label");
        }

    }


    class PinPane extends StackPane {


        BooleanProperty isPined = new SimpleBooleanProperty(false);

        Region pinIcon = new Region();

        public PinPane() {
            super();
            pinIcon.getStyleClass().add("icon-pin");
            pseudoClassStateChanged(PseudoClass.getPseudoClass("activated"), false);
            getChildren().addAll(pinIcon);


            getStyleClass().add("pin-pane");
            addEventFilter(MouseEvent.MOUSE_CLICKED, event -> isPined.setValue(!isPined.get()));

            isPined.addListener((observable, oldValue, newValue) -> {
                pseudoClassStateChanged(PseudoClass.getPseudoClass("activated"), newValue);
                stage.setAlwaysOnTop(newValue);
            });
        }

    }

    class ClosePane extends StackPane {

        private final Region closeIcon;

        public ClosePane() {
            super();
            closeIcon = new Region();
            closeIcon.getStyleClass().add("icon-close");
            getChildren().add(closeIcon);
            getStyleClass().add("close-pane");
            addEventFilter(MouseEvent.MOUSE_CLICKED, event -> stage.close());
        }

    }

    class WindowStatePane extends StackPane {

        BooleanProperty isMax = new SimpleBooleanProperty(true);

        Region maxIcon = new Region();
        Region restoreIcon = new Region();

        public WindowStatePane() {
            super();
            maxIcon.visibleProperty().bind(isMax);
            maxIcon.getStyleClass().add("icon-max");

            restoreIcon.visibleProperty().bind(isMax.not());
            restoreIcon.getStyleClass().add("icon-restore");

            getChildren().addAll(maxIcon, restoreIcon);

            getStyleClass().add("window-state-pane");
            addEventFilter(MouseEvent.MOUSE_CLICKED, this::switchSize);
        }

        void switchSize(MouseEvent event) {
            BorderPane back = (BorderPane) stage.getScene().getRoot();
            if (isMax.get()) {
                if (back != null) {
                    back.setPadding(new Insets(0));
                }
                stage.setMaximized(isMax.get());
                correctSize(stage);
            } else {
                if (back != null) {
                    back.setPadding(new Insets(5));
                }
                stage.setMaximized(isMax.get());
            }
            isMax.setValue(!isMax.get());
        }


        /**
         * 修正stage的长度和宽度,保证在最大化时不遮挡任务栏
         *
         * @param stage stage
         */
        private void correctSize(Stage stage) {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setHeight(bounds.getHeight());
            stage.setWidth(bounds.getWidth());
        }

    }

    class MiniPane extends StackPane {

        Region miniIcon = new Region();

        public MiniPane() {
            super();
            miniIcon.getStyleClass().add("icon-min");
            getChildren().addAll(miniIcon);


            getStyleClass().add("min-pane");
            addEventFilter(MouseEvent.MOUSE_CLICKED, event -> stage.setIconified(true));

        }

    }


}

