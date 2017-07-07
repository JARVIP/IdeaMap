package ge.ideadesigngroup.ideamap.TabFragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ge.ideadesigngroup.ideamap.Models.SearchModel;
import ge.ideadesigngroup.ideamap.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private List<SearchModel> history;

    public CategoryAdapter(List<SearchModel> history)
    {
        this.history = history;
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder itemViewHolder, int i) {
        final SearchModel model = history.get(i);
        itemViewHolder.bind(model);
    }


    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_list_row, viewGroup, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public int getItemCount()
    {
        return history.size();
    }


}

