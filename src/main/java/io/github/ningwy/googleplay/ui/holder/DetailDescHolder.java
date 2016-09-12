package io.github.ningwy.googleplay.ui.holder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.domain.AppPageInfo;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 应用详情——应用描述
 * Created by ningwy on 2016/9/8.
 */
public class DetailDescHolder extends BaseHolder<AppPageInfo> {

    private TextView tvDes;
    private TextView tvAuthor;
    private ImageView ivArrow;
    private RelativeLayout rlToggle;

    private LinearLayout.LayoutParams mParams;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.layout_detail_desinfo);

        tvDes = (TextView) view.findViewById(R.id.tv_detail_des);
        tvAuthor = (TextView) view.findViewById(R.id.tv_detail_author);
        ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);
        rlToggle = (RelativeLayout) view.findViewById(R.id.rl_detail_toggle);

        rlToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        return view;
    }

    private boolean isOpen = false;//标记文字是否展开,默认为关闭状态

    /**
     * 状态开关
     */
    private void toggle() {
        int shortHeight = getShortHeight();
        int longHeight = getLongHeight();
        ValueAnimator animator = null;
        if (isOpen) {
            //打开
            isOpen = false;
            if (longHeight > shortHeight) {
                animator = ValueAnimator.ofInt(longHeight, shortHeight);
            }
        } else {
            //关闭
            isOpen = true;
            if (longHeight > shortHeight) {
                animator = ValueAnimator.ofInt(shortHeight, longHeight);
            }
        }

        if (animator != null) {
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int newHeight = (int) animation.getAnimatedValue();
                    mParams.height = newHeight;
                    tvDes.setLayoutParams(mParams);
                }
            });

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //改变箭头状态
                    if (isOpen) {
                        ivArrow.setImageResource(R.drawable.arrow_up);
                    } else {
                        ivArrow.setImageResource(R.drawable.arrow_down);
                    }

                    final ScrollView scrollView = getScrollView();
                    //打开时滑动到最底部
                    // 为了运行更加安全和稳定, 可以讲滑动到底部方法放在消息队列中执行
                    scrollView.post(new Runnable() {

                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);// 滚动到底部
                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            animator.setDuration(200);
            animator.start();
        }

    }

    @Override
    public void refreshData(AppPageInfo data) {
        tvDes.setText(data.des);
        tvAuthor.setText(data.author);

        // 放在消息队列中运行, 解决当低于7行描述时也是7行高度的bug
        tvDes.post(new Runnable() {
            @Override
            public void run() {
                mParams = (LinearLayout.LayoutParams) tvDes.getLayoutParams();
                mParams.height = getShortHeight();
                tvDes.setLayoutParams(mParams);
            }
        });

    }

    //获取7行文字的高度
    public int getShortHeight() {
        int width = tvDes.getMeasuredWidth();

        TextView view = new TextView(UIUtils.getContext());
        view.setText(getData().des);// 设置文字
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        view.setMaxLines(7);//设置最大为7行

        // 宽不变, 确定值, match_parent
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        // 高度包裹内容, wrap_content;当包裹内容时,参1表示尺寸最大值,暂写2000, 也可以是屏幕高度
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST);

        //开始测量
        view.measure(widthMeasureSpec, heightMeasureSpec);

        // 返回测量后的高度
        return view.getMeasuredHeight();
    }

    //获取全部文字的高度
    public int getLongHeight() {
        int width = tvDes.getMeasuredWidth();

        TextView view = new TextView(UIUtils.getContext());
        view.setText(getData().des);// 设置文字
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        view.setMaxLines(7);

        // 宽不变, 确定值, match_parent
        int widthMeasure = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        // 高度包裹内容, wrap_content;当包裹内容时,参1表示尺寸最大值,暂写2000, 也可以是屏幕高度
        int heightMeasure = View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST);

        //开始测量
        view.measure(widthMeasure, heightMeasure);

        // 返回测量后的高度
        return view.getMeasuredHeight();
    }

    //获得ScrollView
    public ScrollView getScrollView() {
        ViewParent parent = tvDes.getParent();
        while (!(parent instanceof ScrollView)) {
            parent = parent.getParent();
        }
        return (ScrollView) parent;
    }

}
