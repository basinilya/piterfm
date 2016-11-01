package ru.piter.fm.util;

import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 08.06.12
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
public class SearchFilter extends Filter{

    public List<? extends Filterable> objects;
    private ArrayAdapter adapter;

    public SearchFilter(List<? extends Filterable> objects, ArrayAdapter adapter) {
        this.objects = objects;
        this.adapter = adapter;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        constraint = constraint.toString().toLowerCase();
        FilterResults result = new FilterResults();
        if (constraint != null && constraint.toString().length() > 0) {
            List<Filterable> founded = new ArrayList<Filterable>();
            for (Filterable f : objects) {
                try {
                    if (f.toFilterString().contains(constraint))
                        founded.add(f);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            result.values = founded;
            result.count = founded.size();
        } else {
            result.values = objects;
            result.count = objects.size();
        }
        return result;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.clear();
        if (filterResults.values != null && filterResults.count > 0) {
            for (Filterable o : (List<Filterable>) filterResults.values)
                adapter.add(o);
            adapter.notifyDataSetChanged();
        }
    }

    public interface Filterable{
        String toFilterString();
    }

}
