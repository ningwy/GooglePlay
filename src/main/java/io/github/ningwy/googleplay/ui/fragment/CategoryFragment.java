package io.github.ningwy.googleplay.ui.fragment;

import android.view.View;

import java.util.List;

import io.github.ningwy.googleplay.domain.CategoryInfo;
import io.github.ningwy.googleplay.http.protocol.CategoryProtocol;
import io.github.ningwy.googleplay.ui.adapter.MyBaseAdapter;
import io.github.ningwy.googleplay.ui.holder.BaseHolder;
import io.github.ningwy.googleplay.ui.holder.CategoryHolder;
import io.github.ningwy.googleplay.ui.holder.TitleHolder;
import io.github.ningwy.googleplay.ui.view.LoadingPage;
import io.github.ningwy.googleplay.ui.view.MyListView;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 分类
 * Created by ningwy on 2016/9/1.
 */
public class CategoryFragment extends BaseFragment {

    private List<CategoryInfo> data;

    @Override
    public View onCreateSuccessView() {
        MyListView view = new MyListView(UIUtils.getContext());
        view.setAdapter(new CategoryAdapter(data));
        return view;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        CategoryProtocol categoryProtocol = new CategoryProtocol();
        data = categoryProtocol.getData(0);
        return check(data);
    }

    class CategoryAdapter extends MyBaseAdapter<CategoryInfo> {

        public CategoryAdapter(List<CategoryInfo> data) {
            super(data);
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;//在原有基础上加1，即比原来的多一个布局
        }

        @Override
        public int getInnerType(int position) {
            CategoryInfo categoryInfo = data.get(position);
            if (categoryInfo.isTitle) {
                //不为空，说明是标题布局
                return super.getInnerType(position) + 1;
            } else {
                return super.getInnerType(position);
            }
        }

        @Override
        public boolean hasMore() {
            return false;//返回false表明没有更多数据，不需要显示加载更多布局
        }

        @Override
        public BaseHolder getHolder(int position) {
            CategoryInfo categoryInfo = data.get(position);

            if (categoryInfo.isTitle) {
                return new TitleHolder();
            } else {
                return new CategoryHolder();
            }
        }

        @Override
        public List<CategoryInfo> onLoadMore() {
            return null;
        }
    }
}
