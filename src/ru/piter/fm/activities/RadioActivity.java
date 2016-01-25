package ru.piter.fm.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.*;
import static android.support.v4.view.MenuItemCompat.*;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import ru.piter.fm.App;
import ru.piter.fm.fragments.RadioAdapter;
import ru.piter.fm.fragments.RadioFragment;
import ru.piter.fm.player.PlayerInterface;
import ru.piter.fm.prototype.R;
import ru.piter.fm.radio.RadioFactory;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.Settings;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 24.08.2010
 * Time: 20:16:15
 * To change this template use File | SettingsActivity | File Templates.
 */
public class RadioActivity extends SherlockFragmentActivity implements ViewPager.OnPageChangeListener, ActionBar.TabListener, SharedPreferences.OnSharedPreferenceChangeListener {


    public static final int REQUEST_EXIT = 5;
    private ViewPager mPager;
    private RadioAdapter mAdapter;
    private ActionBar actionBar;
    private EditText search;

    private boolean isSettingsChanged = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //checkLicense();
        initUI();

        Settings.getPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void initUI() {
        setContentView(R.layout.main);

        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new RadioAdapter(getSupportFragmentManager());
        actionBar = getSupportActionBar();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            String caption = RadioAdapter.getCity(i);
            if (RadioFactory.FAVOURITE.equals(caption)) caption = "FAV";
            actionBar.addTab(actionBar.newTab().setText(caption).setTabListener(this));
        }

        //mAdapter.count = 2;
        /*
        if(Settings.isFavouritesEnabled()){
            addFavTab();
            mAdapter.count = 3;
        }
        */

        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(this);
    }

    private void addFavTab() {
        actionBar.addTab(actionBar.newTab().setText("FAV").setTabListener(this));
    }


    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

        menu.add(0, 100, 100, R.string.ac_refresh).setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 200, 200, R.string.ac_settings).setIcon(R.drawable.ic_action_settings).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 300, 300, R.string.ac_search).setIcon(R.drawable.ic_action_search).setActionView(R.layout.action_search)
                                    .setShowAsAction(SHOW_AS_ACTION_IF_ROOM | SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menu.add(0, 350, 350, R.string.ac_redownload).setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 400, 400, R.string.ac_exit    ).setIcon(R.drawable.ic_cancel).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ArrayAdapter<?> adapter = mAdapter.getPrimaryFragment().getListAdapter();
            adapter.getFilter().filter(s);
        }
    };

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()){
            case 100:
                mAdapter.getPrimaryFragment().updateChannels(false);
                break;
            case 200:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case 300:
                search = (EditText) item.getActionView();
                search.addTextChangedListener(filterTextWatcher);
                break;
            case 350:
                ((RadioFragment) mAdapter.getItem(mPager.getCurrentItem())).updateChannels(true);
                break;
            case 400:

                final AlertDialog alert;
                AlertDialog.Builder builder = new AlertDialog.Builder(RadioActivity.this)
                        .setTitle(R.string.request_exit)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                App.getPlayer().pause();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                alert = builder.create();
                alert.show();
                break;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction transaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction transaction) {
        mPager.setCurrentItem(tab.getPosition());
        getSupportActionBar().setTitle(tab.getText());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction transaction) {}

    @Override
    public void onPageScrolled(int i, float v, int i1) {}

    @Override
    public void onPageSelected(int i) {
        getSupportActionBar().selectTab(getSupportActionBar().getTabAt(i));
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(Settings.FAVOURITES)) {
            /*
            if (Settings.isFavouritesEnabled()) {
                if (mAdapter.count == 2) {
                    addFavTab();
                    mAdapter.count = 3;
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                if (mAdapter.count == 3) {
                    actionBar.removeTabAt(2);
                    mAdapter.count = 2;
                    mAdapter.notifyDataSetChanged();
                }
            }
            */
        }
        if (key.equals(Settings.CHANNEL_SORT_TYPE))
        isSettingsChanged = true;
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (search != null)
            search.removeTextChangedListener(filterTextWatcher);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isSettingsChanged) {
            isSettingsChanged = false;
            finish();
            startActivity(new Intent(this, RadioActivity.class));
        }

    }




}
