package com.swjtu.netty.netty_demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HelloClient {
    public static void run() {
        String msg = "hello";
        try {
            new Bootstrap()
                    .group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringEncoder());

                        }
                    })
                    .connect(InetAddress.getLocalHost(), 8888)
                    .sync()
                    .channel()
                    .writeAndFlush(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        run();
    }
}
