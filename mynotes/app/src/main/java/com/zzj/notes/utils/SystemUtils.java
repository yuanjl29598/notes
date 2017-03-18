package com.zzj.notes.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.zzj.notes.NoteApplication;

import java.io.File;
import java.security.MessageDigest;
import java.util.List;


@SuppressLint("SimpleDateFormat")
public final class SystemUtils {

    /**
     * 获取手机系统SDK版本
     *
     * @return 如API 17 则返回 17
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取系统版本
     *
     * @return 形如2.3.3
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 调用系统发送短信
     */
    public static void sendSMS(Context cxt, String smsBody) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", smsBody);
        cxt.startActivity(intent);
    }

    /**
     * 判断网络是否连接
     */
    public static boolean checkNet(Context context) {
        if (context == null) {
            context = NoteApplication.getNoteApplication();
        }

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null;// 网络是否连接
    }


    /**
     * 判断是否为wifi联网
     */
    public static boolean isWiFi(Context cxt) {
        ConnectivityManager cm =
                (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        // wifi的状态：ConnectivityManager.TYPE_WIFI
        // 3G的状态：ConnectivityManager.TYPE_MOBILE
        State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        return State.CONNECTED == state;
    }

    /**
     * 隐藏系统键盘
     * <p>
     * <br>
     * <b>警告</b> 必须是确定键盘显示时才能调用
     */
    public static void hideKeyBoard(Activity aty) {
        ((InputMethodManager) aty.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(aty.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 安装apk
     *
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.package-archive");
        intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 获取当前应用程序的版本号
     * <p>
     * versionName
     */
    public static String getAppVersionName(Context context) {
        String version = "0";
        try {
            version =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            throw new RuntimeException(SystemUtils.class.getName() + "the application not found");
        }
        return version;
    }

    /**
     * 获取当前应用程序的版本序号
     * <p>
     * versionCode
     */
    public static int getAppVersionCode(Context context) {
        int version = 0;
        try {
            version =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException(SystemUtils.class.getName() + "the application not found");
        }
        return version;
    }

    /**
     * 回到home，后台运行
     */
    public static void goHome(Context context) {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    }

    /**
     * 获取应用签名
     *
     * @param context
     * @param pkgName
     */
    public static String getSign(Context context, String pkgName) {
        try {
            PackageInfo pis =
                    context.getPackageManager().getPackageInfo(pkgName,
                            PackageManager.GET_SIGNATURES);
            return hexdigest(pis.signatures[0].toByteArray());
        } catch (NameNotFoundException e) {
            throw new RuntimeException(SystemUtils.class.getName() + "the " + pkgName
                    + "'s application not found");
        }
    }

    /**
     * 将签名字符串转换成需要的32位签名
     */
    private static String hexdigest(byte[] paramArrayOfByte) {
        final char[] hexDigits =
                {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar = new char[32];
            for (int i = 0, j = 0; ; i++, j++) {
                if (i >= 16) {
                    return new String(arrayOfChar);
                }
                int k = arrayOfByte[i];
                arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
                arrayOfChar[++j] = hexDigits[(k & 0xF)];
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取设备的可用内存大小
     *
     * @param cxt 应用上下文对象context
     * @return 当前内存大小
     */
    public static int getDeviceUsableMemory(Context cxt) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        // 返回当前系统的可用内存
        return (int) (mi.availMem / (1024 * 1024));
    }

    /**
     * 清理后台进程与服务
     *
     * @param cxt 应用上下文对象context
     * @return 被清理的数量
     */
    public static int gc(Context cxt) {
        long i = getDeviceUsableMemory(cxt);
        int count = 0; // 清理掉的进程数
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取正在运行的service列表
        List<RunningServiceInfo> serviceList = am.getRunningServices(100);
        if (serviceList != null) for (RunningServiceInfo service : serviceList) {
            if (service.pid == android.os.Process.myPid()) continue;
            try {
                android.os.Process.killProcess(service.pid);
                count++;
            } catch (Exception e) {
                e.getStackTrace();
                continue;
            }
        }

        // 获取正在运行的进程列表
        List<RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        if (processList != null) for (RunningAppProcessInfo process : processList) {
            // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
            // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
            if (process.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                // pkgList 得到该进程下运行的包名
                String[] pkgList = process.pkgList;
                for (String pkgName : pkgList) {
                    // KJLoger.debug("======正在杀死包名：" + pkgName);
                    try {
                        am.killBackgroundProcesses(pkgName);
                        count++;
                    } catch (Exception e) { // 防止意外发生
                        e.getStackTrace();
                        continue;
                    }
                }
            }
        }
        // KJLoger.debug("清理了" + (getDeviceUsableMemory(cxt) - i) + "M内存");
        return count;
    }

    public static String getMetaData(Context context, String key) {
        if (!TextUtils.isEmpty(key)) {
            ApplicationInfo appInfo;
            try {
                appInfo =
                        context.getPackageManager().getApplicationInfo(context.getPackageName(),
                                PackageManager.GET_META_DATA);
                return appInfo.metaData.getString(key);
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return null;
    }

    public static boolean isTopActivity(Activity activity) {
        String packageName = activity.getPackageName();
        ActivityManager activityManager =
                (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            Log.e("", "isTopActivity " + tasksInfo.get(0).topActivity.getPackageName());
            // 应用程序位于堆栈的顶层
            if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (currentPackageName != null && currentPackageName.equals(context.getPackageName())) {
            return true;
        }

        return false;
    }

    public static boolean isAppOnForeground(Context context) {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager =
                (ActivityManager) context.getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();

        boolean result = false;

        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
                if (runningAppProcessInfo.processName.equals(packageName)) {
                    int status = runningAppProcessInfo.importance;
                    if (status == RunningAppProcessInfo.IMPORTANCE_VISIBLE
                            || status == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForegroundEx(Context context) {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager =
                (ActivityManager) context.getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {

                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前应用程序是否后台运行
     */
    public static boolean isBackground(Context context) {
        try {
            ActivityManager activityManager =
                    (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningAppProcessInfo> appProcesses =
                    activityManager.getRunningAppProcesses();
            for (RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(context.getPackageName())) {
                    if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                        // 后台运行
                        return true;
                    } else {
                        // 前台运行
                        return false;
                    }
                }
            }

        } catch (Exception e) {
            // 会出现空错误
        }
        return false;
    }

    /**
     * 判断手机是否处理睡眠
     */
    public static boolean isSleeping(Context context) {
        KeyguardManager kgMgr =
                (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean isSleeping = kgMgr.inKeyguardRestrictedInputMode();
        return isSleeping;
    }

    public static void jumpActivity(final Context context, Class cls) {
        jumpActivity(context, cls, null);
    }

    public static void jumpActivity(final Context context, Class cls, Bundle bundle) {
        jumpActivity(context, cls, bundle, false);
    }

    public static void jumpActivity(final Context context, Class cls, Bundle bundle, boolean bTop) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        intent.setClass(context, cls);
        intent.putExtra("start_time", System.currentTimeMillis());
        // if (bTop) {
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // }

        context.startActivity(intent);
    }

    public static void jumpActivityForResult(final Activity context, Class cls, int requestCode) {
        jumpActivityForResult(context, cls, requestCode, null);
    }

    public static void jumpActivityForResult(final Activity context, Class cls, int requestCode,
                                             Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        intent.setClass(context, cls);

        context.startActivityForResult(intent, requestCode);
    }


    public static boolean isMeizu() {
        if (Build.BRAND.contains("Meizu")) {
            return true;
        }

        return false;
    }


    public static String getWifiInfo(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
            return info.getMacAddress();// 获取手机mac地址
        } else {
            return null;
        }
    }


    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 获取设备机型.
     *
     * @return 设备机型字符串
     */
    public static String getDeviceType() {
        return Build.MODEL;
    }


    /**
     * 获取当前线程名
     *
     * @param context
     * @param pid
     * @return
     */
    public static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}
