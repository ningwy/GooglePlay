package io.github.ningwy.googleplay.ui.holder;

import android.text.format.Formatter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.domain.AppPageInfo;
import io.github.ningwy.googleplay.domain.DownloadInfo;
import io.github.ningwy.googleplay.http.HttpHelper;
import io.github.ningwy.googleplay.manager.DownloadManager;
import io.github.ningwy.googleplay.ui.view.ProgressArc;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 主页ViewHolder
 * Created by ningwy on 2016/9/4.
 */
public class HomeHolder extends BaseHolder<AppPageInfo> implements DownloadManager.DownloadObserver, View
        .OnClickListener {

    @ViewInject(value = R.id.iv_home_icon)
    private ImageView ivIcon;
    @ViewInject(value = R.id.tv_home_name)
    private TextView tvName;
    @ViewInject(value = R.id.tv_home_size)
    private TextView tvSize;
    @ViewInject(value = R.id.tv_home_desc)
    private TextView tvDesc;
    @ViewInject(value = R.id.rb_home_rating)
    private RatingBar ratingBar;

    @ViewInject(value = R.id.fl_progress)
    private FrameLayout flProgress;
    @ViewInject(value = R.id.tv_download)
    private TextView tvDownload;

    private ProgressArc pbProgress;

    private DownloadManager mDM;

    private int mCurrentState;
    private float mProgress;

    /**
     * 用途：加载布局和初始化控件
     * @return
     */
    @Override
    public View initView() {
        View mRootView = UIUtils.inflate(R.layout.home_list_item);
        x.view().inject(this, mRootView);

        // 初始化进度条
        flProgress.setOnClickListener(this);

        pbProgress = new ProgressArc(UIUtils.getContext());
        // 设置圆形进度条直径
        pbProgress.setArcDiameter(UIUtils.dip2px(26));
        // 设置进度条颜色
        pbProgress.setProgressColor(UIUtils.getColor(R.color.progress));
        // 设置进度条宽高布局参数
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                UIUtils.dip2px(27), UIUtils.dip2px(27));
        flProgress.addView(pbProgress, params);

        // pbProgress.setOnClickListener(this);

        mDM = DownloadManager.getInstance();
        mDM.registerDownloadObserver(this);// 注册观察者, 监听状态和进度变化

        return mRootView;
    }

    //刷新数据
    @Override
    public void refreshData(AppPageInfo data) {
        tvName.setText(data.name);
        tvSize.setText(Formatter.formatFileSize(UIUtils.getContext(), data.size));
        tvDesc.setText(data.des);
        ratingBar.setRating(data.stars);
        x.image().bind(ivIcon, HttpHelper.URL + "image?name=" + data.iconUrl);

        // 判断当前应用是否下载过
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

        refreshUI(mCurrentState, mProgress, data.id);
    }

    /**
     * 刷新界面
     *
     * @param progress
     * @param state
     */
    private void refreshUI(int state, float progress, String id) {
        // 由于listview重用机制, 要确保刷新之前, 确实是同一个应用
        if (!getData().id.equals(id)) {
            return;
        }

        mCurrentState = state;
        mProgress = progress;
        switch (state) {
            case DownloadManager.STATE_UNDO:
                // 自定义进度条背景
                pbProgress.setBackgroundResource(R.drawable.ic_download);
                // 没有进度
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
                tvDownload.setText("下载");
                break;
            case DownloadManager.STATE_READY:
                pbProgress.setBackgroundResource(R.drawable.ic_download);
                // 等待模式
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_WAITING);
                tvDownload.setText("等待");
                break;
            case DownloadManager.STATE_DOWNLOADING:
                pbProgress.setBackgroundResource(R.drawable.ic_pause);
                // 下载中模式
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_DOWNLOADING);
                pbProgress.setProgress(progress, true);
                tvDownload.setText((int) (progress * 100) + "%");
                break;
            case DownloadManager.STATE_PAUSE:
                pbProgress.setBackgroundResource(R.drawable.ic_resume);
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
                break;
            case DownloadManager.STATE_ERROR:
                pbProgress.setBackgroundResource(R.drawable.ic_redownload);
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
                tvDownload.setText("下载失败");
                break;
            case DownloadManager.STATE_SUCCESS:
                pbProgress.setBackgroundResource(R.drawable.ic_install);
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
                tvDownload.setText("安装");
                break;

            default:
                break;
        }
    }

    // 主线程更新ui 3-4
    private void refreshUIOnMainThread(final DownloadInfo info) {
        // 判断下载对象是否是当前应用
        AppPageInfo appInfo = getData();
        if (appInfo.id.equals(info.id)) {
            UIUtils.runOnUIThread(new Runnable() {

                @Override
                public void run() {
                    refreshUI(info.currentState, info.getProgress(), info.id);
                }
            });
        }
    }

    @Override
    public void onDownloadStateChanged(DownloadInfo downloadInfo) {
        refreshUIOnMainThread(downloadInfo);
    }

    @Override
    public void onDownloadProgressChanged(DownloadInfo downloadInfo) {
        refreshUIOnMainThread(downloadInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
