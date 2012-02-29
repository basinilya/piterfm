package ru.piter.fm.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import ru.piter.fm.R;
import ru.piter.fm.activities.MainActivity;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.RadioUtil;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 30.08.2010
 * Time: 15:37:13
 * To change this template use File | Settings | File Templates.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener {


    private static final String APP_DIR = Environment.getExternalStorageDirectory() + "/piterfm/";
    private static final int TIME_TO_SEEK = 2000;
    public static final int STOP = 0;
    public static final int PLAY = 1;
    public static int CURRENT_STATE = STOP;


    private final IBinder mBinder = new PlayerBinder();


    private MediaPlayer player;
    private Channel currentChannel;
    private String currentTrack = "";
    private String nextTrack = "";
    private String trackToDelete = "";

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class PlayerBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }


    @Override
    public void onCreate() {
        if (this.player == null) {
            this.player = new MediaPlayer();
            this.player.setOnCompletionListener(this);
        }
        RadioUtil.clearDirectory(RadioUtil.APP_DIR);
    }

    @Override
    public void onDestroy() {
        this.stop(true);
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
        RadioUtil.clearDirectory(RadioUtil.APP_DIR);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private void nextSong() {
        deleteTrack(trackToDelete);
        play(currentChannel);

    }


    public void play(Channel channel) {
        boolean sameChannel = channel.equals(currentChannel);
        currentChannel = channel;
        if (!sameChannel) {
            currentTrack = RadioUtil.getTrackUrl(channel);
            downloadTrack(currentTrack, true);
        } else {
            currentTrack = nextTrack;
        }

        nextTrack = RadioUtil.getNextTrackUrl(currentTrack);
        downloadTrack(nextTrack, false);


        try {
            player.reset();
            player.setDataSource(APP_DIR + RadioUtil.getTrackNameFromUrl(currentTrack));
            player.prepare(); // Вот тут!
            player.seekTo(TIME_TO_SEEK);
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (CURRENT_STATE != PLAY) Notifications.showAppNotification(this, currentChannel.getName(), 4);
            CURRENT_STATE = PLAY;
        trackToDelete = currentTrack;
    }

    private void downloadTrack(final String track, boolean isSameThread) {
        if (isSameThread) {
            doDownload(track);
        } else {
            new Thread() {
                public void run() {
                    doDownload(track);
                }
            }.start();
        }
    }

    private void doDownload(String track) {
        if (!RadioUtil.isSdAvailible()) {
            Notifications.showErrorNotification(this, getResources().getString(R.string.sdCardUnavailable), 2);
            return;
        }
        try {
            RadioUtil.downloadTrack(track);
        } catch (Exception e) {
            e.printStackTrace();
            Notifications.showErrorNotification(this, getResources().getString(R.string.translationUnavailable), 2);
        }
    }

    private void deleteTrack(final String track) {
        new Thread() {
            public void run() {
                RadioUtil.deletePreviousTrack(track);
            }
        }.start();
    }


    public void stop(boolean killNotification) {
        if (killNotification) Notifications.killAppNotification(this, 4);
        CURRENT_STATE = STOP;
        this.player.stop();
        this.player.reset();
        currentChannel = null;
        trackToDelete = null;
        nextTrack = null;
    }




    public void onCompletion(MediaPlayer mediaPlayer) {
        nextSong();
    }

    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    public Channel getCurrentChannel() {
        return currentChannel;
    }
}
