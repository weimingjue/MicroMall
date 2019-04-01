package com.glela.micro_mall.interfaceabstract;

/**
 * 主要是解决自定义view回调命名繁琐的问题
 * 实现类:自己写,很简单,把那些乱七八糟的回调名改掉,例:
 * public void setOnItemClickListener(OnOkClickListener<Void, Integer> listener) {
 * mListener = listener;
 * }
 */
public interface OnOkCancelClickListener<OBJ, DATA> {
    void clickOk(OBJ obj, DATA data);

    void clickCancel(OBJ obj);
}
