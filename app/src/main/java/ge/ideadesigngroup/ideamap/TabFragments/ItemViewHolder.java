package ge.ideadesigngroup.ideamap.TabFragments;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ge.ideadesigngroup.ideamap.Map;
import ge.ideadesigngroup.ideamap.Models.SearchModel;
import ge.ideadesigngroup.ideamap.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public TextView name_TextView;
    public ImageView imageView;
    public LinearLayout layout;
   // private Context context = new Map().getContext();
    public ItemViewHolder(View itemView) {
        super(itemView);
        itemView.setClickable(true);
        name_TextView = (TextView) itemView.findViewById(R.id.name);
        imageView = (ImageView) itemView.findViewById(R.id.ImageView_id);
        layout = (LinearLayout) itemView.findViewById(R.id.category_list);
    }
    public void bind(final SearchModel searchModel) {
        name_TextView.setText(searchModel.getName());
        final String name = searchModel.getCategory().getTitle();
        switch (name)
        {
            case "Tourism":imageView.setImageResource(R.drawable.ic_hotel);
                break;
            case "Food":imageView.setImageResource(R.drawable.ic_food);
                break;
            case "Financial institutes":imageView.setImageResource(R.drawable.ic_bank);
                break;
            default: imageView.setImageResource(R.drawable.ic_search);
                break;
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context,Map.class);
                double a =searchModel.getLatLong().getLatitude();
                double z = searchModel.getLatLong().getLongitude();
                intent.putExtra("Latitude", a);
                intent.putExtra("Longitude",z);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(intent);
            }
        });
    }


}
