package ge.ideadesigngroup.ideamap.Models;

/**
 * Created by jarvis on 4/9/17.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ge.ideadesigngroup.ideamap.R;

public class CategorySearchList extends ArrayAdapter<CategorySearchModel>
{
    private final Activity context;
    private final ArrayList<CategorySearchModel>category;
    private View rowView;
    public CategorySearchList(Activity context,ArrayList<CategorySearchModel> web) {
        super(context, R.layout.category_list_row,web);
        this.context = context;
        this.category = web;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.category_list_row, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        String cat;
        cat=category.get(position).getCategory();
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(category.get(position).getName());
        switch (cat) {
            case "Food":
                imageView.setImageResource(R.drawable.ic_food);
                break;
            case "Financial institutes":
                imageView.setImageResource(R.drawable.ic_bank);
                break;
            case "Tourism":
                imageView.setImageResource(R.drawable.ic_hotel);
                break;
            default:
                imageView.setImageResource(R.drawable.download);
                break;
        }

        return rowView;
    }
}
