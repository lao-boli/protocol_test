package org.hqu.lly.enums;

import lombok.Getter;
import org.hqu.lly.factory.*;

import static org.hqu.lly.enums.PaneType.*;

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
    TCP_CLIENT_TAB_FACTORY(TCP_CLIENT, new TCPClientTabFactory()),
    TCP_SERVER_TAB_FACTORY(TCP_SERVER, new TCPServerTabFactory()),
    UDP_CLIENT_TAB_FACTORY(UDP_CLIENT, new UDPClientTabFactory()),
    UDP_SERVER_TAB_FACTORY(UDP_SERVER, new UDPServerTabFactory()),
    WEB_SOCKET_CLIENT_TAB_FACTORY(WS_CLIENT, new WSClientTabFactory()),
    WEB_SOCKET_SEVER_TAB_FACTORY(WS_SERVER, new WSServerTabFactory());

    private PaneType paneType;

    private BaseTabFactory tabFactory;

    TabFactoryEnum(PaneType paneType, BaseTabFactory tabFactory) {
        this.paneType = paneType;
        this.tabFactory = tabFactory;
    }

    public static TabFactoryEnum getByPaneType(PaneType paneType) {
        for (TabFactoryEnum value : TabFactoryEnum.values()) {
            if (value.paneType.equals(paneType)) {
                return value;
            }
        }
        return null;
    }
}
