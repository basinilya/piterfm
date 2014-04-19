package ru.piter.fm.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import java.io.Serializable;
import ru.piter.fm.App;
import ru.piter.fm.player.PlayerService;
import ru.piter.fm.player.PlayerService.State;
import ru.piter.fm.radio.Channel;

public class Notifications
{
  public static final int CANT_LOAD_CHANNELS = 2;
  public static final int CANT_LOAD_TRACK = 4;
  public static final int CANT_LOAD_TRACKS = 3;
  public static final int PLAY_STOP = 5;
  public static final int SD_CARD_UNAVAILABLE = 1;
  private static Context context = ;
  private static NotificationManager nm = (NotificationManager)context.getSystemService("notification");
  
  public static void killNotification(int paramInt)
  {
    nm.cancel(paramInt);
  }
  
  public static void show(int paramInt, Intent paramIntent)
  {
    String str = "";
    Notification localNotification = new Notification(2130837608, "", System.currentTimeMillis());
    switch (paramInt)
    {
    default: 
      PendingIntent localPendingIntent = PendingIntent.getActivity(context, 0, paramIntent, 134217728);
      localNotification.tickerText = str;
      localNotification.contentIntent = localPendingIntent;
      localNotification.flags = 16;
      localNotification.setLatestEventInfo(context, "PITER FM", str, localPendingIntent);
      if (paramInt != 5) {
        localNotification.vibrate = new long[] { 0L, 300L };
      }
      break;
    }
    for (;;)
    {
      if (Settings.isNotificationsSoundEnabled()) {
        localNotification.defaults = (0x1 | localNotification.defaults);
      }
      if (Settings.isNotificationsEnabled()) {
        nm.notify(paramInt, localNotification);
      }
      return;
      str = context.getResources().getString(2131361805);
      break;
      str = context.getResources().getString(2131361806);
      break;
      str = context.getResources().getString(2131361807);
      break;
      str = context.getResources().getString(2131361808);
      break;
      Serializable localSerializable = paramIntent.getExtras().getSerializable("channel");
      if (localSerializable == null) {
        break;
      }
      str = ((Channel)localSerializable).getName();
      break;
      App.getPlayer();
      if (PlayerService.state == PlayerService.State.Playing) {
        localNotification.flags = 32;
      }
    }
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.util.Notifications
 * JD-Core Version:    0.7.0.1
 */