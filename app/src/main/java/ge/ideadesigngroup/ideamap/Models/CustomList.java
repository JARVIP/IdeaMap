package ge.ideadesigngroup.ideamap.Models;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ge.ideadesigngroup.ideamap.R;

public class CustomList extends ArrayAdapter<String>
{
    private final Activity context;
    private final String[] web;
    private final int Id;
    private View rowView;
    public CustomList(Activity context,
                      String[] web, int Id) {
        super(context,(Id==1)?R.layout.local_map_list:R.layout.download_map_list, web);
        this.context = context;
        this.web = web;
        this.Id = Id;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        if(Id==1) {
            rowView = inflater.inflate(R.layout.local_map_list, null, true);
        }else
        {
            rowView = inflater.inflate(R.layout.download_map_list, null, true);
        }
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web[position]);

        if(Id==1) {
            imageView.setImageResource(R.drawable.map);
        }else {
            imageView.setImageResource(R.drawable.download);
        }
        return rowView;
    }
}
