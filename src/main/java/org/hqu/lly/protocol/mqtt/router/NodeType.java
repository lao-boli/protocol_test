package org.hqu.lly.protocol.mqtt.router;

/**
 * <p>
 * 话题路由节点类型枚举
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/3/5 11:00
 */
public enum NodeType {
    /**
     * 根节点
     */
    ROOT,
    /**
     * 不含channel的静态路径节点
     */
    STATIC,
    /**
     * 含channel的节点
     */
    CHANNEL,
    /**
     * 单层通配符: "+"
     */
    SINGLE,
    /**
     * 多层通配符: "#"
     */
    MULTI;
}
