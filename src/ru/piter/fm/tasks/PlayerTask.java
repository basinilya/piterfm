package ru.piter.fm.tasks;

import android.content.Context;
import android.content.Intent;
import ru.piter.fm.App;
import ru.piter.fm.exception.NoInternetException;
import ru.piter.fm.exception.NoSDCardException;
import ru.piter.fm.player.PlayerService;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Track;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.Utils;

public class PlayerTask
  extends BaseTask<Void>
{
  private Channel channel;
  
  public PlayerTask(Context paramContext)
  {
    super(paramContext);
  }
  
  public Void doWork(Object... paramVarArgs)
    throws Exception
  {
    this.channel = ((Channel)paramVarArgs[0]);
    if (paramVarArgs.length > 1)
    {
      Track localTrack = (Track)paramVarArgs[1];
      App.getPlayer().play(this.channel, localTrack);
    }
    for (;;)
    {
      return null;
      App.getPlayer().play(this.channel);
    }
  }
  
  public void onError(Exception paramException)
  {
    paramException.printStackTrace();
    if ((paramException instanceof NoInternetException)) {
      Notifications.show(4, new Intent());
    }
    if ((paramException instanceof NoSDCardException)) {
      Notifications.show(1, new Intent());
    }
  }
  
  protected void onPreExecute()
  {
    if (!Utils.isSdAvailable())
    {
      cancel(true);
      onError(new NoSDCardException("No SD card available"));
      return;
    }
    super.onPreExecute();
  }
  
  public void onResult(Void paramVoid) {}
}


/* Location:
 * Qualified Name:     ru.piter.fm.tasks.PlayerTask
 * JD-Core Version:    0.7.0.1
 */