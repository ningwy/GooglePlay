package io.github.ningwy.googleplay.ui.fragment;

import android.os.SystemClock;
import android.view.View;

import java.util.List;

import io.github.ningwy.googleplay.domain.AppPageInfo;
import io.github.ningwy.googleplay.http.protocol.AppProtocol;
import io.github.ningwy.googleplay.ui.adapter.MyBaseAdapter;
import io.github.ningwy.googleplay.ui.holder.AppHolder;
import io.github.ningwy.googleplay.ui.holder.BaseHolder;
import io.github.ningwy.googleplay.ui.view.LoadingPage;
import io.github.ningwy.googleplay.ui.view.MyListView;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 应用
 * Created by ningwy on 2016/9/1.
 */
public class AppFragment extends BaseFragment {

    private List<AppPageInfo> data;

    //在加载数据成功时回调此方法加载布局，在主线程中执行
    @Override
    public View onCreateSuccessView() {
        MyListView myListView = new MyListView(UIUtils.getContext());
        myListView.setAdapter(new AppAdapter(data));
        return myListView;
    }

    //加载数据，在子线程中执行
    @Override
    public LoadingPage.ResultState onLoad() {
        AppProtocol appProtocol = new AppProtocol();
        data =  appProtocol.getData(0);

        return check(this.data);
    }

    class AppAdapter extends MyBaseAdapter<AppPageInfo> {

        public AppAdapter(List<AppPageInfo> data) {
            super(data);
        }

        @Override
        public BaseHolder getHolder(int position) {
            return new AppHolder();
        }

        @Override
        public List<AppPageInfo> onLoadMore() {

            SystemClock.sleep(1000);

            AppProtocol appProtocol = new AppProtocol();
            List<AppPageInfo> moreData = appProtocol.getData(getDataSize());

            return moreData;
        }
    }
}
