package io.github.ningwy.googleplay.ui.fragment;

import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import io.github.ningwy.googleplay.domain.AppPageInfo;
import io.github.ningwy.googleplay.http.protocol.HomeProtocol;
import io.github.ningwy.googleplay.ui.activity.HomeDetailActivity;
import io.github.ningwy.googleplay.ui.adapter.MyBaseAdapter;
import io.github.ningwy.googleplay.ui.holder.BaseHolder;
import io.github.ningwy.googleplay.ui.holder.HomeHeaderHolder;
import io.github.ningwy.googleplay.ui.holder.HomeHolder;
import io.github.ningwy.googleplay.ui.view.LoadingPage;
import io.github.ningwy.googleplay.ui.view.MyListView;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 主页
 * Created by ningwy on 2016/9/1.
 */
public class HomeFragment extends BaseFragment {

    private List<AppPageInfo> data;
    private List<String> picture;

    @Override
    public View onCreateSuccessView() {

        MyListView listView = new MyListView(UIUtils.getContext());

        listView.setAdapter(new MyHomeAdapter(data));

        HomeHeaderHolder header = new HomeHeaderHolder();
        if (picture != null) {
            // 设置轮播条数据
            header.setData(picture);
        }
        listView.addHeaderView(header.getRootView());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String packageName = data.get(position - 1).packageName;//要去掉头布局
                Intent intent = new Intent(UIUtils.getContext(), HomeDetailActivity.class);
                intent.putExtra("packageName", packageName);
                startActivity(intent);
            }
        });

        return listView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {

        HomeProtocol homeProtocol = new HomeProtocol();
        data = homeProtocol.getData(0);

        picture = homeProtocol.getPictureList();

        return check(data);
    }

    class MyHomeAdapter extends MyBaseAdapter<AppPageInfo> {

        public MyHomeAdapter(List<AppPageInfo> data) {
            super(data);
        }

        @Override
        public BaseHolder<AppPageInfo> getHolder(int position) {
            return new HomeHolder();
        }

        @Override
        public List<AppPageInfo> onLoadMore() {

            SystemClock.sleep(1000);

            HomeProtocol homeProtocol = new HomeProtocol();

            return homeProtocol.getData(getDataSize());
        }
    }

}
