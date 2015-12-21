package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.alarm_manager;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities.LoginActivity;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities.MainActivity;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                MainActivity.ID_ALARM,
                alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmMgr.setRepeating(
                AlarmManager.ELAPSED_REALTIME,
                0,
                MainActivity.INTERVAL_ALARM,
                alarmPendingIntent);
    }
}


