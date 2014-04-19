package ru.piter.fm.tasks;

import android.content.Context;
import android.content.Intent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import ru.piter.fm.exception.NoInternetException;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Track;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.RadioUtils;
import ru.piter.fm.util.Settings;
import ru.piter.fm.util.TrackComparator;
import ru.piter.fm.util.TracksCache;

public class GetTracksTask
  extends BaseTask<List<Track>>
{
  private DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
  public TracksLoadingListener tracksLoadingListener;
  
  public GetTracksTask(Context paramContext)
  {
    super(paramContext);
  }
  
  public List<Track> doWork(Object... paramVarArgs)
    throws Exception
  {
    Object localObject;
    if (!this.isOnline)
    {
      cancel(true);
      onError(new NoInternetException("No Internet Available"));
      localObject = null;
    }
    Date localDate;
    Channel localChannel;
    do
    {
      do
      {
        return localObject;
        localDate = (Date)paramVarArgs[0];
        localChannel = (Channel)paramVarArgs[1];
        if (paramVarArgs.length == 3) {
          break;
        }
        localObject = TracksCache.get(localChannel.getChannelId(), this.df.format(localDate));
      } while (localObject != null);
      localObject = RadioUtils.getTracks(RadioUtils.getTracksUrl(localDate, localChannel));
    } while (localObject == null);
    TracksCache.put(localChannel.getChannelId(), this.df.format(localDate), (List)localObject);
    Collections.sort((List)localObject, new TrackComparator(Settings.getTrackSort()));
    return localObject;
  }
  
  public void onError(Exception paramException)
  {
    paramException.printStackTrace();
    if ((paramException instanceof NoInternetException)) {
      Notifications.show(3, new Intent());
    }
  }
  
  public void onResult(List<Track> paramList)
  {
    Notifications.killNotification(3);
    this.tracksLoadingListener.onTracksLoaded(paramList);
  }
  
  public void setTracksLoadingListener(TracksLoadingListener paramTracksLoadingListener)
  {
    this.tracksLoadingListener = paramTracksLoadingListener;
  }
  
  public static abstract interface TracksLoadingListener
  {
    public abstract void onTracksLoaded(List<Track> paramList);
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.tasks.GetTracksTask
 * JD-Core Version:    0.7.0.1
 */