package com.other.thread;

public class HappenBefore {
    private static int a = 0;
    private static boolean flag = false;

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            Thread t1 = new Thread(() -> {
                a = 1;
                flag = true;
            });
            Thread t2 = new Thread(() -> {
                if (flag) {
                    a *= 1;
                }
                if (a == 0) {
                    System.out.println("happen before a->" + a);//happen before a->1 发生指令重排
                }
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();
        }
    }
}
