package io.github.ningwy.googleplay.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 自定义控件
 * Created by ningwy on 2016/9/9.
 */
public class MyFlowLayout extends ViewGroup {

    //已经使用的宽度
    private int mUsedWidth;

    //两个子控件之间的间距
    private int mHorizontalSpace = UIUtils.dip2px(6);
    //上下两行子控件的垂直间距
    private int mVerticalSpace = UIUtils.dip2px(8);

    private Line mLine;

    // 维护所有行的集合
    private List<Line> mLineList = new ArrayList<>();

    //最多有100行子View
    private static final int MAX_LINE = 100;

    public MyFlowLayout(Context context, int mHorizontalSpace, int mVerticalSpace) {
        this(context);
        this.mHorizontalSpace = mHorizontalSpace;
        this.mVerticalSpace = mVerticalSpace;
    }

    public MyFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFlowLayout(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = l + getPaddingLeft();
        int top = t + getPaddingTop();

        // 遍历所有行对象, 设置每行位置
        for (int i = 0; i < mLineList.size(); i++) {
            Line line = mLineList.get(i);
            line.layout(left, top);

            top += line.mMaxHeight + mVerticalSpace;// 更新top值
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获得有效宽度
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        //宽度模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec) - getPaddingTop() - getPaddingBottom();

        //获得有效高度
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //高度模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (mLine == null) {
            mLine = new Line();
        }

        //获得子控件的个数
        int childCount = getChildCount();

        //遍历子控件
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            // 如果父控件是确定模式, 子控件包裹内容;否则子控件模式和父控件一致
            int measuredWidth = MeasureSpec.makeMeasureSpec(width, (widthMode == MeasureSpec.EXACTLY) ? MeasureSpec
                    .AT_MOST : widthMode);
            int measuredHeight = MeasureSpec.makeMeasureSpec(height, (heightMode == MeasureSpec.EXACTLY) ?
                    MeasureSpec.AT_MOST : heightMode);

            //测量子控件
            childView.measure(measuredWidth, measuredHeight);

            //获得子控件宽高
            int childWidth = childView.getMeasuredWidth();

            mUsedWidth += childWidth;
            if (mUsedWidth < width) {
                //添加到Line中
                mLine.addView(childView);

                mUsedWidth += mHorizontalSpace;
                if (mUsedWidth > width) {
                    //1. 加了水平间距后就没有空间了,此时需要换行
                    if (!newLine()) {
                        //换行不成功
                        break;
                    }
                }
            } else {
                //2. 可用空间不够添加一个子view,此时也需要换行,有两种情况
                //2.1 当前行一个子View都没有(子view很长，比有效宽度还长)
                if (mLine.getLineCount() == 0) {
                    mLine.addView(childView);// 强制添加到当前行

                    if (!newLine()) {// 换行
                        break;
                    }
                } else {
                    //2.2 当前行的子view不为0，但剩余的宽度不足以继续添加一个子view,需要换行
                    if (!newLine()) {
                        //换行不成功
                        break;
                    }

                    //换行之后将当前的子view添加到新行中去
                    mLine.addView(childView);
                    mUsedWidth += childWidth + mHorizontalSpace;// 更新已使用宽度
                }
            }
        }

        //最后一行在上面的情况下不会保存，需要另外保存
        if (mLine != null && mLineList.size() != 0 && !mLineList.contains(mLine)) {
            mLineList.add(mLine);
        }

        //控件的整体宽度
        int mTotalWidth = MeasureSpec.getSize(widthMeasureSpec);
        //控件的整体高度
        int mTotalHeight = 0;
        for (int i = 0; i < mLineList.size(); i++) {
            mTotalHeight += mLineList.get(i).mMaxHeight;
        }
        mTotalHeight += (mLineList.size() - 1) * mVerticalSpace;// 增加竖直间距
        mTotalHeight += getPaddingTop() + getPaddingBottom();// 增加上下边距

        // 根据最新的宽高来测量整体布局的大小
        setMeasuredDimension(mTotalWidth, mTotalHeight);
//         super.onMeasure(mTotalWidth, mTotalHeight);
    }

    /**
     * 该类代表几个子控件占据一行屏幕
     */
    class Line {

        //当前行子view的总宽度
        private int mTotalWidth;
        //当前行子View的最大高度
        public int mMaxHeight;

        private List<View> mListView = new ArrayList<>();//当前行集合

        /**
         * 往当前行添加view
         *
         * @param view 要添加的view
         */
        public void addView(View view) {
            if (view != null) {
                mListView.add(view);

                mTotalWidth += view.getMeasuredWidth();

                mMaxHeight = (mMaxHeight < view.getMeasuredHeight()) ? view.getMeasuredHeight() : mMaxHeight;
            }
        }

        /**
         * 获得当前行子view的个数
         *
         * @return
         */
        public int getLineCount() {
            return mListView.size();
        }

        /**
         * 子控件的布局
         *
         * @param left 左上顶点的左坐标
         * @param top  左上顶点的上坐标
         */
        public void layout(int left, int top) {
            int childCount = getLineCount();

            // 将剩余空间分配给每个子控件
            int validWidth = getMeasuredWidth() - getPaddingLeft()
                    - getPaddingRight();// 屏幕总有效宽度
            // 计算剩余宽度
            int surplusWidth = validWidth - mTotalWidth - (childCount - 1) * mHorizontalSpace;

            if (surplusWidth >= 0) {
                // 有剩余空间
                int space = (int) ((float) surplusWidth / childCount + 0.5f);// 平均每个控件分配的大小

                // 重新测量子控件
                for (int i = 0; i < childCount; i++) {
                    View childView = mListView.get(i);

                    int measuredWidth = childView.getMeasuredWidth();
                    int measuredHeight = childView.getMeasuredHeight();

                    measuredWidth += space;// 宽度增加

                    int widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                            measuredWidth, MeasureSpec.EXACTLY);
                    int heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            measuredHeight, MeasureSpec.EXACTLY);

                    // 重新测量控件
                    childView.measure(widthMeasureSpec, heightMeasureSpec);

                    // 当控件比较矮时,需要居中展示, 竖直方向需要向下有一定偏移
                    int topOffset = (mMaxHeight - measuredHeight) / 2;

                    if (topOffset < 0) {
                        topOffset = 0;
                    }

                    // 设置子控件位置
                    childView.layout(left, top + topOffset, left
                            + measuredWidth, top + topOffset + measuredHeight);
                    left += measuredWidth + mHorizontalSpace;// 更新left值
                }

            } else {
                // 这个控件很长, 占满整行
                View childView = mListView.get(0);
                childView.layout(left, top,
                        left + childView.getMeasuredWidth(),
                        top + childView.getMeasuredHeight());
            }
        }

    }

    /**
     * 换行
     *
     * @return true：换行成功    false：换行失败
     */
    private boolean newLine() {
        //保存上一行的数据
        mLineList.add(mLine);

        if (mLineList.size() < MAX_LINE) {
            //可以继续添加
            mLine = new Line();
            //将已使用宽度置为0
            mUsedWidth = 0;

            return true;
        }

        return false;
    }
}
