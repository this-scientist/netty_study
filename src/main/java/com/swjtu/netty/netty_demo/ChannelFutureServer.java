package com.swjtu.netty.netty_demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 1、细分任务，创建boss worker group
 * 2、如果出现长时间的读写任务  我们将任务交给 特定的EventLoopGroup
 * 3、同步的方式关闭channel
 */
@Slf4j
public class ChannelFutureServer {
    public static void main(String[] args) {
        run();
    }
    public static void run() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        DefaultEventLoopGroup longIOWorker = new DefaultEventLoopGroup();
        ServerBootstrap bs = new ServerBootstrap();
        try {
            ChannelFuture chf = bs.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                            pipeline.addLast("handler1", new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    // msg 没有 string decoder 处理器  ，这个时候是一个bytebuf类型
                                    ByteBuf buf = (ByteBuf) msg;
                                    log.debug("get msg from {} : {}", ch.remoteAddress(), buf.toString(Charset.defaultCharset()));
                                    // do something consume long time
                                    long start = System.currentTimeMillis();
                                    Thread.sleep(100);
                                    long end = System.currentTimeMillis();
                                    if(end - start > 50) {
                                        ctx.fireChannelRead(msg);
                                    }

                                }
                            }).addLast(longIOWorker, "handler2",
                                    new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            if(msg != null) {
                                                ByteBuf buf = (ByteBuf) msg;
                                                log.debug("After long time : {}",  buf.toString(Charset.defaultCharset()));

                                            }
                                        }
                                    });
                        }
                    })
                    .bind(8888);
            // ctrl + q 查看bind  返回值未channel future
            // 同步等待server socket channel建立完成
            ChannelFuture f = chf.sync();
            f.channel().closeFuture().sync();
//            chf.addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture channelFuture) throws Exception {
//
//                }
//            })
        } catch (Exception e) {
            log.debug(e.toString());
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
            longIOWorker.shutdownGracefully();
        }
    }
}
