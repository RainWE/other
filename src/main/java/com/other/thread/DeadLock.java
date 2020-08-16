package com.other.thread;

/**
 * 死锁：过多的同步可能造成相互不释放资源
 * 从而相互等待，一般发生于同步中持有多个对象的锁
 *
 * 避免：不要在同一代码块中，同时持有多个对象的锁
 */
public class DeadLock {

    public static void main(String[] args) {
        Mark g1 = new Mark(1,"AA");
        Mark g2 = new Mark(2,"BB");
        g1.start();
        g2.start();
    }

}
class Lip{

}
class Mir{

}
class Mark extends Thread{
    static Lip lip=new Lip();
    static Mir mir = new Mir();

    int choice;
    String girl;
    public Mark(int choice,String girl){
        this.choice=choice;
        this.girl=girl;
    }
    @Override
    public void run() {
        markup();
    }
    //相互持有对方的对象锁-->可能造成死锁
    private void markup(){
        if (choice==0){
            synchronized (lip){
                System.out.println(this.girl+"获得lip");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /*
                synchronized (mir){
                    System.out.println(this.girl+"获得Mir");
                }
                */
            }
            synchronized (mir){
                System.out.println(this.girl+"获得Mir");
            }
        }else {
            synchronized (mir){
                System.out.println(this.girl+"获得Mir");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /*
                synchronized (lip){
                    System.out.println(this.girl+"获得lip");
                }*/
            }
            synchronized (lip){
                System.out.println(this.girl+"获得lip");
            }
        }
    }
}