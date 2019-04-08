package com.glela.micro_mall.bean;

import com.glela.micro_mall.base.BaseBean;

import java.io.Serializable;

public class ZFBCode implements Serializable {

    public static class ZFBCodeData extends BaseBean {
        public ZFBCode data;
    }

    public String partner;
    public String seller_id;
    public String out_trade_no;
    public String subject;
    public String body;
    public String total_fee;
    public String notify_url;
    public String service;
    public String payment_type;
    public String _input_charset;

    public String it_b_pay;
    public String sign;
    public String sign_type;
    public String url_param;//已经拼接好的数据，直接使用此值即可
}
