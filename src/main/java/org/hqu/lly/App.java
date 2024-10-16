package org.hqu.lly;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.component.ProgressBarDialog;
import org.hqu.lly.utils.DragUtil;
import org.hqu.lly.utils.JSParser;
import org.hqu.lly.utils.ThemeUtil;
import org.hqu.lly.utils.UIUtil;
import org.hqu.lly.view.controller.MainController;

import java.io.IOException;

@Slf4j
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        new Thread(JSParser::preheat).start();

        ProgressBarDialog progressBarDialog = new ProgressBarDialog();
        progressBarDialog.stage.setOnShown(event -> {
            log.warn("on shown");
            MainController controller = null;
            try {
                controller = loadMain(primaryStage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            controller.setProgressBarDialog(progressBarDialog);
            controller.loadConfig();
            MainController finalController = controller;
            primaryStage.setOnShown(e -> finalController.setupSpiltPane());
        });
        progressBarDialog.stage.setOnHidden(event -> primaryStage.show());
        progressBarDialog.show();
        progressBarDialog.stage.getIcons().addAll(
                new Image(ResLoc.APP_ICON_16.toExternalForm()),
                new Image(ResLoc.APP_ICON_32.toExternalForm()),
                new Image(ResLoc.APP_ICON_64.toExternalForm()),
                new Image(ResLoc.APP_ICON_128.toExternalForm()),
                new Image(ResLoc.APP_ICON_256.toExternalForm())
        );

    }

    private static MainController loadMain(Stage primaryStage) throws IOException {
        UIUtil.setPrimaryStage(primaryStage);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResLoc.MAIN_PANE);
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        Scene scene = UIUtil.getShadowScene(root, 660, 400);

        // set app logo
        primaryStage.getIcons().addAll(
                new Image(ResLoc.APP_ICON_16.toExternalForm()),
                new Image(ResLoc.APP_ICON_32.toExternalForm()),
                new Image(ResLoc.APP_ICON_64.toExternalForm()),
                new Image(ResLoc.APP_ICON_128.toExternalForm()),
                new Image(ResLoc.APP_ICON_256.toExternalForm())
        );
        primaryStage.setTitle("protocol test");

        ThemeUtil.applyStyle(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        DragUtil.setDrag(primaryStage, scene.getRoot());
        return controller;
    }

}
