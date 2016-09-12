package io.github.ningwy.googleplay.ui.holder;

import android.view.View;
import android.widget.ImageView;

import org.xutils.x;

import java.util.ArrayList;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.domain.AppPageInfo;
import io.github.ningwy.googleplay.http.HttpHelper;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 应用详情——应用图片模块
 * Created by ningwy on 2016/9/8.
 */
public class DetailPicsHolder extends BaseHolder<AppPageInfo> {

    private ImageView[] ivPics;

    @Override
    public View initView() {

        View view = UIUtils.inflate(R.layout.layout_detail_picinfo);

        ivPics = new ImageView[5];
        ivPics[0] = (ImageView) view.findViewById(R.id.iv_pic1);
        ivPics[1] = (ImageView) view.findViewById(R.id.iv_pic2);
        ivPics[2] = (ImageView) view.findViewById(R.id.iv_pic3);
        ivPics[3] = (ImageView) view.findViewById(R.id.iv_pic4);
        ivPics[4] = (ImageView) view.findViewById(R.id.iv_pic5);

        return view;
    }

    @Override
    public void refreshData(AppPageInfo data) {
        ArrayList<String> screen = data.screen;

        for (int i = 0; i < 5; i++) {
            if (i < screen.size()) {
                x.image().bind(ivPics[i], HttpHelper.URL + "image?name=" + screen.get(i));
            } else {
                ivPics[i].setVisibility(View.GONE);
            }
        }
    }
}
