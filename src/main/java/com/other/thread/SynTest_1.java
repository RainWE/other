package com.other.thread;

public class SynTest_1 {
    public static void main(String[] args) {
        SynWeb12306 web12306 = new SynWeb12306();
        new Thread(web12306, "AA").start();
        new Thread(web12306, "BB").start();
        new Thread(web12306, "CC").start();

    }


}

class SynWeb12306 implements Runnable {
    private int ticketNums = 10;
    private boolean flag = true;

    @Override
    public void run() {
        while (flag) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            test();
        }
    }

    //synchronized锁方法
    private synchronized void test() {
        if (ticketNums <= 0) {
            flag = false;
            return;
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "-->" + ticketNums--);
    }

    public void test2() {
        synchronized (this) {
            if (ticketNums <= 0) {
                flag = false;
                return;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "-->" + ticketNums--);
        }
    }

    public void test3() {
        if (ticketNums <= 0) {//考虑没票的情况下
            flag = false;
            return;
        }
        synchronized (this) {//考虑最后一张票
            if (ticketNums <= 0) {
                flag = false;
                return;
            }
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "-->" + ticketNums--);
    }
}
