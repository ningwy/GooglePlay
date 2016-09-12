package io.github.ningwy.googleplay.ui.holder;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.domain.AppPageInfo;
import io.github.ningwy.googleplay.domain.DownloadInfo;
import io.github.ningwy.googleplay.manager.DownloadManager;
import io.github.ningwy.googleplay.ui.view.ProgressHorizontal;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 应用详情——应用下载模块
 * Created by ningwy on 2016/9/11.
 */
public class DetailDownloadHolder extends BaseHolder<AppPageInfo> implements DownloadManager.DownloadObserver, View
        .OnClickListener {

    private FrameLayout flProgress;
    private Button btnDownload;
    private ProgressHorizontal pbProgress;

    private DownloadManager mDM;

    private int mCurrentState;//当前的下载状态
    private float mProgress;//当前的下载进度

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.layout_detail_download);

        btnDownload = (Button) view.findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(this);

        // 初始化自定义进度条
        flProgress = (FrameLayout) view.findViewById(R.id.fl_progress);
        flProgress.setOnClickListener(this);

        pbProgress = new ProgressHorizontal(UIUtils.getContext());
        pbProgress.setProgressBackgroundResource(R.drawable.progress_bg);// 进度条背景图片
        pbProgress.setProgressResource(R.drawable.progress_normal);// 进度条图片
        pbProgress.setProgressTextColor(Color.WHITE);// 进度文字颜色
        pbProgress.setProgressTextSize(UIUtils.dip2px(18));// 进度文字大小

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT);

        flProgress.addView(pbProgress, params);

        mDM = DownloadManager.getInstance();
        mDM.registerDownloadObserver(this);// 注册观察者, 监听状态和进度变化

        return view;
    }

    @Override
    public void refreshData(AppPageInfo data) {
        DownloadInfo downloadInfo = mDM.getDownloadInfo(data);
        if (downloadInfo != null) {
            // 之前下载过
            mCurrentState = downloadInfo.currentState;
            mProgress = downloadInfo.getProgress();
        } else {
            // 没有下载过
            mCurrentState = DownloadManager.STATE_UNDO;
            mProgress = 0;
        }

        refreshUI(mCurrentState, mProgress);
    }

    /**
     * 刷新ui
     *
     * @param currentState 当前的下载状态
     * @param progress     当前的下载位置
     */
    private void refreshUI(int currentState, float progress) {
        mCurrentState = currentState;
        mProgress = progress;

        switch (currentState) {
            //默认状态
            case DownloadManager.STATE_UNDO:
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("下载");
                break;

            //准备下载
            case DownloadManager.STATE_READY:
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("等待中..");
                break;

            //开始下载
            case DownloadManager.STATE_DOWNLOADING:
                flProgress.setVisibility(View.VISIBLE);
                btnDownload.setVisibility(View.GONE);
                pbProgress.setCenterText("");
                pbProgress.setProgress(mProgress);// 设置下载进度
                break;

            //下载暂停
            case DownloadManager.STATE_PAUSE:
                flProgress.setVisibility(View.VISIBLE);
                btnDownload.setVisibility(View.GONE);
                pbProgress.setCenterText("暂停");
                pbProgress.setProgress(mProgress);
                break;

            //下载失败
            case DownloadManager.STATE_ERROR:
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("下载失败");
                break;

            //下载成功
            case DownloadManager.STATE_SUCCESS:
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("安装");
                break;
        }
    }

    //主线程更新UI
    public void refreshOnUIThread(final DownloadInfo downloadInfo) {

        AppPageInfo data = getData();
        if (data.id.equals(downloadInfo.id)) {
            UIUtils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    refreshUI(downloadInfo.currentState, downloadInfo.getProgress());
                }
            });
        }
    }

    //更新下载状态，该方法有时候在主线程执行，有时候在子线程执行
    @Override
    public void onDownloadStateChanged(DownloadInfo downloadInfo) {
        refreshOnUIThread(downloadInfo);
    }

    //更新进度条，该方法在在子线程中执行
    @Override
    public void onDownloadProgressChanged(DownloadInfo downloadInfo) {
        refreshOnUIThread(downloadInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:
            case R.id.fl_progress:
                // 根据当前状态来决定下一步操作
                if (mCurrentState == DownloadManager.STATE_UNDO
                        || mCurrentState == DownloadManager.STATE_ERROR
                        || mCurrentState == DownloadManager.STATE_PAUSE) {
                    mDM.download(getData());// 开始下载
                } else if (mCurrentState == DownloadManager.STATE_DOWNLOADING
                        || mCurrentState == DownloadManager.STATE_READY) {
                    mDM.pause(getData());// 暂停下载
                } else if (mCurrentState == DownloadManager.STATE_SUCCESS) {
                    mDM.install(getData());// 开始安装
                }

                break;

            default:
                break;
        }
    }
}
