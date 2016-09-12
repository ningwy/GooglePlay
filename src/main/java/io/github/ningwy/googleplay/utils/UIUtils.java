package io.github.ningwy.googleplay.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;

import io.github.ningwy.googleplay.global.GooglePlayApplication;

/**
 * UI工具类
 * Created by ningwy on 2016/9/1.
 */
public class UIUtils {

    //获得context
    public static Context getContext() {
        return GooglePlayApplication.getContext();
    }

    //获得Handler
    public static Handler getHandler() {
        return GooglePlayApplication.getHandler();
    }

    //获得主线程id
    public static int getMainThreadId() {
        return GooglePlayApplication.getMainThreadId();
    }

    ///////////////////////////获取资源///////////////////////////////////

    //获取字符串
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    //获取字符串数组
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    //获取图片
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    //获取颜色
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    //根据id获取颜色状态选择器
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    //获取尺寸
    public static int getDimension(int id) {
        return getContext().getResources().getDimensionPixelSize(id);//返回具体像素值
    }

    ///////////////////////////dp和px转换///////////////////////////////////

    //dip转换成px
    public static int dip2px(float dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5f);
    }

    //px转换成dip
    public static float px2dip(float px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    ///////////////////////////加载布局文件///////////////////////////////////

    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    ///////////////////////////判断是否运行在主线程///////////////////////////////////

    public static boolean isRunOnUIThread() {
        //获取当前线程的id
        int myTid = android.os.Process.myTid();
        if (getMainThreadId() == myTid) {
            return true;
        }
        return false;
    }

    //在主线程执行
    public static void runOnUIThread(Runnable r) {
        if (isRunOnUIThread()) {
            //如果在主线程，直接执行run方法
            r.run();
        } else {
            //如果在子线程，则借助Handler让其运行在主线程
            getHandler().post(r);
        }
    }

}
