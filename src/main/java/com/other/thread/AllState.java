package com.other.thread;

public class AllState {
    public static void main(String[] args) {
        Thread t= new Thread(()->{
            for (int i=0;i<5;i++){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("---");
            }
        });
        //观察状态 NEW, RUNNABLE,BLOCKED,WAITING,TIMED_WAITING,TERMINATED;
        Thread.State state=t.getState();
        System.out.println(state);//NEW

        t.start();
        state=t.getState();
        System.out.println(state);//RUNNABLE


        while (state!=Thread.State.TERMINATED){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            state=t.getState();
            System.out.println(state);//TIMED_WAITING
        }
    }
}
