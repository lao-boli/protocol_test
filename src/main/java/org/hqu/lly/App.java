package org.hqu.lly;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.hqu.lly.domain.component.MyAlert;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // UIUtil.setPrimaryStage(primaryStage);
        // FXMLLoader fxmlLoader = new FXMLLoader();
        // fxmlLoader.setLocation(ResLoc.MAIN_PANE);
        // Parent root = fxmlLoader.load();
        // MainController controller = fxmlLoader.getController();
        //
        // Scene scene = UIUtil.getShadowScene(root, 660, 400);
        //
        // DarculaFX.applyDarculaStyle(scene);
        // primaryStage.initStyle(StageStyle.TRANSPARENT);
        // primaryStage.setScene(scene);
        // DragUtil.setDrag(primaryStage,scene.getRoot());
        // primaryStage.show();
        new MyAlert(Alert.AlertType.CONFIRMATION).showAndWait();

    }

}