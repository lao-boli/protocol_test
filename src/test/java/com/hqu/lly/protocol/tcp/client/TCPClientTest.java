package com.hqu.lly.protocol.tcp.client;

import io.netty.channel.Channel;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.*;

/**
 * <p>
 *
 * <p>
 *
 * @author liulingyu
 * @date 2022/8/5 19:55
 * @Version 1.0
 */
public class TCPClientTest {

    public static void main(String[] args) {

        TCPClient client = new TCPClient();
        client.setHost("127.0.0.1");
        client.setPort("10250");

        FutureTask<Channel> channel = new FutureTask<Channel>(client);

        new Thread(channel).start();
        try {
            channel.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        client.sendMessage("aaa");
        client.destroy();
    }
}