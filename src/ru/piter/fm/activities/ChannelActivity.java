package ru.piter.fm.activities;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import static android.support.v4.view.MenuItemCompat.*;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.app.ActionBar;

import ru.piter.fm.App;
import ru.piter.fm.player.PlayerInterface;
import ru.piter.fm.player.PlayerInterface.EventType;
import ru.piter.fm.prototype.R;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;
import ru.piter.fm.radio.RadioFactory;
import ru.piter.fm.radio.Track;
import ru.piter.fm.tasks.GetTracksTask;
import ru.piter.fm.tasks.PlayerTask;
import ru.piter.fm
        .util.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 10.11.2010
 * Time: 22:31:59
 * To change this template use File | Settings | File Templates.
 */
public class ChannelActivity extends SherlockListActivity implements
    GetTracksTask.TracksLoadingListener,
    PlayerInterface.EventHandler,
    SharedPreferences.OnSharedPreferenceChangeListener
{

    private static final String Tag = "ChannelActivity";

    private TrackAdapter adapter;
    private boolean needLoadTracks;
    private Track currentTrack;
    private Channel channel;
    private TrackCalendar day;
    private TrackCalendar tmpCal = new TrackCalendar();
    private DateFormat FMT_DATE_BUTTON = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    private EditText search;
    private Button dateButton;
    private Button timeButton;
    private ImageButton playButton;
    private boolean isGreen;

    private LinearLayout buttons;
    private Typeface font;
    private Radio favouriteRadio = RadioFactory.getRadio(RadioFactory.FAVOURITE);
    private boolean isSettingsChanged = false;

    private PlayerInterface player;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel);

        player = App.getPlayer();

        channel = (Channel) getIntent().getExtras().get("channel");
        TimeZone tz = TrackCalendar.getTimezone();
        FMT_DATE_BUTTON.setTimeZone(tz);
        font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getSupportActionBar().setTitle(channel.getName());

        setAsphaltBackground(getListView());


        initUI();
        adapter = (TrackAdapter) getLastNonConfigurationInstance();
        if (adapter == null) {
            needLoadTracks = true;
        } else {
            this.setListAdapter(adapter);
        }
        Settings.getPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(Settings.TRACK_SORT_TYPE))
            isSettingsChanged = true;
    }

    private void setAsphaltBackground(View view) {
        BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(R.drawable.asphalt);
        bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        view.setBackgroundDrawable(bg);
    }


    @Override
    public Object onRetainNonConfigurationInstance() {
        return adapter;
    }

    private void initUI() {

        dateButton = (Button) findViewById(R.id.date_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATEPICKER_DIALOG);
            }
        });

        timeButton = (Button) findViewById(R.id.time_button);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View view) {
                showDialog(TIMEPICKER_DIALOG);
            }
        });

        playButton = (ImageButton) findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPlayerTask().execute(channel);
            }
        });
    }

    private void togglePlayButton() {
        playButton.setImageResource(isGreen ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private PlayerTask newPlayerTask() {
        return new PlayerTask(this) {
            @Override
            protected Intent getPlayingNotificationIntent() {
                Intent intent = new Intent(ChannelActivity.this, ChannelActivity.class);
                intent.putExtra("channel", channel);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                return intent;
            }
        };
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (adapter != null)
                adapter.getFilter().filter(s);
        }
    };


    private boolean dlgClicked; // workaround for event raised twice, see https://code.google.com/p/android/issues/detail?id=34833

    private long lockCalUntil;

    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (!dlgClicked)
                return;
            dlgClicked = false;
            day.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            day.set(Calendar.MONTH, monthOfYear);
            day.set(Calendar.YEAR, year);

            lockCalUntil = System.nanoTime() + (30 * 1000 * 1000000L);
            updateDateButton();
            if (!isFuture()) {
                GetTracksTask task = new GetTracksTask(ChannelActivity.this);
                task.setTracksLoadingListener(ChannelActivity.this);
                task.execute(day.asTracksUrlPart(), channel);
            } else {
                Toast.makeText(ChannelActivity.this, R.string.incorrect_date, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private boolean isFuture() {
        return System.currentTimeMillis() < day.getClientTimeInMillis();
    }

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (!dlgClicked)
                return;
            dlgClicked = false;
            day.set(Calendar.HOUR_OF_DAY, hourOfDay);
            day.set(Calendar.MINUTE, minute);
            day.set(Calendar.SECOND, 0);
            updateTimeButton();

            if (!isFuture()) {
                newPlayerTask().execute(channel, day);
            } else {
                Toast.makeText(ChannelActivity.this, R.string.incorrect_time, Toast.LENGTH_SHORT).show();
            }


        }
    };

    private class TrackAdapter extends ArrayAdapter<Track> {

        private List<Track> tracks;
        private int maxRate;
        private Filter trackFilter;
        private int originalWidth = getWindowManager().getDefaultDisplay().getWidth();

        public TrackAdapter(Context context, int textViewResourceId, List<Track> objects) {
            super(context, textViewResourceId, objects);
            this.tracks = objects;
            this.maxRate = getMaxRate();
            trackFilter = new SearchFilter(new ArrayList<SearchFilter.Filterable>(tracks), this);
            maxRate = getMaxRate();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.track_item, null);
                holder = new ViewHolder();
                holder.trackTime = (TextView) convertView.findViewById(R.id.track_time);
                holder.trackInfo = (TextView) convertView.findViewById(R.id.track);
                //holder.rate = (TextView) convertView.findViewById(R.id.rate);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final int pos = position;
            final Track track = tracks.get(position);
            if (track != null) {


                holder.trackInfo.setTypeface(font);
                holder.trackInfo.setText(composeItemText(track));

                holder.trackTime.setTypeface(font);
                tmpCal.setClientTimeInMillis(track.getClientTimeInMillis());
                holder.trackTime.setText(tmpCal.asHMM());
                //holder.trackTime.setText("" + calculateRatingStars(maxRate, Integer.parseInt(track.getPlayCount())));
//                if (track.getType() == Track.TYPE_SHOW) {
//                     holder.image.setImageResource(R.drawable.ic_mic);
//                }
//                holder.rate.setText(track.getPlayCount());
//                holder.rate.setWidth(getMaxWidth(Integer.parseInt(track.getPlayCount())));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentTrack = (Track) getListAdapter().getItem(pos);
                    tmpCal.setClientTimeInMillis(currentTrack.getClientTimeInMillis());
                    newPlayerTask().execute(channel, tmpCal);
                }

            });
            return convertView;
        }


        private double calculateRatingStars(int maxRate, int rate) {


            if (rate == 0) return 0;
            double t = maxRate / rate;
            return t;

        }

        private int getMaxRate() {
            int max = 1;
            for (Track t : tracks) {
                int playCount = Integer.parseInt(t.getPlayCount());
                if (playCount > max) max = playCount;
            }
            return max;
        }

        private String composeItemText(Track track) {
            //String s = track.getTime().substring(11) + " ";
            String s = "";
            if (track.getType() == Track.TYPE_TRACK)
                s += track.getArtistName() + " - ";
            s += track.getTrackName() + " ";
            s += " (" + RadioUtils.getDuration(track.getDuration()) + ")";
            return s;
        }

        private int getMaxWidth(int rate) {
            int percent = 100 * rate / maxRate;
            int newWidth = originalWidth * percent / 100;
            return newWidth;
        }


        @Override
        public Filter getFilter() {
            return trackFilter;
        }

        protected class ViewHolder {
            TextView trackTime;
            TextView trackInfo;
            TextView rate;
        }

    }


    private final int DATEPICKER_DIALOG = 1;
    private final int TIMEPICKER_DIALOG = 2;


    @Override
    public void onTracksLoaded(List<Track> tracks) {
        if (tracks != null) {
            adapter = new TrackAdapter(ChannelActivity.this, R.layout.track_item, tracks);
            ChannelActivity.this.setListAdapter(adapter);
            ChannelActivity.this.setSelection((tracks.size()));
        }
        // TODO: scroll to
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (search != null)
            search.removeTextChangedListener(filterTextWatcher);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        initDay();
        switch (id) {
            case DATEPICKER_DIALOG:
                DatePickerDialog ddlg = (DatePickerDialog)dialog;
                ddlg.updateDate(day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH));
                return;
            case TIMEPICKER_DIALOG:
                TimePickerDialog tdlg = (TimePickerDialog)dialog;
                tdlg.updateTime(day.get(Calendar.HOUR_OF_DAY), day.get(Calendar.MINUTE));
                return;
        }
        super.onPrepareDialog(id, dialog);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        initDay();
        switch (id) {
            case DATEPICKER_DIALOG:
                return new DatePickerDialog(ChannelActivity.this, mDateSetListener, day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH)) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dlgClicked = true;
                        super.onClick(dialog, which);
                    }
                };
            case TIMEPICKER_DIALOG:
                return new TimePickerDialog(ChannelActivity.this, mTimeSetListener, day.get(Calendar.HOUR_OF_DAY), day.get(Calendar.MINUTE), true) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dlgClicked = true;
                        super.onClick(dialog, which);
                    }
                };
        }
        return super.onCreateDialog(id);
    }


    private boolean isFavouriteChannel(Channel channel) {
        return App.getDb().getChannel(channel.getChannelId(), favouriteRadio) != null;
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        menu.add(0, 100, 100, R.string.ac_refresh).setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 200, 200, R.string.ac_settings).setIcon(R.drawable.ic_action_settings).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 300, 300, R.string.ac_favourite).setIcon(isFavouriteChannel(channel) ? R.drawable.ic_rating_important : R.drawable.ic_rating_not_important).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 400, 400, R.string.ac_search).setIcon(R.drawable.ic_action_search).setActionView(R.layout.action_search)
                .setShowAsAction(SHOW_AS_ACTION_IF_ROOM | SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menu.add(0, 500, 500, R.string.ac_exit).setIcon(R.drawable.ic_cancel).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);

        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case 100:
                GetTracksTask task = new GetTracksTask(this);
                task.setTracksLoadingListener(this);
                task.execute(day.asTracksUrlPart(), channel, true);
                break;
            case 200:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case 300:
                if (!isFavouriteChannel(channel)) {
                    App.getDb().addChannel(channel, favouriteRadio);
                    item.setIcon(R.drawable.ic_rating_important);
                } else {
                    App.getDb().deleteChannel(channel, favouriteRadio);
                    item.setIcon(R.drawable.ic_rating_not_important);
                }
                break;
            case 400:
                search = (EditText) item.getActionView();
                search.addTextChangedListener(filterTextWatcher);
                break;
            case 500:
                final AlertDialog alert;
                AlertDialog.Builder builder = new AlertDialog.Builder(ChannelActivity.this)
                        .setTitle(R.string.request_exit)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                player.pause();
                                //finish();
                                //ChannelActivity.this.moveTaskToBack(true);
                                setResult(RESULT_OK, null);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                alert = builder.create();
                alert.show();
        }
        return true;
    }

    @Override
    protected void onPause() {
        final String funcname = "onPause";
        Log.d(Tag, funcname + ",");

        handler.removeCallbacks(autoUpdateTask);
        player.removeEventHandler(this);

        super.onPause();
    }

    @Override
    protected void onResume() {
        final String funcname = "onResume";
        Log.d(Tag, funcname + ",");

        super.onResume();

        isGreen = App.isPlaying(channel);
        togglePlayButton();

        player.addEventHandler(this);

        if (isSettingsChanged) {
            isSettingsChanged = false;
            finish();
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra("channel", channel);
            startActivity(intent);
            return;
        }

        showPlayerPos();

        if (needLoadTracks) {
            needLoadTracks = false;
            GetTracksTask task = new GetTracksTask(this);
            task.setTracksLoadingListener(this);
            task.execute(day.asTracksUrlPart(), channel);
        }
    }

    private final Runnable autoUpdateTask = new TimerTask() {
        @Override
        public void run() {
            final String funcname = "autoUpdateTask.run";
            Log.d(Tag, funcname + ",");
            long remainNanos = lockCalUntil - System.nanoTime();
            if (remainNanos <= 0) {
                showPlayerPos();
            } else {
                long remain = remainNanos / 1000000L;
                Log.d(Tag, funcname + ",scheduling with remain = " + remain);
                handler.postDelayed(autoUpdateTask, remain);
            }
        }
    };

    private void getDay() {
        day = player.getPosition();
        if (day == null) {
            day = RadioUtils.getCurrentTrackTime(channel.getChannelId());
        }
    }

    private void initDay() {
        if (day == null)
            getDay();
    }
    
    private void showPlayerPos() {
        final String funcname = "showPlayerPos";

        getDay();

        Log.d(Tag, funcname + ",day = " + day);

        handler.removeCallbacks(autoUpdateTask);

        updateDateButton();
        updateTimeButton();

        if (App.isPlaying(channel)) {
            long remain = RadioUtils.TIME_MINUTE - (day.get(Calendar.SECOND) * 1000 + day.get(Calendar.MILLISECOND));
            Log.d(Tag, funcname + ",scheduling with remain = " + remain);

            handler.postDelayed(autoUpdateTask, remain);
        }
    }

    private final Handler handler = new Handler();

    @Override
    public void onEvent(EventType ev) {
        if (ev == EventType.NotBuffering) {
            if (System.nanoTime() - lockCalUntil > 0) {
                showPlayerPos();
            }
        }
        if (isGreen != App.isPlaying(channel)) {
            isGreen = !isGreen;
            togglePlayButton();
        }
    }
    
    private void updateDateButton() {
        dateButton.setText((FMT_DATE_BUTTON).format(day.getTime()));
    }

    private void updateTimeButton() {
        timeButton.setText(day.asHMM());
    }
}
