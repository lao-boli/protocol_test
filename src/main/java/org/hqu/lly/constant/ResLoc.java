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
    public static final URL DATA_SETTING_PANE = realPath("views/DataSettingPane.fxml");
    public static final URL CUSTOM_ALERT = realPath("components/CustomAlert.fxml");
    public static final URL TCP_CLIENT_PANE =realPath("views/TCPClientPane.fxml");
    public static final URL TCP_SERVER_PANE =realPath("views/TCPServerPane.fxml");
    public static final URL UDP_CLIENT_PANE =realPath("views/UDPClientPane.fxml");
    public static final URL UDP_SERVER_PANE =realPath("views/UDPServerPane.fxml");
    public static final URL WEB_SOCKET_SERVER_PANE = realPath("views/WebSocketServerPane.fxml");
    public static final URL WEB_SOCKET_CLIENT_PANE = realPath("views/WebSocketClientPane.fxml");

    public static final URL TAB_TITLE = realPath("css/tabTitle.css");
    public static final URL COMMON_CSS = realPath("css/Common.css");


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
