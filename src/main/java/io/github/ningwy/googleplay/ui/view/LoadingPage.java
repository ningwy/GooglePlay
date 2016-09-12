package io.github.ningwy.googleplay.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.manager.ThreadManager;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 加载页面
 * Created by ningwy on 2016/9/3.
 */
public abstract class LoadingPage extends FrameLayout {

    //什么都没做的状态
    private static final int LOADING_UNDO = 1;
    //加载中状态
    private static final int LOADING = 2;
    //加载失败状态
    private static final int LOADING_ERROR = 3;
    //加载成功状态
    private static final int LOADING_SUCCESS = 4;
    //数据为空状态
    private static final int LOADING_EMPTY = 5;

    //当前状态，默认为什么都没做
    private int mCurrentState = LOADING_UNDO;

    private View mLoading;
    private View mLoadingError;
    private View mLoadingEmpty;
    private View mLoadingSuccess;

    public LoadingPage(Context context) {
        this(context, null);
    }

    public LoadingPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //初始化加载页面
        if (mLoading == null) {
            mLoading = UIUtils.inflate(R.layout.loading);
            addView(mLoading);
        }

        //初始化加载失败页面
        if (mLoadingError == null) {
            mLoadingError = UIUtils.inflate(R.layout.loading_error);
            Button btnRetry = (Button) mLoadingError.findViewById(R.id.btn_retry);
            btnRetry.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //重新加载数据
                    loadData();
                }
            });
            addView(mLoadingError);
        }

        //初始化数据为空页面
        if (mLoadingEmpty == null) {
            mLoadingEmpty = UIUtils.inflate(R.layout.loading_empty);
            addView(mLoadingEmpty);
        }

        switchRightPage();
    }


    /**
     * 切换到正确的加载页面
     */
    private void switchRightPage() {
        mLoading.setVisibility(mCurrentState == LOADING_UNDO || mCurrentState == LOADING ? View.VISIBLE : View.GONE);
        mLoadingError.setVisibility(mCurrentState == LOADING_ERROR ? View.VISIBLE : View.GONE);
        mLoadingEmpty.setVisibility(mCurrentState == LOADING_EMPTY ? View.VISIBLE : View.GONE);

        //当成功布局为空，并且状态为加载成功时，才初始化加载成功布局
        if (mLoadingSuccess == null && mCurrentState == LOADING_SUCCESS) {
            mLoadingSuccess = onCreateSuccessView();
            if (mLoadingSuccess != null) {
//                removeAllViews();
                addView(mLoadingSuccess);
            }
        }

        if (mLoadingSuccess != null) {
            mLoadingSuccess.setVisibility(mCurrentState == LOADING_SUCCESS ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 加载网络数据
     */
    public void loadData() {
        //只有在状态不是加载中时才加载数据
        if (mCurrentState != LOADING) {
            mCurrentState = LOADING;//将当前状态设置为加载中

//            new Thread() {
//                @Override
//                public void run() {
//                    ResultState resultState = onLoad();
//                    if (resultState != null) {
//                        mCurrentState = resultState.getState();
//                    }
//
//                    //主线程更新UI
//                    UIUtils.runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            switchRightPage();
//                        }
//                    });
//                }
//            }.start();

            ThreadManager.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    ResultState resultState = onLoad();
                    if (resultState != null) {
                        mCurrentState = resultState.getState();
                    }

                    //主线程更新UI
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            switchRightPage();
                        }
                    });
                }
            });
        }
    }

    public enum ResultState {

        LOADERROR(LOADING_ERROR), LOADEMPTY(LOADING_EMPTY), LOADSUCCESS(LOADING_SUCCESS);

        private int state;

        ResultState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

    //交由BaseFragment实现
    public abstract View onCreateSuccessView();

    public abstract ResultState onLoad();

}
