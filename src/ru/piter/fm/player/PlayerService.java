package ru.piter.fm.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Track;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.RadioUtils;
import ru.piter.fm.util.Settings;
import ru.piter.fm.util.Utils;

public class PlayerService
  extends Service
  implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener
{
  private static final int TIME_TO_SEEK = 2000;
  private static final int TRACK_MIN_LENGTH_BYTES = 10000;
  public static Channel channel;
  private static String nextTrack;
  private static MediaPlayer player1;
  private static MediaPlayer player2;
  private static MediaPlayer prepared;
  public static int reconnectCount = 0;
  public static State state = State.Stopped;
  private static String track;
  private final IBinder mBinder = new PlayerServiceListener();
  
  private void deleteTrack(final String paramString)
  {
    new Thread()
    {
      public void run()
      {
        Log.d("PiterFM: ", "Delete track " + paramString);
        Utils.deletePreviousTrack(paramString);
      }
    }.start();
  }
  
  private MediaPlayer getPlayer()
  {
    if (player1.isPlaying()) {
      return player2;
    }
    return player1;
  }
  
  private String getStackTrace(Exception paramException)
  {
    StackTraceElement[] arrayOfStackTraceElement = paramException.getStackTrace();
    String str = paramException.toString() + "\n\n";
    for (int i = 0; i < arrayOfStackTraceElement.length; i++) {
      str = str + arrayOfStackTraceElement[i].toString() + "\n";
    }
    return str;
  }
  
  private void play(String paramString)
  {
    play(paramString, 2000);
    Log.d("PiterFM: ", "play track " + paramString);
  }
  
  private void play(final String paramString, final int paramInt)
  {
    track = paramString;
    String str = Utils.CHUNKS_DIR + "/" + RadioUtils.getTrackNameFromUrl(track);
    if (!new File(str).exists()) {}
    try
    {
      Utils.downloadTrack(track);
      if (new File(str).length() < 10000L)
      {
        Log.d("PiterFM", "track not exists " + str);
        return;
      }
      preparePlayer(track);
      reconnectCount = 0;
      Notifications.killNotification(4);
      if (prepared == null) {
        break label369;
      }
      localMediaPlayer = prepared;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        DownloadTrackTask localDownloadTrackTask;
        Object[] arrayOfObject;
        state = State.Stopped;
        localException.printStackTrace();
        Log.d("PiterFM", localException.getMessage() + "\n" + getStackTrace(localException));
        if (reconnectCount == 0) {
          Notifications.show(4, new Intent());
        }
        if (!Settings.isReconnect()) {
          break;
        }
        int i = Settings.getReconnectCount();
        int j = reconnectCount;
        reconnectCount = j + 1;
        if (i <= j) {
          break;
        }
        Log.d("PiterFM", "Reconnect attemp № " + reconnectCount);
        Log.d("PiterFM", "Reconnect timeout " + Settings.getReconnectTimeout() + " sec.");
        new Timer().schedule(new TimerTask()
        {
          public void run()
          {
            PlayerService.this.play(paramString, paramInt);
          }
        }, 1000 * Settings.getReconnectTimeout());
        return;
        MediaPlayer localMediaPlayer = getPlayer();
      }
    }
    localMediaPlayer.seekTo(paramInt);
    localMediaPlayer.start();
    state = State.Playing;
    nextTrack = RadioUtils.getNextTrackUrl(track);
    localDownloadTrackTask = new DownloadTrackTask();
    arrayOfObject = new Object[1];
    arrayOfObject[0] = nextTrack;
    localDownloadTrackTask.execute(arrayOfObject);
    return;
    label369:
  }
  
  private void preparePlayer(String paramString)
  {
    try
    {
      MediaPlayer localMediaPlayer = getPlayer();
      localMediaPlayer.reset();
      localMediaPlayer.setDataSource(Utils.CHUNKS_DIR + "/" + RadioUtils.getTrackNameFromUrl(paramString));
      localMediaPlayer.prepare();
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      Log.d("PiterFM: ", localException.getMessage() + getStackTrace(localException));
    }
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return this.mBinder;
  }
  
  public void onCompletion(MediaPlayer paramMediaPlayer)
  {
    paramMediaPlayer.reset();
    deleteTrack(track);
    if (nextTrack != null) {
      play(nextTrack);
    }
  }
  
  public void onCreate()
  {
    Utils.clearDirectory(Utils.CHUNKS_DIR);
    player1 = new MediaPlayer();
    player2 = new MediaPlayer();
    player1.setOnCompletionListener(this);
    player1.setOnErrorListener(this);
    player1.setOnPreparedListener(this);
    player2.setOnCompletionListener(this);
    player2.setOnErrorListener(this);
    player2.setOnPreparedListener(this);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (player1 != null)
    {
      player1.release();
      player1 = null;
    }
    if (player2 != null)
    {
      player2.release();
      player2 = null;
    }
    Utils.clearDirectory(Utils.CHUNKS_DIR);
  }
  
  public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2)
  {
    return false;
  }
  
  public void onPrepared(MediaPlayer paramMediaPlayer)
  {
    prepared = paramMediaPlayer;
  }
  
  public void pause()
  {
    player1.pause();
    state = State.Paused;
  }
  
  public void play(Channel paramChannel)
  {
    reconnectCount = 0;
    stop();
    if ((channel != null) && (channel.equals(paramChannel)))
    {
      channel = null;
      return;
    }
    channel = paramChannel;
    play(RadioUtils.getTrackUrl(channel));
  }
  
  public void play(Channel paramChannel, Track paramTrack)
  {
    reconnectCount = 0;
    stop();
    channel = paramChannel;
    play(RadioUtils.getTrackUrl(paramTrack.getTime(), channel.getChannelId()), RadioUtils.getTrackOffset(paramTrack.getTime()));
  }
  
  public void resume()
  {
    play(track);
    state = State.Playing;
  }
  
  public void stop()
  {
    if (player1.isPlaying()) {
      player1.stop();
    }
    if (player2.isPlaying()) {
      player2.stop();
    }
    track = null;
    nextTrack = null;
    prepared = null;
    state = State.Stopped;
    Utils.clearDirectory(Utils.CHUNKS_DIR);
  }
  
  public class DownloadTrackTask
    extends AsyncTask
  {
    private Exception exception;
    private String url;
    
    public DownloadTrackTask() {}
    
    public Void doInBackground(Object... paramVarArgs)
    {
      this.url = paramVarArgs[0].toString();
      try
      {
        Utils.downloadTrack(this.url);
        return null;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          localException.printStackTrace();
          this.exception = localException;
          Log.d("PiterFM: ", localException.getMessage() + "\n" + PlayerService.this.getStackTrace(localException));
        }
      }
    }
    
    protected void onPostExecute(Object paramObject)
    {
      if (this.exception == null) {
        PlayerService.this.preparePlayer(this.url);
      }
    }
  }
  
  public class PlayerServiceListener
    extends Binder
  {
    public PlayerServiceListener() {}
    
    public PlayerService getService()
    {
      return PlayerService.this;
    }
  }
  
  public static enum State
  {
    static
    {
      Preparing = new State("Preparing", 1);
      Playing = new State("Playing", 2);
      Paused = new State("Paused", 3);
      State[] arrayOfState = new State[4];
      arrayOfState[0] = Stopped;
      arrayOfState[1] = Preparing;
      arrayOfState[2] = Playing;
      arrayOfState[3] = Paused;
      $VALUES = arrayOfState;
    }
    
    private State() {}
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.player.PlayerService
 * JD-Core Version:    0.7.0.1
 */