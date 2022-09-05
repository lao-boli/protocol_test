package com.hqu.lly.view.handler;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/9/4 19:03
 * @Version 1.0
 */
public class DragWindowHandler implements EventHandler<MouseEvent> {
    private Stage primaryStage;
    private double oldStageX;
    private double oldStageY;
    private double oldScreenX;
    private double oldScreenY;

    public DragWindowHandler(Stage primaryStage) { //构造器
        this.primaryStage = primaryStage;
    }

    @Override
    public void handle(MouseEvent e) {
        if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
            this.oldStageX = this.primaryStage.getX();
            this.oldStageY = this.primaryStage.getY();
            this.oldScreenX = e.getScreenX();
            this.oldScreenY = e.getScreenY();

        } else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            this.primaryStage.setX(e.getScreenX() - this.oldScreenX + this.oldStageX);
            this.primaryStage.setY(e.getScreenY() - this.oldScreenY + this.oldStageY);
        }
    }
}
