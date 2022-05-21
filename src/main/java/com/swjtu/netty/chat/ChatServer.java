package com.swjtu.netty.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        final int PORT = 8888;
        try {
            ChannelFuture chf = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerChannelInit())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .bind(PORT);
            // channel 是 server socket channel
            chf.sync();
            // 连接通道不活跃 或者 用户主动断开 就断开channel
            chf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.debug(e.toString());
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
