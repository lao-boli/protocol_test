package org.hqu.lly.service.impl;

import org.hqu.lly.domain.component.MsgLabel;
import org.hqu.lly.service.MessageService;
import org.hqu.lly.service.NetworkService;

/**
 * <p>
 * 客户端服务抽象父类
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/13 19:50
 */
public abstract class ClientService implements MessageService<MsgLabel>, NetworkService {

}
