package org.hqu.lly.domain.component;

import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.Setter;
import org.hqu.lly.component.controller.CustomAlertController;
import org.hqu.lly.service.TaskService;

import java.util.Optional;

/**
 * <p>
 * 自定义弹框
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/2/7 16:10
 */
@Setter
public class CustomAlert {

    /**
     * 弹框所在的舞台 {@link Stage}
     */
    private Stage stage;

    /**
     * 弹框对应的控制器
     */
    private CustomAlertController controller;

    /**
     * <p>
     *     创建自定义弹窗
     * </p>
     * @param stage 弹框所在的舞台 {@link Stage}
     * @param controller 弹框对应的控制器
     * @return {@link }
     * @date 2023-02-07 18:30:29 <br>
     */
    public CustomAlert(Stage stage, CustomAlertController controller) {
        this.stage = stage;
        this.controller = controller;
    }

    /**
     * <p>
     * 显示弹窗，并返回点击的按钮类型
     * </p>
     * @return 点击的按钮类型
     * @date 2023-02-07 18:27:54 <br>
     */
    public Optional<ButtonType> showAndWait() {
        stage.showAndWait();
        return Optional.ofNullable(controller.getButtonType());

    }

    /**
     * <p>
     * 设置确认按钮回调方法
     * </p>
     * @param task 要执行的操作方法
     * @date 2023-02-08 09:36:55 <br>
     */
    public void setOnConfirm(TaskService task){
        controller.setOnConfirm(task);
    }

    /**
     * <p>
     *     设置{@link CustomAlertController#forceResume}的值。
     * </p>
     * @param force 要设置的{@link CustomAlertController#forceResume} 的值
     * @date 2023-02-08 10:03:15 <br>
     */
    public void setForceResume(boolean force){
        controller.setForceResume(force);
    }


}
