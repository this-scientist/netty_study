package com.swjtu.netty.netty_demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PipelineAndHandlerDemo {
    public static void main(String[] args) {
        run();
    }
    public static void run() {
        ChannelFuture bind = new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 入向一般为读操作所以重写 读事件方法
                        // pipeline: head -> h1 -> h2 -> h3 -> h4 -> tail
                        // addLast 是插到tail前
                        pipeline.addLast("h1", new ChannelInboundHandlerAdapter() {

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("1");
                                // super 位置为什么不能换啊 ，研究一下  ，还要研究一下ctx
                                // channelRead会将handler的执行权交给下一个handler  并将本次的msg传入下一个handler
                                // 保证链路的通畅
                                super.channelRead(ctx, msg);
                            }
                        });
                        pipeline.addLast("h2", new ChannelInboundHandlerAdapter() {

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("2");
                                super.channelRead(ctx, msg);
                                // 是ch哈，不是ctx
                                // ch 是从tail 从后往前找出站处理器
                                // ctx 是从 当前位置从后往前找出站处理器
                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("hello".getBytes()));
                            }
                        });
                        // 只有channel写出事件时才会执行
                        pipeline.addLast("h3", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("3");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h4", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("4");
                                super.write(ctx, msg, promise);
                            }
                        });

                    }
                }).bind(8888);
    }
}
