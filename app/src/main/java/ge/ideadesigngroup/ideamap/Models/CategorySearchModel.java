package ge.ideadesigngroup.ideamap.Models;

import org.mapsforge.core.model.LatLong;

import java.io.Serializable;

/**
 * Created by jarvis on 4/9/17.
 */

public class CategorySearchModel{
    String Category;
    String name;
    long id;
    String Data;
    LatLong latLong;
    public CategorySearchModel(String category,String name,long id,String Data,LatLong latLong)
    {
        this.Category=category;
        this.name=name;
        this.id=id;
        this.Data=Data;
        this.latLong=latLong;
    }
    public String getCategory()
    {
        return Category;
    }
    public String getName()
    {
        return name;
    }
    public long getId()
    {
        return id;
    }
    public String getData(){return Data;}
    public LatLong getLatLong(){return latLong;}

}