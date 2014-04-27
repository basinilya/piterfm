package ru.piter.fm.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Track;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.RadioUtils;
import ru.piter.fm.util.Settings;
import ru.piter.fm.util.Utils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 30.08.2010
 * Time: 15:37:13
 * To change this template use File | SettingsActivity | File Templates.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private static final int TIME_TO_SEEK = 2000;
    private static final int TRACK_MIN_LENGTH_BYTES = 10000;
    private final IBinder mBinder = new PlayerServiceListener();

    private static MediaPlayer player1;
    private static MediaPlayer player2;
    private static MediaPlayer prepared;
    public static String channelId;
    private static String track;
    private static String nextTrack;
    public static State state = State.Stopped;
    public static int reconnectCount = 0;

    public String getChannelId() {
        return channelId;
    }

    public State getState() {
        return state;
    }

    public void onCreate() {
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


    private MediaPlayer getPlayer() {
        if (player1.isPlaying()) return player2;
        return player1;
    }

    public boolean isPaused() {
        return state != State.Playing;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player1 != null) {
            player1.release();
            player1 = null;
        }
        if (player2 != null) {
            player2.release();
            player2 = null;
        }
        Utils.clearDirectory(Utils.CHUNKS_DIR);
    }


    public void play(String channelId, String trackTime) {
        reconnectCount = 0;
        stop();

        String url = RadioUtils.getTrackUrl(trackTime, channelId);
        int offset = RadioUtils.getTrackOffset(trackTime);
        playInternal(url, offset);
    }


    public void play(String ch) {
        reconnectCount = 0;
        stop();

        // if press on already played channel
        if (channelId != null && channelId.equals(ch)) {
            channelId = null;
            return;
        }

        channelId = ch;
        playInternal(RadioUtils.getTrackUrl(channelId));
    }

    private void playInternal(String trackUrl) {
        playInternal(trackUrl, TIME_TO_SEEK);
        Log.d("PiterFM: ", "play track " + trackUrl);
    }


    private void playInternal(final String trackUrl, final int offset) {
        track = trackUrl;
        String trackPath = Utils.CHUNKS_DIR + "/" + RadioUtils.getTrackNameFromUrl(track);

        if (!new File(trackPath).exists()) {
            try {
                Utils.downloadTrack(track);
                if (new File(trackPath).length() < TRACK_MIN_LENGTH_BYTES) {
                    Log.d("PiterFM", "track not exists " + trackPath);
                    return;
                }
                preparePlayer(track);
                reconnectCount = 0;
                Notifications.killNotification(Notifications.CANT_LOAD_TRACK);
            } catch (Exception e) {
                state = State.Stopped;
                e.printStackTrace();
                Log.d("PiterFM", e.getMessage() + "\n" + getStackTrace(e));
                //show notification only first time
                if (reconnectCount == 0)
                    Notifications.show(Notifications.CANT_LOAD_TRACK, new Intent());
                //check reconnect counter
                if (Settings.isReconnect() && Settings.getReconnectCount() > reconnectCount++) {
                    Log.d("PiterFM", "Reconnect attemp № " + reconnectCount);
                    Log.d("PiterFM", "Reconnect timeout " + Settings.getReconnectTimeout() + " sec.");
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            playInternal(trackUrl, offset);
                        }
                    }, Settings.getReconnectTimeout() * 1000); // reconnect timeout in seconds

                }
                return;
            }
        }

        MediaPlayer mp = prepared != null ? prepared : getPlayer();
        mp.seekTo(offset);
        mp.start();
        state = State.Playing;


        nextTrack = RadioUtils.getNextTrackUrl(track);
        new DownloadTrackTask().execute(nextTrack);

    }


    private void preparePlayer(String trackUrl) {
        try {
            MediaPlayer p = getPlayer();
            p.reset();
            p.setDataSource(Utils.CHUNKS_DIR + "/" + RadioUtils.getTrackNameFromUrl(trackUrl));
            p.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("PiterFM: ", e.getMessage() + getStackTrace(e));
        }
    }


    private void deleteTrack(final String track) {
        new Thread() {
            public void run() {
                Log.d("PiterFM: ", "Delete track " + track);
                Utils.deletePreviousTrack(track);
            }
        }.start();
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        prepared = mediaPlayer;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.reset();
        deleteTrack(track);
        if (nextTrack != null)
            playInternal(nextTrack);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        return false;
    }

    public void stop() {
        if (player1.isPlaying()) player1.stop();
        if (player2.isPlaying()) player2.stop();
        track = null;
        nextTrack = null;
        prepared = null;
        state = State.Stopped;
        Utils.clearDirectory(Utils.CHUNKS_DIR);
    }

    public enum State {
        Stopped,
        Playing
    }


    /**
     * Class for clients to access. Because we know this service always runs in
     */
    public class PlayerServiceListener extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class DownloadTrackTask extends AsyncTask {

        private Exception exception;
        private String url;

        @Override
        public Void doInBackground(Object... objects) {
            url = objects[0].toString();
            try {
                Utils.downloadTrack(url);
            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
                Log.d("PiterFM: ", e.getMessage() + "\n" + getStackTrace(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (exception == null)
                preparePlayer(url);
        }

    }

    private String getStackTrace(Exception e) {
        StackTraceElement[] arr = e.getStackTrace();
        String report = e.toString() + "\n\n";
        for (int i = 0; i < arr.length; i++) {
            report += arr[i].toString() + "\n";
        }
        return report;
    }

}
