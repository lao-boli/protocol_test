package com.hqu.lly.view.group;

import com.hqu.lly.factory.TabFactory;
import io.netty.channel.Channel;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

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
