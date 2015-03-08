/**
 * 
 */
package ru.piter.fm.aac;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import ru.piter.fm.player.PlayerInterface.EventHandler;
import ru.piter.fm.util.TrackCalendar;

/**
 * @author Ilya Basin
 * 
 */
public abstract class PiterFMPlayer {

    private MediaPlayer player = new MediaPlayer();
    private B b = new B();
    private PlayerWrap playerWrap = new PlayerWrap();
    private long startTime;
    private String channelId;

    private static final String Tag = "PiterFMPlayer";

    public void open(final String ch, final TrackCalendar trackTime) {
        channelId = ch;
        startTime = trackTime.getTimeInMillis();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            private String streamUrl;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    streamUrl = b.doIt(ch, startTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                final String funcname = "onPostExecute";
                try {
                    Log.d(Tag, funcname + ",calling player.setDataSource(), streamUrl = " + streamUrl);
                    player.setDataSource(streamUrl);
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        task.execute((Void[])null);
    }

    public TrackCalendar getPosition() {
        if (startTime == 0)
            return null;
        TrackCalendar rslt = new TrackCalendar();
        rslt.setTimeInMillis(startTime + player.getCurrentPosition());
        return rslt;
    }

    public void pause() {
        player.pause();
    }

    public void resume() {
        player.start();
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean isPaused() {
        return true;
    }

    protected void assertUIThread() { assertEquals(Looper.getMainLooper().getThread(), Thread.currentThread()); }


    private class PlayerWrap implements MediaPlayer.OnErrorListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener
    {
        {
            final String funcname = "PlayerWrap,";
            Log.d(Tag, funcname + ",");
            player.setOnErrorListener(this);
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            final String funcname = "PlayerWrap,onPrepared";
            Log.d(Tag, funcname + ",");
            player.start();
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // TODO Auto-generated method stub
            return false;
        }
    }

}
