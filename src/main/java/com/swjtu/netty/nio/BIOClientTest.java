package com.swjtu.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

@Slf4j
public class BIOClientTest {
    public static void main(String[] args) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.connect( new InetSocketAddress( InetAddress.getLocalHost(), 8888 ) );
        log.debug( "send..." );
    }
}
