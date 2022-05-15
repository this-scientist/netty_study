package com.swjtu.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * server
 */
@Slf4j
public class NIOServerTest {
    public static void main(String[] args) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate( 10 );
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置为非阻塞
        serverSocketChannel.configureBlocking( false );
        serverSocketChannel.bind( new InetSocketAddress( 8888 ) );
        List<SocketChannel> scs = new ArrayList<>();
        while(true) {
            SocketChannel sc = serverSocketChannel.accept();
            if(sc != null) {
                log.debug( "connected from remote {}", sc );
                //设置为非阻塞
                sc.configureBlocking( false );
                scs.add( sc );
            }
            for(SocketChannel channel : scs) {
                StringBuilder sb = new StringBuilder();
                int n = channel.read( buf );
                if( n > 0 ) {
                    buf.flip();
                    log.info( "remaining: {}", buf.remaining() );
                    byte[] b = new byte[buf.remaining()];
                    buf.get(b);
                    log.info( "receive msg :{}", new String(b) );
                }
                buf.clear();
            }
        }
    }
}
