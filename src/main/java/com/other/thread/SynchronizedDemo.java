package com.other.thread;
/**
 * 先定义一个测试模板类
 *     这里补充一个知识点：Thread.sleep(long)不会释放锁
 *     读者可参见笔者的`并发番@Thread一文通`
 */
public class SynchronizedDemo {

    /**普通方法与同步方法调用互不关联
     * 当一个线程进入同步方法时，其他线程可以正常访问其他非同步方法
     * 普通方法和同步方法是非阻塞执行的
     * @param args
     */
 /*   public static void main(String[] args) {
        SynchronizedDemo synDemo = new SynchronizedDemo();
        Thread thread1 = new Thread(() -> {
            //调用普通方法
            synDemo.method();
        });
        Thread thread2 = new Thread(() -> {
            //调用同步方法
            synDemo.synMethod();
        });
        thread1.start();
        thread2.start();
    }
*/

    /**所有同步方法只能被一个线程访问
     *当一个线程执行同步方法时，其他线程不能访问任何同步方法
     * 通过结果可知，任务的执行是阻塞的，显然Thread-1必须等待Thread-0执行完毕之后才能继续执行
     * @param args
     */
 /*   public static void main(String[] args) {
        SynchronizedDemo synDemo = new SynchronizedDemo();
        Thread thread1 = new Thread(() -> {
            synDemo.synMethod();
            synDemo.synMethod2();
        });
        Thread thread2 = new Thread(() -> {
            synDemo.synMethod2();
            synDemo.synMethod();
        });
        thread2.start();
        thread1.start();
    }
  */

    /**同一个锁的同步代码块同一时刻只能被一个线程访问
     *当同步代码块都是同一个锁时，方法可以被所有线程访问，但同一个锁的同步代码块同一时刻只能被一个线程访问
     *即使普通方法有同步代码块，但方法的访问是非阻塞的，任何线程都可以自由进入
     *对于同一个锁的同步代码块的访问一定是阻塞的
     * @param args
     */
 /*   public static void main(String[] args) {
        SynchronizedDemo synDemo = new SynchronizedDemo();
        Thread thread1 = new Thread(() -> {
            //调用同步块方法
            synDemo.chunkMethod();
            synDemo.chunkMethod2();
        });
        Thread thread2 = new Thread(() -> {
            //调用同步块方法
            synDemo.chunkMethod();
            synDemo.chunkMethod2();
        });
        thread1.start();
        thread2.start();
    }

  */

    /**线程间同时访问同一个锁的多个同步代码的执行顺序不定
     * 线程间同时访问同一个锁多个同步代码的执行顺序不定，即使是使用同一个对象锁，这点跟同步方法有很大差异
     * 现象：虽然是同一个lock对象，但其不同代码块的访问是非阻塞的
     * 原因：根源在于锁的释放和重新竞争，当Thread-0访问完chunkMethod方法后会先释放锁，
     * 这时Thread-1就有机会能获取到锁从而优先执行，依次类推到24行、25行时，Thread-0又重新获取到锁优先执行了
     * 注意：但有一点是必须的，对于同一个锁的同步代码块的访问一定是阻塞的
     * 补充：同步方法之所有会被全部阻塞，是因为synDemo对象一直被线程在内部把持住就没释放过，论把持住的重要性！
     * @param args
     */
  /*  public static void main(String[] args) {
        SynchronizedDemo synDemo = new SynchronizedDemo();
        Thread thread1 = new Thread(() -> {
            //调用同步块方法
            synDemo.chunkMethod();
            synDemo.chunkMethod2();
        });
        Thread thread2 = new Thread(() -> {
            //调用同步块方法
            synDemo.chunkMethod2();
            synDemo.chunkMethod();
        });
        thread1.start();
        thread2.start();
    }

   */

    /**不同锁之间访问非阻塞
     *由于三种使用方式的锁对象都不一样，因此相互之间不会有任何影响
     * 但有两种情况除外：
     * 1.当同步代码块使用的Class对象和类对象一致时属于同一个锁，遵循上面的原则
     * 2.当同步代码块使用的是this，即与同步方法使用锁属于同一个锁，遵循上面的原则
     * 现象：虽然是同一个lock对象，但其不同代码块的访问是非阻塞的
     * 原因：根源在于锁的释放和重新竞争，当Thread-0访问完chunkMethod方法后会先释放锁，
     * 这时Thread-1就有机会能获取到锁从而优先执行，依次类推到24行、25行时，Thread-0又重新获取到锁优先执行了
     * @param args
     */
  /*  public static void main(String[] args) {
        SynchronizedDemo synDemo = new SynchronizedDemo();
        Thread thread1 = new Thread(() -> synDemo.chunkMethod() );
        Thread thread2 = new Thread(() -> synDemo.chunkMethod3());
        Thread thread3 = new Thread(() -> staticMethod());
        Thread thread4 = new Thread(() -> staticMethod2());
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }

   */

    /**Synchronized的可重入性
     *重入锁：当一个线程再次请求自己持有对象锁的临界资源时，这种情况属于重入锁，请求将会成功.
     * 实现：一个线程得到一个对象锁后再次请求该对象锁，是允许的，每重入一次，monitor进入次数+1.
     * 在代码块中继续调用了当前实例对象的另外一个同步方法，再次请求当前实例锁时，将被允许，进而执行方法体代码，这就是重入锁最直接的体现
     * @param args
     */
 /*   public static void main(String[] args) {
        SynchronizedDemo synDemo = new SynchronizedDemo();
        Thread thread1 = new Thread(() -> {
            synDemo.synMethod();
            synDemo.synMethod2();
        });
        Thread thread2 = new Thread(() -> {
            synDemo.synMethod2();
            synDemo.synMethod();
        });
        thread1.start();
        thread2.start();
    }

  */

    /**Synchronized与String锁
     *隐患：由于在JVM中具有String常量池缓存的功能，因此相同字面量是同一个锁！！！
     * 注意：严重不推荐将String作为锁对象，而应该改用其他非缓存对象
     * 提示：对字面量有疑问的话请先回顾一下String的基础，这里不加以解释
     * //分析：输出结果永远都是Thread-0的死循环，也就是说另一个线程，即Thread-1线程根本不会运行
     * //原因：同步块中的锁是同一个字面量
     * @param args
     */
/*    public static void main(String[] args) {
        SynchronizedDemo synDemo = new SynchronizedDemo();
        Thread thread1 = new Thread(() -> synDemo.stringMethod("sally"));//同一个锁
        Thread thread2 = new Thread(() -> synDemo.stringMethod("sally"));//同一个锁
        Thread thread3 = new Thread(() -> synDemo.stringMethod("sally1"));//不是同一个锁
        thread1.start();
        thread2.start();
        thread3.start();
    }

 */

    public static synchronized void staticMethod(){
        System.out.println(Thread.currentThread().getName() + "访问了静态同步方法staticMethod");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "结束访问静态同步方法staticMethod");
    }
    public static void staticMethod2(){
        System.out.println(Thread.currentThread().getName() + "访问了静态同步方法staticMethod2");
        synchronized (SynchronizedDemo.class){
            System.out.println(Thread.currentThread().getName() + "在staticMethod2方法中获取了SynchronizedDemo.class");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized void synMethod(){
        System.out.println(Thread.currentThread().getName() + "访问了同步方法synMethod");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "结束访问同步方法synMethod");
    }
    public synchronized void synMethod2(){
        System.out.println(Thread.currentThread().getName() + "访问了同步方法synMethod2");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "结束访问同步方法synMethod2");
    }
    public void method(){
        System.out.println(Thread.currentThread().getName() + "访问了普通方法method");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "结束访问普通方法method");
    }
    private Object lock = new Object();
    public void chunkMethod(){
        System.out.println(Thread.currentThread().getName() + "访问了chunkMethod方法");
        synchronized (lock){
            System.out.println(Thread.currentThread().getName() + "在chunkMethod方法中获取了lock");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void chunkMethod2(){
        System.out.println(Thread.currentThread().getName() + "访问了chunkMethod2方法");
        synchronized (lock){
            System.out.println(Thread.currentThread().getName() + "在chunkMethod2方法中获取了lock");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void chunkMethod3(){
        System.out.println(Thread.currentThread().getName() + "访问了chunkMethod3方法");
        //同步代码块
        synchronized (this){
            System.out.println(Thread.currentThread().getName() + "在chunkMethod3方法中获取了this");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void stringMethod(String lock){
        synchronized (lock){
            while (true){
                System.out.println(Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}