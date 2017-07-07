package ge.ideadesigngroup.ideamap.Models;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.poi.android.storage.AndroidPoiPersistenceManagerFactory;
import org.mapsforge.poi.storage.ExactMatchPoiCategoryFilter;
import org.mapsforge.poi.storage.PoiCategoryFilter;
import org.mapsforge.poi.storage.PoiCategoryManager;
import org.mapsforge.poi.storage.PoiPersistenceManager;
import org.mapsforge.poi.storage.PointOfInterest;

import java.util.Collection;

public class PoiSearchTask{
    private String category;
    private String POI_FILE;


    public PoiSearchTask(String category, String POI_FILE) {
        this.category = category;
        this.POI_FILE = POI_FILE;
    }
    public PoiSearchTask(String POI_FILE) {
        this.POI_FILE = POI_FILE;
    }

    public Collection<PointOfInterest> poiDbConnection(BoundingBox... params) {
        PoiPersistenceManager persistenceManager = null;
        try {
            persistenceManager = AndroidPoiPersistenceManagerFactory.getPoiPersistenceManager(POI_FILE);
            PoiCategoryManager categoryManager = persistenceManager.getCategoryManager();
            PoiCategoryFilter categoryFilter = new ExactMatchPoiCategoryFilter();
           // categoryFilter.addCategory(categoryManager.getPoiCategoryByTitle(this.category));
            return persistenceManager.findInRect(params[0], categoryFilter, null, Integer.MAX_VALUE);
        } catch (Throwable t) {
            // Log.e(SamplesApplication.TAG, t.getMessage(), t);
        } finally {
            if (persistenceManager != null) {
                persistenceManager.close();
            }
        }
        return null;
    }
    public Collection<PointOfInterest> poiCategoryDbConnection(BoundingBox... params) {
        PoiPersistenceManager persistenceManager = null;
        try {
            persistenceManager = AndroidPoiPersistenceManagerFactory.getPoiPersistenceManager(POI_FILE);
            PoiCategoryManager categoryManager = persistenceManager.getCategoryManager();
            PoiCategoryFilter categoryFilter = new ExactMatchPoiCategoryFilter();
            categoryFilter.addCategory(categoryManager.getPoiCategoryByTitle(this.category));
            return persistenceManager.findInRect(params[0], categoryFilter, null, Integer.MAX_VALUE);
        } catch (Throwable t) {
            // Log.e(SamplesApplication.TAG, t.getMessage(), t);
        } finally {
            if (persistenceManager != null) {
                persistenceManager.close();
            }
        }
        return null;
    }
}