package com.swjtu.netty.chat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class ChatServerEventHandler extends SimpleChannelInboundHandler {
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel addChannel = ctx.channel();
        channels.writeAndFlush("["+ addChannel.remoteAddress() +"]" + "online...");
        channels.add(addChannel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel removeChannel = ctx.channel();
        channels.remove(removeChannel);
        channels.writeAndFlush("["+ removeChannel.remoteAddress() +"]" + "offline...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // 反序列化
            // 从对象中拿到msg
            // 解码(可能加盐)
            // 输出 并 广播
            Channel incoming = ctx.channel();
            log.debug("get msg from {} : {}", incoming.remoteAddress(), msg);

            for(Channel channel : channels) {
                if(!channel.equals(incoming)) {
                    channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + msg);
                } else {
                    channel.writeAndFlush("[you]" + msg);
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("{} online...", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("{} offline...", ctx.channel().remoteAddress());
    }

}
