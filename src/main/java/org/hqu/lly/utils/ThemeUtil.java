package org.hqu.lly.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import org.hqu.lly.constant.ResLoc;

/**
 * <p>
 * 主题工具
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/7/18 15:53
 */
public class ThemeUtil {

    public static void applyStyle(Scene scene) {
        scene.getStylesheets().add(ResLoc.ICON.toExternalForm());
        scene.getStylesheets().add(ResLoc.GLOBE_CSS.toExternalForm());
        scene.getStylesheets().add(ResLoc.IDEA.toExternalForm());
    }

    public static void applyStyle(Parent parent) {
        parent.getStylesheets().add(ResLoc.IDEA.toExternalForm());
    }


}
