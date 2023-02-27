package org.hqu.lly.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hqu.lly.component.controller.CustomAlertController;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.component.CustomAlert;
import org.hqu.lly.utils.UIUtil;

import java.io.IOException;

/**
 * <p>
 * 自定义弹窗的工厂类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/2/6 19:26
 */
public class CustomAlertFactory{

    /**
     * <p>
     * 创建一个弹框
     * </p>
     *
     * @param title 弹框标题
     * @param info 弹框信息
     * @return {@link Stage} 弹框
     * @date 2023-02-06 20:18:43 <br>
     * @author hqully <br>
     */
    public static CustomAlert create(String title, String info) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(ResLoc.CUSTOM_ALERT);
            Parent contentPane = loader.load();

            CustomAlertController controller = loader.getController();

            Scene shadowScene = UIUtil.getShadowScene(contentPane, 310, 190);

            stage.setScene(shadowScene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);

            controller.init(title, info);
            return new CustomAlert(stage,controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
