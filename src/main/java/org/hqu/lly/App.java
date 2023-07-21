package org.hqu.lly;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.utils.DragUtil;
import org.hqu.lly.utils.JSParser;
import org.hqu.lly.utils.ThemeUtil;
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

        ThemeUtil.applyStyle(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        DragUtil.setDrag(primaryStage, scene.getRoot());
        primaryStage.show();
        // example(primaryStage);

    }

    public void example(Stage stage) {

        ListView<Node> lv = new ListView<>();
        for (int i = 0; i < 10; i++) {
            TextFlow tf = new TextFlow(new Text("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
            // lv.getItems().add("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            lv.getItems().add(tf);
        }
        lv.setMaxWidth(100);
        setCellFactory(lv);

        stage.setScene(new Scene(lv,600,400));
        stage.show();
    }

    public void setCellFactory(ListView lv) {
        lv.setCellFactory(param -> {
            return new ListCell<TextFlow>() {
                @Override
                protected void updateItem(TextFlow item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                        // other stuff to do...
                    } else {
                        setMinWidth(100);
                        setMaxWidth(100);
                        setPrefWidth(100);
                        System.out.println("update");
                        // allow wrapping
                        setWrapText(true);
                        setText(item.getChildren().get(0).toString());
                        setGraphic(item);

                    }
                }
            };
        });

    }

}