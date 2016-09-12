package io.github.ningwy.googleplay.ui.fragment;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import io.github.ningwy.googleplay.http.protocol.RecommendProtocol;
import io.github.ningwy.googleplay.ui.view.LoadingPage;
import io.github.ningwy.googleplay.ui.view.fly.ShakeListener;
import io.github.ningwy.googleplay.ui.view.fly.StellarMap;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 推荐
 * Created by ningwy on 2016/9/1.
 */
public class RecommendFragment extends BaseFragment {

    private List<String> data;

    @Override
    public View onCreateSuccessView() {

        final StellarMap stellarMap = new StellarMap(UIUtils.getContext());
        stellarMap.setAdapter(new RecommendAdapter());
        // 随机方式, 将控件划分为9行6列的的格子, 然后在格子中随机展示
        stellarMap.setRegularity(6, 9);
        // 设置内边距10dp
        int padding = UIUtils.dip2px(10);
        stellarMap.setInnerPadding(padding, padding, padding, padding);
        // 设置默认页面, 第一组数据
        stellarMap.setGroup(0, true);

        //添加震动监听
        ShakeListener shake = new ShakeListener(UIUtils.getContext());
        shake.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                stellarMap.zoomIn();// 跳到下一页数据
            }
        });


        return stellarMap;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        RecommendProtocol recommendProtocol = new RecommendProtocol();
        data = recommendProtocol.getData(0);
        return check(data);
    }

    class RecommendAdapter implements StellarMap.Adapter {

        //返回组的个数
        @Override
        public int getGroupCount() {
            return 2;
        }

        //返回每组标签的个数
        @Override
        public int getCount(int group) {
            int count = data.size() / getGroupCount();
            if (group == getGroupCount() - 1) {
                //如果是最后一组，则将除不尽的都放到最后一组上
                count += data.size() % getGroupCount();
            }
            return count;
        }

        @Override
        public View getView(int group, int position, View convertView) {
            // 因为position每组都会从0开始计数, 所以需要将前面几组数据的个数加起来,才能确定当前组获取数据的角标位置
            position += group * getCount(group - 1);

            final String des = data.get(position);
            TextView textView = new TextView(UIUtils.getContext());
            textView.setText(des);

            Random random = new Random();
            //设置随机字体大小 16-25sp
            int size = 16 + random.nextInt(10);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

            //设置随机字体颜色 rgb:30-230
            int r = 30 + random.nextInt(200);
            int g = 30 + random.nextInt(200);
            int b = 30 + random.nextInt(200);
            textView.setTextColor(Color.rgb(r, g, b));

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), des, Toast.LENGTH_SHORT).show();
                }
            });

            return textView;
        }

        //根据上滑还是下滑得到下一组数据
        @Override
        public int getNextGroupOnZoom(int group, boolean isZoomIn) {
            //上滑 isZoonIn:false  下滑 isZoomIn:true
            if (isZoomIn) {
                //下滑到上一页
                if (group > 0) {
                    group--;
                } else {
                    //跳转到最后一页
                    group = getGroupCount() - 1;
                }
            } else {
                //上滑到下一页
                if (group < getGroupCount() - 1) {
                    group++;
                } else {
                    //跳转到第一页
                    group = 0;
                }
            }
            return group;
        }
    }
}
