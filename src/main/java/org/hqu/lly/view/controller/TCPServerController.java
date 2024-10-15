package org.hqu.lly.view.controller;

import org.hqu.lly.domain.config.ConfigStore;
import org.hqu.lly.domain.config.ServerSessionConfig;
import org.hqu.lly.domain.config.TCPServerSessionConfig;
import org.hqu.lly.enums.ConfigType;
import org.hqu.lly.enums.PaneType;
import org.hqu.lly.protocol.tcp.server.TCPServer;

/**
 * <p>
 * TCP服务端功能控制器
 * </p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022-08-10 09:15
 */
public class TCPServerController extends ConnectedServerController{

    @Override
    public void init(ServerSessionConfig config) {
        ConfigStore.controllers.add(this);
        if (config == null) {
            initConfig();
        } else {
            serverConfig = config;
            sendSettingConfig = config.getSendSettingConfig();
            tab.setTitle(config.getTabName());
            serverPort.setText(config.getPort());
            msgInput.setText(config.getMsgInput());
        }
        setServer();
        setServerService();
        initSendSetting();

    }

    @Override
    protected void setServer() {
        server = new TCPServer();
        assert serverConfig instanceof TCPServerSessionConfig;
        ((TCPServer) server).setIdleHandler(((TCPServerSessionConfig) serverConfig).getIdleStateHandler());

        serverConfig.setPaneType(PaneType.TCP_SERVER);
    }


    @Override
    public void initConfig() {
        serverConfig = (TCPServerSessionConfig) ConfigStore.createConfig(ConfigType.TCP_SERVER);
        sendSettingConfig = serverConfig.getSendSettingConfig();
    }


}
