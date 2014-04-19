package ru.piter.fm.util;

import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchFilter
  extends Filter
{
  private ArrayAdapter adapter;
  private List<? extends Filterable> objects;
  
  public SearchFilter(List<? extends Filterable> paramList, ArrayAdapter paramArrayAdapter)
  {
    this.objects = paramList;
    this.adapter = paramArrayAdapter;
  }
  
  protected Filter.FilterResults performFiltering(CharSequence paramCharSequence)
  {
    String str = paramCharSequence.toString().toLowerCase();
    Filter.FilterResults localFilterResults = new Filter.FilterResults();
    if ((str != null) && (str.toString().length() > 0))
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = this.objects.iterator();
      while (localIterator.hasNext())
      {
        Filterable localFilterable = (Filterable)localIterator.next();
        try
        {
          if (localFilterable.toFilterString().contains(str)) {
            localArrayList.add(localFilterable);
          }
        }
        catch (NullPointerException localNullPointerException)
        {
          localNullPointerException.printStackTrace();
        }
      }
      localFilterResults.values = localArrayList;
      localFilterResults.count = localArrayList.size();
      return localFilterResults;
    }
    localFilterResults.values = this.objects;
    localFilterResults.count = this.objects.size();
    return localFilterResults;
  }
  
  protected void publishResults(CharSequence paramCharSequence, Filter.FilterResults paramFilterResults)
  {
    this.adapter.clear();
    if ((paramFilterResults.values != null) && (paramFilterResults.count > 0))
    {
      Iterator localIterator = ((List)paramFilterResults.values).iterator();
      while (localIterator.hasNext())
      {
        Filterable localFilterable = (Filterable)localIterator.next();
        this.adapter.add(localFilterable);
      }
      this.adapter.notifyDataSetChanged();
    }
  }
  
  public static abstract interface Filterable
  {
    public abstract String toFilterString();
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.util.SearchFilter
 * JD-Core Version:    0.7.0.1
 */