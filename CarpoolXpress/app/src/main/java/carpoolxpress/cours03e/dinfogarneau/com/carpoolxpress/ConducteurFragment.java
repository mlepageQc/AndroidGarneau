package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

/**
 * Created by Maxime on 2015-09-09.
 */
public class ConducteurFragment extends Fragment implements Button.OnClickListener{

    private LinearLayout dateDialog;
    private LinearLayout timeDialog;
    private Button btnChoixDate;
    private Button btnChoixHeure;
    private Button btnAjoutDepart;
    private DatePicker datePicker;
    private TimePicker timePicker;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle){
        LinearLayout conducteurFragment = (LinearLayout)inflater.inflate(R.layout.conducteur_fragment, container, false);

        dateDialog = (LinearLayout)inflater.inflate(R.layout.dialog_date, null);
        timeDialog = (LinearLayout)inflater.inflate(R.layout.dialog_heure, null);

        btnChoixDate = (Button)conducteurFragment.findViewById(R.id.btn_date_conducteur);
        btnChoixHeure = (Button)conducteurFragment.findViewById(R.id.btn_heure_conducteur);
        btnAjoutDepart = (Button)conducteurFragment.findViewById(R.id.btn_ajouter_depart);
        datePicker = (DatePicker)dateDialog.findViewById(R.id.date_picker);
        timePicker = (TimePicker)timeDialog.findViewById(R.id.time_picker);

        btnChoixDate.setOnClickListener(this);
        btnChoixHeure.setOnClickListener(this);
        btnAjoutDepart.setOnClickListener(this);
        timePicker.setIs24HourView(true);

        return conducteurFragment;
    }

    @Override
    public void onClick(View v) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        switch(v.getId()) {
            case R.id.btn_date_conducteur:
                AlertDialog.Builder dateBuilder = new AlertDialog.Builder(getActivity());

                dateBuilder.setTitle(R.string.titreDate)
                        .setView(dateDialog)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                btnChoixDate.setText(datePicker.getDayOfMonth() + " / " + datePicker.getMonth() + " / " + datePicker.getYear());
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                break;

            case R.id.btn_heure_conducteur:
                AlertDialog.Builder heureBuilder = new AlertDialog.Builder(getActivity());

                heureBuilder.setTitle(R.string.titreHeure)
                        .setView(timeDialog)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                btnChoixHeure.setText(timePicker.getCurrentHour() + " : " + timePicker.getCurrentMinute());
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                break;

            case R.id.btn_ajouter_depart:
                Intent i = new Intent(getActivity(), DepartsConducteur.class);
                startActivity(i);
                break;
        }
    }
}
