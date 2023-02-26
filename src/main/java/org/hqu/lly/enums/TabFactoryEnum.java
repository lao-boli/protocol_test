package org.hqu.lly.enums;

import lombok.Getter;
import org.hqu.lly.constant.ContentPaneConsts;
import org.hqu.lly.factory.*;

/**
 * <p>
 * 标签页工厂枚举类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/9 9:47
 */
@Getter
public enum TabFactoryEnum {
    /**
     *标签页工厂
     */
    TCP_CLIENT_TAB_FACTORY(ContentPaneConsts.TCP_CLIENT_PANE, new TCPClientTabFactory()),
    TCP_SERVER_TAB_FACTORY(ContentPaneConsts.TCP_SERVER_PANE, new TCPServerTabFactory()),
    UDP_CLIENT_TAB_FACTORY(ContentPaneConsts.UDP_CLIENT_PANE, new UDPClientTabFactory()),
    UDP_SERVER_TAB_FACTORY(ContentPaneConsts.UDP_SERVER_PANE, new UDPServerTabFactory()),
    WEB_SOCKET_CLIENT_TAB_FACTORY(ContentPaneConsts.WEB_SOCKET_CLIENT_PANE, new WSClientTabFactory()),
    WEB_SOCKET_SEVER_TAB_FACTORY(ContentPaneConsts.WEB_SOCKET_SERVER_PANE, new WSServerTabFactory());

    private String paneType;

    private BaseTabFactory tabFactory;

    TabFactoryEnum(String paneType, BaseTabFactory tabFactory) {
        this.paneType = paneType;
        this.tabFactory = tabFactory;
    }

    public static TabFactoryEnum getByPaneType(String paneType) {
        for (TabFactoryEnum value : TabFactoryEnum.values()) {
            if (value.paneType.equals(paneType)) {
                return value;
            }
        }
        return null;
    }
}
