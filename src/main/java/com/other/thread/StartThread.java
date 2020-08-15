package com.other.thread;


/**
 * 创建线程方式一:
 * 1.创建：继承Thread-->重写run()
 * 2.启动：创建子类对象-->start()
 */
public class StartThread extends Thread{

    public static void main(String[] args) {
        //创建子类对象——StartThread
        StartThread startThread =new StartThread();
        startThread.start();//不保证线程中的方法立即运行，看cpu调用
//        startThread.run();//普通方法调用
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
