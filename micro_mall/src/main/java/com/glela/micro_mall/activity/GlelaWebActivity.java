package com.glela.micro_mall.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.glela.micro_mall.BuildConfig;
import com.glela.micro_mall.R;
import com.glela.micro_mall.base.BaseActivity;
import com.glela.micro_mall.base.BaseWebView;
import com.glela.micro_mall.base.GlelaUrls;
import com.glela.micro_mall.base.MapUtils;
import com.glela.micro_mall.bean.WXCode;
import com.glela.micro_mall.bean.ZFBCode;
import com.glela.micro_mall.interfaceabstract.IUiController;
import com.glela.micro_mall.interfaceabstract.OKHttpListener;
import com.glela.micro_mall.utils.GlelaHttpUtils;
import com.glela.micro_mall.utils.GlelaUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import okhttp3.Request;
import okhttp3.RequestBody;

@Keep
public class GlelaWebActivity extends BaseActivity {

    /**
     * @param appId     协商的appId
     * @param userId    协商的用户Id
     * @param companyId 协商的公司Id
     * @param listener  当拉起支付时会回调此方法
     * @param lat       sdk需要位置信息
     * @param lng       sdk需要位置信息
     */
    public static void toThisActivity(Activity activity, @NonNull String appId, @NonNull String userId, String companyId, double lat, double lng, @NonNull OnWebListener listener) {
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(appId) || TextUtils.isEmpty(companyId)) {
            throw new RuntimeException("appId：" + appId + "userId：" + userId + "companyId：" + companyId + "为必传项");
        }
        mGlobalWebListener = listener;
        activity.startActivity(new Intent(activity, GlelaWebActivity.class)
                .putExtra(I_A, appId)
                .putExtra(I_B, userId)
                .putExtra(I_C, companyId)
                .putExtra(I_D, lat)
                .putExtra(I_E, lng));
    }

    /**
     * Destroy时已置null
     */
    private static OnWebListener mGlobalWebListener;

    private String mAppId, mUserId, mCompanyId;

    private double mLat, mLng;

    private ImageView mIvBack;
    private TextView mTvTitle;
    private BaseWebView mBwv;


    @Override
    protected int getLayouRes() {
        return R.layout.activity_glela_web;
    }

    @Override
    protected void initData() {
        mAppId = getIntent().getStringExtra(I_A);
        mUserId = getIntent().getStringExtra(I_B);
        mCompanyId = getIntent().getStringExtra(I_C);
        mLat = getIntent().getDoubleExtra(I_D, 0);
        mLng = getIntent().getDoubleExtra(I_E, 0);

        mIvBack = findViewById(R.id.iv_glelaweb_back);
        mTvTitle = findViewById(R.id.tv_glelaweb_title);
        mBwv = findViewById(R.id.bwv_glelaweb);

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTvTitle.setText("商城");

        //添加js
        mBwv.addJavascriptInterface(new JSClass(this, mBwv), "JSAndroid");
        mBwv.setWebChromeClient(new BaseWebChromeClient(this, mBwv) {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTvTitle.setText(view.getTitle());
            }
        });

        mBwv.loadUrl(GlelaUrls.WEB_HOME + "thirdAppId=" + mAppId + "&thirdUserId=" + mUserId +
                "&companyId=" + mCompanyId + "&lat=" + mLat + "&lng=" + mLng);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void onDestroy() {
        mGlobalWebListener = null;
        ((ViewGroup) mBwv.getParent()).removeView(mBwv);
        mBwv.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mBwv.canGoBack()) {
            mBwv.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * js回调类
     */
    @Keep
    public static class JSClass {

        private final IUiController mController;
        private final BaseWebView mWebView;

        /**
         * 没有自定义WebViewClient,如果自定义请使用三参构造
         */
        public JSClass(@NonNull IUiController controller, @NonNull BaseWebView wv) {
            mController = controller;
            mWebView = wv;
            mWebView.getWebViewClient().mNowUrl = wv.getUrl();
        }

        ///////////////////////////////////////////////////////////////////////////
        // 以下是js回调方法
        ///////////////////////////////////////////////////////////////////////////

        @JavascriptInterface
        public String getVersion() {
            return BuildConfig.VERSION_NAME;
        }

        @JavascriptInterface
        public void h5Finish() {
            mController.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mController.getActivity().finish();
                }
            });
        }

        /**
         * h5根据订单号请求支付
         *
         * @param payWay  支付方式:1微信，2支付宝
         * @param orderSn 订单号
         */
        @JavascriptInterface
        public void requestPaySupportWithPayWay(final String payWay, final String orderSn, final String userId, final String userToken) {
            mController.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int payType = Integer.parseInt(payWay);
                    //入参
                    MapUtils map = MapUtils.getHttpInstance().put("orderSn", orderSn).put("payType", payType).put(GlelaHttpUtils.KEY_USERID, Long.parseLong(userId));
                    //请求体
                    final Request.Builder request = new Request.Builder().post(RequestBody.create(GlelaHttpUtils.mMediaType, map.toString())).addHeader(GlelaHttpUtils.KEY_USERTOKEN, userToken);
                    //回调
                    final OnThirdResultListener resultListener = new OnThirdResultListener() {
                        @Override
                        public void onPayResult(int status) {
                            super.onPayResult(status);
                            if (mController instanceof GlelaWebActivity) {
                                ((GlelaWebActivity) mController).mBwv.loadUrl("javascript:payResultWithPayWay(" + status + ")");
                            }
                        }
                    };
                    //请求获取支付信息
                    switch (payType) {
                        case PAY_WX:
                            GlelaHttpUtils.httpCustom(mController, GlelaUrls.PAY_WX, request, WXCode.WXCodeData.class, GlelaHttpUtils.mClient, new OKHttpListener<WXCode.WXCodeData>() {
                                @Override
                                public void onSuccess(WXCode.WXCodeData bean) {
                                    if (mGlobalWebListener != null) {
                                        mGlobalWebListener.onWxPay(mController.getActivity(), bean.data, resultListener);
                                    }
                                }
                            });
                            break;
                        case PAY_ALI:
                            GlelaHttpUtils.httpCustom(mController, GlelaUrls.PAY_ALI, request, ZFBCode.ZFBCodeData.class, GlelaHttpUtils.mClient, new OKHttpListener<ZFBCode.ZFBCodeData>() {
                                @Override
                                public void onSuccess(ZFBCode.ZFBCodeData bean) {
                                    if (mGlobalWebListener != null) {
                                        mGlobalWebListener.onAliPay(mController.getActivity(), bean.data.url_param, resultListener);
                                    }
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    /**
     * WebChromeClient类
     * 增加上传返回图片
     */
    @Keep
    public static class BaseWebChromeClient extends BaseWebView.MyWebChromeClient {

        private final IUiController mController;

        public BaseWebChromeClient(IUiController controller, BaseWebView bwv) {
            super(bwv);
            mController = controller;
        }
//
//        /**
//         * <3.0
//         */
//        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
//        }
//
//        /**
//         * <4.0
//         */
//        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
//        }

        /**
         * <4.4和4.4.3,如果使用混淆必须忽略此方法,不然会被混淆掉导致无法回调
         */
        public void openFileChooser(final ValueCallback<Uri> uploadFile, String acceptType, String capture) {
            if (mGlobalWebListener != null) {
                mGlobalWebListener.onPhotoSelect(mController.getActivity(), new OnThirdResultListener() {
                    @Override
                    public void onPhotoResult(String path) {
                        super.onPhotoResult(path);
                        uploadFile.onReceiveValue(TextUtils.isEmpty(path) ? null : Uri.parse(path));
                    }
                });
            }
        }

        //4.4,4.4.1,4.4.2无解,可以尝试第三方webView或js回调来解决这问题

        /**
         * 5.0及以上
         */
        @TargetApi(21)
        @Override
        public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mGlobalWebListener != null) {
                mGlobalWebListener.onPhotoSelect(mController.getActivity(), new OnThirdResultListener() {
                    @Override
                    public void onPhotoResult(String path) {
                        super.onPhotoResult(path);
                        filePathCallback.onReceiveValue(TextUtils.isEmpty(path) ? null : new Uri[]{GlelaUtils.getMediaContentUri(mController.getBaseActivity(), path)});
                    }
                });
            }
            return true;
        }

        // 一个回调接口使用的主机应用程序通知当前页面的自定义视图已被撤职
        private CustomViewCallback mCustomViewCallback;
        private View mVideoView = null;//视频播放的view
        private OnUiStatusChangedListener mStatusListener;

        /**
         * 进入全屏的时候
         */
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            //正在全屏则立即退出全屏
            if (mCustomViewCallback != null) {
                onHideCustomView();
            }
            // 赋值给callback
            mCustomViewCallback = callback;
            // 声明video，把之后的视频放到这里面去
            ViewGroup p = (ViewGroup) mController.getActivity().getWindow().getDecorView();
            // 将video放到当前视图中
            mVideoView = view;
            view.setBackgroundColor(0xffffffff);
            p.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // 横屏显示
            mController.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            // 设置全屏
            // 设置全屏的相关属性，获取当前的屏幕状态，然后设置全屏
            mController.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // 全屏下的状态码：1098974464
            // 窗口下的状态吗：1098973440

            if (mStatusListener == null) {
                mStatusListener = new OnActivityStatusChangedListener() {
                    @Override
                    public int onActivityKeyDown(BaseActivity activity, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && mVideoView != null) {
                            //如果mVideoView!=null说明是全屏状态,需要拦截返回键,此时系统会自动调用onHideCustomView
                            return KEY_STATUS_TRUE;
                        }
                        return super.onActivityKeyDown(activity, keyCode, event);
                    }
                };
            }
            mController.addUiStatusChangedListener(mStatusListener);//添加事件拦截key来退出全屏
        }

        /**
         * 退出全屏的时候
         */
        @Override
        public void onHideCustomView() {
            if (mCustomViewCallback != null) {
                // 隐藏掉
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
            }
            // 用户当前的首选方向
            mController.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ViewGroup p = (ViewGroup) mController.getActivity().getWindow().getDecorView();
            p.removeView(mVideoView);
            mVideoView = null;
            mController.removeUiStatusChangedListener(mStatusListener);
            // 退出全屏
            // 声明当前屏幕状态的参数并获取
            final WindowManager.LayoutParams attrs = mController.getActivity().getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mController.getActivity().getWindow().setAttributes(attrs);
            mController.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 回调
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public interface OnWebListener {
        /**
         * @param activity 当前Activity
         * @param listener 当支付有结果时请调用{@link OnThirdResultListener#onPayResult}
         */
        void onWxPay(Activity activity, WXCode wxCode, OnThirdResultListener listener);

        void onAliPay(Activity activity, String payData, OnThirdResultListener listener);

        /**
         * @param listener 当选择完图片时请调用{@link OnThirdResultListener#onPhotoResult}
         */
        void onPhotoSelect(Activity activity, OnThirdResultListener listener);
    }

    public static abstract class OnThirdResultListener {
        /**
         * 当支付有结果时必须回调此方法
         *
         * @param status 支付结果：0失败，1成功
         */
        public void onPayResult(int status) {
        }

        /**
         * 当选择完图片时
         *
         * @param path 图片的绝对路径，如果取消请传null
         */
        public void onPhotoResult(@Nullable String path) {
        }
    }

    public static final int PAY_WX = 1, PAY_ALI = 2;

    @IntDef({PAY_WX, PAY_ALI})
    @Retention(RetentionPolicy.SOURCE)
    public @interface pay {
    }//该变量只能传入上面几种,否则会报错
}
