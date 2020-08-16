package com.other.thread;


/**
 * 可重入锁:锁可以延续使用
 */
public class LockTest {

    public void test(){
        synchronized (this){
            while (true) {
                synchronized (this) {
                    System.out.println("ReetrantLock");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        LockTest lockTest =new LockTest();
        lockTest.test();
    }
}
