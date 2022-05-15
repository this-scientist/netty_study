package com.swjtu.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * server
 */
@Slf4j
public class BIOServerTest {
    public static void main(String[] args) throws IOException {
        //写缓存
        ByteBuffer buf = ByteBuffer.allocate( 10 );
        //1、创建服务器
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //2、绑定监听端口
        serverSocketChannel.bind( new InetSocketAddress( 8888 ) );
        List<SocketChannel> scs = new ArrayList<>();
        //3、循环监听端口  并根据请求建立连接
        while(true) {
            log.debug( "connecting..." );
            //接收连接请求是阻塞的
            SocketChannel sc = serverSocketChannel.accept();
            if(sc != null) {
                log.debug( "connected from remote {}", sc );
                scs.add( sc );
            }
            //对建立连接的数据通道中的数据进行处理
            for(SocketChannel channel : scs) {
                StringBuilder sb = new StringBuilder();
                int n = channel.read( buf );
                //如果buf中有数据则切换为读模式，并读数据
                if( n > 0 ) {
                    buf.flip();
                    for(int i = 0; i < n; i++) {
                        sb.append( (char) buf.get() );
                    }
                    log.info( "receive msg :{}", sb.toString() );
                }
                buf.clear();
            }
        }
    }
}
