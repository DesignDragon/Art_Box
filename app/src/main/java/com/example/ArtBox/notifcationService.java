package com.example.ArtBox;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class notifcationService extends FirebaseMessagingService {
    String notif_title="";
    String notif_body="";
    String channel_id="myNotification";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage!=null){
            notif_title=remoteMessage.getNotification().getTitle().toString();
            notif_body=remoteMessage.getNotification().getBody().toString();
            createnotifcation(notif_title,notif_body);
        }
    }
    void createnotifcation(String title,String body){
        Intent intent=new Intent(this,MainActivity.class);
        PendingIntent pin=PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder notificationdis=new NotificationCompat.Builder(this,channel_id)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pin);
        NotificationManager manage=(NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(channel_id,"ArtBox", NotificationManager.IMPORTANCE_DEFAULT);
            manage.createNotificationChannel(channel);
        }
        manage.notify(0,notificationdis.build());
    }
}
