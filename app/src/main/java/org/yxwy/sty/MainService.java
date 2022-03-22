package org.yxwy.sty;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class MainService extends Service {

    private NotificationManager notificationManager;
    private String notificationId = "Sty_Keeping";
    private String notificationName = "生态园系统通知服务";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            NotificationChannel channel_message = new NotificationChannel("Sty_Message", "园区通知", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel_message);
        }
        startForeground(1, getNotification());
    }

    private Notification getNotification() {

        Context application = getApplicationContext();
        Intent resultIntent = new Intent(application, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(application, 0, resultIntent, 0);

        Notification.Builder builder = new Notification.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("生态园系统服务运行中")
                .setOngoing(true)
                .setContentIntent(resultPendingIntent)
                .setContentText("接受系统通知，加快App访问速度...");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }else{
            builder.setSmallIcon(R.drawable.ic_sty);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                builder.setColor(Color.GREEN);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        Notification notification = builder.build();
        return notification;
    }
}