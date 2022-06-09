package com.wuui.community;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @author Dee
 * @create 2022-06-01-23:35
 * @describe
 */
@SpringBootTest
@Slf4j
public class ThreadPoolTests {

    //JDK的普通线程池
    private ExecutorService executorService = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

    //JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(5);

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    private void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                log.info("ExecutorService正在执行......");
            }
        };

        for (int i = 0; i < 100; i++) {
            executorService.submit(task);
        }
        sleep(10000);
    }

    @Test
    public void testScheduledExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                log.info("ScheduledExecutorService正在执行......");
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(task, 1000, 2000, TimeUnit.MILLISECONDS);
        sleep(10000);
    }

    @Test
    public void testThreadPoolTaskExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                log.info("ThreadPoolTaskExecutor正在执行......");
            }
        };

        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }
        sleep(10000);
    }

    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                log.info("ThreadPoolTaskScheduler正在执行......");
            }
        };
        Date date = new Date(System.currentTimeMillis() + 10000);
        taskScheduler.scheduleAtFixedRate(task, date, 1000);
        sleep(130000);
    }

}
