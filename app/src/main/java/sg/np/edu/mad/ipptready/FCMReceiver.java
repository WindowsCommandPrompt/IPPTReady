package sg.np.edu.mad.ipptready;

import static android.app.AlarmManager.INTERVAL_DAY;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;


import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Map;

public class FCMReceiver extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "FCM";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        int time = Integer.parseInt(data.get("_routineAlarm"));
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(300);
        int hour = time/60;
        int minute = (time -60*hour)%60;
        removeAlarm();
        setAlarm(this, hour, minute);

        setNotification(this, "IPPTReady", "Routine Alarm set to go off at " +
                        String.format("%02d:%02d", hour, minute));
    }

    public static void setNotification(Context context,
                                String ContentTitle,
                                String ContentText) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.appicon)
                .setContentTitle(ContentTitle)
                .setContentText(ContentText);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "FCMChannel", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(100, notificationBuilder.build());
    }

    public static void setAlarm(Context context ,int hour, int minute) {
        Intent routineAlertIntent = new Intent(context, RoutineAlertReceiver.class);
        routineAlertIntent.putExtra("hour", hour);
        routineAlertIntent.putExtra("minute", minute);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 400, routineAlertIntent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() +
                (calendar.getTimeInMillis() < System.currentTimeMillis() ? INTERVAL_DAY :
                        0), pendingIntent);
    }

    private void removeAlarm() {
        Intent routineAlertIntent = new Intent(this, RoutineAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 400, routineAlertIntent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }
}
