package com.other.thread;

/**
 * 线程安全：在并发时保证数据的正确性、效率尽可能高
 * synchronized
 * 1.同步方法
 * 2.同步块
 */
public class SynTest {
    public static void main(String[] args) {
        Account account=new Account(100,"礼金");
        Drawing you =new Drawing(account,80,"你");
        Drawing wife =new Drawing(account,90,"wife");
        you.start();
        wife.start();

    }
}

//账户
class Account{
    int money;
    String name;

    public Account(int money, String name) {
        this.money = money;
        this.name = name;
    }
}

class Drawing extends Thread{
    Account account;
    int drawingMoney;
    int packetTotal;
    public Drawing(Account account,int drawingMoney,String name){
        super();
        this.account=account;
        this.drawingMoney=drawingMoney;
    }
    @Override
    public void run() {
        test();
    }
    public void test(){

        //同步块
        synchronized(account){
            if (account.money-drawingMoney<0){
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            account.money-=drawingMoney;
            packetTotal+=drawingMoney;
            System.out.println(this.getName()+"-->账户余额为："+account.money);
            System.out.println(this.getName()+"-->口袋的钱为："+packetTotal);
        }
    }
}
