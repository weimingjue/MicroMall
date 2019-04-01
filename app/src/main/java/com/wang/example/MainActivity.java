package com.wang.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.glela.micro_mall.activity.GlelaWebActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //测试代码
        GlelaWebActivity.toThisActivity(this, "z02", "2yj1zjyklj018ojexwmzes", "21",
                new GlelaWebActivity.OnWebListener() {
                    @Override
                    protected void onPay(int payType, String orderSn) {
                        switch (payType) {
                            case GlelaWebActivity.PAY_ALI://支付宝支付
                                //...
                                //当支付成功时传1
                                onPayResult(payType, 1);
                                //当支付失败时传0
//                        onPayResult(payType, 0);
                                break;
                            case GlelaWebActivity.PAY_WX://微信支付
                                //...
                                break;
                        }
                    }
                });
    }
}
