package org.hqu.lly.view.group;

import org.hqu.lly.view.controller.BaseClientController;
import org.hqu.lly.view.controller.TabPaneController;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 存储控制view的controller
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/2/1 11:20
 */
public class ControllerGroup {


    public static Set<BaseClientController<?>> clientControllerSet = ConcurrentHashMap.newKeySet();

    public static Set<TabPaneController> tabPaneControllerSet = ConcurrentHashMap.newKeySet();



}
