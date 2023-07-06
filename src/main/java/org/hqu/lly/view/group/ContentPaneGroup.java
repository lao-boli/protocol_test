package org.hqu.lly.view.group;

import javafx.scene.Parent;
import org.hqu.lly.enums.PaneType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 内容面板管理组,用于GUI界面的面板切换.
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/8 9:30
 */
public class ContentPaneGroup {

    /**
     * 存储服务级面板(服务端和客户端).
     */
    public static Map<PaneType, Parent> contentPaneMap = new ConcurrentHashMap<>();
}
