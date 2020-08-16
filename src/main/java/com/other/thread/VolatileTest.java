package com.other.thread;


/**
 * volatile用于保证数据的同步，也就是可见性
 */
public class VolatileTest {
    private volatile static int num =0;
    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            while (num==0){

            }
        }).start();
        Thread.sleep(100);
        num=1;
    }
}
