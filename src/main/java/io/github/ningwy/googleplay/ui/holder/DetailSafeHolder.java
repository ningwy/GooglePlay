package io.github.ningwy.googleplay.ui.holder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xutils.x;

import java.util.ArrayList;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.domain.AppPageInfo;
import io.github.ningwy.googleplay.http.HttpHelper;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 应用详情——应用安全模块
 * Created by ningwy on 2016/9/7.
 */
public class DetailSafeHolder extends BaseHolder<AppPageInfo> {

    private ImageView[] mSafeIcons;// 安全标识图片
    private ImageView[] mDesIcons;// 安全描述图片
    private TextView[] mSafeDes;// 安全描述文字
    private LinearLayout[] mSafeDesBar;// 安全描述条目(图片+文字)

    private RelativeLayout rlDesRoot;
    private LinearLayout llDesRoot;
    private ImageView ivArrow;

    //llDesRoot的高度
    private int llDesRootHeight;

    private LinearLayout.LayoutParams mParams;

    //标签：代表llDesRoot是否开启
    //false:关闭    true:开启
    private boolean isOpen = false;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.layout_detail_safeinfo);

        mSafeIcons = new ImageView[4];
        mSafeIcons[0] = (ImageView) view.findViewById(R.id.iv_safe1);
        mSafeIcons[1] = (ImageView) view.findViewById(R.id.iv_safe2);
        mSafeIcons[2] = (ImageView) view.findViewById(R.id.iv_safe3);
        mSafeIcons[3] = (ImageView) view.findViewById(R.id.iv_safe4);

        mDesIcons = new ImageView[4];
        mDesIcons[0] = (ImageView) view.findViewById(R.id.iv_des1);
        mDesIcons[1] = (ImageView) view.findViewById(R.id.iv_des2);
        mDesIcons[2] = (ImageView) view.findViewById(R.id.iv_des3);
        mDesIcons[3] = (ImageView) view.findViewById(R.id.iv_des4);

        mSafeDes = new TextView[4];
        mSafeDes[0] = (TextView) view.findViewById(R.id.tv_des1);
        mSafeDes[1] = (TextView) view.findViewById(R.id.tv_des2);
        mSafeDes[2] = (TextView) view.findViewById(R.id.tv_des3);
        mSafeDes[3] = (TextView) view.findViewById(R.id.tv_des4);

        mSafeDesBar = new LinearLayout[4];
        mSafeDesBar[0] = (LinearLayout) view.findViewById(R.id.ll_des1);
        mSafeDesBar[1] = (LinearLayout) view.findViewById(R.id.ll_des2);
        mSafeDesBar[2] = (LinearLayout) view.findViewById(R.id.ll_des3);
        mSafeDesBar[3] = (LinearLayout) view.findViewById(R.id.ll_des4);

        rlDesRoot = (RelativeLayout) view.findViewById(R.id.rl_des_root);
        llDesRoot = (LinearLayout) view.findViewById(R.id.ll_des_root);
        ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);

        rlDesRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        return view;
    }

    @Override
    public void refreshData(AppPageInfo data) {
        ArrayList<AppPageInfo.SafeInfo> safeInfos = data.safe;
        for (int i = 0; i < 4; i++) {
            if (i < safeInfos.size()) {
                AppPageInfo.SafeInfo safeInfo = safeInfos.get(i);
                //安全标识图片
                x.image().bind(mSafeIcons[i], HttpHelper.URL + "image?name=" + safeInfo.safeUrl);
                // 安全描述文字
                mSafeDes[i].setText(safeInfo.safeDes);
                // 安全描述图片
                x.image().bind(mDesIcons[i], HttpHelper.URL + "image?name=" + safeInfo.safeDesUrl);
            } else {
                //隐藏剩下的图片
                mSafeIcons[i].setVisibility(View.GONE);
                //隐藏其他文字说明
                mSafeDesBar[i].setVisibility(View.GONE);
            }
        }

        //获得llDesRoot的高度
        llDesRoot.measure(0, 0);
        llDesRootHeight = llDesRoot.getMeasuredHeight();

        mParams = (LinearLayout.LayoutParams) llDesRoot.getLayoutParams();
        //开始默认高度为0，隐藏llDesRoot
        mParams.height = 0;
        llDesRoot.setLayoutParams(mParams);
    }

    private void toggle() {
        ValueAnimator animator = null;
        if (isOpen) {
            //关闭
            isOpen = false;
            animator = ValueAnimator.ofInt(llDesRootHeight, 0);
        } else {
            //打开
            isOpen = true;
            animator = ValueAnimator.ofInt(0, llDesRootHeight);
        }

        //动画更新的监听
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();

                // 重新修改布局高度
                mParams.height = height;
                llDesRoot.setLayoutParams(mParams);
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束的事件
                // 更新小箭头的方向
                if (isOpen) {
                    ivArrow.setImageResource(R.drawable.arrow_up);
                } else {
                    ivArrow.setImageResource(R.drawable.arrow_down);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.setDuration(200);// 动画时间
        animator.start();// 启动动画
    }
}
