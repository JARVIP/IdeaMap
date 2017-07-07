package ge.ideadesigngroup.ideamap.Models;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.poi.storage.PoiCategory;

import java.io.Serializable;

/**
 * Created by IDGUser on 4/7/2017.
 */

public class SearchModel implements Serializable {
    int image;
    String name;
    PoiCategory category;
    long id;
    String Data;
    LatLong latLong;

    public SearchModel(int image, String name)
    {
        this.image=image;
        this.name=name;
    }
    public SearchModel(String name, PoiCategory category,long id,String Data,LatLong latLong)
    {
        this.name=name;
        this.category=category;
        this.id=id;
        this.Data=Data;
        this.latLong=latLong;
    }
    public int getImage()
    {
        return image;
    }
    public String getName()
    {
        return name;
    }
    public PoiCategory getCategory()
    {
        return category;
    }
    public long getId(){return id;}
    public String getData(){return Data;}
    public LatLong getLatLong(){return latLong;}
}
