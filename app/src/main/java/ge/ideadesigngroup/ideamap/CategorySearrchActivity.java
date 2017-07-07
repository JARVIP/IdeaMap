package ge.ideadesigngroup.ideamap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.poi.storage.PointOfInterest;

import java.util.ArrayList;
import java.util.Collection;

import ge.ideadesigngroup.ideamap.Models.CategorySearchList;
import ge.ideadesigngroup.ideamap.Models.CategorySearchModel;
import ge.ideadesigngroup.ideamap.Models.PoiSearchTask;

public class CategorySearrchActivity extends AppCompatActivity {
    private ListView list;
    private Toolbar toolbar;
    private ArrayList<CategorySearchModel> searches,searchModel,model;


    BoundingBox box = new BoundingBox(41.60000,44.6000,41.84,45 );//es misaxedia

    private PoiSearchTask poiSearchTask;
    private String POI_FILE ="/storage/emulated/0/graphhopper/maps/berlin/berlin.poi";


    public ArrayList<CategorySearchModel> getPOI(Collection<PointOfInterest> pointOfInterests)
    {
        ArrayList<CategorySearchModel> cm = new ArrayList<>();
        for (final PointOfInterest pointOfInterest : pointOfInterests) {
            cm.add(new CategorySearchModel(pointOfInterest.getCategory().getParent().getTitle(),pointOfInterest.getName(),pointOfInterest.getId(),pointOfInterest.getData(),pointOfInterest.getLatLong()));
        }
        return cm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_searrch);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        String Category = getIntent().getStringExtra("Category");

        poiSearchTask = new PoiSearchTask(Category,POI_FILE);
        list = (ListView)findViewById(R.id.category_search_list);
        model= getPOI(poiSearchTask.poiCategoryDbConnection(box));
        CategorySearchList  adapter = new CategorySearchList(this,model);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent("ge.ideadesigngroup.ideamap.Map");
                double a =model.get(position).getLatLong().getLatitude();
                double z = model.get(position).getLatLong().getLongitude();
                intent.putExtra("Latitude", a);
                intent.putExtra("Longitude",z);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void search(String search)
    {
        searches = getPOI(poiSearchTask.poiCategoryDbConnection(box));
        searchModel = new ArrayList<>();
        for(CategorySearchModel d : searches){
            if(d.getName() != null && d.getName().contains(search))
                searchModel.add(new CategorySearchModel(d.getCategory(),d.getName(),d.getId(),d.getData(),d.getLatLong()));
        }
        CategorySearchList  adapter = new CategorySearchList(this,searchModel);
        list.setAdapter(adapter);
    }
}
