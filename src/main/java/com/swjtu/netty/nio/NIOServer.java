package com.swjtu.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * server
 * 缺点：
 * 1、遍历selectionkey集合的时候需要遍历之后删除  如果忘记了会报空指针异常
 * 2、对buf需要不断的切换 flip
 * 3、对于突然断开的客户端需要 捕获异常  并从selectionkey中删除对于的key
 * 4、对于正常断开连接的用户，也需要删除key  因为用户断开就是触发读事件
 */
@Slf4j
public class NIOServer {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking( false );
        SelectionKey sscKey = serverSocketChannel.register( selector, 0, null );
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug( "register:", sscKey );
        serverSocketChannel.bind( new InetSocketAddress( 8888 ) );
        while(true) {
            log.debug( "selector begin select..." );
            // 如果有新的事件就向selectionkey中添加key
            selector.select();
            // 遍历集合的时候进行删除需要使用迭代器
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                 // key处理完后需要删除  课：P30
                iterator.remove();
                if(key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    log.debug( "remote host : {}", sc );
                    sc.configureBlocking( false );
                    SelectionKey scKey = sc.register( selector, 0, null );
                    scKey.interestOps(SelectionKey.OP_READ);
                } else if(key.isReadable()) {
                    // 如果不使用try/catch 就会因为一个用户断开连接 报错
                    try {
                        // 因为要读数据了  所以需要建立缓存
                        ByteBuffer buf = ByteBuffer.allocate( 16 );
                        SocketChannel sc = (SocketChannel)key.channel();
                        int n = sc.read( buf );
                        if(n > 0) {
                            buf.flip();
                            byte[] b = new byte[buf.remaining()];
                            buf.get(b);
                            log.debug( "remote msg: {}", new String(b) );
                        } else if(n == -1) {
                            // 正常断开连接处理
                            log.debug( "disconnect from {}", sc );
                            key.cancel();
                        }
                        buf.clear();
                    } catch (IOException exception) {
                        // 对于用户异常断开连接需要进行处理 相当于read事件
                        log.debug( exception.getMessage() );
                        key.cancel();
                    }
                }

            }
        }
    }
}
