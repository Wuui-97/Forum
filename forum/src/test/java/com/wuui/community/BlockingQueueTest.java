package com.wuui.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dee
 * @create 2022-05-23-21:20
 * @describe
 */
public class BlockingQueueTest {

    public static void main(String[] args) {
        //有界队列
        BlockingQueue<Integer> queue = new LinkedBlockingDeque<>(10);
        //生产者1号
        new Thread(new Producer(queue)).start();

        //消费者1、2 、3、4号
        new Thread(new Customer(queue)).start();
        new Thread(new Customer(queue)).start();
        new Thread(new Customer(queue)).start();
        new Thread(new Customer(queue)).start();
    }

}
//生产者
class Producer implements Runnable{
    //阻塞队列
    private BlockingQueue<Integer> queue;
    //原子类，防止线程安全问题
    private AtomicInteger count = new AtomicInteger();

    public Producer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            //模拟业务，每隔1000ms生产一个产品
            Thread.sleep(20);
            for(int i = 0; i < 100; i++){
                //生产产品
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产了：" + queue.size());
            }


        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
//消费者
class Customer implements Runnable{
    //阻塞队列
    private BlockingQueue<Integer> queue;

    public Customer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }


    @Override
    public void run() {
        try{
            while(true){
                //模拟业务，随机睡眠0-1000ms
                Thread.sleep(new Random().nextInt(1000));
                //消费产品
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费了：" + queue.size());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
