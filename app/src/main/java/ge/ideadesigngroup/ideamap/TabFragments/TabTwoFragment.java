package ge.ideadesigngroup.ideamap.TabFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mapsforge.core.model.BoundingBox;

import java.util.ArrayList;

import ge.ideadesigngroup.ideamap.Models.SearchModel;
import ge.ideadesigngroup.ideamap.Models.PoiSearchTask;
import ge.ideadesigngroup.ideamap.R;

public class TabTwoFragment extends Fragment {

    private RecyclerView recyclerview;
    private CategoryAdapter adapter;
    private ArrayList<SearchModel> searchModel;
    private static final String POI_FILE = "/storage/emulated/0/graphhopper/maps/berlin/Tbilisi.poi";
    private final String category = "Hotels";
    BoundingBox box = new BoundingBox(41.60000,44.6000,41.84,45 );
    PoiSearchTask poiSearchTask = new PoiSearchTask(category, POI_FILE);
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_two_fragment, container, false);

        recyclerview = (RecyclerView) view.findViewById(R.id.categoryView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);

        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        searchModel = new ArrayList<>();
        searchModel.add(new SearchModel(R.drawable.ic_hotel,"Tourism"));
        searchModel.add(new SearchModel(R.drawable.ic_food,"Food"));
        //searchModel.add(new SearchModel(R.drawable.ic_shop,"Shop"));
        searchModel.add(new SearchModel(R.drawable.ic_bank,"Financial institutes"));
        //searchModel.add(new SearchModel(R.drawable.ic_atm,"ATM"));

        adapter = new CategoryAdapter(searchModel);
        recyclerview.setAdapter(adapter);
    }

}