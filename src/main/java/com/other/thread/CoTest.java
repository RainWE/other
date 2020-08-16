package com.other.thread;

/**
 * 协作模式 ：生产者消费者实现方式一：管程法
 * 借助缓冲区
 */
public class CoTest {
    public static void main(String[] args) {
        SynContainers containers =new SynContainers();
        new Productor(containers).start();
        new Consumer(containers).start();

    }
}
//生产者
class Productor extends Thread{
    SynContainers containers;
    public Productor(SynContainers synContainers){
        this.containers=synContainers;
    }
    @Override
    public void run() {
        //生产
        for (int i=0;i<100;i++){
            System.out.println("生产-->"+i+"个商品");
            containers.push(new Commodity(i));
        }
    }
}
//消费者
class Consumer extends Thread{
    SynContainers containers;
    public Consumer(SynContainers synContainers){
        this.containers=synContainers;
    }
    @Override
    public void run() {
        //消费
        super.run();for (int i=0;i<101;i++){
            System.out.println("消费-->"+containers.pop().id+"个商品");
        }
    }
}
//缓冲区
class SynContainers{
    Commodity[] commodities= new Commodity[4];
    int count =0;
    //存储 生产
    public synchronized void push(Commodity commodity){
        //何时能生产 容器存在空间
        //不能生产 只有等待
        if (count==commodities.length){
            try {
                this.wait();//线程阻塞 消费者通知生产解除
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //存在空间可以生产
        commodities[count]=commodity;
        count++;
        this.notifyAll();//存在数据了，唤醒对方消费
    }
    //消费
    public synchronized Commodity pop(){
        //何时消费  容器中是否有数据
        //没有数据 等待
        if (count==0){
            try {
                this.wait();//线程阻塞 生产者通知消费解除
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        count--;
        //存在数据可以消费
        Commodity commodity = commodities[count];
        this.notifyAll();//存在空间，唤醒对方生产
        return commodity;
    }
}
//商品
class Commodity{
    int id;

    public Commodity(int id) {
        this.id = id;
    }
}