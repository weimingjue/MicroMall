package com.wang.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.glela.micro_mall.GlelaWebUtil;
import com.glela.micro_mall.activity.GlelaWebActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //测试代码
        GlelaWebUtil.toWebActivity(this, "z02", "2yj1zjyklj018ojexwmzes", "21",
                new GlelaWebActivity.OnWebListener() {
                    @Override
                    public void onPay(Activity activity, int payType, String orderSn, GlelaWebActivity.OnThirdResultListener listener) {
                        switch (payType) {
                            case GlelaWebActivity.PAY_ALI://支付宝支付
                                //...
                                //当支付成功时传1
                                listener.onPayonPayResult(payType, 1);
                                //当支付失败时传0
                                //listener.onPayonPayResult(payType, 1);
                                break;
                            case GlelaWebActivity.PAY_WX://微信支付
                                //...
                                break;
                        }
                    }

                    @Override
                    public void onPhotoSelect(Activity activity, GlelaWebActivity.OnThirdResultListener listener) {
                        //选择图片逻辑
                        //....
                        //无论是否选择了图片都必须回调
                        listener.onPhotoResult("图片绝对路径");
                        //或
                        //listener.onPhotoResult(null);
                    }
                });
    }
}
