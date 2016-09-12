package io.github.ningwy.googleplay.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import org.xutils.x;

/**
 * 自定义application
 * Created by ningwy on 2016/9/1.
 */
public class GooglePlayApplication extends Application {

    private static Context context;

    private static Handler handler;

    //主线程id
    private static int mainThreadId;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new Handler();
        mainThreadId = android.os.Process.myTid();

        //xuitls全局注入
        x.Ext.init(this);
    }

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }
}
