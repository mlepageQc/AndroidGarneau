package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.MenuInflater;
import android.widget.LinearLayout;

/**
 * Created by Maxime on 2015-09-09.
 */
public class PassagerFragment extends Fragment implements Button.OnClickListener{

    private Button btnChoixDate;
    private Button btnChoixHeure;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle){

        LinearLayout passagerFragment = (LinearLayout)inflater.inflate(R.layout.passager_fragment, container, false);

        btnChoixDate = (Button)passagerFragment.findViewById(R.id.btn_date_passager);
        btnChoixHeure = (Button)passagerFragment.findViewById(R.id.btn_heure_passager);

        btnChoixDate.setOnClickListener(this);
        btnChoixHeure.setOnClickListener(this);

        return passagerFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        registerForContextMenu(btnChoixDate);
        registerForContextMenu(btnChoixHeure);
    }

    @Override
    public void onClick(View v) {
      getActivity().openContextMenu(v);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_choix_date, menu);
    }
}
