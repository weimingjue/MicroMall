package com.glela.micro_mall.base;

public interface GlelaUrls {
    /**
     * 0正式，1测试
     */
    int HEADER_POSITION = 1;
    String[][] HEADERS = {{"正式", "http://api.app.glela.com/yg_ygapp", "http://h5.mall.glela.com/"},
            {"测试", "http://api.test.app.glela.cn/yg_ygapp", "http://h5mall.test.glela.cn/"}};

    String URL_HTTP_HEADER = HEADERS[HEADER_POSITION][1];
    String URL_H5 = HEADERS[HEADER_POSITION][2];

    String WEB_HOME = URL_H5 + "?";//改成了二级域名的根目录
    String PAY_WX = URL_HTTP_HEADER + "order/wecha/weChaPay";
    String PAY_ALI = URL_HTTP_HEADER + "order/alipay/getOrderString";
}
