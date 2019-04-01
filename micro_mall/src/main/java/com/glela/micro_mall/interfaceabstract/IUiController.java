package com.glela.micro_mall.interfaceabstract;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.glela.micro_mall.base.BaseActivity;

import java.util.ArrayList;

/**
 * 所有ui的接口,由基类实现,不需要单独实现
 * {@link BaseActivity}{@link BaseFragment}
 */
public interface IUiController extends HttpInterface {

    /**
     * 当前ui是否关闭了
     */
    boolean isUiDestroyed();

    /**
     * 调用关闭当前ui
     */
    void finishUi();

    /**
     * 获得当前的Activity,不会是null
     */
    @NonNull
    BaseActivity getBaseActivity();

    /**
     * 添加一个Activity的onCreate,onStart,Result,Permission等状态变化监听
     * 注意:多次添加内部类会被多次回调,请及时remove掉
     */
    void addUiStatusChangedListener(@NonNull OnUiStatusChangedListener listener);

    /**
     * 删除状态监听
     *
     * @param listener null表示全部删除
     */
    void removeUiStatusChangedListener(@Nullable OnUiStatusChangedListener listener);

    ArrayList<OnUiStatusChangedListener> getUiStatusChangedListener();

    android.support.v4.app.FragmentManager getSupportFragmentManager();

    void startActivity(Class activityClass);

    void startActivity(Intent intent);

    void startActivity(Intent intent, @Nullable Bundle options);

    void startActivityForResult(Intent intent, int requestCode);

    void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options);

    void runOnUiThread(Runnable action);

    /**
     * ui回调监听类
     * frag没有onRestart,onNewIntent,onKeyDown回调
     * 想使用这三个请监听Activity的{@link BaseActivity.OnActivityStatusChangedListener}
     */
    abstract class OnUiStatusChangedListener {

        public void onUiCreated(IUiController UiController, Bundle savedInstanceState) {
        }

        public void onUiStarted(IUiController UiController) {
        }

        public void onUiResumed(IUiController UiController) {
        }

        public void onUiPaused(IUiController UiController) {
        }

        public void onUiStopped(IUiController UiController) {
        }

        public void onUiSaveInstanceState(IUiController UiController, Bundle outState) {
        }

        public void onUiDestroyed(IUiController UiController) {
        }

        public void onUiResult(IUiController UiController, int requestCode, int resultCode, Intent data) {
        }

        public void onUiRequestPermissionsResult(IUiController UiController, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        }
    }
}
