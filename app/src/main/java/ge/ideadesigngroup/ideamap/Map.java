package ge.ideadesigngroup.ideamap;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.view.ActionMode;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Constants;
import com.graphhopper.util.Helper;
import com.graphhopper.util.Parameters.Algorithms;
import com.graphhopper.util.Parameters.Routing;
import com.graphhopper.util.PointList;
import com.graphhopper.util.ProgressListener;
import com.graphhopper.util.StopWatch;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.poi.storage.PointOfInterest;
import org.oscim.android.MapView;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.core.Tile;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.Layer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.layers.vector.PathLayer;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ge.ideadesigngroup.ideamap.Models.AndroidDownloader;
import ge.ideadesigngroup.ideamap.Models.AndroidHelper;
import ge.ideadesigngroup.ideamap.Models.CategorySearchModel;
import ge.ideadesigngroup.ideamap.Models.GHAsyncTask;
import ge.ideadesigngroup.ideamap.Models.MapModels;
import ge.ideadesigngroup.ideamap.Models.PoiSearchTask;
import ge.ideadesigngroup.ideamap.Models.SearchModel;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;

public class Map extends MapModels implements NavigationView.OnNavigationItemSelectedListener, AppCompatCallback {
    private static final int NEW_MENU_ID = Menu.FIRST + 1;
    private MapView mapView;
    private GraphHopper hopper;
    private GeoPoint start;
    private ArrayList<GeoPoint> PointList;
    private GeoPoint end;
    private volatile boolean prepareInProgress = false;
    private volatile boolean shortestPathRunning = false;
    private String currentArea = "berlin";
    private String fileListURL = "http://download2.graphhopper.com/public/maps/" + Constants.getMajorVersion() + "/";
    private String prefixURL = fileListURL;
    private String downloadURL;
    private File mapsFolder, folder;
    private ItemizedLayer<MarkerItem> itemizedLayer;
    private ItemizedLayer<MarkerItem> GPSitemizedLayer;
    private ItemizedLayer<MarkerItem> POIitemizedLayer;
    private ItemizedLayer<MarkerItem> SearchedPOIitemizedLayer;
    private PathLayer pathLayer;
    private PathLayer circle;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FloatingActionButton fab, fab1, fab2, fab3, fab4;
    private ImageView route_submenu;
    private ViewGroup rout_menu;
    private ViewGroup map_header;
    private boolean isFABOpen = false, isEverythingHidden=false;
    private boolean route_submenu_isopen = false;
    private TextView routeTextkillometers;
    private ImageView route_disable_button;
    private String paths;
    private MarkerItem markerIt;
    private Button Car;

    private PoiSearchTask poiSearchTask;
    private String POI_FILE ="/storage/emulated/0/graphhopper/maps/berlin/Tbilisi.poi";

    protected boolean onLongPress(GeoPoint p) {
        if (!isReady())
            return false;

        if (shortestPathRunning) {
            logUser("Calculation still in progress");
            return false;
        }
//        if (start != null && middle !=null && end == null) {
//            end = p;
//            shortestPathRunning = true;
//            itemizedLayer.addItem(createMarkerItem(p, R.drawable.marker_icon_red));
//            mapView.map().updateMap(true);
//
//            calcPath(start.getLatitude(), start.getLongitude(), end.getLatitude(),
//                    end.getLongitude());
//        } else {
        //start = p;]
        PointList = new ArrayList<GeoPoint>();
        PointList.add(p);
        // remove routing layers
        //end = null;
        mapView.map().layers().remove(pathLayer);
       // itemizedLayer.removeAllItems();

        itemizedLayer.addItem(createMarkerItem(p, R.drawable.marker_icon_green));
      //  updateGpsLocation(p);
        mapView.map().updateMap(true);
       // }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Tile.SIZE = Tile.calculateTileSize(getResources().getDisplayMetrics().scaledDensity);
        poiSearchTask = new PoiSearchTask("Hotels",POI_FILE);
        mapView = new MapView(this);
        rout_menu = (ViewGroup) findViewById(R.id.route_info_menu_id);
        routeTextkillometers = (TextView) findViewById(R.id.kilometers);
        route_disable_button = (ImageView) findViewById(R.id.imageView1);
        route_disable_button.setClickable(true);
        route_disable_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rout_menu.animate().translationY(getResources().getDimension(R.dimen.standard_80));
                if (route_submenu_isopen) {
                    route_submenu.setImageResource(R.drawable.menu);
                    route_submenu_isopen = false;
                }
                showFloatingButtons(fab, fab1, fab2, fab3, fab4);

            }
        });
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("ge.ideadesigngroup.ideamap.Search");
                startActivity(intent);
            }
        });
        Car = (Button) findViewById(R.id.car);
        Car.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void  onClick(View v)
            {
                shortestPathRunning = true;
                calcPath(PointList);

            }
        });
       // GPSitemizedLayer = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
      //  mapView.map().layers().add(GPSitemizedLayer);



        map_header = (ViewGroup) findViewById(R.id.map_header);
        route_submenu = (ImageView) findViewById(R.id.route_sub_menu);
        route_submenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!route_submenu_isopen) {
                    rout_menu.animate().translationY(-getResources().getDimension(R.dimen.standard_120));
                    route_submenu.setImageResource(R.drawable.ic_down_arrow);
                    route_submenu_isopen = true;
                } else {
                    route_submenu.setImageResource(R.drawable.menu);
                    rout_menu.animate().translationY(-getResources().getDimension(R.dimen.standard_80));
                    route_submenu_isopen = false;
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu(fab, fab1, fab2, fab3, fab4);
                    isFABOpen = true;
                } else {
                    closeFABMenu(fab, fab1, fab2, fab3, fab4);
                    isFABOpen = false;
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFloatingButtons(fab, fab1, fab2, fab3, fab4);
                rout_menu.animate().translationY(-getResources().getDimension(R.dimen.standard_80));
                //  map_header.animate().translationY(getResources().getDimension(R.dimen.standard_80));

            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("ge.ideadesigngroup.ideamap.SETTINGS");
                startActivity(intent);
            }
        });
        final EditText input = new EditText(this);
        input.setText(currentArea);
        boolean greaterOrEqKitkat = Build.VERSION.SDK_INT >= 19;
        if (greaterOrEqKitkat) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                logUser("GraphHopper is not usable without an external storage!");
                return;
            }
            mapsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "/graphhopper/maps/");
            folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/graphhopper/maps/berlin");
        } else
            mapsFolder = new File(Environment.getExternalStorageDirectory(), "/graphhopper/maps/");
        folder = new File(Environment.getExternalStorageDirectory(), "/graphhopper/maps/berlin");

        if (!mapsFolder.exists())
            mapsFolder.mkdirs();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        paths = folder.getPath();
        String s = getIntent().getStringExtra("selectedArea");
        initFiles(s);




        // TODO get user confirmation to download
        //GPS
        //setPOIes();
        new Thread(new Runnable() {
            public void run() {
               setPOIes();
            }
        }).start();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GeoPoint GPSpoint = new GeoPoint(location.getLatitude(),location.getLongitude());
                updateGpsLocation(GPSpoint);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
//                if(locationManager!=null) {
//                    double latitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
//                    double longitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
//                    mapView.map().setMapPosition(latitude, longitude, 1 << 20);
//                    mapView.map().updateMap(true);
//                }
            }
        });
    }


    private void initFiles(String area) {
        prepareInProgress = true;
        currentArea = area;
        downloadingFiles();
    }

    void downloadingFiles() {
        final File areaFolder = new File(mapsFolder, currentArea + "-gh");
        if (downloadURL == null || areaFolder.exists()) {
            loadMap(areaFolder);
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Downloading and uncompressing " + downloadURL);
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();

        new GHAsyncTask<Void, Integer, Object>() {
            protected Object saveDoInBackground(Void... _ignore)
                    throws Exception {
                String localFolder = Helper.pruneFileEnd(AndroidHelper.getFileName(downloadURL));
                localFolder = new File(mapsFolder, localFolder + "-gh").getAbsolutePath();
                log("downloading & unzipping " + downloadURL + " to " + localFolder);
                AndroidDownloader downloader = new AndroidDownloader();
                downloader.setTimeout(30000);
                downloader.downloadAndUnzip(downloadURL, localFolder,
                        new ProgressListener() {
                            @Override
                            public void update(long val) {
                                publishProgress((int) val);
                            }
                        });
                return null;
            }

            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                dialog.setProgress(values[0]);
            }

            protected void onPostExecute(Object _ignore) {
                dialog.dismiss();
                if (hasError()) {
                    String str = "An error happened while retrieving maps:" + getErrorMessage();
                    log(str, getError());
                    logUser(str);
                } else {
                    loadMap(areaFolder);
                }
            }
        }.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        closeFABMenu(fab, fab1, fab2, fab3, fab4);



        isFABOpen = false;
        mapView.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        double latitude = intent.getDoubleExtra("Latitude",0);
        double longitude = intent.getDoubleExtra("Longitude",0);


        if(latitude==0||longitude==0)
        {}
        else {
            SearchedPOIitemizedLayer.removeAllItems();
            Drawable drawable = getResources().getDrawable(R.drawable.ic_poi);
            Bitmap bitmap = drawableToBitmap(drawable);
            final MarkerSymbol symbol;
            symbol = new MarkerSymbol(bitmap, MarkerSymbol.HotspotPlace.CENTER);
            Drawable drawable1 =getResources().getDrawable(R.drawable.ic_transparent);
            Bitmap bitmap1 = drawableToBitmap(drawable1);
            final MarkerSymbol symbol1;
            symbol1 = new MarkerSymbol(bitmap1, MarkerSymbol.HotspotPlace.CENTER);




            GeoPoint point = new GeoPoint(latitude,longitude);
            if(markerIt!=null)
                markerIt.setMarker(symbol1);
            final MarkerItem markerItem = new MarkerItem("Your Location is: ","",point);
            markerItem.setMarker(symbol);
            SearchedPOIitemizedLayer.addItem(markerItem);
            SearchedPOIitemizedLayer.setOnItemGestureListener(new ItemizedLayer.OnItemGestureListener<MarkerItem>() {
                @Override
                public boolean onItemSingleTapUp(int index, MarkerItem item) {
                    Toast.makeText(Map.this,item.getTitle()+" "+item.getPoint(), Toast.LENGTH_SHORT).show();

                    return true;
                }
                @Override
                public boolean onItemLongPress(int index, MarkerItem item) {
                    return false;
                }
            });
            mapView.map().setMapPosition(latitude, longitude, 1 << 20);
            mapView.map().updateMap(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        closeFABMenu(fab, fab1, fab2, fab3, fab4);
        isFABOpen = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hopper != null)
            hopper.close();

        hopper = null;
        // necessary?
        System.gc();

        // Cleanup VTM
        mapView.map().destroy();
    }

    boolean isReady() {
        // only return true if already loaded
        if (hopper != null)
            return true;

        if (prepareInProgress) {
            logUser("Preparation still in progress");
            return false;
        }
        logUser("Prepare finished but hopper not ready. This happens when there was an error while loading the files");
        return false;
    }

    void loadMap(File areaFolder) {
        logUser("loading map");
        // Map events rdeceiver
        mapView.map().layers().add(new Map.MapEventsReceiver(mapView.map()));

        // Map file source
        MapFileTileSource tileSource = new MapFileTileSource();
        tileSource.setMapFile(new File(areaFolder, currentArea + ".map").getAbsolutePath());
        VectorTileLayer l = mapView.map().setBaseMap(tileSource);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String style = pref.getString("style", "1");
        if (style.equals("0")) {
            mapView.map().setTheme(VtmThemes.DEFAULT);
        } else if (style.equals("1")) {
            mapView.map().setTheme(VtmThemes.TRONRENDER);
        }
        mapView.map().layers().add(new BuildingLayer(mapView.map(), l));
        mapView.map().layers().add(new LabelLayer(mapView.map(), l));

        // Markers layer
        itemizedLayer = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
        mapView.map().layers().add(itemizedLayer);
        GPSitemizedLayer = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
        mapView.map().layers().add(GPSitemizedLayer);
        POIitemizedLayer = new ItemizedLayer<>(mapView.map(),(MarkerSymbol) null);
        mapView.map().layers().add(POIitemizedLayer);
        SearchedPOIitemizedLayer = new ItemizedLayer<>(mapView.map(),(MarkerSymbol) null);
        mapView.map().layers().add(SearchedPOIitemizedLayer);
        // Map position
        GeoPoint mapCenter = tileSource.getMapInfo().boundingBox.getCenterPoint();
        mapView.map().setMapPosition(mapCenter.getLatitude(), mapCenter.getLongitude(), 1 << 15);

        ViewGroup map_view = (ViewGroup) findViewById(R.id.map_view);
        map_view.addView(mapView);



        ViewGroup layout = (ViewGroup) findViewById(R.id.map);
        setContentView(layout);
        loadGraphStorage();
    }

    void loadGraphStorage() {
        logUser("loading graph (" + Constants.VERSION + ") ... ");
        new GHAsyncTask<Void, Void, Path>() {
            protected Path saveDoInBackground(Void... v) throws Exception {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                tmpHopp.load(new File(mapsFolder, currentArea).getAbsolutePath() + "-gh");
                log("found graph " + tmpHopp.getGraphHopperStorage().toString() + ", nodes:" + tmpHopp.getGraphHopperStorage().getNodes());
                hopper = tmpHopp;
                return null;
            }

            protected void onPostExecute(Path o) {
                if (hasError()) {
                    logUser("An error happened while creating graph:"
                            + getErrorMessage());
                } else {
                    logUser("Finished loading graph. Long press to define where to start and end the route.");
                }

                finishPrepare();
            }
        }.execute();
    }

    private void finishPrepare() {
        prepareInProgress = false;
    }

    private PathLayer createPathLayer(PathWrapper response) {
        Style style = Style.builder()
                .generalization(Style.GENERALIZATION_SMALL)
                .strokeColor(0x9900cc33)
                .strokeWidth(4 * getResources().getDisplayMetrics().density)
                .build();
        PathLayer pathLayer = new PathLayer(mapView.map(), style);
        List<GeoPoint> geoPoints = new ArrayList<>();
        PointList pointList = response.getPoints();
        for (int i = 0; i < pointList.getSize(); i++)
            geoPoints.add(new GeoPoint(pointList.getLatitude(i), pointList.getLongitude(i)));
        pathLayer.setPoints(geoPoints);
        return pathLayer;
    }

    @SuppressWarnings("deprecation")
    private MarkerItem createMarkerItem(GeoPoint p, int resource) {
        Drawable drawable = getResources().getDrawable(resource);
        Bitmap bitmap = drawableToBitmap(drawable);
        MarkerSymbol markerSymbol = new MarkerSymbol(bitmap, 0.5f, 1);
        MarkerItem markerItem = new MarkerItem("", "", p);
        markerItem.setMarker(markerSymbol);
        return markerItem;
    }

    public void calcPath(final ArrayList<GeoPoint> pointList){

        log("calculating path ...");
        new AsyncTask<Void, Void, ArrayList<PathWrapper>>() {
            //float time;

            protected ArrayList<PathWrapper> doInBackground(Void... v) {
                ArrayList<PathWrapper> path = new ArrayList<PathWrapper>();
                for(int i = 0; i< pointList.size()-1;i++)
                {
                    GHRequest req = new GHRequest(pointList.get(i).getLatitude(), pointList.get(i).getLongitude(), pointList.get(i+1).getLatitude(), pointList.get(i+1).getLongitude()).
                            setAlgorithm(Algorithms.DIJKSTRA_BI);
                    req.getHints().
                            put(Routing.INSTRUCTIONS, "false");
                    GHResponse resp = hopper.route(req);
                    path.add(resp.getBest());
                }
//                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon).
//                        setAlgorithm(Algorithms.DIJKSTRA_BI);
//                req.getHints().
//                        put(Routing.INSTRUCTIONS, "false");
//                req.setVehicle("foot");
//                req.setWeighting("fastest");
               // GHResponse resp = hopper.route(req);
                //GHResponse resp1 = hopper.route(req);
                //time = sw.stop().getSeconds();
//                PathWrapper a = resp.getBest();
//                PathWrapper b = resp.getBest();
//                double z = a.getDistance();
//                ArrayList<PathWrapper> e = new ArrayList<PathWrapper>();
//                e.add(a);
                return path;
            }

            protected void onPostExecute(ArrayList<PathWrapper> resp) {
                double routeSum=0;
                for(PathWrapper item : resp)
                {
                    if(!item.hasErrors())
                    {
                        pathLayer = createPathLayer(item);
                        mapView.map().layers().add(pathLayer);
                        mapView.map().updateMap(true);
                        routeSum+=(item.getDistance() / 100) / 10f;
                    } else {
                        logUser("Error:" + item.getErrors());
                    }
                }
                logSnackbar((int)routeSum + " კმ.");
//                if (!resp.hasErrors()) {
////                    log("from:" + fromLat + "," + fromLon + " to:" + toLat + ","
////                            + toLon + " found path with distance:" + resp.getDistance()
////                            / 1000f + ", nodes:" + resp.getPoints().getSize() + ", time:"
////                            + time + " " + resp.getDebugInfo());
//                    logSnackbar((int) (resp.getDistance() / 100) / 10f + " კმ.");
//                    pathLayer = createPathLayer(resp);
//                    mapView.map().layers().add(pathLayer);
//                    mapView.map().updateMap(true);
//                } else {
//                    logUser("Error:" + resp.getErrors());
//                }
                shortestPathRunning = false;
            }
        }.execute();
    }


//    public void updateGpsLocation(double lat, double leng) {
//        Paint CIRCLE = Utils.createPaint(AndroidGraphicFactory.INSTANCE.createColor(128, 0, 0, 255), 0, org.mapsforge.core.graphics.Style.FILL);
//        Circle circl = new Circle(leng,lat,20);
//
//        GeoPoint center = new GeoPoint(lat,leng);
//        CircleDrawable circleDrawable = new CircleDrawable(center,12);
//        GPSitemizedLayer.removeAllItems();
//        GeoPoint z = new GeoPoint(leng, lat);
//
//        GPSitemizedLayer.addItem(createMarkerItem(z, circleDrawable));
//        mapView.map().layers().add(new Circle(;))
//        mapView.map().updateMap(true);
//    }
    public void updateGpsLocation(GeoPoint z) {
        GPSitemizedLayer.removeAllItems();
        Drawable drawable =getResources().getDrawable(R.drawable.ic_gps_location);
        Bitmap bitmap = drawableToBitmap(drawable);
        final MarkerSymbol symbol;
        symbol = new MarkerSymbol(bitmap, MarkerSymbol.HotspotPlace.CENTER);
        final MarkerItem markerItem = new MarkerItem("Your Location is: ","",z);
        markerItem.setMarker(symbol);
        GPSitemizedLayer.addItem(markerItem);
        GPSitemizedLayer.setOnItemGestureListener(new ItemizedLayer.OnItemGestureListener<MarkerItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerItem item) {
                Toast.makeText(Map.this,item.getTitle()+" "+item.getPoint(), Toast.LENGTH_SHORT).show();
                return true;
            }
            @Override
            public boolean onItemLongPress(int index, MarkerItem item) {
                return false;
            }
        });
        mapView.map().updateMap(true);
    }


    private void log(String str) {
        Log.i("GH", str);
    }

    private void log(String str, Throwable t) {
        Log.i("GH", str, t);
    }

    private void logUser(String str) {
        log(str);
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    private void logSnackbar(String str) {
        routeTextkillometers.setText(str);
        hideFloatingButtons(fab, fab1, fab2, fab3, fab4);
        if (!route_submenu_isopen) {
            rout_menu.animate().translationY(-getResources().getDimension(R.dimen.standard_80));
        } else {
        }
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }







    Collection<PointOfInterest> pointOfInterests;

    public void setPOIes()
    {
        BoundingBox box = new BoundingBox(41.60000,44.6000,41.84,45 );//es misaxedia
        Drawable drawable =getResources().getDrawable(R.drawable.ic_poi);
        Bitmap bitmap = drawableToBitmap(drawable);
        final MarkerSymbol symbol;
        symbol = new MarkerSymbol(bitmap, MarkerSymbol.HotspotPlace.CENTER);
        Drawable drawable1 =getResources().getDrawable(R.drawable.ic_transparent);
        Bitmap bitmap1 = drawableToBitmap(drawable1);
        final MarkerSymbol symbol1;
        symbol1 = new MarkerSymbol(bitmap1, MarkerSymbol.HotspotPlace.CENTER);
        MarkerItem markerItem;
        pointOfInterests = poiSearchTask.poiDbConnection(box);
        for(PointOfInterest d : pointOfInterests){
            markerItem = new MarkerItem(d.getName(),"",new GeoPoint(d.getLatitude(),d.getLongitude()));
            markerItem.setMarker(symbol1);
            POIitemizedLayer.addItem(markerItem);
            POIitemizedLayer.setOnItemGestureListener(new ItemizedLayer.OnItemGestureListener<MarkerItem>() {
                @Override
                public boolean onItemSingleTapUp(int index, MarkerItem item) {
                    SearchedPOIitemizedLayer.removeAllItems();
                    Toast.makeText(Map.this,item.getTitle()+" "+item.getPoint(), Toast.LENGTH_SHORT).show();
                    item.setMarker(symbol);
                    if(markerIt!=null)
                    {
                        if(item==markerIt)
                        {}else {
                            markerIt.setMarker(symbol1);
                            markerIt= item;
                        }
                    }else{
                        markerIt = item;
                    }
                    return true;
                }
                @Override
                public boolean onItemLongPress(int index, MarkerItem item) {
                    return false;
                }
            });
        }
        mapView.map().updateMap(true);
    }



    class MapEventsReceiver extends Layer implements GestureListener {



        MapEventsReceiver(org.oscim.map.Map map) {
            super(map);
        }

        @Override
        public boolean onGesture(Gesture g, MotionEvent e) {
            if (g instanceof Gesture.LongPress) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
                return onLongPress(p);
            }
            if (g instanceof Gesture.Tap) {
//                if(!isEverythingHidden)
//                {
//                    hideFloatingButtons(fab, fab1, fab2, fab3, fab4);
//                    rout_menu.animate().translationY(getResources().getDimension(R.dimen.standard_80));
//                    if (route_submenu_isopen) {
//                        route_submenu.setImageResource(R.drawable.menu);
//                        route_submenu_isopen = false;
//                    }
//                    isEverythingHidden=true;
//                }
//                else
//                {
//                    showFloatingButtons(fab, fab1, fab2, fab3, fab4);
//                    isEverythingHidden=false;
//                }

//                Collection<PointOfInterest> pointOfInterests = poiSearchTask.poiDbConnection(box);
//                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
//                for (final PointOfInterest pointOfInterest : pointOfInterests) {
////                    double z =(p.getLatitude() - pointOfInterest.getLatitude()) * (p.getLatitude() - pointOfInterest.getLatitude()) + (p.getLongitude() - pointOfInterest.getLongitude()) * ((p.getLongitude() - pointOfInterest.getLongitude()));
////                    double k = p.getLatitude();
//
//                    //final Circle circle = new FixedPixelCircle(pointOfInterest.getLatLong(), 16, CIRCLE, null);
//                    if(isInRectangle(pointOfInterest.getLatitude(),pointOfInterest.getLongitude(),0.0001627859,p.getLatitude(),p.getLongitude()));
//                    {
//                        Toast.makeText(Map.this, pointOfInterest.getName(), Toast.LENGTH_SHORT).show();
//                    }
//                    double centerx =pointOfInterest.getLatitude();
//                    double centery= pointOfInterest.getLongitude();
//                    double x = p.getLatitude();
//                    double y= p.getLongitude();
//                    double first = centerx-0.0001627859;
//                    double second=centerx+0.0001627859;
//                    double forth = centery-0.0001627859;
//                    double five = centery +0.0001627859;
//                   // pointOfInterest.getLatLong();




            }
            return false;
        }

        boolean isInRectangle(double centerX, double centerY, double radius,
                              double x, double y)
        {
            return centerX >= x - radius && centerX <= x + radius &&
                    centerY >= y - radius && centerY <= y + radius;
        }
    }
}