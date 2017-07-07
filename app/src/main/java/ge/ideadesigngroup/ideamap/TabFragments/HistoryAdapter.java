package ge.ideadesigngroup.ideamap.TabFragments;;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ge.ideadesigngroup.ideamap.Models.SearchModel;
import ge.ideadesigngroup.ideamap.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private List<SearchModel> history;

    public HistoryAdapter(List<SearchModel> history) {
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
    public int getItemCount() {
        return history.size();
    }

    public void setFilter(List<ListModel> history){
        history = new ArrayList<>();
        history.addAll(history);
        notifyDataSetChanged();
    }

}
