package android.support.v4.app;

import android.view.View;
import com.actionbarsherlock.ActionBarSherlock.OnCreatePanelMenuListener;
import com.actionbarsherlock.ActionBarSherlock.OnMenuItemSelectedListener;
import com.actionbarsherlock.ActionBarSherlock.OnPreparePanelListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import java.util.ArrayList;

public abstract class _ActionBarSherlockTrojanHorse
  extends FragmentActivity
  implements ActionBarSherlock.OnCreatePanelMenuListener, ActionBarSherlock.OnPreparePanelListener, ActionBarSherlock.OnMenuItemSelectedListener
{
  private static final boolean DEBUG = false;
  private static final String TAG = "_ActionBarSherlockTrojanHorse";
  private ArrayList<Fragment> mCreatedMenus;
  
  public abstract MenuInflater getSupportMenuInflater();
  
  public abstract boolean onCreateOptionsMenu(Menu paramMenu);
  
  public boolean onCreatePanelMenu(int paramInt, Menu paramMenu)
  {
    if (paramInt == 0)
    {
      boolean bool1 = onCreateOptionsMenu(paramMenu);
      MenuInflater localMenuInflater = getSupportMenuInflater();
      ArrayList localArrayList1 = this.mFragments.mActive;
      ArrayList localArrayList2 = null;
      boolean bool2 = false;
      if (localArrayList1 != null) {
        for (int j = 0; j < this.mFragments.mAdded.size(); j++)
        {
          Fragment localFragment2 = (Fragment)this.mFragments.mAdded.get(j);
          if ((localFragment2 != null) && (!localFragment2.mHidden) && (localFragment2.mHasMenu) && (localFragment2.mMenuVisible) && ((localFragment2 instanceof OnCreateOptionsMenuListener)))
          {
            bool2 = true;
            ((OnCreateOptionsMenuListener)localFragment2).onCreateOptionsMenu(paramMenu, localMenuInflater);
            if (localArrayList2 == null) {
              localArrayList2 = new ArrayList();
            }
            localArrayList2.add(localFragment2);
          }
        }
      }
      if (this.mCreatedMenus != null) {
        for (int i = 0; i < this.mCreatedMenus.size(); i++)
        {
          Fragment localFragment1 = (Fragment)this.mCreatedMenus.get(i);
          if ((localArrayList2 == null) || (!localArrayList2.contains(localFragment1))) {
            localFragment1.onDestroyOptionsMenu();
          }
        }
      }
      this.mCreatedMenus = localArrayList2;
      return bool1 | bool2;
    }
    return false;
  }
  
  public boolean onMenuItemSelected(int paramInt, MenuItem paramMenuItem)
  {
    if (paramInt == 0)
    {
      if (onOptionsItemSelected(paramMenuItem)) {
        return true;
      }
      if (this.mFragments.mActive != null) {
        for (int i = 0;; i++)
        {
          if (i >= this.mFragments.mAdded.size()) {
            break label113;
          }
          Fragment localFragment = (Fragment)this.mFragments.mAdded.get(i);
          if ((localFragment != null) && (!localFragment.mHidden) && (localFragment.mHasMenu) && (localFragment.mMenuVisible) && ((localFragment instanceof OnOptionsItemSelectedListener)) && (((OnOptionsItemSelectedListener)localFragment).onOptionsItemSelected(paramMenuItem))) {
            break;
          }
        }
      }
    }
    label113:
    return false;
  }
  
  public abstract boolean onOptionsItemSelected(MenuItem paramMenuItem);
  
  public abstract boolean onPrepareOptionsMenu(Menu paramMenu);
  
  public boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu)
  {
    if (paramInt == 0)
    {
      boolean bool1 = onPrepareOptionsMenu(paramMenu);
      ArrayList localArrayList = this.mFragments.mActive;
      boolean bool2 = false;
      if (localArrayList != null) {
        for (int i = 0; i < this.mFragments.mAdded.size(); i++)
        {
          Fragment localFragment = (Fragment)this.mFragments.mAdded.get(i);
          if ((localFragment != null) && (!localFragment.mHidden) && (localFragment.mHasMenu) && (localFragment.mMenuVisible) && ((localFragment instanceof OnPrepareOptionsMenuListener)))
          {
            bool2 = true;
            ((OnPrepareOptionsMenuListener)localFragment).onPrepareOptionsMenu(paramMenu);
          }
        }
      }
      return (bool1 | bool2) & paramMenu.hasVisibleItems();
    }
    return false;
  }
  
  public static abstract interface OnCreateOptionsMenuListener
  {
    public abstract void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater);
  }
  
  public static abstract interface OnOptionsItemSelectedListener
  {
    public abstract boolean onOptionsItemSelected(MenuItem paramMenuItem);
  }
  
  public static abstract interface OnPrepareOptionsMenuListener
  {
    public abstract void onPrepareOptionsMenu(Menu paramMenu);
  }
}


/* Location:
 * Qualified Name:     android.support.v4.app._ActionBarSherlockTrojanHorse
 * JD-Core Version:    0.7.0.1
 */