package ru.piter.fm.activities;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import ru.piter.fm.App;
import ru.piter.fm.player.PlayerService;
import ru.piter.fm.player.PlayerService.State;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;
import ru.piter.fm.radio.RadioFactory;
import ru.piter.fm.radio.Track;
import ru.piter.fm.tasks.GetTracksTask;
import ru.piter.fm.tasks.GetTracksTask.TracksLoadingListener;
import ru.piter.fm.tasks.PlayerTask;
import ru.piter.fm.util.DBAdapter;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.RadioUtils;
import ru.piter.fm.util.SearchFilter;
import ru.piter.fm.util.Settings;

public class ChannelActivity
  extends SherlockListActivity
  implements GetTracksTask.TracksLoadingListener, SharedPreferences.OnSharedPreferenceChangeListener
{
  private final int BUFFERING_DIALOG = 0;
  private final int DATEPICKER_DIALOG = 1;
  private final int TIMEPICKER_DIALOG = 2;
  private TrackAdapter adapter;
  private LinearLayout buttons;
  private Channel channel;
  private Track currentTrack;
  private Button dateButton;
  private Calendar day;
  private Radio favouriteRadio = RadioFactory.getRadio("Favourite");
  private TextWatcher filterTextWatcher = new TextWatcher()
  {
    public void afterTextChanged(Editable paramAnonymousEditable) {}
    
    public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    
    public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      if (ChannelActivity.this.adapter != null) {
        ChannelActivity.this.adapter.getFilter().filter(paramAnonymousCharSequence);
      }
    }
  };
  private Typeface font;
  private boolean isSettingsChanged = false;
  private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
  {
    public void onDateSet(DatePicker paramAnonymousDatePicker, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      ChannelActivity.this.day.set(5, paramAnonymousInt3);
      ChannelActivity.this.day.set(2, paramAnonymousInt2);
      ChannelActivity.this.day.set(1, paramAnonymousInt1);
      ChannelActivity.this.dateButton.setText(new SimpleDateFormat("dd.MM.yyyy").format(ChannelActivity.this.day.getTime()));
      if (!new Date().before(ChannelActivity.this.day.getTime()))
      {
        GetTracksTask localGetTracksTask = new GetTracksTask(ChannelActivity.this);
        localGetTracksTask.setTracksLoadingListener(ChannelActivity.this);
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = ChannelActivity.this.day.getTime();
        arrayOfObject[1] = ChannelActivity.this.channel;
        localGetTracksTask.execute(arrayOfObject);
        return;
      }
      Toast.makeText(ChannelActivity.this, 2131361859, 0).show();
    }
  };
  private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener()
  {
    public void onTimeSet(TimePicker paramAnonymousTimePicker, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      ChannelActivity.this.day.set(ChannelActivity.this.day.get(1), ChannelActivity.this.day.get(2), ChannelActivity.this.day.get(5), paramAnonymousInt1, paramAnonymousInt2);
      ChannelActivity.this.timeButton.setText(ChannelActivity.this.day.get(11) + ":" + ChannelActivity.this.getRightMinutes(ChannelActivity.this.day));
      Track localTrack = new Track();
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
      ChannelActivity.this.day.setTimeZone(TimeZone.getTimeZone("GMT+4"));
      Date localDate = ChannelActivity.this.day.getTime();
      localTrack.setTime(localSimpleDateFormat.format(localDate));
      if (!new Date().before(localDate))
      {
        PlayerTask local1 = new PlayerTask(ChannelActivity.this)
        {
          public void onResult(Void paramAnonymous2Void)
          {
            ChannelActivity.this.inflatePlayStopButton();
          }
        };
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = ChannelActivity.this.channel;
        arrayOfObject[1] = localTrack;
        local1.execute(arrayOfObject);
        return;
      }
      Toast.makeText(ChannelActivity.this, 2131361858, 0).show();
    }
  };
  private ImageButton playButton;
  private EditText search;
  private Button timeButton;
  
  private String getRightMinutes(Calendar paramCalendar)
  {
    int i = paramCalendar.get(12);
    if (i < 9) {
      return "0" + i;
    }
    return String.valueOf(i);
  }
  
  private void inflatePlayStopButton()
  {
    Channel localChannel = this.channel;
    App.getPlayer();
    int i;
    ImageButton localImageButton;
    if (localChannel.equals(PlayerService.channel))
    {
      App.getPlayer();
      if (PlayerService.state == PlayerService.State.Playing)
      {
        i = 1;
        localImageButton = this.playButton;
        if (i == 0) {
          break label103;
        }
      }
    }
    label103:
    for (int j = 2130837605;; j = 2130837600)
    {
      localImageButton.setImageResource(j);
      if (i == 0) {
        break label110;
      }
      Intent localIntent = new Intent(this, ChannelActivity.class);
      localIntent.putExtra("channel", this.channel);
      localIntent.addFlags(67108864);
      Notifications.show(5, localIntent);
      return;
      i = 0;
      break;
    }
    label110:
    Notifications.killNotification(5);
  }
  
  private void initUI()
  {
    this.dateButton = ((Button)findViewById(2131034153));
    this.dateButton.setText(new SimpleDateFormat("dd.MM.yyyy").format(this.day.getTime()));
    this.dateButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ChannelActivity.this.showDialog(1);
      }
    });
    this.timeButton = ((Button)findViewById(2131034154));
    this.timeButton.setText(this.day.get(11) + ":" + getRightMinutes(this.day));
    this.timeButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ChannelActivity.this.showDialog(2);
      }
    });
    this.playButton = ((ImageButton)findViewById(2131034155));
    this.playButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PlayerTask local1 = new PlayerTask(ChannelActivity.this)
        {
          public void onResult(Void paramAnonymous2Void)
          {
            ChannelActivity.this.inflatePlayStopButton();
          }
        };
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = ChannelActivity.this.channel;
        local1.execute(arrayOfObject);
      }
    });
    Channel localChannel = this.channel;
    App.getPlayer();
    int i;
    ImageButton localImageButton;
    if (localChannel.equals(PlayerService.channel))
    {
      App.getPlayer();
      if (PlayerService.state == PlayerService.State.Playing)
      {
        i = 1;
        localImageButton = this.playButton;
        if (i == 0) {
          break label262;
        }
      }
    }
    label262:
    for (int j = 2130837605;; j = 2130837600)
    {
      localImageButton.setImageResource(j);
      LinearLayout localLinearLayout = (LinearLayout)findViewById(2131034151);
      AdView localAdView = new AdView(this, AdSize.BANNER, "a15044929d0ad8b");
      localLinearLayout.addView(localAdView);
      localAdView.loadAd(new AdRequest());
      return;
      i = 0;
      break;
    }
  }
  
  private boolean isFavouriteChannel(Channel paramChannel)
  {
    return App.getDb().getChannel(paramChannel.getChannelId(), this.favouriteRadio) != null;
  }
  
  private void setAsphaltBackground(View paramView)
  {
    BitmapDrawable localBitmapDrawable = (BitmapDrawable)getResources().getDrawable(2130837589);
    localBitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    paramView.setBackgroundDrawable(localBitmapDrawable);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903061);
    this.channel = ((Channel)getIntent().getExtras().get("channel"));
    this.day = Calendar.getInstance(TimeZone.getTimeZone("GMT+4"));
    this.font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(true);
    getSupportActionBar().setNavigationMode(0);
    getSupportActionBar().setTitle(this.channel.getName());
    setAsphaltBackground(getListView());
    initUI();
    this.adapter = ((TrackAdapter)getLastNonConfigurationInstance());
    if (this.adapter == null)
    {
      GetTracksTask localGetTracksTask = new GetTracksTask(this);
      localGetTracksTask.setTracksLoadingListener(this);
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = this.day.getTime();
      arrayOfObject[1] = this.channel;
      localGetTracksTask.execute(arrayOfObject);
    }
    for (;;)
    {
      Settings.getPreferences().registerOnSharedPreferenceChangeListener(this);
      return;
      setListAdapter(this.adapter);
    }
  }
  
  protected Dialog onCreateDialog(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return super.onCreateDialog(paramInt);
    case 0: 
      return ProgressDialog.show(this, "", getResources().getString(2131361803), true);
    case 1: 
      return new DatePickerDialog(this, this.mDateSetListener, this.day.get(1), this.day.get(2), this.day.get(5));
    }
    return new TimePickerDialog(this, this.mTimeSetListener, this.day.get(11), this.day.get(12), true);
  }
  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    paramMenu.add(0, 1, 1, 2131361810).setIcon(2130837599).setShowAsAction(1);
    paramMenu.add(0, 2, 2, 2131361809).setIcon(2130837594).setShowAsAction(1);
    MenuItem localMenuItem = paramMenu.add(0, 3, 3, 2131361813);
    if (isFavouriteChannel(this.channel)) {}
    for (int i = 2130837601;; i = 2130837602)
    {
      localMenuItem.setIcon(i).setShowAsAction(1);
      paramMenu.add(0, 4, 4, 2131361811).setIcon(2130837593).setActionView(2130903060).setShowAsAction(9);
      paramMenu.add(0, 5, 5, 2131361812).setIcon(2130837596).setShowAsAction(1);
      return super.onCreateOptionsMenu(paramMenu);
    }
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
    default: 
      return true;
    case 16908332: 
      finish();
      return true;
    case 1: 
      GetTracksTask localGetTracksTask = new GetTracksTask(this);
      localGetTracksTask.setTracksLoadingListener(this);
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = this.day.getTime();
      arrayOfObject[1] = this.channel;
      arrayOfObject[2] = Boolean.valueOf(true);
      localGetTracksTask.execute(arrayOfObject);
      return true;
    case 2: 
      startActivity(new Intent(this, SettingsActivity.class));
      return true;
    case 3: 
      if (!isFavouriteChannel(this.channel))
      {
        App.getDb().addChannel(this.channel, this.favouriteRadio);
        paramMenuItem.setIcon(2130837601);
        return true;
      }
      App.getDb().deleteChannel(this.channel, this.favouriteRadio);
      paramMenuItem.setIcon(2130837602);
      return true;
    case 4: 
      this.search = ((EditText)paramMenuItem.getActionView());
      this.search.addTextChangedListener(this.filterTextWatcher);
      return true;
    }
    new AlertDialog.Builder(this).setTitle(2131361814).setPositiveButton(2131361815, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        App.getPlayer().stop();
        Notifications.killNotification(5);
        ChannelActivity.this.setResult(-1, null);
        ChannelActivity.this.finish();
      }
    }).setNegativeButton(2131361816, null).create().show();
    return true;
  }
  
  protected void onResume()
  {
    super.onResume();
    if (this.isSettingsChanged)
    {
      this.isSettingsChanged = false;
      finish();
      Intent localIntent = new Intent(this, ChannelActivity.class);
      localIntent.putExtra("channel", this.channel);
      startActivity(localIntent);
    }
  }
  
  public Object onRetainNonConfigurationInstance()
  {
    return this.adapter;
  }
  
  public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString)
  {
    if (paramString.equals("track_sort_key")) {
      this.isSettingsChanged = true;
    }
  }
  
  public void onTracksLoaded(List<Track> paramList)
  {
    if (paramList != null)
    {
      this.adapter = new TrackAdapter(this, 2130903067, paramList);
      setListAdapter(this.adapter);
      setSelection(paramList.size());
    }
  }
  
  private class TrackAdapter
    extends ArrayAdapter<Track>
  {
    private int maxRate;
    private int originalWidth = ChannelActivity.this.getWindowManager().getDefaultDisplay().getWidth();
    private Filter trackFilter;
    private List<Track> tracks;
    
    public TrackAdapter(int paramInt, List<Track> paramList)
    {
      super(paramList, localList);
      this.tracks = localList;
      this.maxRate = getMaxRate();
      this.trackFilter = new SearchFilter(new ArrayList(this.tracks), this);
      this.maxRate = getMaxRate();
    }
    
    private double calculateRatingStars(int paramInt1, int paramInt2)
    {
      if (paramInt2 == 0) {
        return 0.0D;
      }
      return paramInt1 / paramInt2;
    }
    
    private String composeItemText(Track paramTrack)
    {
      String str1 = "";
      if (paramTrack.getType() == 1) {
        str1 = str1 + paramTrack.getArtistName() + " - ";
      }
      String str2 = str1 + paramTrack.getTrackName() + " ";
      return str2 + " (" + RadioUtils.getDuration(paramTrack.getDuration()) + ")";
    }
    
    private int getMaxRate()
    {
      int i = 1;
      Iterator localIterator = this.tracks.iterator();
      while (localIterator.hasNext())
      {
        int j = Integer.parseInt(((Track)localIterator.next()).getPlayCount());
        if (j > i) {
          i = j;
        }
      }
      return i;
    }
    
    private int getMaxWidth(int paramInt)
    {
      return paramInt * 100 / this.maxRate * this.originalWidth / 100;
    }
    
    public Filter getFilter()
    {
      return this.trackFilter;
    }
    
    public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      ViewHolder localViewHolder;
      if (paramView == null)
      {
        paramView = ((LayoutInflater)ChannelActivity.this.getSystemService("layout_inflater")).inflate(2130903067, null);
        localViewHolder = new ViewHolder();
        localViewHolder.trackTime = ((TextView)paramView.findViewById(2131034160));
        localViewHolder.trackInfo = ((TextView)paramView.findViewById(2131034161));
        paramView.setTag(localViewHolder);
      }
      for (;;)
      {
        Track localTrack = (Track)this.tracks.get(paramInt);
        if (localTrack != null)
        {
          localViewHolder.trackInfo.setTypeface(ChannelActivity.this.font);
          localViewHolder.trackInfo.setText(composeItemText(localTrack));
          localViewHolder.trackTime.setTypeface(ChannelActivity.this.font);
          localViewHolder.trackTime.setText(localTrack.getTime().substring(11));
        }
        paramView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            ChannelActivity.access$802(ChannelActivity.this, (Track)ChannelActivity.this.getListAdapter().getItem(paramInt));
            PlayerTask local1 = new PlayerTask(ChannelActivity.this)
            {
              public void onResult(Void paramAnonymous2Void)
              {
                ChannelActivity.this.inflatePlayStopButton();
              }
            };
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = ChannelActivity.this.channel;
            arrayOfObject[1] = ChannelActivity.this.currentTrack;
            local1.execute(arrayOfObject);
          }
        });
        return paramView;
        localViewHolder = (ViewHolder)paramView.getTag();
      }
    }
    
    protected class ViewHolder
    {
      TextView rate;
      TextView trackInfo;
      TextView trackTime;
      
      protected ViewHolder() {}
    }
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.activities.ChannelActivity
 * JD-Core Version:    0.7.0.1
 */