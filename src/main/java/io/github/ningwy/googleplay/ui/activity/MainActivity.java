package io.github.ningwy.googleplay.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.ui.fragment.BaseFragment;
import io.github.ningwy.googleplay.ui.fragment.FragmentFactory;
import io.github.ningwy.googleplay.ui.view.PagerTab;
import io.github.ningwy.googleplay.utils.UIUtils;

public class MainActivity extends BaseActivity {

    private PagerTab mPagerTab;
    private ViewPager mViewPager;

    private MyAdapter mAdapter;

//    private ActionBarDrawerToggle toggle;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
        initActionBar();
    }

    /**
     * 初始化ActionBar
     */
    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();

        if (actionbar != null) {
            actionbar.setHomeButtonEnabled(true);// home处可以点击
            actionbar.setDisplayHomeAsUpEnabled(true);// 显示左上角返回键,当和侧边栏结合时展示三个杠图片

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl_home);

            // 初始化抽屉开关
//            toggle = new ActionBarDrawerToggle(this, drawer,
//                    R.drawable.ic_drawer_am, R.string.drawer_open,
//                    R.string.drawer_close);
            toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.drawer_open, R.string.drawer_close);

            toggle.syncState();// 同步状态, 将DrawerLayout和开关关联在一起
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 切换抽屉
                toggle.onOptionsItemSelected(item);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //添加页面滑动的监听
        mPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                BaseFragment baseFragment = FragmentFactory.creatFragment(position);
                baseFragment.loadData();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mAdapter = new MyAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mAdapter);
        mPagerTab.setViewPager(mViewPager);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mPagerTab = (PagerTab) findViewById(R.id.pagertab);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    class MyAdapter extends FragmentPagerAdapter {

        private String[] tabNames = null;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            tabNames = UIUtils.getStringArray(R.array.tab_names);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames[position];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = FragmentFactory.creatFragment(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }
    }
}
