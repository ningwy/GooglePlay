package io.github.ningwy.googleplay.ui.holder;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.xutils.x;

import java.util.List;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.http.HttpHelper;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 主页头部轮播图holder
 * Created by ningwy on 2016/9/7.
 */
public class HomeHeaderHolder extends BaseHolder<List<String>> {

    private ViewPager mViewPager;

    private LinearLayout llContainer;

    private List<String> data;

    //记录小圆点滑动时的前一个位置
    private int prePos;

    @Override
    public View initView() {
        View view = createView();
        return view;
    }

    private View createView() {
        //根视图
        RelativeLayout rlRoot = new RelativeLayout(UIUtils.getContext());
        ListView.LayoutParams rlParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils
                .dip2px(150));
        rlRoot.setLayoutParams(rlParams);

        //轮播图ViewPager
        mViewPager = new ViewPager(UIUtils.getContext());
        RelativeLayout.LayoutParams vpParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        //添加到根视图
        rlRoot.addView(mViewPager, vpParams);

        //右下角和轮播图对应的小圆点，用LinearLayout来包裹
        llContainer = new LinearLayout(UIUtils.getContext());
        RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        llParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        int padding = UIUtils.dip2px(10);
        llContainer.setPadding(padding, padding, padding, padding);
        llContainer.setLayoutParams(llParams);
        //添加到根视图
        rlRoot.addView(llContainer);

        return rlRoot;
    }

    @Override
    public void refreshData(List<String> data) {
        this.data = data;
        mViewPager.setAdapter(new HomeHeaderAdapter());
        mViewPager.setCurrentItem(data.size() * 10000);
//        prePos = data.size() - 1;//默认为最后一个点，第一个点的前一点即为最后一个点
        //获得数据之后才能初始化小圆点，不然报NullPointException
        initPoints();
        initEvent();
        //轮播图自动播放
        HomeHeaderTask task = new HomeHeaderTask();
        task.startPlay();
    }

    class HomeHeaderTask implements Runnable {

        //开始自动播放轮播图
        public void startPlay() {
            //清除所有消息
            UIUtils.getHandler().removeCallbacksAndMessages(null);
            UIUtils.getHandler().postDelayed(this, 3000);
        }

        @Override
        public void run() {

            int currentItem = mViewPager.getCurrentItem();
            currentItem++;
            mViewPager.setCurrentItem(currentItem);

            UIUtils.getHandler().postDelayed(this, 3000);
        }
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //防止下标越界
                position = position % data.size();

                //当前点被选中
                ImageView nowPoint = (ImageView) llContainer.getChildAt(position);
                nowPoint.setImageResource(R.drawable.indicator_selected);

                //上个点未被选中
                ImageView prePoint = (ImageView) llContainer.getChildAt(prePos);
                prePoint.setImageResource(R.drawable.indicator_normal);

                //重新记录值
                prePos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化小圆点
     */
    private void initPoints() {
        for (int i = 0; i < data.size(); i++) {
            //初始化小圆点
            ImageView point = new ImageView(UIUtils.getContext());
            LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            //默认第一个是亮点
            if (i == 0) {
                point.setImageResource(R.drawable.indicator_selected);
            } else {
                //其他的都是暗点
                point.setImageResource(R.drawable.indicator_normal);
                ivParams.leftMargin = UIUtils.dip2px(6);
            }
            //将小圆点添加到LinearLayout中去
            llContainer.addView(point, ivParams);
        }
    }

    class HomeHeaderAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //防止下标越界
            position = position % data.size();

            //轮播图
            ImageView imageView = new ImageView(UIUtils.getContext());
            String ivUrl = data.get(position);
            x.image().bind(imageView, HttpHelper.URL + "image?name=" + ivUrl);

            container.addView(imageView);
            return imageView;
        }
    }
}
