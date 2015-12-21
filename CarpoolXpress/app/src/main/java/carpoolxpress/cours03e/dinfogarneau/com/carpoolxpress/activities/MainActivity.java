package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu;
import android.view.WindowManager;
import android.widget.Toast;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.alarm_manager.AlarmReceiver;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.alarm_manager.BootReceiver;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.fragments.FragmentTabsAdapter;

public class MainActivity extends ActionBarActivity implements android.support.v7.app.ActionBar.TabListener{

    //Attributs de la classe
    private FragmentPagerAdapter fragmentsAdapter;
    private ViewPager viewPager;
    private android.support.v7.app.ActionBar actionBar;

    //Notifs alarm manager
    private AlarmManager alarmMgr;
    private Intent alarmIntent;

    public final static int ID_ALARM = 12345;
    public final static int INTERVAL_ALARM = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentsAdapter = new FragmentTabsAdapter(getSupportFragmentManager());

        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(fragmentsAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        setAlarmNotifs();

        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.addTab(actionBar.newTab().setText(R.string.passager).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.conducteur).setTabListener(this));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        int tabPosition = getIntent().getIntExtra("fragmentPosition", 0);
        String mode = getIntent().getStringExtra("modeModif");

        if(mode != null) {
            actionBar.setTitle(mode);
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        viewPager.setCurrentItem(tabPosition);
    }

    public void setAlarmNotifs() {
        // Création de l'intention pour le déclenchement de l'alarme.
        this.alarmIntent = new Intent(this, AlarmReceiver.class);
        // Récupération du gestionnaire d'alarme.
        this.alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // Création d'une nouvelle intention en suspens.
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, ID_ALARM, this.alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        // Activation de l'alarme : donc l'intention en suspens est envoyée à l'alarm manager
        // Privilégiez l'utilisation de la méthode "setInexactRepeating" au lieu de "setRepeating".
        this.alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, INTERVAL_ALARM, alarmPendingIntent);

        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_deconnexion) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            Toast.makeText(getApplicationContext(), "Déconnexion réussie.", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.home) {
            this.finish();
        }
        if(id == R.id.mes_offres) {
            Intent i = new Intent(this, DepartsConducteur.class);
            startActivity(i);
        }
        if(id == R.id.mes_demandes) {
            Intent i = new Intent(this, ListeDemande.class);
            startActivity(i);
        }
        if(id == R.id.aide_interactive_main){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aide");
            builder.setMessage("Pour cette activité, vous avez le choix de deux fenêtre, Passager ou Conducteur, en tant que passager, vous pouver tracer votre trajet souhaité, avec une saisie de date pour ensuite rechercher les trajets disponibles selon vos critères.  Pour les conducteurs, vous devez saisir un titre, un point de départ et d'arrivée, la date et l'heure et le nombre de place, ensuite vous pouvez envoyer un trajet disponible aux utilisateurs.");
            builder.setCancelable(true);
            builder.setNeutralButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }


}
