package ru.piter.fm.activities;

import android.app.*;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.*;
import android.widget.*;
import ru.piter.fm.R;
import ru.piter.fm.ServiceHolder;
import ru.piter.fm.player.PlayerService;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.IRadio;
import ru.piter.fm.radio.RadioFactory;
import ru.piter.fm.util.DBAdapter;
import ru.piter.fm.util.Notifications;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 24.08.2010
 * Time: 20:21:01
 * To change this template use File | Settings | File Templates.
 */
public class RadioActivity extends ListActivity {

    private static final String TAG = "RadioActivity:";
    private static final String ACTION_SELECT = "select";
    private static final String ACTION_UPDATE = "update";
    private IRadio radio;
    private ChannelAdapter adapter;
    private DBAdapter db = new DBAdapter(this);
    private TelephonyManager telephonyManager;
    private PhoneStateListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null && bundle.getString("radio") != null)
            radio = RadioFactory.getRadio(bundle.getString("radio"));
        else
            radio = RadioFactory.getRadio(RadioFactory.PITER_FM);

        initPlayer();
        new GetChannelsTask().execute(ACTION_SELECT);

        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            listener = new PhoneStateListener() {

                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    switch (state) {
                        case TelephonyManager.CALL_STATE_IDLE:

                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            if (getPlayer() != null )getPlayer().stop(false);   // stop player but don't kill notification
                            break;
                        case TelephonyManager.CALL_STATE_RINGING:
                            if (getPlayer() != null ) getPlayer().stop(false);   // stop player but don't kill notification
                            break;
                    }
                }
            };

            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void initPlayer() {
        if (!((ServiceHolder) getApplicationContext()).isBound()) {
            ((ServiceHolder) getApplicationContext()).startService();
        }
    }

    private PlayerService getPlayer() {
        if (((ServiceHolder) getApplicationContext()).isBound()) {
            return ((ServiceHolder) getApplicationContext()).getPlayerService();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            for (Channel ch : adapter.channels) {
                if (ch.getLogo() != null) ch.getLogo().recycle();
            }
            adapter = null;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                ProgressDialog dialog = ProgressDialog.show(this, "", getResources().getString(R.string.buffering), true);
                return dialog;

        }
        return super.onCreateDialog(id);
    }


      private void stop() {
        getPlayer().stop(true);
    }

    private void play(final Channel ch) {
        showDialog(0);
        new Thread() {
            public void run() {
                getPlayer().play(ch);
                dismissDialog(0);
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.radio_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quit:
                Notifications.killAppNotification(this, 4);
                ((ServiceHolder) getApplication()).stopService();
                super.onDestroy();
                this.finish();
                return true;
            case R.id.refresh:
                new GetChannelsTask().execute(ACTION_UPDATE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class GetChannelsTask extends AsyncTask {
        private ProgressDialog dialog = new ProgressDialog(RadioActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getResources().getString(R.string.loadChannels));
            this.dialog.show();
        }


        @Override
        protected List<Channel> doInBackground(Object... objects) {
            String action = (String) objects[0];

            List<Channel> channels = null;

            if (action.equals(ACTION_SELECT)) channels = db.selectAllChannels(radio.getRadioId());
            if (channels == null) {
                try {
                    channels = radio.getChannels();
                    if (channels != null) {
                        if (action.equals(ACTION_SELECT)) db.insertChannels(channels, radio.getRadioId());
                        if (action.equals(ACTION_UPDATE)) db.updateChannels(channels, radio.getRadioId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (this.dialog.isShowing()) this.dialog.dismiss();
                    Notifications.showErrorNotification(RadioActivity.this, getResources().getString(R.string.cantLoadChannels), 3);
                }
            }
            Collections.sort(channels);
            return channels;


        }

        @Override
        protected void onPostExecute(Object result) {
            if (this.dialog.isShowing()) this.dialog.dismiss();
            if (result != null) {
                adapter = new ChannelAdapter(RadioActivity.this, R.layout.station_item, (List<Channel>) result);
                RadioActivity.this.setListAdapter(adapter);
            }

        }


    }

    private class ChannelAdapter extends ArrayAdapter<Channel> {

        private List<Channel> channels;

        public ChannelAdapter(Context context, int textViewResourceId, List<Channel> objects) {
            super(context, textViewResourceId, objects);
            this.channels = objects;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.station_item, null);
            }
            final Channel ch = channels.get(position);
            if (ch != null) {

                TextView channelInfo = (TextView) v.findViewById(R.id.channel_info);
                channelInfo.setText(ch.getName() + " " + ch.getRange());

                ImageView channelIcon = (ImageView) v.findViewById(R.id.channelIcon);
                channelIcon.setAdjustViewBounds(true);
                channelIcon.setMaxHeight(30);
                channelIcon.setMaxWidth(30);
                channelIcon.setImageBitmap(ch.getLogo());


                ImageButton button = (ImageButton) v.findViewById(R.id.play_stop_button);
                button.setImageResource(R.drawable.play_buttons);

                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (getPlayer() != null) {
                            if (PlayerService.CURRENT_STATE == PlayerService.STOP) {
                                play(ch);
                            } else {
                                boolean flag = ch.equals(getPlayer().getCurrentChannel());
                                if (PlayerService.CURRENT_STATE == PlayerService.PLAY && !flag) {
                                    stop();
                                    play(ch);
                                } else {
                                    stop();
                                }
                            }
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "Player service is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                if (getPlayer().getCurrentChannel() != null && ch.equals(getPlayer().getCurrentChannel()))
                    button.setImageResource(R.drawable.stop_buttons);
            }
            return v;
        }


    }




}
