package io.github.ningwy.googleplay.manager;

import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.github.ningwy.googleplay.domain.AppPageInfo;
import io.github.ningwy.googleplay.domain.DownloadInfo;
import io.github.ningwy.googleplay.http.HttpHelper;
import io.github.ningwy.googleplay.utils.IOUtils;
import io.github.ningwy.googleplay.utils.LogUtils;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 下载管理器
 * <p>
 * 五种状态：
 * -未下载
 * -准备下载
 * -下载中
 * -下载暂停
 * -下载失败
 * -下载完成
 * <p>
 * -下载管理器就是被观察者：需要提供方法通知下载状态和下载进度的改变
 * <p>
 * Created by ningwy on 2016/9/12.
 */
public class DownloadManager {

    public static final int STATE_UNDO = 1;
    public static final int STATE_READY = 2;
    public static final int STATE_DOWNLOADING = 3;
    public static final int STATE_PAUSE = 4;
    public static final int STATE_ERROR = 5;
    public static final int STATE_SUCCESS = 6;

    //观察者集合
    private List<DownloadObserver> mObservers = new ArrayList<>();

    //下载对象集合
//    private Map<String, DownloadInfo> mDownloadInfoMap = new HashMap<>();
    //用线程安全的ConcurrentHashMap
    private ConcurrentHashMap<String, DownloadInfo> mDownloadInfoMap = new ConcurrentHashMap<>();

//    private Map<String, DownloadTask> mDownloadTaskMap = new HashMap<>();
    private ConcurrentHashMap<String, DownloadTask> mDownloadTaskMap = new ConcurrentHashMap<>();

    //单例模式
    private static DownloadManager mDM = new DownloadManager();

    public static DownloadManager getInstance() {
        return mDM;
    }

    //下载
    public synchronized void download(AppPageInfo appInfo) {
        DownloadInfo downloadInfo = mDownloadInfoMap.get(appInfo.id);
        if (downloadInfo == null) {
            downloadInfo = DownloadInfo.copy(appInfo);
        }
        //添加到集合中
        mDownloadInfoMap.put(appInfo.id, downloadInfo);

        //改变状态和通知状态改变
        downloadInfo.currentState = STATE_READY;
        notifyDownloadStateChanged(downloadInfo);

        //初始化下载任务并添加到线程池中去执行
        DownloadTask task = new DownloadTask(downloadInfo);
        ThreadManager.getThreadPool().execute(task);

        //添加到下载任务集合中
        mDownloadTaskMap.put(appInfo.id, task);
    }

    //暂停下载
    public synchronized void pause(AppPageInfo appInfo) {
        DownloadInfo downloadInfo = mDownloadInfoMap.get(appInfo.id);
        if (downloadInfo != null) {
            //只有正在下载和等待下载时才需要暂停
            if (downloadInfo.currentState == STATE_DOWNLOADING || downloadInfo.currentState == STATE_READY) {
                //改变状态为暂停
                downloadInfo.currentState = STATE_PAUSE;
                //通知状态发生改变
                notifyDownloadStateChanged(downloadInfo);

                DownloadTask task = mDownloadTaskMap.get(appInfo.id);
                if (task != null) {
                    //取消任务
                    ThreadManager.getThreadPool().cancel(task);
                }
            }
        }
    }

    //安装
    public synchronized void install(AppPageInfo info) {
        DownloadInfo downloadInfo = mDownloadInfoMap.get(info.id);
        if (downloadInfo != null) {
            // 跳到系统的安装页面进行安装
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + downloadInfo.path),
                    "application/vnd.android.package-archive");
            UIUtils.getContext().startActivity(intent);
        }
    }

    class DownloadTask implements Runnable {

        private DownloadInfo downloadInfo;

        public DownloadTask(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void run() {
            // 状态切换为正在下载
            downloadInfo.currentState = STATE_DOWNLOADING;
            notifyDownloadStateChanged(downloadInfo);

            HttpHelper.HttpResult httpResult;

            File file = new File(downloadInfo.path);
            //判断是从头开始下载还是断点续传
            if (!file.exists() || file.length() != downloadInfo.currentPos || downloadInfo.currentPos == 0) {
                //从头开始下载
                file.delete();
                //注意就算文件不存在也是可以删除的，就是没有效果而已
                downloadInfo.currentPos = 0;//将下载进度置为0
                httpResult = HttpHelper.download(HttpHelper.URL + "download?name=" + downloadInfo.downloadUrl);

            } else {
                //断点续传
                httpResult = HttpHelper.download(HttpHelper.URL + "download?name=" + downloadInfo.downloadUrl +
                        "&range=" + file.length());
            }

            //将下载的文件写入到sdcard中
            if (httpResult != null && httpResult.getInputStream() != null) {
                InputStream is = httpResult.getInputStream();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file, true);//添加true表示在原有文件上继续写
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = is.read(buffer)) != -1 && downloadInfo.currentState == STATE_DOWNLOADING) {
                        fos.write(buffer, 0, len);

                        fos.flush();

                        //更新下载进度
                        downloadInfo.currentPos += len;
                        notifyDownloadProgressChanged(downloadInfo);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.close(fos);
                    IOUtils.close(is);
                }

                //文件下载开始后，根据下载状态通知所有观察者更新状态
                if (file.length() == downloadInfo.size) {
                    // 文件完整, 表示下载成功
                    downloadInfo.currentState = STATE_SUCCESS;
                    notifyDownloadStateChanged(downloadInfo);
                } else if (downloadInfo.currentState == STATE_PAUSE) {
                    // 中途暂停
                    notifyDownloadStateChanged(downloadInfo);
                } else {
                    // 下载失败
                    file.delete();// 删除无效文件
                    downloadInfo.currentState = STATE_ERROR;
                    LogUtils.e("写入失败");
                    downloadInfo.currentPos = 0;
                    notifyDownloadStateChanged(downloadInfo);
                }

            } else {
                //网络异常
                // 下载失败
                file.delete();// 删除无效文件
                downloadInfo.currentState = STATE_ERROR;
                LogUtils.e("网络异常");
                downloadInfo.currentPos = 0;
                notifyDownloadStateChanged(downloadInfo);
            }

            // 从集合中移除下载任务
            mDownloadTaskMap.remove(downloadInfo.id);
        }
    }

    //通知下载状态改变
    public void notifyDownloadStateChanged(DownloadInfo downloadInfo) {
        for (DownloadObserver observer : mObservers) {
            observer.onDownloadStateChanged(downloadInfo);
        }
    }

    //通知下载进度改变
    public void notifyDownloadProgressChanged(DownloadInfo downloadInfo) {
        for (DownloadObserver observer : mObservers) {
            observer.onDownloadProgressChanged(downloadInfo);
        }
    }

    public DownloadInfo getDownloadInfo(AppPageInfo info) {
        return mDownloadInfoMap.get(info.id);
    }

    //观察者接口
    public interface DownloadObserver {

        //下载状态改变的回调
        void onDownloadStateChanged(DownloadInfo downloadInfo);

        //下载进度改变的回调
        void onDownloadProgressChanged(DownloadInfo downloadInfo);
    }

    //注册监听
    public void registerDownloadObserver(DownloadObserver observer) {
        if (observer != null && !mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    //取消注册
    public void unRegisterDownloadObserver(DownloadObserver observer) {
        if (observer != null && mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

}
