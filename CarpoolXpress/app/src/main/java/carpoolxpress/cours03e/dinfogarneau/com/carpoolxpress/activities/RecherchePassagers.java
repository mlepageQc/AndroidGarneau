package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Offre;


public class RecherchePassagers extends ActionBarActivity implements ListView.OnItemClickListener {

    private ListView resultats;
    private ActionBar actionBar;
    private ArrayList<Offre> lstOffres;
    private LigneAdapter m_Adapter;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_passagers);

        //Récupération des préférences partagées
        sp = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        lstOffres = new ArrayList<Offre>();

        List<Offre> allOffres = (List<Offre>)getIntent().getSerializableExtra("lstResultatsOffres");

        for (int i =0; i < allOffres.size(); i ++) {
            if(!(allOffres.get(i).getUsername().equals(sp.getString("Utilisateur", "")))) {
                lstOffres.add(allOffres.get(i));
            }
        }

        actionBar = getSupportActionBar();

        resultats = (ListView)this.findViewById(R.id.list_view_resultats);
        this.m_Adapter = new LigneAdapter();
        resultats.setAdapter(m_Adapter);
        m_Adapter.notifyDataSetChanged();

        actionBar.setTitle(R.string.resultats_recherche);
        registerForContextMenu(resultats);
        resultats.setOnItemClickListener(this);

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        if(id == R.id.aide_interactive_recherche_passagers) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aide");
            builder.setMessage("Fenêtre de recherche de passagers : " +
                    "Sur cette fenêtre, vous pouvez voir les résultats de votre recherche d'offres.");
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(this, ConsultationOffre.class);
        i.putExtra("offreCourante", lstOffres.get(position));
        startActivity(i);
    }

    private class LigneAdapter extends ArrayAdapter<Offre> {
        public LigneAdapter() {
            super(RecherchePassagers.this, android.R.layout.simple_list_item_1, lstOffres);
        }

        @Override
        public View getView(int position, View vrow, ViewGroup list) {
            View row = super.getView(position, vrow, list);

            Offre o = lstOffres.get(position);
            TextView label = (TextView) row.findViewById(android.R.id.text1);
            label.setText(String.valueOf(position + 1) + " - " + o.getTitre());
            return row;
        }
    }

}
