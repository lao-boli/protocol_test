package com.hqu.lly.domain.vo;

import com.hqu.lly.service.SwitchPaneService;
import javafx.scene.control.TreeItem;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/9/4 20:31
 * @version 1.0
 */
public class ServiceItem<T> extends TreeItem {

    private SwitchPaneService switchPaneService;


    public ServiceItem(T name, SwitchPaneService switchPaneService) {

        super(name);
        this.switchPaneService = switchPaneService;
    }

    public void switchPane() {
        switchPaneService.switchPane();
    }
}
