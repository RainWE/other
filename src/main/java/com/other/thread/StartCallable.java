package com.other.thread;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 创建线程方式一:
 * 1.创建：继承Thread-->重写run()
 * 2.启动：创建子类对象-->start()
 */
public class StartCallable implements Callable<Boolean> {
    /**实现call方法
     * 线程入口点
     */
    @Override
    public Boolean call() throws Exception {
        for (int i=0;i<20;i++){
            System.out.println("一边听歌");
        }
        return true;
    }

    public static void main(String[] args) throws Exception{
        //创建执行服务：
        ExecutorService ser = Executors.newFixedThreadPool(3);
        //提交执行：
        Future<Boolean> res=ser.submit(new StartCallable());
        //获取结果：
        boolean rb = res.get();
        //关闭服务：
        ser.shutdownNow();

        for (int i=0;i<20;i++){
            System.out.println("一边打代码");
        }
    }


}
