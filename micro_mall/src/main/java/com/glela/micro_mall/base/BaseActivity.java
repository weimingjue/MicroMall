package com.glela.micro_mall.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.glela.micro_mall.GlelaWebUtil;
import com.glela.micro_mall.interfaceabstract.IUiController;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * 所有activity的基类
 * 界面跳转标准写法,fragment同理
 * private static final String INTENT_TITLE=I_A;
 * public static void toThisActivity(Activity activity,String title){
 * activity.startActivity(new Intent(activity,BaseActivity.class)
 * .putExtra(INTENT_TITLE,title));
 * }
 * 跳转的标准写法
 * BaseActivity.toThisActivity("呵呵");
 * 取值的标准写法
 * String title = getIntent().getStringExtra(INTENT_TITLE);
 */
public abstract class BaseActivity extends AppCompatActivity implements IUiController {
    public final String TAG = getClass().getSimpleName();
    public static final Handler mGlobalHandler = new Handler();
    @Nullable
    private ArrayList<OnUiStatusChangedListener> mListStatusListener;
    ///////////////////////////////////////////////////////////////////////////
    // 跳转相关字段
    ///////////////////////////////////////////////////////////////////////////
    public static final String INTENT_FROM = "from";
    protected static final String I_A = "A", I_B = "B", I_C = "C", I_D = "D",
            I_E = "E", I_F = "F", I_G = "G", I_H = "H", I_I = "I", I_J = "J";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 以下是新增常用方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获得ColorInt值
     */
    @ColorInt
    public final int getColorInt(@ColorRes int id) {
        return ContextCompat.getColor(this, id);
    }

    /**
     * 界面跳转简化版
     */
    @Override
    public final void startActivity(Class activityClass) {
        startActivity(new Intent(getActivity(), activityClass));
    }

    @Override
    public final BaseActivity getActivity() {
        return this;
    }

    /**
     * 添加一个Activity的onCreate,onStart,Result,Permission等状态变化监听
     * 注意:多次添加内部类会被多次回调,请及时remove掉
     */
    @Override
    public final void addUiStatusChangedListener(@NonNull final OnUiStatusChangedListener listener) {
        if (mListStatusListener == null) {
            mListStatusListener = new ArrayList<>();
        }
        //验重
        for (OnUiStatusChangedListener oascl : mListStatusListener) {
            if (oascl == listener) return;
        }
        //可能出现mListStatusListener遍历时又add了一个
        mGlobalHandler.post(new Runnable() {
            @Override
            public void run() {
                mListStatusListener.add(listener);
            }
        });
    }

    /**
     * 删除状态监听
     *
     * @param listener null表示全部删除
     */
    @Override
    public final void removeUiStatusChangedListener(@Nullable final OnUiStatusChangedListener listener) {
        //可能出现mListStatusListener遍历时又add了一个
        mGlobalHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListStatusListener != null) {
                    if (listener == null) {
                        mListStatusListener.clear();
                    } else {
                        mListStatusListener.remove(listener);
                    }
                }
            }
        });
    }

    @Override
    public final ArrayList<OnUiStatusChangedListener> getUiStatusChangedListener() {
        return mListStatusListener;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 抽象方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 返回布局资源
     *
     * @return 如果小于1（使用代码new的没有布局资源）则必须提前为mView赋值，具体见某些实现类
     */
    @LayoutRes
    protected abstract int getLayouRes();

    protected abstract void initData();

    protected abstract void setListener();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // OnUiStatusChangedListener的拓展,增加onRestart,onNewIntent,onKeyDown的回调
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class OnActivityStatusChangedListener extends OnUiStatusChangedListener {
        public static final int KEY_STATUS_FALSE = 1, KEY_STATUS_TRUE = 2, KEY_STATUS_SUPER = 4;

        @IntDef({KEY_STATUS_FALSE, KEY_STATUS_TRUE, KEY_STATUS_SUPER, KEY_STATUS_FALSE | KEY_STATUS_SUPER, KEY_STATUS_TRUE | KEY_STATUS_SUPER})
        @Retention(RetentionPolicy.SOURCE)
        private @interface keyStatus {
        }//该变量只能传入上面几种,否则会报错

        public void onActivityRestart(BaseActivity activity) {
        }

        public void onActivityNewIntent(BaseActivity activity, Intent intent) {
        }

        /**
         * Activity的onKeyDown事件
         * false|super先调用super,然后直接返回false
         *
         * @return false, true, super, false|super,ture|super就这几种
         */
        @keyStatus
        public int onActivityKeyDown(BaseActivity activity, int keyCode, KeyEvent event) {
            return KEY_STATUS_SUPER;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 以下方法是IUiController相关代码,均不重要,可以暂时忽略
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isUiDestroyed() {
        return isFinishing();
    }

    @NonNull
    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }

    @Override
    public void finishUi() {
        finish();
    }

    /**
     * {@link IUiController}的接口,默认finish时丢弃(因为可能崩溃)
     */
    @Override
    public boolean isDiscardHttp() {
        return isFinishing();
    }

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (GlelaWebUtil.mGlelaApp == null) GlelaWebUtil.mGlelaApp = getApplication();
        if (getRequestedOrientation() < 0) {//没有就默认竖屏,有就跳过
            throw new RuntimeException("必须在xml中配置android:screenOrientation=\"${SCREENORIENTATION}\"");
        }
        if (getWindow().getAttributes().softInputMode <= 0) {//没有就默认隐藏键盘,有就跳过
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        int layouRes = getLayouRes();
        if (layouRes > 0) {
            setContentView(layouRes);
        }
        ButterKnife.bind(this);//注册黄油刀
        initData();
        setListener();

        if (mListStatusListener != null) {
            for (OnUiStatusChangedListener listener : mListStatusListener) {
                listener.onUiCreated(this, savedInstanceState);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OnActivityStatusChangedListener相关
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        if (mListStatusListener != null) {
            for (OnUiStatusChangedListener listener : mListStatusListener) {
                listener.onUiStarted(this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListStatusListener != null) {
            for (OnUiStatusChangedListener listener : mListStatusListener) {
                listener.onUiResumed(this);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mListStatusListener != null) {
            for (OnUiStatusChangedListener listener : mListStatusListener) {
                listener.onUiPaused(this);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mListStatusListener != null) {
            for (OnUiStatusChangedListener listener : mListStatusListener) {
                listener.onUiStopped(this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListStatusListener != null) {
            for (OnUiStatusChangedListener listener : mListStatusListener) {
                listener.onUiSaveInstanceState(this, outState);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListStatusListener != null) {
            for (OnUiStatusChangedListener listener : mListStatusListener) {
                listener.onUiDestroyed(this);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mListStatusListener != null) {
            for (OnUiStatusChangedListener listener : mListStatusListener) {
                if (listener instanceof OnActivityStatusChangedListener) {
                    ((OnActivityStatusChangedListener) listener).onActivityRestart(this);
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mListStatusListener != null) {
            for (OnUiStatusChangedListener listener : mListStatusListener) {
                if (listener instanceof OnActivityStatusChangedListener) {
                    ((OnActivityStatusChangedListener) listener).onActivityNewIntent(this, intent);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mListStatusListener != null) {
            for (OnUiStatusChangedListener listener : mListStatusListener) {
                listener.onUiResult(this, requestCode, resultCode, data);
            }
        }
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mListStatusListener != null) {
            for (OnUiStatusChangedListener listener : mListStatusListener) {
                listener.onUiRequestPermissionsResult(this, requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mListStatusListener == null || mListStatusListener.size() == 0) {
            return super.onKeyDown(keyCode, event);
        } else {
            int allStatus = OnActivityStatusChangedListener.KEY_STATUS_SUPER;
            for (OnUiStatusChangedListener listener : mListStatusListener) {
                int status = allStatus;
                if (listener instanceof OnActivityStatusChangedListener) {
                    status = ((OnActivityStatusChangedListener) listener).onActivityKeyDown(this, keyCode, event);
                }
                if (allStatus > status) {//多个取最小的那个
                    allStatus = status;
                }
            }
            switch (allStatus) {//总共就5种
                case OnActivityStatusChangedListener.KEY_STATUS_FALSE:
                    return false;
                case OnActivityStatusChangedListener.KEY_STATUS_TRUE:
                    return true;
                case OnActivityStatusChangedListener.KEY_STATUS_FALSE | OnActivityStatusChangedListener.KEY_STATUS_SUPER:
                    super.onKeyDown(keyCode, event);
                    return false;
                case OnActivityStatusChangedListener.KEY_STATUS_TRUE | OnActivityStatusChangedListener.KEY_STATUS_SUPER:
                    super.onKeyDown(keyCode, event);
                    return true;
                case OnActivityStatusChangedListener.KEY_STATUS_SUPER:
                default:
                    return super.onKeyDown(keyCode, event);
            }
        }
    }
}
