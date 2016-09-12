package io.github.ningwy.googleplay.ui.holder;

import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.domain.AppPageInfo;
import io.github.ningwy.googleplay.http.HttpHelper;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 *
 * Created by ningwy on 2016/9/6.
 */
public class AppHolder extends BaseHolder<AppPageInfo> {

    @ViewInject(value = R.id.iv_app_icon)
    private ImageView ivIcon;
    @ViewInject(value = R.id.tv_app_name)
    private TextView tvName;
    @ViewInject(value = R.id.tv_app_size)
    private TextView tvSize;
    @ViewInject(value = R.id.tv_app_desc)
    private TextView tvDesc;
    @ViewInject(value = R.id.rb_app_rating)
    private RatingBar ratingBar;

    @Override
    public View initView() {
        View mRootView = UIUtils.inflate(R.layout.app_list_item);
        x.view().inject(this, mRootView);
        return mRootView;
    }

    @Override
    public void refreshData(AppPageInfo data) {
        tvName.setText(data.name);
        tvSize.setText(Formatter.formatFileSize(UIUtils.getContext(), data.size));
        tvDesc.setText(data.des);
        ratingBar.setRating(data.stars);
        ImageOptions options = new ImageOptions.Builder()
                // 是否忽略GIF格式的图片
                .setIgnoreGif(false)
                // 图片缩放模式
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                // 下载中显示的图片
//                .setLoadingDrawableId(R.drawable.home_scroll_default)
                // 下载失败显示的图片
//                .setFailureDrawableId(R.drawable.home_scroll_default)
                // 得到ImageOptions对象
                .build();
        x.image().bind(ivIcon, HttpHelper.URL + "image?name=" + data.iconUrl, options);
    }
}
