package ge.ideadesigngroup.ideamap.TabFragments;

/**
 * Created by IDGUser on 4/7/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ge.ideadesigngroup.ideamap.Models.SearchModel;
import ge.ideadesigngroup.ideamap.R;

public class RVAdapter extends RecyclerView.Adapter<ItemViewHolder>
{

    private List<SearchModel> history;

    public RVAdapter(List<SearchModel> history) {
        this.history = history;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        final SearchModel model = history.get(i);
        itemViewHolder.bind(model);
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_list_row, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount()
    {
        return history.size();
    }

//    public void setFilter(List<ListModel> history){
//        history = new ArrayList<>();
//        history.addAll(history);
//        notifyDataSetChanged();
//    }


}