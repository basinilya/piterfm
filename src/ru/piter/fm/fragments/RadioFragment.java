package ru.piter.fm.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.*;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.piter.fm.App;
import ru.piter.fm.activities.ChannelActivity;
import ru.piter.fm.activities.RadioActivity;
import ru.piter.fm.player.PlayerInterface;
import ru.piter.fm.player.PlayerInterface.EventType;
import ru.piter.fm.prototype.R;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;
import ru.piter.fm.radio.RadioFactory;
import ru.piter.fm.tasks.GetChannelsTask;
import ru.piter.fm.tasks.PlayerTask;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.SearchFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 15.03.12
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
public class RadioFragment extends ListFragment implements
        PlayerInterface.EventHandler,
        GetChannelsTask.ChannelsLoadingListener
        {

    private Radio radio;
    private LayoutInflater inflater;
    private ChannelAdapter adapter;
    private Typeface font;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private String greenChannel1;

    public RadioFragment() {
    }

    public RadioFragment(Radio radio) {
        this.radio = radio;
    }

    public void updateChannels(boolean redownload) {
        GetChannelsTask task = new GetChannelsTask(getActivity());
        task.setChannelsLoadingListener(this);
        task.execute(radio, redownload);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ChannelAdapter(getActivity(), R.layout.channel_item, new ArrayList<Channel>());
        setListAdapter(adapter);
        if (savedInstanceState != null) {
            radio = (Radio) savedInstanceState.getSerializable("radio");
        }
        font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        updateChannels(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        ListView list = (ListView) inflater.inflate(R.layout.radio, container, false);
        BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(R.drawable.asphalt);
        bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        list.setBackgroundDrawable(bg);
        return list;
    }


    @Override
    public void onChannelsLoaded(List<Channel> channels) {
        if (channels == null)
            return;
        adapter.setNotifyOnChange(false);
        adapter.clear();
        for (int i = 0; i < channels.size(); i++) {
            adapter.add(channels.get(i));
        }
        adapter.setNotifyOnChange(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    public ChannelAdapter getListAdapter() {
        return adapter;
    }


    private class ChannelAdapter extends ArrayAdapter<Channel> {

        private Filter channelFilter;

        public ChannelAdapter(Context context, int textViewResourceId, List<Channel> objects) {
            super(context, textViewResourceId, objects);
            this.channelFilter = new SearchFilter(new ArrayList<SearchFilter.Filterable>(objects), this);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.channel_item, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.channelIcon);
                holder.channelInfo = (TextView) convertView.findViewById(R.id.channel_info);
                holder.button = (ImageButton) convertView.findViewById(R.id.play_stop_button);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Channel ch = getItem(position);

            if (ch != null) {
                boolean isPlaying = App.isPlaying(ch);
                if (isPlaying)
                    greenChannel1 = ch.getChannelId();
                holder.channelInfo.setTypeface(font);
                holder.channelInfo.setText(ch.getName() + " " + ch.getRange());
                imageLoader.displayImage(ch.getLogoUrl(), holder.image);
                holder.button.setImageResource(isPlaying ? R.drawable.play_on : R.drawable.play);
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new PlayerTask(getActivity()) {
                            @Override
                            public Intent getPlayingNotificationIntent() {
                                Intent intent = new Intent(getActivity(), RadioActivity.class);
                                intent.putExtra("radio", radio);
                                intent.putExtra("channel", ch);
                                return intent;
                            }
                        }.execute(ch);

                    }
                });


            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ChannelActivity.class);
                    intent.putExtra("channel", ch);
                    getActivity().startActivityForResult(intent, RadioActivity.REQUEST_EXIT);
                }
            });

            return convertView;
        }


        @Override
        public Filter getFilter() {
            return channelFilter;
        }

        protected class ViewHolder {
            ImageView image;
            TextView channelInfo;
            ImageButton button;
        }


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);    //To change body of overridden methods use File | Settings | File Templates.
        outState.putSerializable("radio", radio );
    }


    @Override
    public void onEvent(EventType _unused) {
        PlayerInterface player = App.getPlayer();

        if (player.isPaused()) {
            if (greenChannel1 == null) {
                return;
            }
        } else {
            if (player.getChannelId().equals(greenChannel1))
                return;
        }
        greenChannel1 = null;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        App.getPlayer().removeEventHandler(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (radio != null && radio.getName().equals(RadioFactory.FAVOURITE)) {
            updateChannels(false);
        }else{
        }
        App.getPlayer().addEventHandler(this);
        greenChannel1 = null;
        adapter.notifyDataSetChanged();
    }
}
