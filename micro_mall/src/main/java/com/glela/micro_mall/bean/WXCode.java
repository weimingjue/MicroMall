package com.glela.micro_mall.bean;

import com.glela.micro_mall.base.BaseBean;

import java.io.Serializable;

public class WXCode implements Serializable {
    public static class WXCodeData extends BaseBean {
        public WXCode data;
    }

    public String nonce_str;
    public String paySign;//对应sign
    public String appid;
    //public String sign;//支付是paySign
    public String trade_type;
    public String return_msg;
    public String result_code;
    public String mch_id;//对应partnerId
    public String return_code;
    public String prepay_id;
    public int timestamp;
}
