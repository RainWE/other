package com.other.thread;

public class UnsafeTest {
    public static void main(String[] args) {
        Account account=new Account(100,"礼金");
        Drawing you =new Drawing(account,80,"你");
        Drawing wife =new Drawing(account,90,"wife");
        you.start();
        wife.start();

    }
}
//账户
class Account1{
    int money;
    String name;

    public Account1(int money, String name) {
        this.money = money;
        this.name = name;
    }
}

class Drawing1 extends Thread{
    Account account;
    int drawingMoney;
    int packetTotal;
    public Drawing1(Account account,int drawingMoney,String name){
        super();
        this.account=account;
        this.drawingMoney=drawingMoney;
    }


    @Override
    public void run() {
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
