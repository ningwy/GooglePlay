package io.github.ningwy.googleplay.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.github.ningwy.googleplay.utils.LogUtils;

/**
 * 线程管理类
 * Created by ningwy on 2016/9/11.
 */
public class ThreadManager {

    private static ThreadPool mThreadPool;

    public static ThreadPool getThreadPool() {
        if (mThreadPool == null) {
            synchronized (ThreadManager.class) {
                if (mThreadPool == null) {

                    int cpuCount = Runtime.getRuntime().availableProcessors();//得到cpu数量
                    LogUtils.e("cpuCount:" + cpuCount);
                    int corePoolSize = 10;

                    mThreadPool = new ThreadPool(corePoolSize, corePoolSize, 1L);
                }
            }
        }
        return mThreadPool;
    }


    public static class ThreadPool {

        //核心线程数
        private int corePoolSize;
        //总线程数
        private int maxPoolSize;
        //休息时间
        private long keepAliveTime;

        private ThreadPoolExecutor executor;

        private ThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maxPoolSize = maxPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        //从消息队列中取出线程来执行
        public void execute(Runnable r) {
            if (executor == null) {
                executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit
                        .SECONDS, new LinkedBlockingDeque<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
                // 参1:核心线程数;参2:最大线程数;参3:线程休眠时间;参4:时间单位;参5:线程队列;参6:生产线程的工厂;参7:线程异常处理策略
            }

            //执行线程池中的线程
            executor.execute(r);
        }

        //取消消息队列中的线程执行
        public void cancel(Runnable r) {
            if (executor != null) {
                executor.getQueue().remove(r);
            }
        }
    }

}
