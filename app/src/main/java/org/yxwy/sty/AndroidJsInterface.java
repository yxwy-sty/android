package org.yxwy.sty;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class AndroidJsInterface {

    Context mContext;
    String versionName;
    int versionCode;

    private final static AtomicInteger c = new AtomicInteger(0);

    AndroidJsInterface(Context c) {
        mContext = c;
        PackageManager pm = c.getPackageManager();
        PackageInfo    pi;
        try {
            pi = pm.getPackageInfo(c.getPackageName(), 0);
        }
        catch (Exception e) { return; }

        versionName = pi.versionName;
        versionCode = pi.versionCode;

    }

    @JavascriptInterface
    // https://www.jianshu.com/p/f55385602364
    public  void shareWechat( String content, File picFile) {
        if (CommonUtil.isInstallApp(mContext, CommonUtil.PACKAGE_WECHAT)) {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            if (picFile != null) {
                if (picFile.isFile() && picFile.exists()) {
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(mContext, CommonUtil.AUTHORITY, picFile);
                    } else {
                        uri = Uri.fromFile(picFile);
                    }
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                }
            }
            intent.putExtra("description", !TextUtils.isEmpty(content) ? content : "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
        }
    }

    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void notify(String title, String message, String to_url) {
        Intent     resultIntent = new Intent(mContext, MainActivity.class);
        if (!to_url.isEmpty()) {
            resultIntent.putExtra("to_url",to_url);
        }
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.Builder builder = new Notification.Builder(mContext)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle(title)
//                .setColor(Color.LTGRAY)
                .setContentIntent(resultPendingIntent)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentText(message);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            //如果是小于5.0系统的，设置原图
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }else{
            //如果是大于等于5.0系统的，设置透明图
            builder.setSmallIcon(R.drawable.ic_sty);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                //如果小于7.0系统,设置背景色
                builder.setColor(Color.GREEN);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("Sty_Message");
        }
//        Toast.makeText(mContext, "正在发送通知...", Toast.LENGTH_SHORT).show();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notificationManager.notify(c.incrementAndGet(), notification);
    }

}

class CommonUtil {
    public static final String PACKAGE_WECHAT = "com.tencent.mm";
    public static final String AUTHORITY = "org.yxwy.sty.fileprovider";
    public static boolean isInstallApp(Context context, String app_package){
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                if (app_package.equals(pn)) { return true; } }
        }
        return false;
    }
}
