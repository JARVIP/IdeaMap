package ge.ideadesigngroup.ideamap.TabFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.poi.storage.PointOfInterest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ge.ideadesigngroup.ideamap.Models.SearchModel;
import ge.ideadesigngroup.ideamap.Models.PoiSearchTask;
import ge.ideadesigngroup.ideamap.R;

public class TabOneFragment extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerview;
    private List<SearchModel>searchModel = new ArrayList<>();
    private List<SearchModel> searches;
    private RVAdapter adapter;
    private Menu menu;
    private String search;
    private static final String POI_FILE ="/storage/emulated/0/graphhopper/maps/berlin/Tbilisi.poi";
    private final String category="Food";
    BoundingBox box = new BoundingBox(41.60000,44.6000,41.84,45 );
    PoiSearchTask poiSearchTask = new PoiSearchTask(category,POI_FILE);



    public ArrayList<SearchModel> getPOI(Collection<PointOfInterest> pointOfInterests)
    {
        ArrayList<SearchModel> cm = new ArrayList<>();
        for (final PointOfInterest pointOfInterest : pointOfInterests) {
            cm.add(new SearchModel(pointOfInterest.getName(),pointOfInterest.getCategory().getParent(),pointOfInterest.getId(),pointOfInterest.getData(),pointOfInterest.getLatLong()));
        }
        return cm;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            public void run() {
                searchModel=getPOI(poiSearchTask.poiDbConnection(box));
            }
        }).start();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_one_fragment, container, false);

        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);

        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }
    public void search(String search)
    {
        //searches = getPOI(poiSearchTask.poiDbConnection(box));
       // ArrayList<SearchModel> cm = new ArrayList<>();
        searches = new ArrayList<>();
//        for (final PointOfInterest pointOfInterest : poiSearchTask.poiDbConnection(box)) {
//            if(pointOfInterest.getName() != null && pointOfInterest.getName().contains(search))
//            {
//                searchModel.add(new SearchModel(pointOfInterest.getName(),pointOfInterest.getCategory().getParent(),pointOfInterest.getId(),pointOfInterest.getData(),pointOfInterest.getLatLong()));
//            }
//            //cm.add(new SearchModel(pointOfInterest.getName(),pointOfInterest.getCategory().getParent(),pointOfInterest.getId(),pointOfInterest.getData(),pointOfInterest.getLatLong()));
//        }

//        for(SearchModel d : searches){
//            if(d.getName() != null && d.getName().contains(search))
//                searchModel.add(new SearchModel(d.getName(),d.getCategory(),d.getId(),d.getData(),d.getLatLong()));
//        }
        for(SearchModel s :searchModel)
        {
            if(s.getName()!=null && s.getName().contains(search))
            {
                searches.add(s);
                adapter = new RVAdapter(searches);
                recyclerview.setAdapter(adapter);
            }
        }

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        this.menu=menu;
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(item,
        new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
               // adapter.setFilter(searches);
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true; // Return true to expand action view
            }
        });
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        search=newText;
       //search in history
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        search=query;
        search(search);
        return true;
    }
}
