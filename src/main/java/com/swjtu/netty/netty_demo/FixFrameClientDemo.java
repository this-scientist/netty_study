package com.swjtu.netty.netty_demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Random;

public class FixFrameClientDemo {
    public FixFrameClientDemo() {
    }

    public void run() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ChannelFuture cf = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buf = ctx.alloc().buffer();
                                    char c = 'a';
                                    Random random = new Random();
                                    for (int i = 0; i < 10; i++) {
                                        byte[] b = fill10Bytes(c, random.nextInt(10) + 1);
                                        c++;
                                        buf.writeBytes(b);
                                    }
                                    ctx.writeAndFlush(buf);
                                }
                            });
                        }
                    })
                    .connect("localhost", 8888);
            cf.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
    public static void main(String[] args) {
        FixFrameClientDemo fixFrameClientDemo = new FixFrameClientDemo();
        fixFrameClientDemo.run();
    }
    // 生成定长的报文，当长度不足时自动补齐
    public byte[] fill10Bytes(char c, int len) {
        byte[] res = new byte[10];
        for (int i = 0; i < len; i++) {
            res[i] = (byte) c;
        }
        for (int i = len; i < 10; i++) {
            res[i] = (byte) '_';
        }
        return res;
    }
}
