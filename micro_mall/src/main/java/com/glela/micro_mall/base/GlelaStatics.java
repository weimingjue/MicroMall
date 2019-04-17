package com.glela.micro_mall.base;

import android.app.Application;

/**
 * 商城保存的静态属性值，仅限内部使用，切勿修改
 */
public class GlelaStatics {

    public static Application mGlelaApp;

    /**
     * 0正式，1测试
     */
    public static int mModelPosition = 0;
}
