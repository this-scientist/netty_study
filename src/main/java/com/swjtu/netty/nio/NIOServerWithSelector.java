package com.swjtu.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * server
 */
@Slf4j
public class NIOServerWithSelector {
    public static void main(String[] args) throws IOException {
        // 1. 创建selector
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking( false );
        // 2.channel注册到selector 并返回selectionkey
        // 通过selectionkey可以知道那个channel有事件发生，相当于对注册过的channel的一个表示
        // 同时每个channel又关注不同的事件，所以要将selectiokey进行分类
        SelectionKey sscKey = serverSocketChannel.register( selector, 0, null );
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug( "register:", sscKey );
        serverSocketChannel.bind( new InetSocketAddress( 8888 ) );
        while(true) {
            log.debug( "selector begin select..." );
            // 阻塞, 原来nio会一直执行，就会导致没有事件的情况下CPU飙升
            selector.select();
            // selectedKeys是一个set集合（未处理事件的集合）
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                log.debug( "key:", key );
                ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                SocketChannel sc = channel.accept();
                log.debug( "remote host : {}", sc );
            }
        }
    }
}
