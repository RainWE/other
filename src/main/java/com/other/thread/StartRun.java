package com.other.thread;


/**
 * 创建线程方式二:
 * 1.创建：继承Runnable-->重写run()
 * 2.启动：创建实现类对象-->Thread对象+start()
 */
public class StartRun implements Runnable{

    public static void main(String[] args) {
        //创建实现类对象
        StartRun startRun =new StartRun();
        //创建代理类对象
        Thread thread = new Thread(startRun);
        //启动
        thread.start();//不保证线程中的方法立即运行，看cpu调用
//        startThread.run();//普通方法调用
        //简写
        Thread simpleWrite = new Thread(new StartRun());
        simpleWrite.run();
        //或者
        new Thread(new StartRun()).start();
        for (int i=0;i<20;i++){
            System.out.println("一边打代码");
        }
    }

    /**
     * 线程入口点
     */
    @Override
    public void run() {
        for (int i=0;i<20;i++){
            System.out.println("一边听歌");
        }
    }

}
