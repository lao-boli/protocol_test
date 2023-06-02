package org.hqu.lly.domain.component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import lombok.SneakyThrows;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.view.controller.TitleBarController;

/**
 * <p>
 * 自定义弹框
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/5/30 21:12
 */
public class MyAlert extends Alert {


    @SneakyThrows
    public MyAlert(AlertType alertType) {
        super(alertType);
        // this.initStyle(StageStyle.TRANSPARENT);
        this.getDialogPane().getStylesheets().add(ResLoc.MY_ALERT_CSS.toExternalForm());

        FXMLLoader loader = new FXMLLoader(ResLoc.TITLE_BAR);
        Parent contentPane = loader.load();

        TitleBarController controller = loader.getController();
        this.getDialogPane().setHeader(contentPane);
        this.close();

    }

}
