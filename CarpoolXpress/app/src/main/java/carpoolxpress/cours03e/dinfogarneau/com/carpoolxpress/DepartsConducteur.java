package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress;

import android.app.ActionBar;
import android.app.LauncherActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DepartsConducteur extends ActionBarActivity {

    private ListView departs;
    private android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departs_conducteur);

        ArrayList<String> donneesFictives = new ArrayList<String>();

        for(int i=0; i< 20; i++){
            donneesFictives.add("Depart " + String.valueOf(i));
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, donneesFictives);
        departs = (ListView)this.findViewById(R.id.list_view_departs);
        departs.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.departs_titre);

        registerForContextMenu(departs);

        actionBar.setDisplayHomeAsUpEnabled(true);

        Toast.makeText(getApplicationContext(), "Départ ajouté avec succès!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_deconnexion) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            Toast.makeText(getApplicationContext(), "Déconnexion réussie.", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Menu");
        menu.add(0, v.getId(), 0, "Supprimer");
        menu.add(0, v.getId(), 0, "Modifier");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Supprimer"){
            supprimerDepart();
        }
        else if(item.getTitle()=="Modifier"){modifierDepart(
            String.valueOf(item.getTitle()));
        }
        return true;
    }

    public void supprimerDepart() {
        Toast.makeText(getApplicationContext(), "Départ supprimé.", Toast.LENGTH_SHORT).show();
    }

    public void modifierDepart(String title) {
        Intent i = new Intent(this, MainActivity.class);
        int fragmentPosition = 1;
        i.putExtra("fragmentPosition", fragmentPosition);
        i.putExtra("mode", R.string.modifier);
        startActivity(i);
    }
}
