package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class RecherchePassagers extends ActionBarActivity {

    private ListView resultats;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_passagers);

        ArrayList<String> donneesFictives = new ArrayList<String>();
        actionBar = getSupportActionBar();

        for(int i=0; i< 20; i++){
            donneesFictives.add("Resultat " + String.valueOf(i));
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, donneesFictives);
        resultats = (ListView)this.findViewById(R.id.list_view_resultats);
        resultats.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        actionBar.setTitle(R.string.resultats_recherche);
        registerForContextMenu(resultats);

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Menu");
        menu.add(0, v.getId(), 0, "Demander ce départ");
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Demander ce départ"){
            Toast.makeText(getApplicationContext(), "Demande envoyée.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


}
