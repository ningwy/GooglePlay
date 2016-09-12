package io.github.ningwy.googleplay.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import io.github.ningwy.googleplay.http.protocol.HotProtocol;
import io.github.ningwy.googleplay.ui.view.LoadingPage;
import io.github.ningwy.googleplay.ui.view.MyFlowLayout;
import io.github.ningwy.googleplay.utils.DrawableUtils;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 排行
 * Created by ningwy on 2016/9/1.
 */
public class HotFragment extends BaseFragment {

    private List<String> data;

    @Override
    public View onCreateSuccessView() {

        ScrollView scrollView = new ScrollView(UIUtils.getContext());

//        FlowLayout flowLayout = new FlowLayout(UIUtils.getContext());
        MyFlowLayout flowLayout = new MyFlowLayout(UIUtils.getContext());

        int padding = UIUtils.dip2px(10);
        flowLayout.setPadding(padding, padding, padding, padding);// 设置内边距

//        flowLayout.setHorizontalSpacing(UIUtils.dip2px(6));// 水平间距
//        flowLayout.setVerticalSpacing(UIUtils.dip2px(8));// 竖直间距

        for (int i = 0; i < data.size(); i++) {
            TextView textView = new TextView(UIUtils.getContext());
            textView.setText(data.get(i));

            textView.setTextColor(Color.WHITE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);// 18sp
            textView.setPadding(padding, padding, padding, padding);
            textView.setGravity(Gravity.CENTER);

            // 生成随机颜色
            Random random = new Random();
            int r = 30 + random.nextInt(200);
            int g = 30 + random.nextInt(200);
            int b = 30 + random.nextInt(200);

            int color = 0xffcecece;// 按下后偏白的背景色

            StateListDrawable selector = DrawableUtils.getSelector(Color.rgb(r, g, b), color, UIUtils.dip2px(6));

            textView.setBackgroundDrawable(selector);

            //设置点击监听
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), data.get(finalI), Toast.LENGTH_SHORT).show();
                }
            });

            flowLayout.addView(textView);
        }

        scrollView.addView(flowLayout);

        return scrollView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        HotProtocol hotProtocol = new HotProtocol();
        data = hotProtocol.getData(0);
        return check(data);
    }
}
