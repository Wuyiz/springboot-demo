package com.example.temp;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QuickTest {

    private Long module_10(int finalI) {
        //取hash
        String key = String.valueOf(finalI);
        long hashCode = key.hashCode();
        long hash = hashCode ^ (hashCode >>> 16);
        long index = hash % 10;
        return index;
    }

    private Long module_8(int finalI) {
        //数量为2的幂次方(2,8,16,32)
        int queueSize = 8;
        //取hash
        String key = String.valueOf(finalI);
        long hashCode = key.hashCode();
        long hash = hashCode ^ (hashCode >>> 32);
        long index = (queueSize - 1) & hash;
        return index;
    }


    @Test
    void hashTest() {
        //取模方法一
        long bt = System.currentTimeMillis();
        final int count = 100000;
        ExecutorService pool = Executors.newFixedThreadPool(8);
        // 判断线程是否全部执行结束
        CountDownLatch countDownLatch = new CountDownLatch(count);
        Map<Long, Integer> occurrences = new ConcurrentHashMap<>(16);
        for (int i = 0; i < count; i++) {
            int finalI = i;
            pool.execute(() -> {
                try {
                    // Long index = module_10(finalI);
                    Long index = module_8(finalI);
                    occurrences.compute(index, (k, v) -> {
                        if (v == null) {
                            v = 1;
                        } else {
                            v += 1;
                        }
                        return v;
                    });
                    double sum = occurrences.values().stream().mapToDouble(s -> s).sum();
                    double per = sum / count;
                    if (per >= 0.15555 && per < 0.15557) {
                        System.out.println(per + " => " + occurrences);
                    } else if (per >= 0.35555 && per < 0.35557) {
                        System.out.println(per + " => " + occurrences);
                    } else if (per >= 0.55555 && per < 0.55557) {
                        System.out.println(per + " => " + occurrences);
                    } else if (per >= 0.75555 && per < 0.75557) {
                        System.out.println(per + " => " + occurrences);
                    } else if (per >= 0.95555 && per < 0.95557) {
                        System.out.println(per + " => " + occurrences);
                    }
                    //StringBuilder str = new StringBuilder();
                    //str.append("key => ").append(key).append(" hashCode => ").append(hashCode);
                    //str.append(" hash => ").append(hash).append("(").append(hashCode >>> 32).append(")");
                    //str.append(" index => ").append(index);
                    //System.out.println(str.toString());
                } catch (Exception e) {
                    System.out.println("线程池异常" + e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            // 等待线程全部执行结束
            countDownLatch.await();
        } catch (Exception e) {
            System.out.println("countDownLatch.await" + e.getMessage());
        } finally {
            // 关闭线程池
            pool.shutdown();
        }
        long et = System.currentTimeMillis();
        System.out.println(occurrences);
        System.out.println(occurrences.values().stream().mapToInt(s -> s).sum());
        System.out.println("[1]耗时:" + (et - bt) + "ms");
    }

    @Test
    void mapTableSizeTest() {
        long bt = System.currentTimeMillis();
        int count = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            int var1 = i - 1;
            var1 |= var1 >>> 1;
            var1 |= var1 >>> 2;
            var1 |= var1 >>> 4;
            var1 |= var1 >>> 8;
            var1 |= var1 >>> 16;
            // System.out.println(var1);
            // System.out.println(var1 < 0 ? 1 : (var1 >= 1073741824 ? 1073741824 : var1 + 1));
            countDownLatch.countDown();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.out.println("countDownLatch.await" + e.getMessage());
        }
        long et = System.currentTimeMillis();
        System.out.println("[1]耗时:" + (et - bt) + "ms");
    }






    @Test
    void parallelStreamTest() {
        System.out.println("Hello World!");
        // 构造一个10000个元素的集合
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(i);
        }
        // 统计并行执行list的线程
        Set<Thread> threadSet = new CopyOnWriteArraySet<>();
        // 并行执行
        list.parallelStream().forEach(integer -> {
            Thread thread = Thread.currentThread();
            // System.out.println(thread);
            // 统计并行执行list的线程
            threadSet.add(thread);
        });
        System.out.println("threadSet一共有" + threadSet.size() + "个线程");
        System.out.println("系统一个有" + Runtime.getRuntime().availableProcessors() + "个cpu");
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            list1.add(i);
            list2.add(i);
        }
        Set<Thread> threadSetTwo = new CopyOnWriteArraySet<>();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Thread threadA = new Thread(() -> {
            list1.parallelStream().forEach(integer -> {
                Thread thread = Thread.currentThread();
                // System.out.println("list1" + thread);
                threadSetTwo.add(thread);
            });
            countDownLatch.countDown();
        });
        Thread threadB = new Thread(() -> {
            list2.parallelStream().forEach(integer -> {
                Thread thread = Thread.currentThread();
                // System.out.println("list2" + thread);
                threadSetTwo.add(thread);
            });
            countDownLatch.countDown();
        });

        threadA.start();
        threadB.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.print("threadSetTwo一共有" + threadSetTwo.size() + "个线程");

        System.out.println("---------------------------");
        System.out.println(threadSet);
        System.out.println(threadSetTwo);
        System.out.println("---------------------------");
        threadSetTwo.addAll(threadSet);
        System.out.println(threadSetTwo);
        System.out.println("threadSetTwo一共有" + threadSetTwo.size() + "个线程");
        System.out.println("系统一个有" + Runtime.getRuntime().availableProcessors() + "个cpu");
    }

    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    @Test
    void threadLocalTest() {
        threadLocal.set("testVal");
        new Thread(() -> {
            System.out.println(threadLocal.get());
        }).start();
        System.out.println("线程中的本地变量值:" + threadLocal.get());
    }

    public int inc = 0;
    public AtomicInteger atInc = new AtomicInteger();
    Lock lock = new ReentrantLock();

    public void increase() {
        atInc.getAndIncrement();
    }

    public synchronized void increase1() {
        inc++;
    }

    public void increase2() {
        lock.lock();
        try {
            inc++;
        } finally {
            lock.unlock();
        }
    }

    @Test
    void volatileTest1() {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        TimeInterval timer = DateUtil.timer();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    increase();
                    // increase1();
                    // increase2();
                }
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("花费时间：" + timer.interval() + "ms");
        System.out.println(inc);
        System.out.println(atInc);
    }

    final Object object = new Object();

    void eatBaozi() {
        synchronized (this) {
            System.out.println("我想吃包子");
            System.out.println("3:" + Thread.currentThread().getName());
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("我吃到包子了");
            countDownLatch.countDown();
        }
    }

    CountDownLatch countDownLatch = new CountDownLatch(1);

    void makeBaozi() {
        synchronized (this) {
            System.out.println("包子做好了");
            this.notify();
        }
    }

    @Test
    void waitAndNotifyTest() {
        Thread t1 = new Thread(() -> {
            System.out.println("1:" + Thread.currentThread().getName());
            eatBaozi();
        });
        Thread t2 = new Thread(() -> {
            System.out.println("2:" + Thread.currentThread().getName());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            makeBaozi();
        });
        t1.start();
        t2.start();
        System.out.println("t1: " + t1.getName() + t1.isDaemon());
        System.out.println("t2: " + t2.getName() + t2.isDaemon());
        System.out.println("主线程结束");
        // try {
        //     countDownLatch.await();
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
    }
}
