package org.hqu.lly.view.group;

import javafx.scene.Parent;
import javafx.scene.control.TabPane;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * tabPane group
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/8 9:30
 * @Version 1.0
 */
public class ContentPaneGroup {

    public static Map<String, TabPane> tabPaneMap = new ConcurrentHashMap<>();
    public static Map<String, Parent> contentPaneMap = new ConcurrentHashMap<>();
}
