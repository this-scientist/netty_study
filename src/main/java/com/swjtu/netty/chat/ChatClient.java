package com.swjtu.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        run();
    }
    public static void run() {
        String HOST = "localhost";
        int PORT = 8888;
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ChannelFuture cf = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientChannelInit())
                    .connect(HOST, PORT);
            Channel channel = cf.sync().channel();
            channel.closeFuture().sync();
            log.debug("client close...");
        } catch (InterruptedException e) {
            log.debug(e.toString());
        } finally {
            worker.shutdownGracefully();
        }
    }
}
