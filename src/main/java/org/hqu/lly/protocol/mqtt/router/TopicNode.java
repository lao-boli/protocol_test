package org.hqu.lly.protocol.mqtt.router;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * mqtt topic 节点
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/3/5 10:50
 */
@Data
public class TopicNode {

    private String path;
    private NodeType type;
    private String indices = "";
    private List<TopicNode> children = new ArrayList<>();
    //    private Set<Channel> channels = new HashSet<>();
    private Set<Dog> channels = new HashSet<>();
    private boolean containMulti = false;
    private boolean containSingle = false;

    public TopicNode() {
    }

    public TopicNode(String path, String indices) {
        this.path = path;
        this.indices = indices;
    }

    public TopicNode(String path, String indices, List<TopicNode> children) {
        this.path = path;
        this.indices = indices;
        this.children = children;
    }

    public TopicNode(String path, NodeType type, String indices, List<TopicNode> children) {
        this.path = path;
        this.type = type;
        this.indices = indices;
        this.children = children;
    }

    public void printTree(int ind) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(ind));
        sb.append("|─");
        sb.append("p=").append(path)
                .append(", is=").append(indices)
                .append(", cm=").append(containMulti)
                .append(", dogs=").append(channels);
        System.out.println(sb.toString());
        for (TopicNode child : children) {
            child.printTree(ind + 2);
        }
    }

    public void printTree() {
        printTree(0);
    }

    private String min(String s1, String s2) {
        return s1.length() < s2.length() ? s1 : s2;
    }

    /**
     * 添加路由
     *
     * @param path 要添加的路径
     * @return 节点
     */
    public TopicNode addRoute(String path, Dog channel) {
        // 公共前缀长度/位置
        int pos;
        // 当前节点不为空
        if (this.path.length() > 0 || !children.isEmpty()) {

            // 处理连续的两个+路径，如test/+/+/的情况
            if (path.startsWith("+/")) {
                insertSingle(path, channel);
                return this;
            }

            if (path.equals(this.path)) {
                // todo handle equal
                addChannel(channel);
                return this;
            }

            // 找出公共前缀的位置
            for (pos = 0; pos < min(this.path, path).length(); pos++) {
                if (this.path.charAt(pos) != path.charAt(pos)) {
                    break;
                }
            }

            // 如果相同前缀的长度比当前节点保存的 path 短
            if (pos > 0 && pos < this.path.length()) {
                // 复制本节点为子节点
                TopicNode child = new TopicNode(this.path.substring(pos), NodeType.STATIC, indices, children);
                child.setChannels(this.channels);
                child.setContainMulti(this.containMulti);
                child.setContainSingle(this.containSingle);

                //
                ArrayList<TopicNode> children = new ArrayList<>();
                children.add(child);
                this.setChildren(children);
                // 子路径的首个字符作为索引
                this.setIndices(this.path.substring(pos, pos + 1));
                // 更新当前节点的 path 为新的公共前缀
                this.setPath(this.path.substring(0, pos));
                this.setChannels(null);
                this.setContainMulti(false);
                this.setContainSingle(false);
            }


            // 去除公共前缀后的路径[removed prefix path]
            String rpPath = path.substring(pos);
            // 新路径去除公共前缀后的首个字符
            char c = rpPath.charAt(0);

            // 通配符节点直接插入
            if (c == '+' || c == '#') {
                this.insertChild(rpPath, String.valueOf(c), channel);
                return this;
            }
            // 检验新添加的路径是否和已存在的子路径存在公共前缀
            for (int i = 0; i < this.indices.length(); i++) {
                // 若存在,则递归调用
                if (c == indices.charAt(i)) {
                    this.children.get(i).addRoute(rpPath, channel);
                    return this;
                }
            }

            // 将新路径插入子节点中
            this.insertChild(rpPath, String.valueOf(c), channel);

        } else {
            // 当前节点为空,直接设置
            this.setPath(path);
        }
        return this;
    }

    /**
     * 将新路径节点插入本节点的子节点列表中
     *
     * @param path   去除公共前缀后的子节点路径
     * @param prefix 子节点路径的首个字符
     */
    public void insertChild(String path, String prefix, Dog channel) {

        if (prefix.equals("+")) {
            insertSingle(path, channel);
            return;
        }

        if (prefix.equals("#")) {
            insertMulti(channel);
            return;
        }

        TopicNode child = new TopicNode(path, "");
        child.addChannel(channel);
        this.indices += prefix;
        children.add(child);
    }

    private void insertSingle(String path, Dog channel) {
        TopicNode single = null;
        if (this.containSingle) {
            single = children.get(indices.indexOf("+"));
        } else {
            single = new TopicNode("+/", "");
            single.setType(NodeType.SINGLE);
            children.add(single);
            indices += "+";
        }
        // 去除 "+/"
        String subPath = path.substring(2);
        if (!subPath.isEmpty()) {
            single.addRoute(subPath, channel);
        } else {
            single.addChannel(channel);
        }
        this.containSingle = true;
        return;
    }

    private void insertMulti(Dog channel) {
        if (this.containMulti) {
            TopicNode multi = children.get(indices.indexOf("#"));
            multi.addChannel(channel);
            return;
        }

        TopicNode multi = new TopicNode("#", "");
        multi.setType(NodeType.MULTI);
        multi.addChannel(channel);

        children.add(multi);
        indices += "#";
        this.containMulti = true;
        return;
    }

    public void getValues(String path, Set<Dog> res) {
        if (this.containMulti) {
            res.addAll(children.get(indices.indexOf("#")).getChannels());
        }

        if (this.containSingle) {
            int index = path.indexOf("/");
            // 本节点不是[+/]则跳过两层/
            if (!NodeType.SINGLE.equals(this.type)) {
                index = path.indexOf("/", index + 1);
            }
            String p = path.substring(index + 1);
            TopicNode single = children.get(indices.indexOf("+"));
            if (p.isEmpty()) {
                res.addAll(single.getChannels());
            } else {
                single.getValues(p, res);
            }
        }

        if (NodeType.SINGLE.equals(this.type)) {
            // 通过查找路径的首字符，快速找到包含这个字符的子节点
            for (int i = 0; i < indices.length(); i++) {
                // 找到节点,递归查询
                if (path.charAt(0) == indices.charAt(i)) {
                    children.get(i).getValues(path, res);
                }
            }
            return;

            // 查询路径大于本节点路径
        } else if (path.length() > this.path.length()) {
            // 如果寻找的路径和节点保存的路径有相同的前缀
            if (path.startsWith(this.path)) {
                // 将寻找路径的前缀部分去除
                String subPath = path.substring(this.path.length());
                // 通过查找路径的首字符，快速找到包含这个字符的子节点
                for (int i = 0; i < indices.length(); i++) {
                    // 找到节点,递归查询
                    if (subPath.charAt(0) == indices.charAt(i)) {
                        children.get(i).getValues(subPath, res);
                    }
                }
            }
        }

        if (path.equals(this.path)) {
            res.addAll(this.channels);
            return;
        }

        return;
    }

    public void addChannel(Dog channel) {
        if (this.channels == null) {
            this.channels = new HashSet<>();
        }
        this.channels.add(channel);
    }

}
