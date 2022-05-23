package com.swjtu.netty.chat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Scanner;

@Slf4j
public class ChatClientEventHandler extends SimpleChannelInboundHandler {
    /**
     * 一旦channel收到消息
     * @param ctx
     * @param o
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {

        try{
            // 反序列化
            // 从对象中拿到msg
            // 解码(可能加盐)
            // 输出 并 广播
            log.debug("get msg from {} : {}", ctx.channel().remoteAddress(), o);
        } finally {

        }
    }

    /**
     * 一旦连接建立
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("连接那建立{}", ctx.channel().remoteAddress());
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(true) {
                if(scanner.hasNextLine()) {
                    String msg = scanner.nextLine();
                    if(msg.equals("q")) {
                        ctx.channel().close();
                        break;
                    } else {
                        ctx.channel().writeAndFlush(msg);
                    }
                }
            }
        }, "input" ).start();
    }



}
