package org.hqu.lly.protocol.mqtt.server;

import io.netty.channel.Channel;
import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.service.impl.ConnectedServerService;
import org.junit.jupiter.api.Test;

/**
 * <p>
 *
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/3/1 20:07
 */
public class MQTTServerTest {

    @Test
    public void init() {
        MQTTServer mqttServer = new MQTTServer();
        mqttServer.setPort(8888);
        mqttServer.setService(new ConnectedServerService() {
            @Override
            public void updateMsgList(MsgLabel msg) {

            }

            @Override
            public void addChannel(Channel channel) {

            }

            @Override
            public void removeChannel(Channel channel) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onError(Throwable e, String errorMessage) {

            }

            @Override
            public void onClose() {

            }
        });
        mqttServer.init();
    }

}
