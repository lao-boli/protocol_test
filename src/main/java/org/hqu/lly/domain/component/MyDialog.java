package org.hqu.lly.domain.component;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import lombok.val;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.utils.DragUtil;
import org.hqu.lly.utils.UIUtil;
import org.hqu.lly.view.handler.DragWindowHandler;

/**
 * <p>
 * my dialog
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023-07-15 09:32
 */
public class MyDialog<T extends Node> {

    protected TitleBar titleBar;
    protected Stage stage = new Stage();

    protected T content;
    protected BorderPane pane = new BorderPane();

    public void close() {
        stage.close();
    }

    public void show() {
        stage.show();
    }

    public MyDialog() {

        // title bar
        initTitleBar();

        // scene and stage
        final Scene scene = initScene();
        DragUtil.setDrag(stage, scene.getRoot());
        DarculaFX.applyDarculaStyle(scene);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    /**
     * init owner stage
     */
    public MyDialog(Stage parent) {
        this();
        initOwner(parent);
    }

    protected Scene initScene() {
        return initScene(450, 300);
    }

    protected Scene initScene(double width, double height) {
        val scene = UIUtil.getShadowScene(pane, width, height);
        return scene;
    }

    protected void initTitleBar() {
        initTitleBar("title");
    }

    protected TitleBar initTitleBar(String title) {
        titleBar = new TitleBar(this, title);
        pane.setTop(titleBar);
        return titleBar;
    }


    public void initOwner(Window owner) {
        stage.initOwner(owner);
    }

    protected class TitleBar extends BorderPane {

        Label titleLabel;

        StackPane close;

        MyDialog dialog;

        public TitleBar(MyDialog dialog, String title) {
            this.dialog = dialog;
            this.getStyleClass().add("alert-title-bar");
            this.getStylesheets().add(ResLoc.ALERT_TITLE_BAR_CSS.toExternalForm());

            // todo perf ugly
            DragWindowHandler dragWindowHandler = new DragWindowHandler(dialog.stage);
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
            close.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> dialog.close());
            setRight(close);

        }

    }


}
