package org.hqu.lly;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hqu.lly.constant.ResLocConsts;
import org.hqu.lly.view.controller.MainController;

import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.initStyle(StageStyle.TRANSPARENT);

        FXMLLoader fxmlLoader = new FXMLLoader();

        URL resource = getClass().getClassLoader().getResource(ResLocConsts.MAIN_PANE);
        fxmlLoader.setLocation(resource);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 620, 450);

        DarculaFX.applyDarculaStyle(scene);

        MainController controller = fxmlLoader.getController();

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}