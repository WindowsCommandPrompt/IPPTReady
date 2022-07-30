package sg.np.edu.mad.ipptready;

import static android.app.AlarmManager.INTERVAL_DAY;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class RoutineAlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);
        FCMReceiver.setNotification(context, "IPPTReady",
                "Time To Do your Routine!");
        setNextAlarm(context, intent.getIntExtra("hour", 12),
                intent.getIntExtra("minute", 0));
    }

    private void setNextAlarm(Context context, int hour, int minute) {
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

        manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + INTERVAL_DAY,
                pendingIntent);
    }
}
