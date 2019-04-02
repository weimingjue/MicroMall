package com.glela.micro_mall;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.glela.micro_mall.activity.GlelaWebActivity;

public class GlelaWebUtil {

    /**
     * @param appId     协商的appId
     * @param userId    协商的用户Id
     * @param companyId 协商的公司Id
     * @param listener  当拉起支付时会回调此方法
     */
    public static void toWebActivity(Activity activity, @NonNull String appId, @NonNull String userId, String companyId, @NonNull GlelaWebActivity.OnWebListener listener) {
        GlelaWebActivity.toThisActivity(activity, appId, userId, companyId, listener);
    }
}
