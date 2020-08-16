package com.other.thread;

/**
 * yield礼让线程，暂停线程，直接进入就绪状态不是阻塞状态
 */
public class YieldThread {

    public static void main(String[] args) {
        MyYield my =new MyYield();
        new Thread(my,"a").start();;
        new Thread(my,"b").start();;
    }
}
class MyYield implements Runnable{
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+"-->start");
        Thread.yield();//礼让
        System.out.println(Thread.currentThread().getName()+"-->end");

    }
}