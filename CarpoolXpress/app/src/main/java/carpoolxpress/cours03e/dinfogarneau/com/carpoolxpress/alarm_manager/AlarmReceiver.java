package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.alarm_manager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities.MainActivity;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.alarm_manager.AlarmService;

/*
 * BroadcastReceiver pour le déclenchement de l'alarme.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmService.class);
        context.startService(intentService);
    }
}
