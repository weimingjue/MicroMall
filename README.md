# 供第三方使用，示例如下

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
`implementation（或api） 'com.github.weimingjue:'`
