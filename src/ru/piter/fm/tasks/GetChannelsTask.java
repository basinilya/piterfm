package ru.piter.fm.tasks;

import android.content.Context;
import android.content.Intent;
import java.util.Collections;
import java.util.List;
import ru.piter.fm.App;
import ru.piter.fm.exception.NoInternetException;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;
import ru.piter.fm.util.ChannelComparator;
import ru.piter.fm.util.DBAdapter;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.RadioUtils;
import ru.piter.fm.util.Settings;

public class GetChannelsTask
  extends BaseTask<List<Channel>>
{
  private ChannelsLoadingListener channelsLoadingListener;
  
  public GetChannelsTask(Context paramContext)
  {
    super(paramContext);
  }
  
  public List<Channel> doWork(Object... paramVarArgs)
    throws Exception
  {
    Radio localRadio = (Radio)paramVarArgs[0];
    DBAdapter localDBAdapter = App.getDb();
    if (localRadio.getName().equals("Favourite")) {}
    for (List localList = localDBAdapter.selectAllChannels(localRadio);; localList = RadioUtils.getRadioChannels(localRadio, this.context))
    {
      if (localList != null) {
        Collections.sort(localList, new ChannelComparator(Settings.getChannelSort()));
      }
      return localList;
    }
  }
  
  public void onError(Exception paramException)
  {
    paramException.printStackTrace();
    if ((paramException instanceof NoInternetException)) {
      Notifications.show(2, new Intent());
    }
  }
  
  public void onResult(List<Channel> paramList)
  {
    Notifications.killNotification(2);
    this.channelsLoadingListener.onChannelsLoaded(paramList);
  }
  
  public void setChannelsLoadingListener(ChannelsLoadingListener paramChannelsLoadingListener)
  {
    this.channelsLoadingListener = paramChannelsLoadingListener;
  }
  
  public static abstract interface ChannelsLoadingListener
  {
    public abstract void onChannelsLoaded(List<Channel> paramList);
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.tasks.GetChannelsTask
 * JD-Core Version:    0.7.0.1
 */