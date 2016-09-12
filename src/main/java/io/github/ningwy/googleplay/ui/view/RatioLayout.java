package io.github.ningwy.googleplay.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import io.github.ningwy.googleplay.R;

/**
 * 自定义根据图片大小比例缩放宽高的FrameLayout
 * Created by ningwy on 2016/9/6.
 */
public class RatioLayout extends FrameLayout {

    /**
     * 图片宽高比
     */
    private float ratio;

    public RatioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        //获取属性值
        ratio = typedArray.getFloat(R.styleable.RatioLayout_ratio, -1);
    }

    public RatioLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 三种模式：
         *  MeasureSpec.AT_MOST:最大模式，控件的宽度或高度能够达到它所能达到的最大值，有点类似于wrap_content
         MeasureSpec.EXACTLY:精确模式，控件的宽高已经确定，类似于match_parent
         MeasureSpec.UNSPECIFIED:未指定模式，什么都还没确定
         */

        //获得控件宽度值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        //获得控件宽度模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //获得控件高度值
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //获得控件高度模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //在宽度确定，高度不确定，宽高比大于0的时候，重新测量高度
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY && ratio > 0) {
            //获得图片宽度：控件宽度减去左右边距
            int imageWidth = width - getPaddingLeft() - getPaddingRight();
            //根据图片宽度重新计算图片高度
            int imageHeight = (int) (imageWidth / ratio + 0.5f);
            //计算控件高度:加上上下边距
            height = imageHeight + getPaddingBottom() + getPaddingTop();
            // 根据最新的高度来重新生成heightMeasureSpec(高度模式是确定模式)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        // 按照最新的高度测量控件
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
