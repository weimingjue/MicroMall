# 供第三方使用，示例如下

```
GlelaWebUtil.toWebActivity("activity", "商定的appId", "商定的用户Id", "商定的公司Id", "定位的lat", "定位的lng", new GlelaWebActivity.OnWebListener() {
    @Override
    public void onWxPay(Activity activity, WXCode wxCode, GlelaWebActivity.OnThirdResultListener listener) {
        //拉起微信支付，wxCode里面的内容为微信支付必需的数据，对应值见博客：https://blog.csdn.net/weimingjue/article/details/80047273
        //支付完成调用listener.onPayResult(0或1)
    }

    @Override
    public void onAliPay(Activity activity, String payData, GlelaWebActivity.OnThirdResultListener listener) {
        //拉起支付宝支付，payData为完整拼接好的值,可以直接拉起：alipay.pay(payData, true)
        //支付完成调用listener.onPayResult(0或1)
    }

    @Override
    public void onPhotoSelect(Activity activity, GlelaWebActivity.OnThirdResultListener listener) {
        //h5要上传图片，请使用第三方或原生打开图片选择器选择一张图片
        //无论是否选择了图片还是其他任何情况结束后都必须回调listener.onPhotoResult(null或"你的图片绝对路径")
    }
});
```
具体例子仅供参考
```
//首先申请获取定位loaction数据
//...
//然后调用util跳到web界面
GlelaWebUtil.toWebActivity(this, "z02", "b9x742dv602xmn7v3cn7", "21",
        loaction.getLatitude(), loaction.getLongitude(), new GlelaWebActivity.OnWebListener() {

            @Override
            public void onWxPay(Activity activity, WXCode wxCode, final GlelaWebActivity.OnThirdResultListener listener) {
                //调用微信支付，这里只是举例，如果想使用这种方式参考博客：https://blog.csdn.net/weimingjue/article/details/80047273
                //wxCode和微信的对应值也在博客里面
                WXPayEntryActivity.WXPay(activity, wxCode, new WXEntryActivity.OnWXListener() {

                    @Override
                    public void onWXSuccess(BaseResp resp) {
                        listener.onPayResult(1);//成功
                    }

                    @Override
                    public void onError(int errType, String errorMsg, @Nullable BaseResp resp) {
                        Utils.toast(errorMsg);
                        listener.onPayResult(0);//失败
                    }
                });
            }

            @Override
            public void onAliPay(Activity activity, String payData, final GlelaWebActivity.OnThirdResultListener listener) {
                //调用支付宝，payData已经拼接好了可以直接调用
                PayUtils.doAliPay(activity, payData, new OnPayAliListener() {
                    @Override
                    public void paySuccess(PayResult result) {
                        listener.onPayResult(1);
                    }

                    @Override
                    public void payWait(PayResult result) {
                        Utils.toast("支付结果等待中，请稍后查看");
                        listener.onPayResult(0);//暂定为失败
                    }

                    @Override
                    public void payError(PayResult result) {
                        listener.onPayResult(0);
                    }
                });
            }

            @Override
            public void onPhotoSelect(Activity activity, final GlelaWebActivity.OnThirdResultListener listener) {
                //调用选择图片1张，建议使用一些第三方框架
                //此处要注意一点，无论选没选都必须回调回去，不能只回调了选择图片的而不去管取消的
                GalleryFinal.openGalleryMuti(997, 1, new GalleryFinal.OnHanlderResultCallback() {
                    @Override
                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                        if (997 != reqeustCode || Utils.isEmptyArray(resultList)) {
                            listener.onPhotoResult(null);//没选择图片，一定要回调回去
                            return;
                        }
                        listener.onPhotoResult(resultList.get(0).getPhotoPath());//选择了图片
                    }

                    @Override
                    public void onHanlderFailure(int requestCode, String errorMsg) {
                        listener.onPhotoResult(null);//没选择图片，一定要回调回去
                    }
                });
            }
        });
```
## 导入方式
你的build.gradle要有jitpack.io，大致如下
```
    repositories {
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url 'https://jitpack.io' }
        google()
        jcenter()
    }
```
然后导入
`implementation（或api） 'com.github.weimingjue:MicroMall:0.97'`

测试环境在版本前面加上T即可（发版时别忘了改回来）

## 混淆相关
如果使用的是android的Proguard则不需要额外增加混淆逻辑

（一次性不混淆所有第三方可以参考：https://blog.csdn.net/weimingjue/article/details/84976058 ）

如果是其他混淆框架：不混淆GlelaWebActivity整个类、内部类及其属性即可
