package com.example.srividhya.filescanner;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by srividhya on 7/24/2017.
 */

public class Handler {
    private static Handler sHandler = new Handler();

    public static Handler getInstance(){return sHandler;}
    private Map<Integer,String> notifications;

    private Handler(){
        notifications = new HashMap<>();
    }

    public void initialize(int id,String message,Context context){
        notifications.put(id, message);
        Intent intent = new Intent(context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notification = mBuilder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.file_scanner)
                .setAutoCancel(false)
                .build();

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.progressbar);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setProgressBar(R.id.progressBar,0,0,true);
        notification.contentView = remoteViews;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id,notification);
    }
    public void updateMessage(int id,String message,Context context){
        notifications.put(id,message);
        initialize(id,message,context);
    }
    public void remove(int id){

        notifications.remove(id);
    }

}
