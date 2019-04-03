package com.wang.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.glela.micro_mall.GlelaWebUtil;
import com.glela.micro_mall.activity.GlelaWebActivity;
import com.glela.micro_mall.bean.WXCode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //测试代码
        GlelaWebUtil.toWebActivity(this, "z02", "b9x742dv602xmn7v3cn7", "21", 1d, 1d,
                new GlelaWebActivity.OnWebListener() {

                    @Override
                    public void onWxPay(Activity activity, WXCode wxCode, GlelaWebActivity.OnThirdResultListener listener) {
                        //微信支付
                        //...此处省略支付操作
                        //当支付成功时传1
                        listener.onPayResult(1);
                        //当支付失败时传0
                        //listener.onPayResult(payType, 1);
                    }

                    @Override
                    public void onAliPay(Activity activity, String payData, GlelaWebActivity.OnThirdResultListener listener) {
                        //支付宝支付同理
                    }

                    @Override
                    public void onPhotoSelect(Activity activity, GlelaWebActivity.OnThirdResultListener listener) {
                        //选择图片逻辑
                        //....此处省略选择图片操作
                        //无论是否选择了图片都必须回调
                        //选择了图片
                        listener.onPhotoResult("图片绝对路径");
                        //未选择图片
                        //listener.onPhotoResult(null);
                    }
                });
    }
}
