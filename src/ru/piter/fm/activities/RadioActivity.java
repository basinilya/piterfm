package ru.piter.fm.activities;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import ru.piter.fm.App;
import ru.piter.fm.fragments.RadioAdapter;
import ru.piter.fm.fragments.RadioFragment;
import ru.piter.fm.player.PlayerService;
import ru.piter.fm.radio.RadioFactory;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.Settings;

public class RadioActivity
  extends SherlockFragmentActivity
  implements ViewPager.OnPageChangeListener, ActionBar.TabListener, SharedPreferences.OnSharedPreferenceChangeListener
{
  public static final int REQUEST_EXIT = 5;
  private ActionBar actionBar;
  private TextWatcher filterTextWatcher = new TextWatcher()
  {
    public void afterTextChanged(Editable paramAnonymousEditable) {}
    
    public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    
    public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      if (RadioActivity.this.mAdapter != null) {
        ((RadioFragment)RadioActivity.this.mAdapter.getItem(RadioActivity.this.mPager.getCurrentItem())).getAdapter().getFilter().filter(paramAnonymousCharSequence);
      }
    }
  };
  private boolean isSettingsChanged = false;
  private RadioAdapter mAdapter;
  private ViewPager mPager;
  private PhoneStateListener phoneListener = new PhoneStateListener()
  {
    public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString)
    {
      switch (paramAnonymousInt)
      {
      case 0: 
      default: 
        return;
      case 2: 
        App.getPlayer().stop();
        return;
      }
      App.getPlayer().stop();
    }
  };
  private EditText search;
  
  private void initUI()
  {
    setContentView(2130903063);
    this.mPager = ((ViewPager)findViewById(2131034159));
    this.mAdapter = new RadioAdapter(getSupportFragmentManager());
    this.actionBar = getSupportActionBar();
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    getSupportActionBar().setNavigationMode(2);
    getSupportActionBar().setDisplayShowTitleEnabled(true);
    this.actionBar.addTab(this.actionBar.newTab().setText("PITER FM").setTabListener(this));
    this.actionBar.addTab(this.actionBar.newTab().setText("MOSKVA FM").setTabListener(this));
    this.mAdapter.addFragment(new RadioFragment(RadioFactory.getRadio("PiterFM")));
    this.mAdapter.addFragment(new RadioFragment(RadioFactory.getRadio("MoskvaFM")));
    if (Settings.isFavouritesEnabled())
    {
      this.actionBar.addTab(this.actionBar.newTab().setText("FAV").setTabListener(this));
      this.mAdapter.addFragment(new RadioFragment(RadioFactory.getRadio("Favourite")));
    }
    this.mPager.setAdapter(this.mAdapter);
    this.mPager.setOnPageChangeListener(this);
    LinearLayout localLinearLayout = (LinearLayout)findViewById(2131034151);
    AdView localAdView = new AdView(this, AdSize.BANNER, "a15044929d0ad8b");
    localLinearLayout.addView(localAdView);
    localAdView.loadAd(new AdRequest());
    ((TelephonyManager)getSystemService("phone")).listen(this.phoneListener, 32);
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((paramInt1 == 5) && (paramInt2 == -1)) {
      finish();
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    initUI();
    Settings.getPreferences().registerOnSharedPreferenceChangeListener(this);
  }
  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    paramMenu.add(0, 1, 1, "Refresh").setIcon(2130837599).setShowAsAction(1);
    paramMenu.add(0, 2, 2, "Settings").setIcon(2130837594).setShowAsAction(1);
    paramMenu.add(0, 3, 3, "Search").setIcon(2130837593).setActionView(2130903060).setShowAsAction(9);
    paramMenu.add(0, 4, 4, 2131361812).setIcon(2130837596).setShowAsAction(1);
    return super.onCreateOptionsMenu(paramMenu);
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    if (this.search != null) {
      this.search.removeTextChangedListener(this.filterTextWatcher);
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    }
    for (;;)
    {
      return true;
      ((RadioFragment)this.mAdapter.getItem(this.mPager.getCurrentItem())).updateChannels();
      continue;
      startActivity(new Intent(this, SettingsActivity.class));
      continue;
      this.search = ((EditText)paramMenuItem.getActionView());
      this.search.addTextChangedListener(this.filterTextWatcher);
      continue;
      new AlertDialog.Builder(this).setTitle(2131361814).setPositiveButton(2131361815, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          App.getPlayer().stop();
          Notifications.killNotification(5);
          RadioActivity.this.finish();
        }
      }).setNegativeButton(2131361816, null).create().show();
    }
  }
  
  public void onPageScrollStateChanged(int paramInt) {}
  
  public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {}
  
  public void onPageSelected(int paramInt)
  {
    getSupportActionBar().selectTab(getSupportActionBar().getTabAt(paramInt));
  }
  
  protected void onResume()
  {
    super.onResume();
    if (this.isSettingsChanged)
    {
      this.isSettingsChanged = false;
      finish();
      startActivity(new Intent(this, RadioActivity.class));
    }
  }
  
  public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString)
  {
    if (paramString.equals("channel_sort_key")) {
      this.isSettingsChanged = true;
    }
  }
  
  public void onTabReselected(ActionBar.Tab paramTab, FragmentTransaction paramFragmentTransaction) {}
  
  public void onTabSelected(ActionBar.Tab paramTab, FragmentTransaction paramFragmentTransaction)
  {
    this.mPager.setCurrentItem(paramTab.getPosition());
    getSupportActionBar().setTitle(paramTab.getText());
  }
  
  public void onTabUnselected(ActionBar.Tab paramTab, FragmentTransaction paramFragmentTransaction) {}
}


/* Location:
 * Qualified Name:     ru.piter.fm.activities.RadioActivity
 * JD-Core Version:    0.7.0.1
 */