package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Maxime on 2015-09-09.
 */
public class ConducteurFragment extends Fragment{

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle){
        return inflater.inflate(R.layout.conducteur_fragment, container, false);
    }
}
