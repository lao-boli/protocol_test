package com.hqu.lly.enums;

import com.hqu.lly.constant.ContentPaneConsts;
import com.hqu.lly.factory.*;
import lombok.Getter;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/9 9:47
 * @Version 1.0
 */
@Getter
public enum TabFactoryEnum {
    /**
     *
     */
    TCP_CLIENT_TAB_FACTORY(ContentPaneConsts.TCP_CLIENT_PANE,new TCPClientTabFactory()),
    TCP_SERVER_TAB_FACTORY(ContentPaneConsts.TCP_SERVER_PANE,new TCPServerTabFactory()),
    WEB_SOCKET_CLIENT_TAB_FACTORY(ContentPaneConsts.WEB_SOCKET_CLIENT_PANE,new WSClientTabFactory()),
    WEB_SOCKET_SEVER_TAB_FACTORY(ContentPaneConsts.WEB_SOCKET_SERVER_PANE,new WSServerTabFactory());

    private String paneType;

    private TabFactory tabFactory;

    TabFactoryEnum(String paneType, TabFactory tabFactory) {
       this.paneType = paneType;
       this.tabFactory = tabFactory;
    }

    public static TabFactoryEnum getByPaneType(String paneType) {
        for (TabFactoryEnum value : TabFactoryEnum.values()) {
            if (value.paneType.equals(paneType)){
                return value;
            }
        }
        return null;
    }
}
