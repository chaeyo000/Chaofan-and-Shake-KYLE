package com.example.chaofanandshake;


import android.app.NotificationChannel;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.chaofanandshake.DashboardbtnActivity;
import com.example.chaofanandshake.R;

public class NotificationHelper {
    private static final String CHANNEL_ID = "order_channel";
    private static final String CHANNEL_NAME = "Order Notifications";
    private static final String CHANNEL_DESC = "Notifications for order status updates";

    private Context mContext;
    private NotificationManager mNotificationManager;

    public NotificationHelper(Context context) {
        this.mContext = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);

            mNotificationManager = mContext.getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(channel);
        } else {
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }

    public void sendNotification(String title, String message) {
        Intent intent = new Intent(mContext, DashboardbtnActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.notify) // Use your notification icon
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        mNotificationManager.notify(1, builder.build());
    }
}