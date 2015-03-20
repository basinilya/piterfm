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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.app.ActionBar;

import ru.piter.fm.App;
import ru.piter.fm.R;
import ru.piter.fm.player.PlayerService;
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
public class ChannelActivity extends SherlockListActivity implements GetTracksTask.TracksLoadingListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private TrackAdapter adapter;
    private Track currentTrack;
    private Channel channel;
    private Calendar day;
    private DateFormat FMT_TRACK_TIME = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", Locale.US);
    private DateFormat FMT_DATE_BUTTON = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    private EditText search;
    private Button dateButton;
    private Button timeButton;
    private ImageButton playButton;

    private LinearLayout buttons;
    private Typeface font;
    private Radio favouriteRadio = RadioFactory.getRadio(RadioFactory.FAVOURITE);
    private boolean isSettingsChanged = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel);


        channel = (Channel) getIntent().getExtras().get("channel");
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        day = Calendar.getInstance(tz);
        FMT_TRACK_TIME.setTimeZone(tz);
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
            GetTracksTask task = new GetTracksTask(this);
            task.setTracksLoadingListener(this);
            task.execute(day.getTime(), channel);
            //new GetTracksTask().execute(day.getTime());
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
        dateButton.setText((FMT_DATE_BUTTON).format(day.getTime()));
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATEPICKER_DIALOG);
            }
        });

        timeButton = (Button) findViewById(R.id.time_button);
        timeButton.setText(day.get(Calendar.HOUR_OF_DAY) + ":" + getRightMinutes(day));
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(TIMEPICKER_DIALOG);
            }
        });

        playButton = (ImageButton) findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PlayerTask(ChannelActivity.this) {
                    @Override
                    public void onResult(Void result) {
                        inflatePlayStopButton();
                    }
                }.execute(channel);
            }
        });
        // inflatePlayStopButton();
        boolean isPlaying = channel.equals(App.getPlayer().channel) && (App.getPlayer().state == PlayerService.State.Playing);
        playButton.setImageResource(isPlaying ? R.drawable.ic_stop : R.drawable.ic_play);

    }

    private void inflatePlayStopButton() {
        boolean isPlaying = channel.equals(App.getPlayer().channel) && (App.getPlayer().state == PlayerService.State.Playing);
        playButton.setImageResource(isPlaying ? R.drawable.ic_stop : R.drawable.ic_play);
        if (isPlaying) {
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra("channel", channel);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Notifications.show(Notifications.PLAY_STOP, intent);
        } else {
            Notifications.killNotification(Notifications.PLAY_STOP);
        }

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

    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (!dlgClicked)
                return;
            dlgClicked = false;
            day.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            day.set(Calendar.MONTH, monthOfYear);
            day.set(Calendar.YEAR, year);


            dateButton.setText((FMT_DATE_BUTTON).format(day.getTime()));
            if (!new Date().before(day.getTime())) {
                GetTracksTask task = new GetTracksTask(ChannelActivity.this);
                task.setTracksLoadingListener(ChannelActivity.this);
                task.execute(day.getTime(), channel);
            } else {
                Toast.makeText(ChannelActivity.this, R.string.incorrect_date, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (!dlgClicked)
                return;
            dlgClicked = false;
            //day.set(Calendar.HOUR_OF_DAY, hourOfDay);
            // day.set(Calendar.MINUTE, minute);
            // day.set(Calendar.SECOND, 0);
            day.set(day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
            timeButton.setText(day.get(Calendar.HOUR_OF_DAY) + ":" + getRightMinutes(day));
            Track ti = new Track();
            Date trackTime = day.getTime();
            ti.setTime(FMT_TRACK_TIME.format(trackTime));

            if (!new Date().before(trackTime)) {
                new PlayerTask(ChannelActivity.this) {
                    @Override
                    public void onResult(Void result) {
                        inflatePlayStopButton();
                    }
                }.execute(channel, ti);
            } else {
                Toast.makeText(ChannelActivity.this, R.string.incorrect_time, Toast.LENGTH_SHORT).show();
            }


        }
    };

    private String getRightMinutes(Calendar calendar) {
        int min = calendar.get(Calendar.MINUTE);
        if (min < 9) return "0" + min;
        return String.valueOf(min);
    }


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
                holder.trackTime.setText(track.getTime().substring(11));
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
                    new PlayerTask(ChannelActivity.this) {
                        @Override
                        public void onResult(Void result) {
                            inflatePlayStopButton();
                        }
                    }.execute(channel, currentTrack);
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


    private final int BUFFERING_DIALOG = 0;
    private final int DATEPICKER_DIALOG = 1;
    private final int TIMEPICKER_DIALOG = 2;


    @Override
    public void onTracksLoaded(List<Track> tracks) {
        if (tracks != null) {
            adapter = new TrackAdapter(ChannelActivity.this, R.layout.track_item, tracks);
            ChannelActivity.this.setListAdapter(adapter);
            ChannelActivity.this.setSelection((tracks.size()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (search != null)
            search.removeTextChangedListener(filterTextWatcher);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case BUFFERING_DIALOG:
                ProgressDialog dialog = ProgressDialog.show(this, "", getResources().getString(R.string.buffering), true);
                return dialog;
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
        menu.add(0, 1, 1, R.string.ac_refresh).setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 2, 2, R.string.ac_settings).setIcon(R.drawable.ic_action_settings).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 3, 3, R.string.ac_favourite).setIcon(isFavouriteChannel(channel) ? R.drawable.ic_rating_important : R.drawable.ic_rating_not_important).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 4, 4, R.string.ac_search).setIcon(R.drawable.ic_action_search).setActionView(R.layout.action_search)
                .setShowAsAction(SHOW_AS_ACTION_IF_ROOM | SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menu.add(0, 5, 5, R.string.ac_exit).setIcon(R.drawable.ic_cancel).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);

        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case 1:
                GetTracksTask task = new GetTracksTask(this);
                task.setTracksLoadingListener(this);
                task.execute(day.getTime(), channel, true);
                break;
            case 2:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case 3:
                if (!isFavouriteChannel(channel)) {
                    App.getDb().addChannel(channel, favouriteRadio);
                    item.setIcon(R.drawable.ic_rating_important);
                } else {
                    App.getDb().deleteChannel(channel, favouriteRadio);
                    item.setIcon(R.drawable.ic_rating_not_important);
                }
                break;
            case 4:
                search = (EditText) item.getActionView();
                search.addTextChangedListener(filterTextWatcher);
                break;
            case 5:
                final AlertDialog alert;
                AlertDialog.Builder builder = new AlertDialog.Builder(ChannelActivity.this)
                        .setTitle(R.string.request_exit)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                App.getPlayer().stop();
                                Notifications.killNotification(Notifications.PLAY_STOP);
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
    protected void onResume() {
        super.onResume();
        if (isSettingsChanged) {
            isSettingsChanged = false;
            finish();
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra("channel", channel);
            startActivity(intent);
        }
    }
}
