package com.github.h01d.chatapp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.github.h01d.chatapp.R;
import com.github.h01d.chatapp.activities.ChatActivity;
import com.google.firebase.messaging.RemoteMessage;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author Raf (https://github.com/h01d)
 * @version 1.1
 * @since 27/02/2018
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        // Notification data

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= 26)
        {
            // API 26+ is required to provide a channel Id

            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.default_notification_channel_id), "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Notification.DEFAULT_LIGHTS);
            notificationChannel.setVibrationPattern(new long[]{0, 100, 100, 100, 100, 100});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        String notificationTitle = remoteMessage.getData().get("title");
        String notificationMessage = remoteMessage.getData().get("body");
        String notificationAction = remoteMessage.getData().get("click_action");
        String notificationFrom = remoteMessage.getData().get("from_user_id");

        if(notificationTitle.equals("You have a new Message"))
        {
            // If it's a message notification
            // Checking if ChatActivity is not open or if its, it should have a different userId from current

            if(!ChatActivity.running || ChatActivity.running && !ChatActivity.otherUserId.equals(notificationFrom))
            {
                // Creating the notification

                NotificationCompat.Builder notification = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationMessage)
                        .setSmallIcon(R.drawable.logo)
                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher_round))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                Intent intent = new Intent(notificationAction);
                intent.putExtra("userid", notificationFrom);

                // Extract a unique notification from sender userId so we can have only 1 notification per user

                int notificationId = Integer.parseInt(notificationFrom.replaceAll("[^0-9]", ""));

                PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId % 65535, intent, PendingIntent.FLAG_ONE_SHOT);

                notification.setContentIntent(pendingIntent);

                // Pushing notification to device

                notificationManager.notify(notificationId % 65535, notification.build());
            }
        }
        else if(notificationTitle.equals("You have a Friend Request"))
        {
            // If it's friend request notification

            // Creating the notification

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationMessage)
                    .setSmallIcon(R.drawable.ic_person_add_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher_round))
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            Intent intent = new Intent(notificationAction);
            intent.putExtra("userid", notificationFrom);

            // Extract a unique notification from sender userId so we can have only 1 notification per user

            int notificationId = Integer.parseInt(notificationFrom.replaceAll("[^0-9]", ""));

            // Adding +1 to notification Id se we can have a Friend Request and a Message Notification at the same time

            PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId + 1 % 65535, intent, PendingIntent.FLAG_ONE_SHOT);

            notification.setContentIntent(pendingIntent);

            // Pushing notification to device

            notificationManager.notify(notificationId + 1 % 65535, notification.build());
        }
        else if(notificationTitle.equals("You have a new friend"))
        {
            // If it's a new friend

            // Creating the notification

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationMessage)
                    .setSmallIcon(R.drawable.ic_person_add_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher_round))
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            Intent intent = new Intent(notificationAction);
            intent.putExtra("userid", notificationFrom);

            // Extract a unique notification from sender userId so we can have only 1 notification per user

            int notificationId = Integer.parseInt(notificationFrom.replaceAll("[^0-9]", ""));

            // Adding +2 to notification Id se we can have a all notifications at the same time

            PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId + 2 % 65535, intent, PendingIntent.FLAG_ONE_SHOT);

            notification.setContentIntent(pendingIntent);

            // Pushing notification to device

            notificationManager.notify(notificationId + 2 % 65535, notification.build());
        }
    }
}
