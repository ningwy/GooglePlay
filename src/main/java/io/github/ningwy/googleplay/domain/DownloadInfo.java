package io.github.ningwy.googleplay.domain;

import android.os.Environment;

import java.io.File;

import io.github.ningwy.googleplay.manager.DownloadManager;
import io.github.ningwy.googleplay.utils.LogUtils;

/**
 * 下载信息对象的封装
 * Created by ningwy on 2016/9/12.
 */
public class DownloadInfo {

    public String id;
    public String name;
    public String packageName;
    public long size;
    public String downloadUrl;

    public long currentPos;//下载位置
    public int currentState;//下载状态
    public String path;//下载路径

    private static final String GOOGLE_MARKET = "googlePlay";
    private static final String DOWNLOAD = "download";

    //从AppInfo复制出一个DownloadInfo的对象
    public static DownloadInfo copy(AppPageInfo appInfo) {
        DownloadInfo downloadInfo = new DownloadInfo();

        downloadInfo.id = appInfo.id;
        downloadInfo.name = appInfo.name;
        downloadInfo.packageName = appInfo.packageName;
        downloadInfo.size = appInfo.size;
        downloadInfo.downloadUrl = appInfo.downloadUrl;

        downloadInfo.currentPos = 0;//默认从0开始
        downloadInfo.currentState = DownloadManager.STATE_UNDO;//默认undo状态
        downloadInfo.path = downloadInfo.getFilePath();

        LogUtils.e("path:" + downloadInfo.path);

        return downloadInfo;
    }

    //获取下载进度
    public float getProgress() {
        if (size == 0) {
            return 0;
        }
        return currentPos / (float) size;
    }

    //获取文件路径
    public String getFilePath() {

        StringBuffer sb = new StringBuffer();
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        sb.append(sdcard);
        // sb.append("/");
        sb.append(File.separator);
        sb.append(GOOGLE_MARKET);
        sb.append(File.separator);
        sb.append(DOWNLOAD);

        if (createDir(sb.toString())) {
            // 文件夹存在或者已经创建完成
            return sb.toString() + File.separator + name + ".apk";// 返回文件路径
        }

        return null;
    }

    //创建文件夹
    public boolean createDir(String dir) {

        File file = new File(dir);

        //如果文件不存在或者文件不是一个文件夹
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdirs();//创建文件夹
            /**
             * 注意：此处不可用mkdir()方法，mkdirs()方法会在上一级文件夹如果不在的时候把上一级的文件夹也创建了
             * 而mkdir()方法只会创建路径指定的文件夹，如果上一级文件夹不存在，则会创建失败
             */
        }

        //如果文件存在则表示已经创建完成
        return true;


    }
}
