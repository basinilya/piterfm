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
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
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



    private PhoneStateListener phoneListener = new PhoneStateListener() {
        private boolean wasPausedByMe;
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            PlayerInterface pl = App.getPlayer();
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (wasPausedByMe) {
                        wasPausedByMe = false;
                        if (App.getPlayer().getChannelId() != null)
                            App.getPlayer().resume();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    /* fallthrough */
                case TelephonyManager.CALL_STATE_RINGING:
                    if (!pl.isPaused()) {
                        wasPausedByMe = true;
                        App.getPlayer().pause();
                    }
                    break;
            }
        }
    };

    private void initUI() {
        setContentView(R.layout.main);

        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new RadioAdapter(getSupportFragmentManager());
        actionBar = getSupportActionBar();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        actionBar.addTab(actionBar.newTab().setText("PITER FM").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("MOSKVA FM").setTabListener(this));

        mAdapter.addFragment(new RadioFragment(RadioFactory.getRadio(RadioFactory.PITER_FM)));
        mAdapter.addFragment(new RadioFragment(RadioFactory.getRadio(RadioFactory.MOSKVA_FM)));

        if(Settings.isFavouritesEnabled()){
            actionBar.addTab(actionBar.newTab().setText("FAV").setTabListener(this));
            mAdapter.addFragment(new RadioFragment(RadioFactory.getRadio(RadioFactory.FAVOURITE)));
        }

        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(this);

        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        tm.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }


    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

        menu.add(0, 1, 1, "Refresh").setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM );
        menu.add(0, 2, 2, "Settings").setIcon(R.drawable.ic_action_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 3, 3, "Search").setIcon(R.drawable.ic_action_search).setActionView(R.layout.action_search)
                                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menu.add(0, 4, 4, R.string.ac_exit    ).setIcon(R.drawable.ic_cancel).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mAdapter != null ){
                ArrayAdapter adapter = ((RadioFragment)mAdapter.getItem(mPager.getCurrentItem())).getAdapter();
                adapter.getFilter().filter(s);
            }

        }
    };

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()){
            case 1:
                ((RadioFragment) mAdapter.getItem(mPager.getCurrentItem())).updateChannels();
                break;
            case 2:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case 3:
                search = (EditText) item.getActionView();
                search.addTextChangedListener(filterTextWatcher);
                break;
            case 4:

                final AlertDialog alert;
                AlertDialog.Builder builder = new AlertDialog.Builder(RadioActivity.this)
                        .setTitle(R.string.request_exit)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                App.getPlayer().release();
                                Notifications.killNotification(Notifications.PLAY_STOP);
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
