package org.hqu.lly.protocol.mqtt.router;

import lombok.Data;

import java.util.*;

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

    @Override
    public String toString() {
        return "TopicNode{" +
                "path='" + path + '\'' +
                ", type=" + type +
                ", indices='" + indices + '\'' +
                ", children=" + children +
                ", channels=" + channels +
                ", containMulti=" + containMulti +
                ", containSingle=" + containSingle +
                '}';
    }

    private String path;
    private NodeType type;
    private String indices = "";
    private TopicNode parent;
    private List<TopicNode> children = new ArrayList<>();
    //    private Set<Channel> channels = new HashSet<>();
    private Set<Dog> channels = new HashSet<>();
    private boolean containMulti = false;
    private boolean containSingle = false;

    public TopicNode(String path, NodeType type, String indices, List<TopicNode> children, Set<Dog> channels, boolean containMulti, boolean containSingle) {
        this.path = path;
        this.type = type;
        this.indices = indices;
        this.children = children;
        this.channels = channels;
        this.containMulti = containMulti;
        this.containSingle = containSingle;
    }

    public TopicNode() {
    }

    public TopicNode(String path) {
        this.path = path;
    }

    public void getTree(TopicNode node, String level, StringBuilder sb) {
        List<TopicNode> children = node.getChildren();
        if (children.isEmpty()) {
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            sb.append("\n").append(level);
            if (i == children.size() - 1) {
                sb.append("└─ ").append(children.get(i).desc());
                children.get(i).getTree(children.get(i), level + "   ", sb);
            } else {
                sb.append("├─ ").append(children.get(i).desc());
                children.get(i).getTree(children.get(i), level + "|  ", sb);
            }
        }
    }

    /**
     * 节点信息描述
     *
     * @return 描述信息
     */
    public String desc() {
        StringBuilder sb = new StringBuilder();
        sb.append("p=").append(path)
                .append(", t=").append(type)
                .append(", is=").append(indices)
                .append(", pa=").append(parent == null ? "" : parent.getPath())
                .append(", dogs=").append(channels);
        return sb.toString();
    }


    public void printTree() {
        System.out.println(getTree().toString());
    }

    public StringBuilder getTree(){
        StringBuilder sb = new StringBuilder();
        sb.append(desc());
        getTree(this, "", sb);
        return sb;
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

            // 订阅话题已存在,添加channel
            if (path.equals(this.path)) {
                addChannel(channel);
                return this;
            }

            // 找出公共前缀的位置
            for (pos = 0; pos < min(this.path, path).length(); pos++) {
                if (this.path.charAt(pos) != path.charAt(pos)) {
                    break;
                }
            }

            // 如果相同前缀的长度比当前节点保存的 path 短,节点将进行分裂
            if (pos > 0 && pos < this.path.length()) {
                // 复制本节点为子节点,并设置为本节点的子节点数组
                TopicNode child = new TopicNode(this.path.substring(pos), NodeType.CHANNEL, indices, children, channels, containMulti, containSingle);
                // 更改本节点的子节点的引用为复制后的子节点
                children.forEach(cc -> cc.setParent(child));
                child.setParent(this);
                this.setChildren(new ArrayList<>(Arrays.asList(child)));

                // 将本节点设置为路径节点
                // 由本节点复制的子节点路径的首个字符作为索引
                this.setIndices(this.path.substring(pos, pos + 1));
                // 更新当前节点的 path 为新的公共前缀
                this.setPath(this.path.substring(0, pos));
                this.setChannels(null);
                this.setType(NodeType.STATIC);
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

            // 不和已存在的子路径存在公共前缀，
            //将新路径插入子节点中
            this.insertChild(rpPath, String.valueOf(c), channel);
        } else {
            // 当前节点为空,直接设置
            this.setPath(path);
        }
        return this;
    }

    /**
     * 移除路由
     *
     * @param path 要添加的路径
     * @return 节点
     */
    public TopicNode removeRoute(String path, Dog channel) {
        TopicNode node = getNode(path);
        node.removeChannel(channel);

        if (node.getChildren().isEmpty()){
            TopicNode parent = node.getParent();
            parent.removeChild(node);
            // 父节点是只保存路径的节点,且父节点只有一个子节点
            if (NodeType.STATIC.equals(parent.getType()) && parent.getChildren().size() == 1){
                TopicNode child = parent.getChildren().remove(0);
                child.setPath(parent.getPath() + child.getPath());
                child.setParent(parent.getParent());
                parent.getParent().replaceChild(parent,child);
            }

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

        TopicNode child = new TopicNode(path);
        child.setType(NodeType.CHANNEL);
        child.addChannel(channel);
        child.setParent(this);

        this.indices += prefix;
        children.add(child);
    }


    /**
     * 移除子节点
     */
    public void removeChild(TopicNode node) {
        children.remove(node);
        indices = indices.replace(node.getPath().substring(0,1),"");
    }

    public void replaceChild(TopicNode oldNode,TopicNode newNode) {
        children.set(children.indexOf(oldNode),newNode);
    }

    /**
     * 插入通配符节点"+"
     *
     * @param path    要添加的路径
     * @param channel 要添加的channel
     */
    private void insertSingle(String path, Dog channel) {
        TopicNode single = null;
        if (this.containSingle) {
            single = children.get(indices.indexOf("+"));
        } else {
            single = new TopicNode("+/");
            single.setType(NodeType.SINGLE);
            single.setParent(this);
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

    /**
     * 插入通配符节点"#"
     * @param channel 发起话题订阅的channel
     */
    private void insertMulti(Dog channel) {
        if (this.containMulti) {
            TopicNode multi = children.get(indices.indexOf("#"));
            multi.addChannel(channel);
            return;
        }
        TopicNode multi = new TopicNode("#");
        multi.setType(NodeType.MULTI);
        multi.addChannel(channel);
        multi.setParent(this);

        children.add(multi);
        indices += "#";
        this.containMulti = true;
        return;
    }

    /**
     * 获取子节点.
     * @param path 子节点路径
     * @return 子节点
     */
    public TopicNode getNode(String path) {
        if (path.length() > this.path.length()) {
            // 如果寻找的路径和节点保存的路径有相同的前缀
            if (path.startsWith(this.path)) {
                // 将寻找路径的前缀部分去除
                String subPath = path.substring(this.path.length());
                // 通过查找路径的首字符，快速找到包含这个字符的子节点
                for (int i = 0; i < indices.length(); i++) {
                    // 找到节点,递归查询
                    if (subPath.charAt(0) == indices.charAt(i)) {
                        return children.get(i).getNode(subPath);
                    }
                }
            }
        }

        if (path.equals(this.path)) {
            return this;
        }

        return null;
    }

    /**
     * 获取匹配路径的所有channel
     * @param path 匹配路径
     * @param res 存放结果的集合,应为一个空集合。
     */
    public void getValues(String path, Set<Dog> res) {
        // "#"中的channel匹配/a/,/a/#,
        // 当匹配路径为/a/时,要返回 "/a/"和"/a/#"中的channel
        // 子节点中含有"#"节点,将其中的channel添加到res中
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

    public void removeChannel(Dog channel) {
        if (this.channels == null) {
            return;
        }
        this.channels.remove(channel);
    }

}
