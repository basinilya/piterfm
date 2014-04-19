package ru.piter.fm.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.List;
import ru.piter.fm.App;
import ru.piter.fm.activities.ChannelActivity;
import ru.piter.fm.activities.RadioActivity;
import ru.piter.fm.player.PlayerService;
import ru.piter.fm.player.PlayerService.State;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;
import ru.piter.fm.tasks.GetChannelsTask;
import ru.piter.fm.tasks.GetChannelsTask.ChannelsLoadingListener;
import ru.piter.fm.tasks.PlayerTask;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.SearchFilter;

public class RadioFragment
  extends ListFragment
  implements GetChannelsTask.ChannelsLoadingListener
{
  private ChannelAdapter adapter;
  private Typeface font;
  private ImageLoader imageLoader = ImageLoader.getInstance();
  private LayoutInflater inflater;
  private PlayerService player = App.getPlayer();
  private Radio radio;
  
  public RadioFragment() {}
  
  public RadioFragment(Radio paramRadio)
  {
    this.radio = paramRadio;
  }
  
  public ChannelAdapter getAdapter()
  {
    return this.adapter;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (paramBundle != null) {
      this.radio = ((Radio)paramBundle.getSerializable("radio"));
    }
  }
  
  public void onChannelsLoaded(List<Channel> paramList)
  {
    if (paramList == null) {
      return;
    }
    this.adapter = new ChannelAdapter(getActivity(), 2130903062, paramList);
    setListAdapter(this.adapter);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
    GetChannelsTask localGetChannelsTask = new GetChannelsTask(getActivity());
    localGetChannelsTask.setChannelsLoadingListener(this);
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = this.radio;
    localGetChannelsTask.execute(arrayOfObject);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.inflater = paramLayoutInflater;
    ListView localListView = (ListView)paramLayoutInflater.inflate(2130903064, paramViewGroup, false);
    BitmapDrawable localBitmapDrawable = (BitmapDrawable)getResources().getDrawable(2130837589);
    localBitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    localListView.setBackgroundDrawable(localBitmapDrawable);
    return localListView;
  }
  
  public void onResume()
  {
    super.onResume();
    if ((this.radio != null) && (this.radio.getName().equals("Favourite")))
    {
      GetChannelsTask localGetChannelsTask = new GetChannelsTask(getActivity());
      localGetChannelsTask.setChannelsLoadingListener(this);
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = this.radio;
      localGetChannelsTask.execute(arrayOfObject);
    }
    if (this.adapter != null) {
      this.adapter.notifyDataSetChanged();
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putSerializable("radio", this.radio);
  }
  
  public void updateChannels()
  {
    GetChannelsTask localGetChannelsTask = new GetChannelsTask(getActivity());
    localGetChannelsTask.setChannelsLoadingListener(this);
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = this.radio;
    arrayOfObject[1] = Boolean.valueOf(true);
    localGetChannelsTask.execute(arrayOfObject);
  }
  
  private class ChannelAdapter
    extends ArrayAdapter<Channel>
  {
    private Filter channelFilter;
    private List<Channel> channels;
    
    public ChannelAdapter(int paramInt, List<Channel> paramList)
    {
      super(paramList, localList);
      this.channels = localList;
      this.channelFilter = new SearchFilter(new ArrayList(this.channels), this);
    }
    
    public Filter getFilter()
    {
      return this.channelFilter;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      final ViewHolder localViewHolder;
      final Channel localChannel;
      int i;
      label131:
      ImageButton localImageButton;
      if (paramView == null)
      {
        paramView = RadioFragment.this.inflater.inflate(2130903062, null);
        localViewHolder = new ViewHolder();
        localViewHolder.image = ((ImageView)paramView.findViewById(2131034156));
        localViewHolder.channelInfo = ((TextView)paramView.findViewById(2131034157));
        localViewHolder.button = ((ImageButton)paramView.findViewById(2131034158));
        paramView.setTag(localViewHolder);
        localChannel = (Channel)this.channels.get(paramInt);
        if (localChannel != null)
        {
          if (!localChannel.equals(PlayerService.channel)) {
            break label276;
          }
          App.getPlayer();
          if (PlayerService.state != PlayerService.State.Playing) {
            break label276;
          }
          i = 1;
          localViewHolder.channelInfo.setTypeface(RadioFragment.this.font);
          localViewHolder.channelInfo.setText(localChannel.getName() + " " + localChannel.getRange());
          RadioFragment.this.imageLoader.displayImage(localChannel.getLogoUrl(), localViewHolder.image);
          localImageButton = localViewHolder.button;
          if (i == 0) {
            break label282;
          }
        }
      }
      label276:
      label282:
      for (int j = 2130837610;; j = 2130837609)
      {
        localImageButton.setImageResource(j);
        localViewHolder.button.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            PlayerTask local1 = new PlayerTask(RadioFragment.this.getActivity())
            {
              public void onResult(Void paramAnonymous2Void)
              {
                Channel localChannel = RadioFragment.ChannelAdapter.1.this.val$ch;
                int i;
                int j;
                if (localChannel.equals(PlayerService.channel))
                {
                  App.getPlayer();
                  if (PlayerService.state == PlayerService.State.Playing)
                  {
                    i = 1;
                    ImageButton localImageButton = RadioFragment.ChannelAdapter.1.this.val$holder.button;
                    if (i == 0) {
                      break label164;
                    }
                    j = 2130837610;
                    label69:
                    localImageButton.setImageResource(j);
                    if (i == 0) {
                      break label171;
                    }
                    Intent localIntent = new Intent(RadioFragment.this.getActivity(), RadioActivity.class);
                    localIntent.putExtra("radio", RadioFragment.this.radio);
                    localIntent.putExtra("channel", RadioFragment.ChannelAdapter.1.this.val$ch);
                    Notifications.show(5, localIntent);
                  }
                }
                for (;;)
                {
                  RadioFragment.ChannelAdapter.this.notifyDataSetChanged();
                  return;
                  i = 0;
                  break;
                  label164:
                  j = 2130837609;
                  break label69;
                  label171:
                  Notifications.killNotification(5);
                }
              }
            };
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = localChannel;
            local1.execute(arrayOfObject);
          }
        });
        paramView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            Intent localIntent = new Intent(RadioFragment.this.getActivity(), ChannelActivity.class);
            localIntent.putExtra("channel", localChannel);
            RadioFragment.this.getActivity().startActivityForResult(localIntent, 5);
          }
        });
        return paramView;
        localViewHolder = (ViewHolder)paramView.getTag();
        break;
        i = 0;
        break label131;
      }
    }
    
    protected class ViewHolder
    {
      ImageButton button;
      TextView channelInfo;
      ImageView image;
      
      protected ViewHolder() {}
    }
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.fragments.RadioFragment
 * JD-Core Version:    0.7.0.1
 */