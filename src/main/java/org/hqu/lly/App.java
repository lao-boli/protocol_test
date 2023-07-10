package org.hqu.lly;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.utils.DragUtil;
import org.hqu.lly.utils.JSParser;
import org.hqu.lly.utils.UIUtil;
import org.hqu.lly.view.controller.MainController;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        new Thread(JSParser::preheat).start();

        UIUtil.setPrimaryStage(primaryStage);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResLoc.MAIN_PANE);
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();

        Scene scene = UIUtil.getShadowScene(root, 660, 400);

        DarculaFX.applyDarculaStyle(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        DragUtil.setDrag(primaryStage, scene.getRoot());
        primaryStage.show();

    }

}