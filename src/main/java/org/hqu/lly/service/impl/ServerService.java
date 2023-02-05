package org.hqu.lly.service.impl;

import org.hqu.lly.service.MessageService;
import org.hqu.lly.service.NetworkService;

/**
 * <p>
 * 服务端服务抽象父类,用于将netty接收的消息或发生的异常显示在GUI面板上
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2022/8/13 19:50
 */
public abstract class ServerService implements MessageService, NetworkService {
}
