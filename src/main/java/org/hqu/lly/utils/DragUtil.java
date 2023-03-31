package org.hqu.lly.utils;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import static javafx.scene.Cursor.*;
/**
 * 拉伸工具类
 * @author hqully
 * @version 1.0
 * @date 2023-03-18 21:27
 */
@Slf4j
public class DragUtil {

    /**
     * 判定是否为调整窗口状态的范围与边界距离
     */
    private final static int RESIZE_WIDTH = 5;
    /**
     *  窗口最小宽度
     */
    private final static double MIN_WIDTH = 660;
    /**
     *  窗口最小高度
     */
    private final static double MIN_HEIGHT = 480;

    private static double dragOffsetX;
    private static double dragOffsetY;
    private static Cursor cursorType;
    /**
     * stage右侧边X坐标
     */
    private static double widthPos;
    /**
     * stage底边Y坐标
     */
    private static double heightPos;

    public static void setDrag(Stage stage , Node root){
        if (root instanceof BorderPane){
            setDrag(stage, ((BorderPane) root));
        }
    }
    public static void setDrag(Stage stage , BorderPane root){
        root.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            // 窗口最大化时不触发缩放事件
            if (((Stage) root.getScene().getWindow()).isMaximized()){
                return;
            }

            cursorType = Cursor.DEFAULT;
            double x = event.getSceneX();
            double y = event.getSceneY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            // 鼠标指针在移入stage左边
            if (x < RESIZE_WIDTH){
                // 左下
                if (internal(y,height) < RESIZE_WIDTH ){
                    cursorType = Cursor.SW_RESIZE;
                // 左上
                }else if (y < RESIZE_WIDTH){
                    cursorType = NW_RESIZE;
                // 左边
                } else {
                    cursorType = W_RESIZE;
                }
            // 鼠标指针在移入stage右边
            }else if (internal(x,width) < RESIZE_WIDTH){
                // 右下
                if (internal(y,height) < RESIZE_WIDTH ){
                    cursorType = SE_RESIZE;
                // 右
                }else {
                    cursorType = E_RESIZE;
                }
            // 下
            }else if(internal(y,height) < RESIZE_WIDTH){
                cursorType = S_RESIZE;
            // 上
            }else if(y < RESIZE_WIDTH){
                cursorType = N_RESIZE;
            }
            root.setCursor(cursorType);

        });

        root.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            dragOffsetX = e.getScreenX() - stage.getX();
            dragOffsetY = e.getScreenY() - stage.getY();
            widthPos = stage.getX() + stage.getWidth();
            heightPos = stage.getY() + stage.getHeight();
            // System.out.printf("scrx: %f, scry: %f,sx: %f, sy: %f, width: %f, height: %f%n", e.getScreenX(), e.getScreenY(), stage.getX(), stage.getY(),stage.getWidth(),stage.getHeight());
            // System.out.printf("offx: %f, offy: %f%n", dragOffsetX,dragOffsetY);
        });

        root.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            double x = event.getSceneX();
            double y = event.getSceneY();
            // System.out.printf("x: %f, y: %f,sx: %f, sy: %f, width: %f, height: %f%n", x, y, stageX, stage.getY(), width, height);
            // System.out.printf("scrx: %f%n", event.getScreenX() - dragOffsetX);
            if (cursorType.equals(N_RESIZE)){
                stage.setHeight(heightPos - event.getScreenY());
                stage.setY(event.getScreenY() - dragOffsetY);
            }
            if (cursorType.equals(S_RESIZE)){
                stage.setHeight(y);
            }
            if (cursorType.equals(W_RESIZE)){
                // 从左边resize时，采用记录右侧边位置减去光标x坐标的方式计算宽度
                stage.setWidth(widthPos - event.getScreenX());
                stage.setX(event.getScreenX() - dragOffsetX);
                // System.out.printf("wp: %f, scrx: %f, dis: %f%n",widthPos,event.getScreenX(), event.getScreenX() - dragOffsetX);
                // System.out.printf("scrx: %f, off: %f, dis: %f%n",event.getScreenX(),dragOffsetX, event.getScreenX() - dragOffsetX);
            }
            if (cursorType.equals(E_RESIZE)){
                stage.setWidth(x);
            }
            if (cursorType.equals(SE_RESIZE)){
                stage.setWidth(x);
                stage.setHeight(y);
            }
            if (cursorType.equals(SW_RESIZE)){
                stage.setWidth(widthPos - event.getScreenX());
                stage.setHeight(y);
                stage.setX(event.getScreenX() - dragOffsetX);
            }
            if (cursorType.equals(NW_RESIZE)){
                stage.setWidth(widthPos - event.getScreenX());
                stage.setX(event.getScreenX() - dragOffsetX);
                stage.setHeight(heightPos - event.getScreenY());
                stage.setY(event.getScreenY() - dragOffsetY);
            }
            // stage.setX(stage.getX() + internal(x,width));
            // stage.setHeight(y);
        });
    }

    private static void smoothResize(MouseEvent event, Stage stage) {
        // TODO 等待优化平滑算法
/*
        double width = stage.getWidth();
        double maxWidth = widthPos - event.getScreenX();
        double offset = (maxWidth - width)/100;
        double stageX = stage.getX();
        double posOffset = (event.getScreenX()- stageX)/100;
        System.out.printf("offset: %f%n",offset);
        for (int i = 0; i < 100; i++) {
            System.out.println(offset + " " + i);
            stage.setWidth(width + offset * (i+1));
            stage.setX(stageX + posOffset * (i+1) - (dragOffsetX/100) *(i +1));
            System.out.printf("x: %f%n",stage.getX());
        }
        System.out.printf("exp x: %f,real x: %f, exp: %f,real: %f%n",event.getScreenX() - dragOffsetX,stage.getX(),maxWidth,stage.getWidth());
        System.out.printf("exp wp: %f, real: %f%n",widthPos,stage.getX() + stage.getWidth());
*/

        // stage.setWidth(maxWidth);
        // stage.setX(stageX + (event.getScreenX() - stageX)/10 - dragOffsetX);
    }

    private static double internal(double a, double b){
        return Math.abs(a - b);
    }

}
