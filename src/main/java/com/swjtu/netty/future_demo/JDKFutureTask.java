package com.swjtu.netty.future_demo;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 1、建立线程池
 * 2、提交callable任务到线程池
 * 3、通过future对象去获取任务结果
 */
@Slf4j
public class JDKFutureTask {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> f = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(100);
                return 10;
            }
        });
        log.debug("等待结果");
        // get  方法阻塞主线程获取结果
        log.debug("res: {}", f.get());
        log.debug("syn 执行哦  主线程等待get");
    }
}
