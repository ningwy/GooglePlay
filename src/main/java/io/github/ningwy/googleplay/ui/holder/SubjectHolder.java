package io.github.ningwy.googleplay.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.domain.SubjectInfo;
import io.github.ningwy.googleplay.http.HttpHelper;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 专题页面的ViewHolder
 * Created by ningwy on 2016/9/6.
 */
public class SubjectHolder extends BaseHolder<SubjectInfo> {

    @ViewInject(value = R.id.iv_subject_pic)
    private ImageView ivPic;
    @ViewInject(value = R.id.tv_subject_title)
    private TextView tvTitle;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.subject_list_item);
        x.view().inject(this, view);
        return view;
    }

    @Override
    public void refreshData(SubjectInfo data) {
        tvTitle.setText(data.des);
        ImageOptions options = new ImageOptions.Builder()
                .setIgnoreGif(true)
                .build();
        x.image().bind(ivPic, HttpHelper.URL + "image?name=" + data.url, options);
    }
}
