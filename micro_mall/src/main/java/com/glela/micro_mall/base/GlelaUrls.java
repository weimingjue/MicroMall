package com.glela.micro_mall.base;

public interface GlelaUrls {
//    String HTTP_HEADER = "http://api.app.glela.com/yg_ygapp";
    String HTTP_HEADER = "http://api.test.app.glela.cn/yg_ygapp";


    String WEB_HOME = "http://h5.test.glela.cn/h5_jiFen/?";
    String PAY_WX = HTTP_HEADER + "order/wecha/weChaPay";
    String PAY_ALI = HTTP_HEADER + "order/alipay/getOrderString";
}
