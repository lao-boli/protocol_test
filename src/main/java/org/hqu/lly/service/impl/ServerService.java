package org.hqu.lly.service.impl;

import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.service.MessageService;
import org.hqu.lly.service.NetworkService;

/**
 * <p>
 * 服务端服务抽象父类,用于server和pane的沟通
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/13 19:50
 */
public abstract class ServerService implements MessageService<MsgLabel>, NetworkService {

    public boolean isMuteReq() {
        return false;
    }

}
