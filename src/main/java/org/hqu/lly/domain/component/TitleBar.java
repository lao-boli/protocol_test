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

import java.util.function.Supplier;

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

    private Supplier<Boolean> beforeClose;

    public void setBeforeClose(Supplier<Boolean> beforeClose) {
        this.beforeClose = beforeClose;
    }

    public TitleBar(Node pane, String title) {
        this(pane, title, null, true, true, true, true);
    }

    public TitleBar(Node pane, String title, Node graphic) {
        this(pane, title, graphic, true, true, true, true);
    }

    public TitleBar(Node pane, String title, Node graphic, boolean pin, boolean mini, boolean windowState, boolean close) {
        this.pane = pane;
        this.getStyleClass().add("title-bar");
        this.getStylesheets().addAll(ResLoc.NEW_TITLE_BAR_CSS.toExternalForm(), ResLoc.ICON.toExternalForm());

        Platform.runLater(() -> {
            stage = (Stage) pane.getScene().getWindow();
            DragWindowHandler dragWindowHandler = new DragWindowHandler(stage);
            setOnMousePressed(dragWindowHandler);
            setOnMouseDragged(dragWindowHandler);

            setupTitle(title, graphic);
            setupBtnGroup(pin, mini, windowState, close);
        });


    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    private void setupTitle(String title, Node graphic) {
        if (graphic == null) {
            titleLabel = new TitleLabel(title);
        } else {
            titleLabel = new TitleLabel(title, graphic);
        }
        setLeft(titleLabel);
    }

    void setupBtnGroup(boolean pin, boolean mini, boolean windowState, boolean close) {
        HBox btnGroup = new HBox();
        if (pin) {
            btnGroup.getChildren().add(new PinPane());
        }
        if (mini) {
            btnGroup.getChildren().add(new MiniPane());
        }
        if (windowState) {
            btnGroup.getChildren().add(new WindowStatePane());
        }
        if (close) {
            btnGroup.getChildren().add(new ClosePane());
        }
        setRight(btnGroup);
    }

    class TitleLabel extends Label {

        public TitleLabel(String title) {
            super(title);
            getStyleClass().add("title-label");
        }

        public TitleLabel(String title, Node graphic) {
            super(title, graphic);
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
            addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (beforeClose == null || beforeClose.get()) {
                    stage.close();
                }
            });
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
            if (back != null) {
                back.setPadding(isMax.get() ? new Insets(0): new Insets(5));
            }
            stage.setMaximized(isMax.get());
            if (isMax.get()) {
                correctSize(stage);
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

