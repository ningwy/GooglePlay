package io.github.ningwy.googleplay.ui.fragment;

import android.view.View;

import java.util.List;

import io.github.ningwy.googleplay.domain.SubjectInfo;
import io.github.ningwy.googleplay.http.protocol.SubjectProtocol;
import io.github.ningwy.googleplay.ui.adapter.MyBaseAdapter;
import io.github.ningwy.googleplay.ui.holder.BaseHolder;
import io.github.ningwy.googleplay.ui.holder.SubjectHolder;
import io.github.ningwy.googleplay.ui.view.LoadingPage;
import io.github.ningwy.googleplay.ui.view.MyListView;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 专题
 * Created by ningwy on 2016/9/1.
 */
public class SubjectFragment extends BaseFragment {

    private List<SubjectInfo> data;

    @Override
    public View onCreateSuccessView() {

        MyListView myListView = new MyListView(UIUtils.getContext());
        myListView.setAdapter(new SubjectAdapter(data));
        return myListView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        SubjectProtocol subjectProtocol = new SubjectProtocol();
        data = subjectProtocol.getData(0);

        return check(data);
    }

    class SubjectAdapter extends MyBaseAdapter<SubjectInfo> {

        public SubjectAdapter(List<SubjectInfo> data) {
            super(data);
        }

        @Override
        public BaseHolder getHolder(int position) {
            return new SubjectHolder();
        }

        @Override
        public List<SubjectInfo> onLoadMore() {
            SubjectProtocol subjectProtocol = new SubjectProtocol();
            return subjectProtocol.getData(getDataSize());
        }
    }
}
