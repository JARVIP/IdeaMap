package ge.ideadesigngroup.ideamap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.graphhopper.GraphHopper;
import com.graphhopper.util.Constants;
import com.graphhopper.util.Helper;
import com.graphhopper.util.ProgressListener;

import org.oscim.android.MapView;
import org.oscim.core.Tile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ge.ideadesigngroup.ideamap.Models.CustomList;
import ge.ideadesigngroup.ideamap.Models.GHAsyncTask;
import ge.ideadesigngroup.ideamap.Models.AndroidHelper;
import ge.ideadesigngroup.ideamap.Models.AndroidDownloader;

public class Main extends Activity implements NavigationView.OnNavigationItemSelectedListener, AppCompatCallback {
    private MapView mapView;
    private GraphHopper hopper;
    private String currentArea = "berlin";
    private String fileListURL = "http://download2.graphhopper.com/public/maps/" + Constants.getMajorVersion() + "/";
    private String prefixURL = fileListURL;
    private String downloadURL;
    private File mapsFolder;
    private ListView list;
    private String[] web;
    private AppCompatDelegate delegate;
    private int listId=1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Tile.SIZE = Tile.calculateTileSize(getResources().getDisplayMetrics().scaledDensity);
        mapView = new MapView(this);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        delegate = AppCompatDelegate.create(this, this);

        delegate.setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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
         //   mapsFolder.mkdir();
        } else
            mapsFolder = new File(Environment.getExternalStorageDirectory(), "/graphhopper/maps/");
         //  mapsFolder.mkdir();



        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs();
        }
        // TODO get user confirmation to download
        chooseAreaFromLocal();
        //chooseAreaFromRemote();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_download) {
            chooseAreaFromRemote();
        } else if (id == R.id.nav_gallery) {
            chooseAreaFromLocal();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent("ge.ideadesigngroup.ideamap.SETTINGS");
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
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


    private void initFiles(String area) {
        currentArea = area;
        downloadingFiles();
    }

    private void chooseAreaFromLocal() {
        List<String> nameList = new ArrayList<>();
        String[] files = mapsFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename != null
                        && (filename.endsWith(".ghz") || filename
                        .endsWith("-gh"));
            }
        });


        Collections.addAll(nameList, files);

        if (nameList.isEmpty())
            return;
        list = (ListView) findViewById(R.id.list);
        listId = 1;
        chooseArea(list, nameList,
                new MySpinnerListener() {
                    @Override
                    public void onSelect(String selectedArea, String selectedFile) {
                        Intent intent = new Intent("ge.ideadesigngroup.ideamap.Map");
                        intent.putExtra("selectedArea", selectedArea);
                        downloadURL = null;
                        startActivity(intent);
                    }
                });

    }

    private void chooseAreaFromRemote() {
        new GHAsyncTask<Void, Void, List<String>>() {
            protected List<String> saveDoInBackground(Void... params)
                    throws Exception {
                String[] lines = new AndroidDownloader().downloadAsString(fileListURL, false).split("\n");
                List<String> res = new ArrayList<>();
                for (String str : lines) {
                    int index = str.indexOf("href=\"");
                    if (index >= 0) {
                        index += 6;
                        int lastIndex = str.indexOf(".ghz", index);
                        if (lastIndex >= 0)
                            res.add(prefixURL + str.substring(index, lastIndex)
                                    + ".ghz");
                    }
                }
                return res;
            }

            @Override
            protected void onPostExecute(List<String> nameList) {
                if (hasError()) {
                    getError().printStackTrace();
                    logUser("Are you connected to the internet? Problem while fetching remote area list: "
                            + getErrorMessage());
                    return;
                } else if (nameList == null || nameList.isEmpty()) {
                    logUser("No maps created for your version!? " + fileListURL);
                    return;
                }

                MySpinnerListener spinnerListener = new MySpinnerListener() {
                    @Override
                    public void onSelect(String selectedArea, String selectedFile) {
                        if (selectedFile == null
                                || new File(mapsFolder, selectedArea + ".ghz").exists()
                                || new File(mapsFolder, selectedArea + "-gh").exists()) {
                            downloadURL = null;
                        } else {
                            downloadURL = selectedFile;
                        }
                        if(selectedFile==null) {
                            Intent intent = new Intent("ge.ideadesigngroup.ideamap.Map");
                            intent.putExtra("selectedArea", selectedArea);
                            startActivity(intent);
                        }
                        else
                        {
                            initFiles(selectedArea);
                        }
                    }
                };
                listId=2;
                list = (ListView) findViewById(R.id.list);
                chooseArea(list, nameList,
                        spinnerListener);
            }
        }.execute();
    }

    private void chooseArea(ListView button,
                            List<String> nameList, final MySpinnerListener myListener) {
        final Map<String, String> nameToFullName = new TreeMap<>();
        int index = nameList.size(), i = 0;
        web = new String[index];
        for (String fullName : nameList) {
            String tmp = Helper.pruneFileEnd(fullName);
            if (tmp.endsWith("-gh"))
                tmp = tmp.substring(0, tmp.length() - 3);

            tmp = AndroidHelper.getFileName(tmp);
            web[i] = tmp;
            i++;
            nameToFullName.put(tmp, fullName);
        }

        nameList.clear();
        nameList.addAll(nameToFullName.keySet());
        CustomList adapter = new
                CustomList(Main.this, web, listId);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object o = web[position];
                if (o != null && o.toString().length() > 0 && !nameToFullName.isEmpty()) {
                    String area = o.toString();
                    myListener.onSelect(area, nameToFullName.get(area));
                } else {
                    myListener.onSelect(null, null);
                }

            }
        });


    }

    void downloadingFiles() {
        final File areaFolder = new File(mapsFolder, currentArea + "-gh");
        if (downloadURL == null || areaFolder.exists()) {
            Intent intent = new Intent("ge.ideadesigngroup.ideamap.Map");
            intent.putExtra("selectedArea", currentArea);
            startActivity(intent);
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
                    //loadMap(areaFolder);
                    Intent intent = new Intent("ge.ideadesigngroup.ideamap.Map");
                    intent.putExtra("selectedArea", currentArea);
                    startActivity(intent);

                }
            }
        }.execute();
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

    public interface MySpinnerListener {
        void onSelect(String selectedArea, String selectedFile);
    }

}
