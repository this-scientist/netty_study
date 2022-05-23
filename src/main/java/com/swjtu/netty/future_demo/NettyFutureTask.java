package com.swjtu.netty.future_demo;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class NettyFutureTask {
    public static void main(String[] args) {
        // EventLoopGroup 就是netty中的线程池，同时需要注意一个eventloop就是一个单线程
        NioEventLoopGroup workers = new NioEventLoopGroup();
        EventLoop next = workers.next();
        // 这里的future 是 io.netty.util.concurrent.Future;
        Future<Integer> f = next.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                return 10;
            }
        });
        f.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.debug("ayn res: {}", future.getNow());
            }
        });
        log.debug("异步获取结果哦~ main先执行到这了");
    }
}
