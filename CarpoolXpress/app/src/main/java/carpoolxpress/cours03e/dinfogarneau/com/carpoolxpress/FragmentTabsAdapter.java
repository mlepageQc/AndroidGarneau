package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Maxime on 2015-09-09.
 */
public class FragmentTabsAdapter extends FragmentPagerAdapter{

    public FragmentTabsAdapter(FragmentManager fm){
        super(fm);
    }
    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new PassagerFragment();
            case 1:
                return new ConducteurFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }
}
