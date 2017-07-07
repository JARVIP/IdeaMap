package ge.ideadesigngroup.ideamap.Models;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;

import org.oscim.core.GeoPoint;

import ge.ideadesigngroup.ideamap.R;

/**
 * Created by IDGUser on 4/3/2017.
 */

public class MapModels extends Activity {
    public void hideFloatingButtons(FloatingActionButton fab,
                                    FloatingActionButton fab1,
                                    FloatingActionButton fab2,
                                    FloatingActionButton fab3,
                                    FloatingActionButton fab4)
    {
        fab.hide();
        fab1.hide();
        fab2.hide();
        fab3.hide();
        fab4.hide();
    }
    public void showFloatingButtons(FloatingActionButton fab,
                                    FloatingActionButton fab1,
                                    FloatingActionButton fab2,
                                    FloatingActionButton fab3,
                                    FloatingActionButton fab4)
    {
        fab.show();
        fab1.show();
        fab2.show();
        fab3.show();
        fab4.show();
    }
    protected void showFABMenu(
                               FloatingActionButton fab,
                               FloatingActionButton fab1,
                               FloatingActionButton fab2,
                               FloatingActionButton fab3,
                               FloatingActionButton fab4){

        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_200));
        fab4.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab.animate().rotation(180);

    }

    protected void closeFABMenu(
                              FloatingActionButton fab,
                              FloatingActionButton fab1,
                              FloatingActionButton fab2,
                              FloatingActionButton fab3,
                              FloatingActionButton fab4){
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
        fab4.animate().translationY(0);
        fab.animate().rotation(0);
    }

}
