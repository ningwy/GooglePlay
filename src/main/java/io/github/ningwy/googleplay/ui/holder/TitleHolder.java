package io.github.ningwy.googleplay.ui.holder;

import android.view.View;
import android.widget.TextView;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.domain.CategoryInfo;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 分类目录的标题holder
 * Created by ningwy on 2016/9/7.
 */
public class TitleHolder extends BaseHolder<CategoryInfo> {

    private TextView tvTitle;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.category_list_title_item);
        tvTitle = (TextView) view.findViewById(R.id.tv_category_title);
        return view;
    }

    @Override
    public void refreshData(CategoryInfo data) {
        tvTitle.setText(data.title);
    }
}
