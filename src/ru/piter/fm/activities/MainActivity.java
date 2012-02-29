package ru.piter.fm.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import ru.piter.fm.ExceptionHandler;
import ru.piter.fm.R;
import ru.piter.fm.activities.RadioActivity;
import ru.piter.fm.radio.RadioFactory;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 24.08.2010
 * Time: 20:16:15
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends TabActivity implements TabHost.OnTabChangeListener {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        tabHost.setOnTabChangedListener(this);
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab


        intent = new Intent();
        intent.putExtra("radio", RadioFactory.PITER_FM);
        intent.setClass(this, RadioActivity.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Fire activity every tab click!
        spec = tabHost.newTabSpec(RadioFactory.PITER_FM).setIndicator("Piter FM", res.getDrawable(R.drawable.piter_logos)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent();
        intent.putExtra("radio", RadioFactory.MOSKVA_FM);
        intent.setClass(this, RadioActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // Fire activity every tab click!
        spec = tabHost.newTabSpec(RadioFactory.MOSKVA_FM).setIndicator("Moskva FM", res.getDrawable(R.drawable.moskva_logos)).setContent(intent);
        tabHost.addTab(spec);

        int currentTab = getPreferences(MODE_PRIVATE).getInt("currentTab", 0);
        tabHost.setCurrentTab(currentTab);
        
    }

    @Override
    protected void onStop() {
      super.onStop();
      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = getPreferences(MODE_PRIVATE);
      SharedPreferences.Editor editor = settings.edit();
      editor.putInt("currentTab", getTabHost().getCurrentTab());
      // Commit the edits!
      editor.commit();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public void onTabChanged(String tabId) {

    }
}
