package io.github.ningwy.googleplay.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 自定义ListView
 * Created by ningwy on 2016/9/6.
 */
public class MyListView extends ListView {

    public MyListView(Context context) {
        super(context);
        initView();
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        this.setSelector(new ColorDrawable());// 设置默认状态选择器为全透明
        this.setDivider(null);// 去掉分隔线
        this.setCacheColorHint(Color.TRANSPARENT);// 有时候滑动listview背景会变成黑色,此方法将背景变为全透明
    }
}
