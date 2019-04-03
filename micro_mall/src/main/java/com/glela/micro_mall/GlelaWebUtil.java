package com.glela.micro_mall;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

import com.glela.micro_mall.activity.GlelaWebActivity;

public class GlelaWebUtil {

    public static Application mGlelaApp;

    /**
     * @param appId     协商的appId
     * @param userId    协商的用户Id
     * @param companyId 协商的公司Id
     * @param listener  当拉起支付时会回调此方法
     * @param lat       sdk需要位置信息
     * @param lng       sdk需要位置信息
     */
    public static void toWebActivity(Activity activity, @NonNull String appId, @NonNull String userId, String companyId,
                                     double lat, double lng, @NonNull GlelaWebActivity.OnWebListener listener) {
        if (mGlelaApp == null) mGlelaApp = activity.getApplication();
        GlelaWebActivity.toThisActivity(activity, appId, userId, companyId, lat, lng, listener);
    }
}
