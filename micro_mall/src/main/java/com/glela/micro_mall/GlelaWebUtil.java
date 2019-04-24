package com.glela.micro_mall;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.glela.micro_mall.activity.GlelaWebActivity;
import com.glela.micro_mall.base.GlelaStatics;
import com.glela.micro_mall.base.GlelaUrls;
import com.glela.micro_mall.utils.GlelaUtils;

public class GlelaWebUtil {

    /**
     * 设置为debug模式（默认是正式）
     * 只能使用一次
     * 只能在第一行代码
     * <p>
     * 建议在Application初始化中设置如下：
     * if (BuildConfig.DEBUG) {
     * GlelaWebUtil.setDebug();
     * }
     */
    public static void setDebug() {
        GlelaStatics.mModelPosition = 1;
        System.out.println(GlelaUrls.class);//设置完成后直接加载，不再允许修改
    }

    /**
     * 跳到商城，所有参赛均为非空必传项
     *
     * @param appId     协商的appId
     * @param userId    协商的用户Id
     * @param companyId 协商的公司Id
     * @param listener  当拉起支付时会回调此方法
     * @param timestamp 生成签名时的时间戳
     * @param sign      签名，用于验证
     * @param lat       sdk需要位置信息
     * @param lng       sdk需要位置信息
     */
    public static void toWebActivity(Activity activity, @NonNull String appId, @NonNull String userId, String companyId, long timestamp,
                                     String sign, double lat, double lng, @NonNull GlelaWebActivity.OnWebListener listener) {
        if (GlelaStatics.mGlelaApp == null) {
            GlelaStatics.mGlelaApp = activity.getApplication();
        }
        if (GlelaStatics.mModelPosition != 0) {
            GlelaUtils.toast("当前为测试环境，正式环境不会提示这句");
        }
        GlelaWebActivity.toThisActivity(activity, appId, userId, companyId, timestamp, sign, lat, lng, listener);
    }
}
