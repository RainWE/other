package com.other.thread;

/**
 * 守护线程：是为用户线程服务的；jvm停止不用等待守护线程执行完毕
 * 默认:用户线程；jvm等待用户线程执行完毕才会停止
 */
public class DaemonTest {
    public static void main(String[] args) {
        God god=new God();
        You you=new You();
        Thread t=new Thread(god);
        t.setDaemon(true);//将用户线程调整为守护线程
        t.start();
        new Thread(you).start();//you线程执行完后，god线程就会结束
    }
}
class You extends Thread{
    @Override
    public void run() {
        for (int i=1;i<365*100;i++){
            System.out.println("happy life...");
        }
        System.out.println("oooooo...");
    }
}
class God extends Thread{
    @Override
    public void run() {
        for (;true;){
            System.out.println("bless you ..");
        }
    }
}