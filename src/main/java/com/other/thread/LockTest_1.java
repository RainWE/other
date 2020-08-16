package com.other.thread;


/**
 * 不可重入锁:锁不可以延续使用
 */
public class LockTest_1 {
    Lock lock = new Lock();
    public void  a() throws InterruptedException {
        lock.lock();
        b();
        lock.unclock();
    }
    //不可重入
    public void b() throws InterruptedException {
        lock.lock();
        //..
        lock.unclock();
    }
    public static void main(String[] args) throws InterruptedException {
        LockTest_1 test_1 = new LockTest_1();
        test_1.a();
        test_1.b();
    }
}
//不可重入锁
class Lock{
    //是否占用
    private boolean isLocked= false;
    //使用锁
    public synchronized void lock() throws InterruptedException {
        while (isLocked){
            wait();
        }
        isLocked=true;
    }
    //释放锁
    public synchronized void unclock() throws InterruptedException {
        isLocked=false;
        notify();
    }
}