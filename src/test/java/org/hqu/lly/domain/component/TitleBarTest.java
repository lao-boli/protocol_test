package org.hqu.lly.domain.component;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hqu.lly.utils.DragUtil;
import org.hqu.lly.utils.ThemeUtil;

/**
 * <p>
 *
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/10/12 10:21
 */
public class TitleBarTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane content = new BorderPane();
        content.setStyle("-fx-background-color: #2b2b2b;-fx-pref-width: 520;-fx-pref-height: 240");
        content.setTop(new TitleBar(content,"test"));

        Scene scene = new Scene(content);


        ThemeUtil.applyStyle(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        DragUtil.setDrag(primaryStage, scene.getRoot());
        primaryStage.show();


    }

}
