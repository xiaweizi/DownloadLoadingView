package com.xiaweizi.downloadloadingview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.xiaweizi.lib.DownloadLoadingView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private DownloadLoadingView mLoadingView;
    private DownloadLoadingView mLoadingView1;
    private DownloadLoadingView mLoadingView2;
    private DownloadLoadingView mLoadingView3;
    private MyHandler mHandler;

    private static final int MSG_DOWNLOAD = 101;
    private int mProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoadingView = findViewById(R.id.loading_view);
        mLoadingView1 = findViewById(R.id.loading_view1);
        mLoadingView2 = findViewById(R.id.loading_view2);
        mLoadingView3 = findViewById(R.id.loading_view3);
        mHandler = new MyHandler(this);
        mHandler.sendEmptyMessage(MSG_DOWNLOAD);
    }

    static class MyHandler extends Handler {
        WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity theActivity = mActivity.get();
            if (theActivity == null || theActivity.isFinishing()) {
                return;
            }
            // 消息处理
            switch (msg.what) {
                case MSG_DOWNLOAD:
                    theActivity.mProgress += 1;
                    theActivity.mLoadingView.setProgress(theActivity.mProgress % 100);
                    theActivity.mLoadingView1.setProgress(theActivity.mProgress % 100);
                    theActivity.mLoadingView2.setProgress(theActivity.mProgress % 100);
                    theActivity.mLoadingView3.setProgress(theActivity.mProgress % 100);
                    theActivity.mHandler.sendEmptyMessageDelayed(MSG_DOWNLOAD, 20);
                    break;
                default:
                    break;
            }

        }
    }
}
