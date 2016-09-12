package io.github.ningwy.googleplay.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * 图片工具类
 * Created by ningwy on 2016/9/7.
 */
public class DrawableUtils {

    //获取一个shape对象
    public static GradientDrawable getShape(int color, float radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);// 矩形
        shape.setCornerRadius(radius);// 圆角半径
        shape.setColor(color);// 颜色

        return shape;
    }


    //获取状态选择器
    public static StateListDrawable getSelector(Drawable normal, Drawable press) {

        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[]{android.R.attr.state_pressed}, press);//按下图片
        sld.addState(new int[]{}, normal);//默认图片

        return sld;
    }

    //获取状态选择器
    public static StateListDrawable getSelector(int normal, int press, float radius) {

        GradientDrawable normalShape = getShape(normal, radius);
        GradientDrawable pressShape = getShape(press, radius);

        StateListDrawable sld = getSelector(normalShape, pressShape);

        return sld;
    }

}
