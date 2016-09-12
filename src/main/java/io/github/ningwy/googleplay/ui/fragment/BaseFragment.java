package io.github.ningwy.googleplay.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.github.ningwy.googleplay.ui.view.LoadingPage;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 *
 * Created by ningwy on 2016/9/1.
 */
public abstract class BaseFragment extends Fragment {

    private LoadingPage mLoadingPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mLoadingPage == null) {
            mLoadingPage = new LoadingPage(UIUtils.getContext()) {
                @Override
                public View onCreateSuccessView() {
                    return BaseFragment.this.onCreateSuccessView();
                }

                @Override
                public LoadingPage.ResultState onLoad() {
                    return BaseFragment.this.onLoad();
                }
            };
        }
        return mLoadingPage;
    }

    /**
     * 校验数据结果的合法性
     * @param obj 数据
     * @return 加载的三种状态：成功并有数据、失败、成功但数据为空
     */
    public LoadingPage.ResultState check(Object obj) {
        if (obj != null) {
            if (obj instanceof List) {
                List list = (List) obj;
                if (list.isEmpty()) {
                    return LoadingPage.ResultState.LOADEMPTY;
                } else {
                    return LoadingPage.ResultState.LOADSUCCESS;
                }
            }
        }
        return LoadingPage.ResultState.LOADERROR;
    }

    //加载数据成功的布局交由子类去实现
    public abstract View onCreateSuccessView();
    //加载数据的具体实现交由子类去实现
    public abstract LoadingPage.ResultState onLoad();

    public void loadData() {
        if (mLoadingPage != null) {
            mLoadingPage.loadData();
        }
    }
}
