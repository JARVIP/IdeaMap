package ge.ideadesigngroup.ideamap.TabFragments;

/**
 * Created by IDGUser on 4/7/2017.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ge.ideadesigngroup.ideamap.Models.SearchModel;
import ge.ideadesigngroup.ideamap.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    public TextView name_TextView;
    public ImageView image_ImageView;
    public LinearLayout layout;
    public  RecyclerView recyclerview;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        itemView.setClickable(true);
        name_TextView = (TextView) itemView.findViewById(R.id.name);
        image_ImageView = (ImageView) itemView.findViewById(R.id.ImageView_id);
        layout =(LinearLayout) itemView.findViewById(R.id.category_list);
    }
    public void bind(final SearchModel categoy) {
        name_TextView.setText(categoy.getName());
        image_ImageView.setImageResource(categoy.getImage());
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("ge.ideadesigngroup.ideamap.CategorySearrchActivity");
                intent.putExtra("Category", categoy.getName());
                v.getContext().startActivity(intent);
                return;

            }
        });
    }


}