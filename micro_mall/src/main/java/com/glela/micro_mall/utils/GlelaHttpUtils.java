package com.glela.micro_mall.utils;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.glela.micro_mall.base.BaseBean;
import com.glela.micro_mall.interfaceabstract.HttpInterface;
import com.glela.micro_mall.interfaceabstract.OKHttpListener;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * okhttp的经典封装类,目前不支持文件
 * 所有的bean必须继承于basebean
 * 最好不要把interface传null
 * <p/>
 * GlelaHttpUtils.postDialog(this, Constans.A, MapUtils.getHttpInstance("a", 1).put("b", 2), XXX.class(继承BaseBean的), new OKHttpListener<XXX>() {
 *
 * @Override public void onSuccess(XXX bean) {
 * //此处是服务器返回10000
 * }
 * @Override public void onNetworkError(BaseBean baseBean) {
 * super.onNetworkError(baseBean);
 * //此处是网络连接失败,super是吐司
 * }
 * @Override public void onServiceError(String info) {
 * super.onServiceError(info);
 * //此处是服务器返回其他状态,super是吐司
 * }
 * @Override public void onNext(BaseBean baseBean) {
 * //此处无论成功失败必走
 * }
 * });
 */
public final class GlelaHttpUtils {

    //json中的字段
    public static final String KEY_USERTOKEN = "userToken", KEY_USERID = "userId";

    public static final OkHttpClient mClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    //类型 utf-8
    public static final MediaType mMediaType = MediaType.parse("application/json;charset=UTF-8");

    ///////////////////////////////////////////////////////////////////////////
    // 以下是http公共方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 自定义异步请求，支付可能等待时间很长，增加client入参，没有默认数据需要自己手动增加
     */
    public static <T extends BaseBean> void httpCustom(final HttpInterface httpInterface, final String httpUrl,
                                                       Request.Builder builder, final Class<T> mClass, final OkHttpClient client, final OKHttpListener<T> listener) {
        new AsyncTask<Request.Builder, Void, BaseBean>() {
            @Override
            protected void onPostExecute(BaseBean baseBean) {
                super.onPostExecute(baseBean);
                //如果activity要求丢弃数据
                if (httpInterface != null && httpInterface.isDiscardHttp()) return;
                if (baseBean.httpCode == OKHttpListener.CODE_200) {
                    if (baseBean.code == OKHttpListener.CODE_SUCCESS)
                        listener.onSuccess((T) baseBean);
                    else listener.onServiceError(baseBean);
                } else {
                    listener.onNetworkError(baseBean);
                }
                listener.onNext(baseBean);
            }

            @Override
            protected BaseBean doInBackground(Request.Builder... params) {
                params[0].tag(httpUrl).url(httpUrl);

                try {
                    Response response = client.newCall(params[0].build()).execute();
                    String body = response.body().string();
                    if (response.code() == OKHttpListener.CODE_200) {
                        try {
                            BaseBean bean = JSON.parseObject(body, mClass, Feature.SupportNonPublicField);//支持私有变量
                            bean.httpCode = response.code();
                            bean.response = body;
                            bean.httpUrl = httpUrl;
                            return bean;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new BaseBean(OKHttpListener.CODE_JSONEXCEPTION, null, body, httpUrl, response.headers());
                        }
                    } else {
                        return new BaseBean(response.code(), null, body, httpUrl, response.headers());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return new BaseBean(OKHttpListener.CODE_CONNECTXCEPTEION, null, null, httpUrl, null);
                }
            }
        }.execute(builder);
    }
}
