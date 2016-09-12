package io.github.ningwy.googleplay.ui.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.domain.AppPageInfo;
import io.github.ningwy.googleplay.http.protocol.HomeDetailProtocol;
import io.github.ningwy.googleplay.ui.holder.DetailAppInfoHolder;
import io.github.ningwy.googleplay.ui.holder.DetailDescHolder;
import io.github.ningwy.googleplay.ui.holder.DetailDownloadHolder;
import io.github.ningwy.googleplay.ui.holder.DetailPicsHolder;
import io.github.ningwy.googleplay.ui.holder.DetailSafeHolder;
import io.github.ningwy.googleplay.ui.view.LoadingPage;
import io.github.ningwy.googleplay.utils.UIUtils;

public class HomeDetailActivity extends BaseActivity {

    private String packageName;

    private AppPageInfo data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoadingPage mLoadingPage = new LoadingPage(this) {
            @Override
            public View onCreateSuccessView() {
                return HomeDetailActivity.this.onCreateSuccessView();
            }

            @Override
            public ResultState onLoad() {
                return HomeDetailActivity.this.onLoad();
            }
        };

        setContentView(mLoadingPage);

        //获得包名
        packageName = getIntent().getStringExtra("packageName");

        //加载数据
        mLoadingPage.loadData();

        initActionBar();

    }

    /**
     * 初始化ActionBar
     */
    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public View onCreateSuccessView() {
        View root = UIUtils.inflate(R.layout.home_detail_activity);

        //应用信息模块
        FrameLayout flAppInfo = (FrameLayout) root.findViewById(R.id.fl_detail_appinfo);
        DetailAppInfoHolder appInfoHolder = new DetailAppInfoHolder();
        flAppInfo.addView(appInfoHolder.getRootView());
        appInfoHolder.setData(data);

        //应用安全模块
        FrameLayout flSafeInfo = (FrameLayout) root.findViewById(R.id.fl_detail_safe);
        DetailSafeHolder safeHolder = new DetailSafeHolder();
        flSafeInfo.addView(safeHolder.getRootView());
        safeHolder.setData(data);

        //应用图片
        HorizontalScrollView hsvPics = (HorizontalScrollView) root.findViewById(R.id.hsv_detail_pics);
        DetailPicsHolder picsHolder = new DetailPicsHolder();
        hsvPics.addView(picsHolder.getRootView());
        picsHolder.setData(data);

        //应用描述
        FrameLayout flDesc = (FrameLayout) root.findViewById(R.id.fl_detail_desc);
        DetailDescHolder descHolder = new DetailDescHolder();
        flDesc.addView(descHolder.getRootView());
        descHolder.setData(data);

        //应用下载模块
        FrameLayout flDownload = (FrameLayout) root.findViewById(R.id.fl_detail_download);
        DetailDownloadHolder downloadHolder = new DetailDownloadHolder();
        flDownload.addView(downloadHolder.getRootView());
        downloadHolder.setData(data);

        return root;
    }

    public LoadingPage.ResultState onLoad() {
        HomeDetailProtocol protocol = new HomeDetailProtocol(packageName);
        data = protocol.getData(0);

        if (data != null) {
            //有数据则返回加载成功
            return LoadingPage.ResultState.LOADSUCCESS;
        } else {
            //否则返回加载失败
            return LoadingPage.ResultState.LOADERROR;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
