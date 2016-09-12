package io.github.ningwy.googleplay.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.x;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.domain.CategoryInfo;
import io.github.ningwy.googleplay.http.HttpHelper;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 分类目录的分类holder
 * Created by ningwy on 2016/9/7.
 */
public class CategoryHolder extends BaseHolder<CategoryInfo> implements View.OnClickListener {

    private TextView tvName1, tvName2, tvName3;
    private ImageView ivIcon1, ivIcon2, ivIcon3;
    private LinearLayout llGrid1, llGrid2, llGrid3;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.category_list_cat_item);

        tvName1 = (TextView) view.findViewById(R.id.tv_name1);
        tvName2 = (TextView) view.findViewById(R.id.tv_name2);
        tvName3 = (TextView) view.findViewById(R.id.tv_name3);

        ivIcon1 = (ImageView) view.findViewById(R.id.iv_icon1);
        ivIcon2 = (ImageView) view.findViewById(R.id.iv_icon2);
        ivIcon3 = (ImageView) view.findViewById(R.id.iv_icon3);

        llGrid1 = (LinearLayout) view.findViewById(R.id.ll_grid1);
        llGrid2 = (LinearLayout) view.findViewById(R.id.ll_grid2);
        llGrid3 = (LinearLayout) view.findViewById(R.id.ll_grid3);

        return view;
    }

    @Override
    public void refreshData(CategoryInfo data) {
        tvName1.setText(data.name1);
        tvName2.setText(data.name2);
        tvName3.setText(data.name3);

        x.image().bind(ivIcon1, HttpHelper.URL + "image?name=" + data.url1);
        x.image().bind(ivIcon2, HttpHelper.URL + "image?name=" + data.url2);
        x.image().bind(ivIcon3, HttpHelper.URL + "image?name=" + data.url3);

        llGrid1.setOnClickListener(this);
        llGrid2.setOnClickListener(this);
        llGrid3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        CategoryInfo info = getData();

        switch (v.getId()) {
            case R.id.ll_grid1 :
                Toast.makeText(UIUtils.getContext(), info.name1, Toast.LENGTH_SHORT).show();
                break;

            case R.id.ll_grid2 :
                Toast.makeText(UIUtils.getContext(), info.name2, Toast.LENGTH_SHORT).show();
                break;

            case R.id.ll_grid3 :
                Toast.makeText(UIUtils.getContext(), info.name3, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
