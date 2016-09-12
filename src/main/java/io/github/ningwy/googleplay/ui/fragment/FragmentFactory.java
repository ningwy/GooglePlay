package io.github.ningwy.googleplay.ui.fragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment工厂类，负责创建Fragment
 * Created by ningwy on 2016/9/1.
 */
public class FragmentFactory {

    private static Map<Integer, BaseFragment> baseFragmentMap = new HashMap<>();

    public static BaseFragment creatFragment(int pos) {
        //避免重复创建多个Fragment
        BaseFragment baseFragment = baseFragmentMap.get(pos);
        if (baseFragment == null) {
            switch (pos) {
                //主页
                case 0:
                    baseFragment = new HomeFragment();
                    break;
                //应用
                case 1:
                    baseFragment = new AppFragment();
                    break;
                //游戏
                case 2:
                    baseFragment = new GameFragment();
                    break;
                //专题
                case 3:
                    baseFragment = new SubjectFragment();
                    break;
                //推荐
                case 4:
                    baseFragment = new RecommendFragment();
                    break;
                //分类
                case 5:
                    baseFragment = new CategoryFragment();
                    break;
                //排行
                case 6:
                    baseFragment = new HotFragment();
                    break;
            }
            baseFragmentMap.put(pos, baseFragment);
        }
        return baseFragment;
    }

}
