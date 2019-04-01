package com.glela.micro_mall.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.glela.micro_mall.R;


/**
 * 对webview的getsetting设置了很多,所以的web页面都使用即可
 * 增加了进度条
 */
public class BaseWebView extends WebView {

    private ProgressBar mProgressBar;

    private MyWebChromeClient mWebChromeClient;
    private MyWebViewClient mWebViewClient;

    public BaseWebView(Context context) {
        this(context, null, android.R.attr.webViewStyle);
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData();
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        initData();
    }

    private void initData() {
        mProgressBar = (ProgressBar) inflate(getContext(), R.layout.progress_webview, null);
        mProgressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dip2px(3), 0, 0));
        addView(mProgressBar);
        // 让webview对象支持解析alert()等特殊的javascript语句.该类中主要有网页内部数据变化的回调
        setWebChromeClient(new MyWebChromeClient(this));
        // 不写这句，点击超链地址会跳出程序到浏览器中访问网页。该类中主要是webview加载相关
        setWebViewClient(new MyWebViewClient());
        WebSettings set = getSettings();
        set.setJavaScriptEnabled(true);//可以使用js
        set.setAppCacheEnabled(true);
        set.setBlockNetworkImage(true);//部分人反映加载太慢，此处暂时阻止第一次的图片下载，加载完成再下载图片
        set.setDomStorageEnabled(true);//
        set.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        set.setUseWideViewPort(true);
        set.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//api大于21时默认不使用http和https混合请求
            set.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public int dip2px(float dipValue) {
        return (int) (dipValue * getResources().getDisplayMetrics().density + 0.5f);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // set,getClient相关
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 见下方
     */
    @RequiresApi(999)
    @Deprecated
    @Override
    public void setWebChromeClient(WebChromeClient client) {
        throw new RuntimeException("请使用下面的方法");
    }

    public void setWebChromeClient(MyWebChromeClient client) {
        mWebChromeClient = client;
        super.setWebChromeClient(client);
    }

    /**
     * 原生的api26才有
     */
    @Override
    public MyWebChromeClient getWebChromeClient() {
        return mWebChromeClient;
    }

    /**
     * 见下方
     */
    @RequiresApi(999)
    @Deprecated
    @Override
    public void setWebViewClient(WebViewClient client) {
        throw new RuntimeException("请使用下面的方法");
    }

    public void setWebViewClient(MyWebViewClient client) {
        mWebViewClient = client;
        super.setWebViewClient(client);
    }

    /**
     * 原生的api26才有
     */
    @Override
    public MyWebViewClient getWebViewClient() {
        return mWebViewClient;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Client类
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 增加了进度条
     */
    public static class MyWebChromeClient extends WebChromeClient {
        public BaseWebView mBwv;

        public MyWebChromeClient(BaseWebView bwv) {
            mBwv = bwv;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mBwv.getContext() instanceof Activity && ((Activity) mBwv.getContext()).isFinishing())
                return;
            if (newProgress >= 100) {
                mBwv.mProgressBar.setVisibility(GONE);
                WebSettings set = view.getSettings();
                if (set.getBlockNetworkImage()) {//允许下载图片
                    set.setBlockNetworkImage(false);
                }
            } else {
                mBwv.mProgressBar.setVisibility(VISIBLE);
                mBwv.mProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    /**
     * 增加任意线程访问的{@link #mNowUrl}
     * 增加scheme拦截
     */
    public static class MyWebViewClient extends WebViewClient {

        public String mNowUrl;//当前正在访问的url,js回调用到

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT > 20) {
                String url = request.getUrl().toString();
                if (!url.startsWith("http") && url.contains(":")) {//不是http开头的scheme就隐式跳转
                    try {
                        view.getContext().startActivity(new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(url)));
                        return true;
                    } catch (Exception ignored) {
                    }
                } else {
                    mNowUrl = url;
                }
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Build.VERSION.SDK_INT < 21) {
                if (!url.startsWith("http") && url.contains(":")) {//不是http开头的scheme就隐式跳转
                    try {
                        view.getContext().startActivity(new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(url)));
                        return true;
                    } catch (Exception ignored) {
                    }
                } else {
                    mNowUrl = url;
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
