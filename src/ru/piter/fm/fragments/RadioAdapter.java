package ru.piter.fm.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class RadioAdapter
  extends FragmentPagerAdapter
{
  public List<Fragment> fragments;
  
  public RadioAdapter(FragmentManager paramFragmentManager)
  {
    super(paramFragmentManager);
  }
  
  public void addFragment(Fragment paramFragment)
  {
    if (this.fragments == null) {
      this.fragments = new ArrayList();
    }
    this.fragments.add(paramFragment);
  }
  
  public int getCount()
  {
    return this.fragments.size();
  }
  
  public Fragment getItem(int paramInt)
  {
    return (Fragment)this.fragments.get(paramInt);
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.fragments.RadioAdapter
 * JD-Core Version:    0.7.0.1
 */