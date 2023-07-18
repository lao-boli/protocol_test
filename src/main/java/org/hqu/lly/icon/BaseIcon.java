package org.hqu.lly.icon;

import javafx.scene.layout.Region;
import org.hqu.lly.constant.ResLoc;

/**
 * <p>
 * icon base
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/6/23 10:31
 */
public class BaseIcon extends Region {

    public BaseIcon() {
        this.getStylesheets().add(ResLoc.ICON.toExternalForm());
        defaultIconWidth();
        defaultIconHeight();
    }

    public BaseIcon(String color) {
        this.setStyle("-fx-background-color: " + color);
        this.getStylesheets().add(ResLoc.ICON.toExternalForm());
        defaultIconWidth();
        defaultIconHeight();
    }
    public void setColor(String color) {
        this.setStyle("-fx-background-color: " + color);
    }

    protected void setStyleClass(String styleClass) {
        this.getStyleClass().add(styleClass);
    }

    private void defaultIconHeight() {
        setIconHeight(12);
    }

    private void defaultIconWidth() {
        setIconWidth(12);
    }

    public void setIconHeight(double height) {
        this.setPrefHeight(height);
        this.setMinHeight(height);
        this.setMaxHeight(height);
    }

    public void setIconWidth(double width) {
        this.setPrefWidth(width);
        this.setMinWidth(width);
        this.setMaxWidth(width);
    }

}
