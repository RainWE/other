package com.other.thread;

public class SynchronizedDemo1 {
    static Integer i = 0;   //Integer持有final int value属性

    /**Synchronized与不可变锁
     *不可变类: 不可变类指的是创建类实例后，其属性值不可变 -> 即f inal属性不可变，
     * 所有包装类型及字符串皆为不可变类，内部都维护一个final属性表示自身值
     * 隐患：当使用不可变类对象作为对象锁时，使用synchronized同样会有并发问题
     * 原因：由于不可变特性，当作为锁但同步块内部仍然有计算操作，会生成一个新的锁对象
     * 注意：严重不推荐将不可变类作为锁对象时仍对其有计算操作
     * 分析：跟预想中的20000不一致，当使用Integer作为对象锁时但还有计算操作就会出现并发问题
     * @param args
     * @throws InterruptedException
     */
  /*  public static void main(String[] args) throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int j = 0;j<10000;j++){
                    synchronized (i){
                        i++;
                    }
                }
            }
        };
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(i);
    }
    */

    /**Synchronized与死锁
     * 死锁：当线程间需要相互等待对方已持有的锁时，就形成死锁，进而产生死循环
     * 注意： 代码中严禁出现死锁！！！
     * 分析：线程0获得lock锁，线程1获得lock2锁，但之后由于两个线程还要获取对方已持有的锁，
     * 但已持有的锁都不会被双方释放，线程"假死"，无法往下执行，从而形成死循环，即死锁，之后一直在做无用的死循环，严重浪费系统资源
     * @param args
     */
 /*   public static void main(String[] args) {
        Object lock = new Object();
        Object lock2 = new Object();
        Thread thread1 = new Thread(() -> {
            synchronized (lock){
                System.out.println(Thread.currentThread().getName() + "获取到lock锁");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2){
                    System.out.println(Thread.currentThread().getName() + "获取到lock2锁");
                }
            }
        });
        Thread thread2 = new Thread(() -> {
            synchronized (lock2){
                System.out.println(Thread.currentThread().getName() + "获取到lock2锁");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock){
                    System.out.println(Thread.currentThread().getName() + "获取到lock锁");
                }
            }
        });
        thread1.start();
        thread2.start();
    }
    */

}