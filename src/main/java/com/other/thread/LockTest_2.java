package com.other.thread;


import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁:锁可以延续使用+计数器
 */
public class LockTest_2 {
    Lock_2 lock = new Lock_2();
    public void  a() throws InterruptedException {
        lock.lock();
        System.out.println(lock.getHoldCount());
        b();
        lock.unclock();
        System.out.println(lock.getHoldCount());

    }
    //不可重入
    public void b() throws InterruptedException {
        lock.lock();
        System.out.println(lock.getHoldCount());
        //..
        lock.unclock();
        System.out.println(lock.getHoldCount());

    }
    public static void main(String[] args) throws InterruptedException {
        LockTest_2 test_1 = new LockTest_2();
        test_1.a();
        test_1.b();
        Thread.sleep(1000);
        System.out.println(test_1.lock.getHoldCount());


        //具体实现类
        ReentrantLock lock = new ReentrantLock();
    }
}

//可重入锁
class Lock_2{
    //是否占用
    private boolean isLocked= false;
    private Thread lockedBy=null;//存储线程
    private int holdCount=0;

    //使用锁
    public synchronized void lock() throws InterruptedException {
        Thread t = Thread.currentThread();
        while (isLocked && lockedBy!=t){
            wait();
        }
        isLocked=true;
        lockedBy=t;
        holdCount++;
    }
    //释放锁
    public synchronized void unclock() throws InterruptedException {
        if (Thread.currentThread()==lockedBy){
            holdCount--;
            if (holdCount==0){
                isLocked=false;
                notify();
                lockedBy=null;
            }
        }

    }

    public int getHoldCount() {
        return holdCount;
    }


}