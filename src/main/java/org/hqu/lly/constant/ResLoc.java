package org.hqu.lly.constant;

import lombok.SneakyThrows;

import java.net.URL;

/**
 * <p>
 * 资源路径常量
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/9/6 19:30
 */
public class ResLoc {

    public static final URL TAB_PANE = realPath("views/TabPane.fxml");
    public static final URL MAIN_PANE = realPath("views/MainPane.fxml");
    public static final URL SEND_SETTING_PANE = realPath("views/SendSettingPane.fxml");
    public static final URL TCP_CLIENT_PANE =realPath("views/TCPClientPane.fxml");
    public static final URL TCP_SERVER_PANE =realPath("views/TCPServerPane.fxml");
    public static final URL UDP_CLIENT_PANE =realPath("views/UDPClientPane.fxml");
    public static final URL UDP_SERVER_PANE =realPath("views/UDPServerPane.fxml");
    public static final URL WEB_SOCKET_SERVER_PANE = realPath("views/WebSocketServerPane.fxml");
    public static final URL WEB_SOCKET_CLIENT_PANE = realPath("views/WebSocketClientPane.fxml");

    public static final URL TITLE_BAR = realPath("views/titleBar.fxml");

    public static final URL TAB_TITLE = realPath("css/tabTitle.css");
    public static final URL COMMON_CSS = realPath("css/Common.css");

    public static final URL MY_ALERT_CSS = realPath("css/MyAlert.css");

    public static final URL DATA_SETTING_PANE_CSS = realPath("css/DataSettingPane.css");

    public static final URL HELP_DOC_DIALOG_CSS = realPath("css/HelpDocDialog.css");

    public static final URL ALERT_TITLE_BAR_CSS = realPath("css/alertTitleBar.css");

    public static final URL MESSAGE_POPUP_CSS = realPath("css/MessagePopup.css");
    public static final URL LIST_ITEM_POPUP_CSS = realPath("css/ListItemPopup.css");

    public static final URL STAGING_AREA_CSS = realPath("css/StagingArea.css");


    public static final URL APP_ICON_16 = realPath("icon/app/16x16.png");
    public static final URL APP_ICON_32 = realPath("icon/app/32x32.png");
    public static final URL APP_ICON_48 = realPath("icon/app/48x48.png");

    public static final URL APP_ICON_64 = realPath("icon/app/64x64.png");
    public static final URL APP_ICON_96 = realPath("icon/app/96x96.png");
    public static final URL APP_ICON_128 = realPath("icon/app/128x128.png");
    public static final URL APP_ICON_256 = realPath("icon/app/256x256.png");
    public static final URL APP_ICON_512 = realPath("icon/app/512x512.png");

    public static final URL ICON = realPath("icon/icon.css");

    // region theme
    public static final URL IDEA = realPath("css/IdeaDarkStyle.css");
    // endregion

    // js lib
    public static final URL RANDOM_UTIL = realPath("js/random-util.js");

    public static final URL GLOBE_VARIABLE = realPath("js/globe-variable.js");

    /**
     * <p>
     * 返回资源的真实路径
     * </p>
     *
     * @param path 在resources文件夹下的资源路径
     * @return {@link URL}资源的真实路径
     * @date 2023-02-27 19:33:34 <br>
     */
    @SneakyThrows
    private static URL realPath(String path) {
        return ResLoc.class.getClassLoader().getResource(path);
    }

}
