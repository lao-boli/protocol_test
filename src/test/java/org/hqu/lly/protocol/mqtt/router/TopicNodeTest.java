package org.hqu.lly.protocol.mqtt.router;


import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.junit.jupiter.api.Test;

import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * <p>
 *
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/3/11 15:48
 */
public class TopicNodeTest {


    @Test
    public void addRoute() {
        Channel[] cls = new Channel[10];
        fill(cls);
        TopicNode root = new TopicNode();
        root.setType(NodeType.ROOT);
        root.setPath("");
        root.addRoute("testtopic/#", cls[0]);
        root.addRoute("testto", cls[1]);
        root.addRoute("testtpic/#", cls[2]);
        root.addRoute("/testtpic/#", cls[3]);
        root.addRoute("/testtpic/a", cls[4]);
        root.addRoute("testtopic/#", cls[5]);
        root.printTree();

        Set<Channel> result = new HashSet<>();
        root.getValues("testtopic/", result);
        assertThat(result).hasSize(2).contains(cls[5],cls[0]);
        result = new HashSet<>();

        root.getValues("testtopic/a/b/", result);
        assertThat(result).hasSize(2).contains(cls[5],cls[0]);
        result = new HashSet<>();

        root.getValues("testto", result);
        assertThat(result).hasSize(1).contains(cls[1]);
        result = new HashSet<>();

        root.getValues("testtpic/", result);
        assertThat(result).hasSize(1).contains(cls[2]);
        result = new HashSet<>();

        root.getValues("/testtpic/", result);
        assertThat(result).hasSize(1).contains(cls[3]);
        result = new HashSet<>();

        root.getValues("/testtpic/a", result);
        assertThat(result).hasSize(2).contains(cls[3],cls[4]);
        result = new HashSet<>();
    }

    @Test
    public void testSingle() {
        Channel[] cls = new Channel[10];
        fill(cls);
        System.out.println(Arrays.toString(cls));
        TopicNode root = new TopicNode();
        root.setType(NodeType.ROOT);
        root.setPath("/");
        root.addRoute("/search/", cls[0]);
        root.addRoute("/search/+/", cls[1]);
        root.addRoute("/sup/", cls[2]);
        root.addRoute("/sup/+/+/b/", cls[3]);
        root.addRoute("/sup/+/c/b/", cls[4]);
        root.printTree();

        // getValue
        System.out.println("-------get value-----------");
        Set<Channel> result = new HashSet<>();
        root.getValues("/search/", result);
        assertThat(result).hasSize(1).contains(cls[0]);
        result = new HashSet<>();

        root.getValues("/search/a/", result);
        assertThat(result).hasSize(1).contains(cls[1]);
        result = new HashSet<>();

        root.getValues("/search/a/b", result);
        assertThat(result).hasSize(0);
        result = new HashSet<>();

        root.getValues("/sup/a/b/b/", result);
        assertThat(result).hasSize(1).contains(cls[3]);
        result = new HashSet<>();

        root.getValues("/sup/a/c/b/", result);
        assertThat(result).hasSize(2).contains(cls[3], cls[4]);
        result = new HashSet<>();
    }

    @Test
    public void testMulti() {
        Channel[] cls = new Channel[10];
        fill(cls);
        TopicNode root = new TopicNode();
        root.setType(NodeType.ROOT);
        root.setPath("/");
        root.addRoute("/#", cls[0]);
        root.addRoute("/search/", cls[1]);
        root.addRoute("/search/#", cls[2]);
        root.addRoute("/search/a/", cls[3]);
        root.addRoute("/sup/", cls[4]);
        root.printTree();

        // getValue
        System.out.println("-------get value-----------");
        Set<Channel>[] results = new Set[]{new HashSet<Channel>(), new HashSet<Channel>(), new HashSet<Channel>(), new HashSet<Channel>(), new HashSet<Channel>(), new HashSet<Channel>()};
        Set<Channel> result = new HashSet<>();

        root.getValues("/", result);
        assertThat(result).hasSize(1).contains(cls[0]);
        result = new HashSet<>();

        root.getValues("/search/", result);
        assertThat(result).hasSize(3).contains(cls[0], cls[1], cls[2]);
        result = new HashSet<>();

        root.getValues("/search/b/", result);
        assertThat(result).hasSize(2).contains(cls[0], cls[2]);
        result = new HashSet<>();

        root.getValues("/search/a/", result);
        assertThat(result).hasSize(3).contains(cls[0], cls[2], cls[3]);
        result = new HashSet<>();

        root.getValues("/sup/", result);
        assertThat(result).hasSize(2).contains(cls[0], cls[4]);
        result = new HashSet<>();

        root.getValues("/sup/a", result);
        assertThat(result).hasSize(1).contains(cls[0]);
        result = new HashSet<>();
    }


    @Test
    public void testMix() {
        Channel[] cls = new Channel[10];
        fill(cls);
        TopicNode root = new TopicNode();
        root.setType(NodeType.ROOT);
        root.setPath("/");
        root.addRoute("/search/", cls[0]);
        root.addRoute("/search/+/", cls[1]);
        root.addRoute("/search/#", cls[2]);
        root.addRoute("/search/a/", cls[3]);
        root.addRoute("/sup/", cls[4]);
        root.addRoute("/sup/+/a/",cls[5]);
        root.addRoute("/sup/+/+/b/",cls[6]);
        root.addRoute("/sup/+/+/#",cls[7]);
        root.addRoute("/sup/+/+/#",cls[8]);
        root.addRoute("/sup/+/+/b/+/",cls[9]);
        root.addRoute("/sed/",cls[9]);
        root.printTree();

        // getValue
        System.out.println("-------get value-----------");
        Set<Channel> result = new HashSet<>();

        root.getValues("/", result);
        assertThat(result).hasSize(0);
        result = new HashSet<>();

        root.getValues("/search/", result);
        assertThat(result).hasSize(2).contains(cls[0], cls[2]);
        result = new HashSet<>();

        root.getValues("/search/b/", result);
        assertThat(result).hasSize(2).contains(cls[1], cls[2]);
        result = new HashSet<>();

        root.getValues("/search/a/", result);
        assertThat(result).hasSize(3).contains(cls[1], cls[2], cls[3]);
        result = new HashSet<>();

        root.getValues("/sup/", result);
        assertThat(result).hasSize(1).contains(cls[4]);
        result = new HashSet<>();

        root.getValues("/sup/c/d/", result);
        assertThat(result).hasSize(2).contains(cls[7],cls[8]);
        result = new HashSet<>();

        root.getValues("/sup/c/a/", result);
        assertThat(result).hasSize(3).contains(cls[5],cls[7],cls[8]);
        result = new HashSet<>();

        root.getValues("/sup/c/d/a/", result);
        assertThat(result).hasSize(2).contains(cls[7],cls[8]);
        result = new HashSet<>();

        root.getValues("/sup/c/d/b/", result);
        assertThat(result).hasSize(3).contains(cls[6],cls[7],cls[8]);
        result = new HashSet<>();

        root.getValues("/sup/a/a/b/b/", result);
        assertThat(result).hasSize(3).contains(cls[7],cls[8],cls[9]);
        result = new HashSet<>();

        root.getValues("/sup/a/a/a/b/", result);
        assertThat(result).hasSize(2).contains(cls[7],cls[8]);
        result = new HashSet<>();
    }


    @Test
    public void testGetNode() {
        Channel[] cls = new Channel[10];
        fill(cls);
        TopicNode root = new TopicNode();
        root.setType(NodeType.ROOT);
        root.setPath("/");
        root.addRoute("/search/", cls[0]);
        root.addRoute("/search/+/", cls[1]);
        root.addRoute("/search/#", cls[2]);
        root.addRoute("/search/a/", cls[3]);
        root.addRoute("/sup/", cls[4]);
        root.addRoute("/sup/+/a/",cls[5]);
        root.addRoute("/sup/+/+/b/",cls[6]);
        root.addRoute("/sup/+/+/#",cls[7]);
        root.addRoute("/sup/+/+/#",cls[8]);
        root.addRoute("/sup/+/+/b/+/",cls[9]);
        root.addRoute("/sed/",cls[9]);
        root.printTree();

        System.out.println(root.getNode("/sed/").desc());
        System.out.println(root.getNode("/sup/+/+/b/+/").desc());
        System.out.println(root.getNode("/sup/+/+/#").desc());
        System.out.println(root.getNode("/sup/+/a/").desc());

    }


    @Test
    public void testRemoveNode() {
        // init
        Channel[] cls = new Channel[20];
        fill(cls);
        String[] before = new String[20];
        Arrays.fill(before,"");
        String[] after = new String[20];
        Arrays.fill(after,"");

        TopicNode root = new TopicNode();
        root.setType(NodeType.ROOT);
        root.setPath("/");
        before[0] = root.getTree().toString();
        root.addRoute("/search/", cls[0]);
        before[1] = root.getTree().toString();
        root.addRoute("/search/+/", cls[1]);
        before[2] = root.getTree().toString();
        root.addRoute("/search/#", cls[2]);
        before[3] = root.getTree().toString();
        root.addRoute("/search/a/", cls[3]);
        before[4] = root.getTree().toString();
        root.addRoute("/sup/", cls[4]);
        before[5] = root.getTree().toString();
        root.addRoute("/sup/ab/b/c/",cls[5]);
        before[6] = root.getTree().toString();
        root.addRoute("/sup/ac/b/c/d/",cls[6]);
        before[7] = root.getTree().toString();
        root.addRoute("/search/+/+/", cls[7]);
        before[8] = root.getTree().toString();
        root.addRoute("/search/+/+", cls[8]);
        root.printTree();

        root.removeRoute("/search/+/+",cls[8]);
        after[8] = root.getTree().toString();
        assertEquals(before[8], after[8]);

        root.removeRoute("/search/+/+/",cls[7]);
        after[7] = root.getTree().toString();
        assertEquals(before[7], after[7]);

        root.removeRoute("/sup/ac/b/c/d/",cls[6]);
        after[6] = root.getTree().toString();
        assertEquals(before[6], after[6]);

        root.removeRoute("/sup/ab/b/c/",cls[5]);
        after[5] = root.getTree().toString();
        assertEquals(before[5], after[5]);

        root.removeRoute("/sup/",cls[4]);
        after[4] = root.getTree().toString();
        assertEquals(before[4], after[4]);

        root.removeRoute("/search/a/",cls[3]);
        after[3] = root.getTree().toString();
        assertEquals(before[3], after[3]);

        root.removeRoute("/search/#",cls[2]);
        after[2] = root.getTree().toString();
        assertEquals(before[2], after[2]);

        root.removeRoute("/search/+/",cls[1]);
        after[1] = root.getTree().toString();
        assertEquals(before[1], after[1]);

        root.removeRoute("/search/",cls[0]);
        after[0] = root.getTree().toString();
        assertEquals(before[0], after[0]);


        // getValue
    }

    private void fill(Channel[] arr){
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new TestChannel(i);
        }
    }
    static class TestChannel implements Channel {
        public static TestChannel getInstance() {
            b+=1;
            return new TestChannel(b);
        }
        public static TestChannel getInstance(boolean reset) {
            if (reset){
                b=0;
            }
            b+=1;
            return new TestChannel(b);
        }
        private double a = Math.random();
        private static int b = 0;
        private  int c = 0;

        @Override
        public String toString() {
            return "cl{" +
                    "c=" + c +
                    '}';
        }

        public TestChannel() {
        }
        public TestChannel(int c) {
            this.c = c;
        }

        @Override
        public ChannelId id() {
            return null;
        }

        @Override
        public EventLoop eventLoop() {
            return null;
        }

        @Override
        public Channel parent() {
            return null;
        }

        @Override
        public ChannelConfig config() {
            return null;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public boolean isRegistered() {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public ChannelMetadata metadata() {
            return null;
        }

        @Override
        public SocketAddress localAddress() {
            return null;
        }

        @Override
        public SocketAddress remoteAddress() {
            return null;
        }

        @Override
        public ChannelFuture closeFuture() {
            return null;
        }

        @Override
        public boolean isWritable() {
            return false;
        }

        @Override
        public long bytesBeforeUnwritable() {
            return 0;
        }

        @Override
        public long bytesBeforeWritable() {
            return 0;
        }

        @Override
        public Unsafe unsafe() {
            return null;
        }

        @Override
        public ChannelPipeline pipeline() {
            return null;
        }

        @Override
        public ByteBufAllocator alloc() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
            return null;
        }

        @Override
        public ChannelFuture disconnect() {
            return null;
        }

        @Override
        public ChannelFuture close() {
            return null;
        }

        @Override
        public ChannelFuture deregister() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture disconnect(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture close(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture deregister(ChannelPromise promise) {
            return null;
        }

        @Override
        public Channel read() {
            return null;
        }

        @Override
        public ChannelFuture write(Object msg) {
            return null;
        }

        @Override
        public ChannelFuture write(Object msg, ChannelPromise promise) {
            return null;
        }

        @Override
        public Channel flush() {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg) {
            return null;
        }

        @Override
        public ChannelPromise newPromise() {
            return null;
        }

        @Override
        public ChannelProgressivePromise newProgressivePromise() {
            return null;
        }

        @Override
        public ChannelFuture newSucceededFuture() {
            return null;
        }

        @Override
        public ChannelFuture newFailedFuture(Throwable cause) {
            return null;
        }

        @Override
        public ChannelPromise voidPromise() {
            return null;
        }

        @Override
        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return null;
        }

        @Override
        public <T> boolean hasAttr(AttributeKey<T> key) {
            return false;
        }

        @Override
        public int compareTo(Channel o) {
            return 0;
        }

    }
}
