package com.glela.micro_mall.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.glela.micro_mall.interfaceabstract.IUiController;
import com.glela.micro_mall.interfaceabstract.OnOkCancelClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 对权限申请的封装
 * 如果一部分拒绝,一部分接受,回调的ok和cancel都会调用,自行判断
 * <p>
 * checkPermission(this, "这个权限很重要", new OnOkCancelClickListener<Void, Void>() {
 *
 * @Override public void clickOk(Void aVoid, Void aVoid2) {
 * }
 * @Override public void clickCancel(Void aVoid) {
 * }
 * }, Manifest.permission.SEND_SMS);
 */
@MainThread
public final class PermissionUtil {
    //检查权限
    private static boolean mIsRePermission = false;
    private static final int mRequestCode = 998;
    private static List<String> mListYunXu;

    public static void checkPermission(IUiController uc, final String reShowText,
                                       final OnOkCancelClickListener<Void, Void> listener, final String permission) {
        if (Build.VERSION.SDK_INT > 22 && ContextCompat.checkSelfPermission(uc.getActivity(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            //没有权限
            uc.addUiStatusChangedListener(new IUiController.OnUiStatusChangedListener() {
                @Override
                public void onUiRequestPermissionsResult(IUiController activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                    super.onUiRequestPermissionsResult(activity, requestCode, permissions, grantResults);
                    //是否删除自己,默认每次申请都删除回调,当再次申请时不需要删除
                    boolean isRemoveListener = true;
                    if (requestCode == mRequestCode) {
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            listener.clickOk(null, null);
                        } else {
//                            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
//                                    Manifest.permission.READ_CONTACTS)) {
//                                //没有权限继续申请
//                                Utils.Toast(reShowText);
//                                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
//                            } else {
//                                //彻底拒绝
//                                listener.clickCancel("");
//                            }
                            //上面检查很多机型返回总是false,更改为二次请求,如果再拒绝就不再申请
                            if (!mIsRePermission) {
                                if (!TextUtils.isEmpty(reShowText)) {
                                    Toast.makeText(activity.getBaseActivity(), reShowText, Toast.LENGTH_SHORT).show();
                                }
                                isRemoveListener = false;
                                ActivityCompat.requestPermissions(activity.getActivity(), new String[]{permission}, mRequestCode);
                                mIsRePermission = true;
                            } else {
                                mIsRePermission = false;
                                listener.clickCancel(null);
                            }
                        }
                    }
                    if (isRemoveListener) {
                        activity.removeUiStatusChangedListener(this);
                    }
                }
            });
            ActivityCompat.requestPermissions(uc.getActivity(), new String[]{permission}, mRequestCode);
        } else {
            listener.clickOk(null, null);
        }
    }

    /**
     * 申请多个权限,部分拒绝会同时回调ok和cancel
     *
     * @param reShowText  当被拒绝时的吐司
     * @param listener    list<String>是同意/拒绝的权限集合
     * @param permissions 权限Manifest.permission.ACCESS_COARSE_LOCATION等
     */
    public static void checkPermissions(IUiController uc, final String reShowText,
                                        final OnOkCancelClickListener<List<String>, Void> listener, final String... permissions) {
        if (mListYunXu == null) mListYunXu = new ArrayList<>();
        else mListYunXu.clear();
        if (Build.VERSION.SDK_INT > 22) {
            final ArrayList<String> shouldPermission = new ArrayList<>();
            for (String p : permissions) {
                if (ContextCompat.checkSelfPermission(uc.getActivity(), p)
                        != PackageManager.PERMISSION_GRANTED) {
                    shouldPermission.add(p);
                }
            }

            //有需要申请的权限
            if (shouldPermission.size() > 0) {
                uc.addUiStatusChangedListener(new IUiController.OnUiStatusChangedListener() {
                    @Override
                    public void onUiRequestPermissionsResult(IUiController activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        super.onUiRequestPermissionsResult(activity, requestCode, permissions, grantResults);
                        //是否删除自己,默认每次申请都删除回调,当再次申请时不需要删除
                        boolean isRemoveListener = true;
                        if (requestCode == mRequestCode) {
                            ArrayList<String> listJuJue = new ArrayList<>();
                            //分别存拒绝和允许的权限
                            for (int i = 0; i < grantResults.length; i++) {
                                if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                                    listJuJue.add(shouldPermission.get(i));
                                else mListYunXu.add(shouldPermission.get(i));
                            }
                            //如果有拒绝的权限就继续申请
                            if (listJuJue.size() > 0) {
                                if (!mIsRePermission) {
                                    if (!TextUtils.isEmpty(reShowText)) {
                                        Toast.makeText(activity.getBaseActivity(), reShowText, Toast.LENGTH_SHORT).show();
                                    }
                                    isRemoveListener = false;
                                    requestPermissions(activity.getActivity(), listJuJue);
                                    mIsRePermission = true;
                                } else {
                                    mIsRePermission = false;
                                    listener.clickCancel(listJuJue);
                                }
                            } else {
                                listener.clickOk(mListYunXu, null);
                            }
                        }
                        if (isRemoveListener) {
                            activity.removeUiStatusChangedListener(this);
                        }
                    }
                });
                requestPermissions(uc.getActivity(), shouldPermission);
            } else {
                Collections.addAll(mListYunXu, permissions);
                listener.clickOk(mListYunXu, null);
            }
        } else {
            Collections.addAll(mListYunXu, permissions);
            listener.clickOk(mListYunXu, null);
        }
    }

    public static void checkPermissionsDialog(final IUiController uc, final String reShowText, @NonNull Dialog dialog,
                                              final OnOkCancelClickListener<List<String>, Void> listener, final String... permissions) {
        if (mListYunXu == null) mListYunXu = new ArrayList<>();
        else mListYunXu.clear();
        if (Build.VERSION.SDK_INT > 22) {
            final ArrayList<String> shouldPermission = new ArrayList<>();
            for (String p : permissions) {
                if (ContextCompat.checkSelfPermission(uc.getActivity(), p)
                        != PackageManager.PERMISSION_GRANTED) {
                    shouldPermission.add(p);
                }
            }

            //有需要申请的权限
            if (shouldPermission.size() > 0) {
                dialog.show();
                dialog.setCancelable(false);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        uc.addUiStatusChangedListener(new IUiController.OnUiStatusChangedListener() {
                            @Override
                            public void onUiRequestPermissionsResult(IUiController activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                                super.onUiRequestPermissionsResult(activity, requestCode, permissions, grantResults);
                                //是否删除自己,默认每次申请都删除回调,当再次申请时不需要删除
                                boolean isRemoveListener = true;
                                if (requestCode == mRequestCode) {
                                    ArrayList<String> listJuJue = new ArrayList<>();
                                    //分别存拒绝和允许的权限
                                    for (int i = 0; i < grantResults.length; i++) {
                                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                                            listJuJue.add(shouldPermission.get(i));
                                        else mListYunXu.add(shouldPermission.get(i));
                                    }
                                    //如果有拒绝的权限就继续申请
                                    if (listJuJue.size() > 0) {
                                        if (!mIsRePermission) {
                                            if (!TextUtils.isEmpty(reShowText)) {
                                                Toast.makeText(activity.getBaseActivity(), reShowText, Toast.LENGTH_SHORT).show();
                                            }
                                            isRemoveListener = false;
                                            requestPermissions(activity.getActivity(), listJuJue);
                                            mIsRePermission = true;
                                        } else {
                                            mIsRePermission = false;
                                            listener.clickCancel(listJuJue);
                                        }
                                    } else {
                                        listener.clickOk(mListYunXu, null);
                                    }
                                }
                                if (isRemoveListener) {
                                    activity.removeUiStatusChangedListener(this);
                                }
                            }
                        });
                        requestPermissions(uc.getActivity(), shouldPermission);
                    }
                });
            } else {
                Collections.addAll(mListYunXu, permissions);
                listener.clickOk(mListYunXu, null);
            }
        } else {
            Collections.addAll(mListYunXu, permissions);
            listener.clickOk(mListYunXu, null);
        }
    }

    private static void requestPermissions(Activity activity, ArrayList<String> permissions) {
        ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]), mRequestCode);
    }
}
