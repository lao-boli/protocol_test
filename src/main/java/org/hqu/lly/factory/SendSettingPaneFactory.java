package org.hqu.lly.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.config.ConfigStore;
import org.hqu.lly.domain.config.SendSettingConfig;
import org.hqu.lly.utils.DragUtil;
import org.hqu.lly.utils.ThemeUtil;
import org.hqu.lly.utils.UIUtil;
import org.hqu.lly.view.controller.SendSettingController;

import java.io.IOException;

/**
 * <p>
 * 定时发送功能弹出窗口
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/25 19:33
 */
public class SendSettingPaneFactory {

    /**
     * <p>
     *     创建发送设置面板.
     * </p>
     * @param sendConfig 发送设置
     * @return {@link Stage} 发送设置面板
     * @date 2023-02-05 19:03:05 <br>
     */
    public static Stage create(SendSettingConfig sendConfig) {
        try {
            Stage sendSettingStage = new Stage();
            FXMLLoader loader = new FXMLLoader(ResLoc.SEND_SETTING_PANE);
            Parent contentPane = loader.load();

            SendSettingController controller = loader.getController();

            // 载入要使用的配置类
            controller.setConfig(sendConfig);

            // 若存在本地配置文件,则加载.
            if (ConfigStore.isLoad){
                controller.loadConfig();
            }

            // Scene scene = new Scene(contentPane, 400, 300);
            Scene scene = UIUtil.getShadowScene(contentPane, 400, 350);
            ThemeUtil.applyStyle(scene);

            sendSettingStage.setScene(scene);
            sendSettingStage.initStyle(StageStyle.TRANSPARENT);
            DragUtil.setDrag(sendSettingStage, scene.getRoot());
            sendSettingStage.initModality(Modality.APPLICATION_MODAL);
            sendSettingStage.initOwner(UIUtil.getPrimaryStage());
            return sendSettingStage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
