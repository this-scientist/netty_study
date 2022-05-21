package com.swjtu.netty.netty_demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

@Slf4j
public class ChannelFutureClient {
    public static void main(String[] args) {
        run();
    }
    public static void run() {
        Bootstrap b = new Bootstrap();
        try {
            ChannelFuture cf = b.group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new StringEncoder());
                        }
                    })
                    .connect(InetAddress.getLocalHost(), 8888);
            // 同步获取建立好的channel后 再进行消息发送
            Channel channel = cf.sync().channel();
            // 新建线程负责消息发送，按q关闭channel
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while(true) {
                    if(scanner.hasNextLine()) {
                        String msg = scanner.nextLine();
                        if(msg.equals("q")) {
                            channel.close();
                        } else {
                            channel.writeAndFlush(msg);
                        }
                    }
                }

            }).start();
            channel.closeFuture().sync();
            log.debug("close client...");
        } catch (UnknownHostException | InterruptedException e) {
            log.debug(e.toString());
        }
    }
}
