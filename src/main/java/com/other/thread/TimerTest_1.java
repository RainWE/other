package com.other.thread;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 任务调度：Timer和TimerTask类
 */
public class TimerTest_1 {

    public static void main(String[] args) {
        Timer timer = new Timer();
        //执行安排
        //在指定的延迟之后安排指定的任务执行
//        timer.schedule(new MyTask(),1000);//1s后
        //在指定的延迟之后开始 ，重新执行 固定延迟执行的指定任务。
        timer.schedule(new MyTask(),5000,2000);//第一次5s后执行，后面每隔两秒执行一次

    }
}
//任务类
class MyTask extends TimerTask{
    @Override
    public void run() {
        for (int i=0;i<10;i++){
            System.out.println("放空大脑休息一会");
        }
        System.out.println("结束----");
    }
}
