package io.github.ningwy.googleplay.ui.fragment;

import android.view.View;
import android.widget.TextView;

import io.github.ningwy.googleplay.ui.view.LoadingPage;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 游戏
 * Created by ningwy on 2016/9/1.
 */
public class GameFragment extends BaseFragment {
    @Override
    public View onCreateSuccessView() {
        TextView textView = new TextView(UIUtils.getContext());
        textView.setText("GameFragment");
        return textView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        return LoadingPage.ResultState.LOADSUCCESS;
    }
}
