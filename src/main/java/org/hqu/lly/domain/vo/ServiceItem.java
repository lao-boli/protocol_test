package org.hqu.lly.domain.vo;

import javafx.scene.control.TreeItem;
import lombok.Data;
import org.hqu.lly.service.SwitchPaneService;

/**
 * <p>
 * 服务级菜单树节点,为菜单树节点添加切换功能
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/4 20:31
 */
@Data
public class ServiceItem<T> extends TreeItem<T> {

    private SwitchPaneService switchPaneService;

    public ServiceItem(T name, SwitchPaneService switchPaneService) {
        super(name);
        this.switchPaneService = switchPaneService;
    }

    public void switchPane() {
        switchPaneService.switchPane();
    }
}
