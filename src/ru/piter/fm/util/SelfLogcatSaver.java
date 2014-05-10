/**
 *
 */
package ru.piter.fm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

import android.os.StatFs;
import android.util.Log;

/**
 * @author Ilya Basin
 *
 */
public final class SelfLogcatSaver {

    private static final String Tag = "SelfLogcatSaver";

    private static final String LOGFILE_PREFIX = "PiterFM";

    private final Object lock = new Object();

    private boolean enabled;
    private Thread thread;

    public void enable() {
        synchronized(lock) {
            enabled = true;
            if (thread == null) {
                thread = new Thread(Tag) {
                    @Override
                    public void run() {
                        for (;;) {
                            synchronized (lock) {
                                if (!enabled) {
                                    thread = null;
                                    break;
                                }
                            }
                            forkAndPump();
                        }

                    }
                };
                thread.setDaemon(true);
                thread.start();
            }
        }
    }

    public void disable() {
        synchronized(lock) {
            enabled = false;
            if (logcatProcess != null) {
                // kills `logcat` and thus unblocks read()
                logcatProcess.destroy();
                logcatProcess = null;
            }
            if (awaitDeathProcess != null) {
                // kills `sh` and then closes the pipe, so `cat` dies too
                awaitDeathProcess.destroy();
                awaitDeathProcess = null;
            }
        }
    }

    private Process awaitDeathProcess;
    private Process logcatProcess;

    @SuppressWarnings("deprecation")
    private long getAvailableBytes(String dir) {
        StatFs stat = new StatFs(dir);

        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        return availableBlocks * blockSize;
    }

    public void forkAndPump() {
        int myPid = android.os.Process.myPid();
        String s = String.format(Locale.US, "^..-.. ..:..:...... %5d .*", myPid);
        Pattern pattern = Pattern.compile(s);
        FileHandler logHandler = null;
        String logDir = Utils.LOG_DIR.getAbsolutePath();

        long deathlogsz = 409600;
        long desiredlogsz = 80000000L;
        long totalsz = deathlogsz + desiredlogsz;

        int logssz = (int)Math.min(totalsz, getAvailableBytes(logDir) / 2);
        int numfiles = 4;
        if (logssz < 3000000) {
            Log.d(Tag, "too little free space");
            return;
        }
        logssz -= deathlogsz;

        Formatter logFormatter = new Formatter() {
            @Override
            public String format(LogRecord r) {
                String s = r.getMessage() + '\n';
                return s;
            }
        };

        BufferedReader reader = null;
        try {
            synchronized(lock) {
                if (!enabled)
                    return;
                try {

                    if (awaitDeathProcess == null) {
                        awaitDeathProcess = Runtime.getRuntime().exec(new String[] {
                                "sh", "-c", "cat && logcat -v threadtime -f \"" + logDir + "/" + LOGFILE_PREFIX + "-death.log" + "\" -r100 -t1000"
                        });
                    }

                    logHandler = new FileHandler(logDir + "/" + LOGFILE_PREFIX + "-%g.log", logssz / numfiles, numfiles);
                    logHandler.setFormatter(logFormatter);

                    logcatProcess = Runtime.getRuntime().exec("logcat -v threadtime");

                    logcatProcess.getOutputStream().close();
                    logcatProcess.getErrorStream().close();

                    reader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
                } catch (IOException e) {
                    enabled = false;
                    e.printStackTrace();
                    return;
                }
            }

            s = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US).format(new Date())
                    + String.format(" %5d Logging started", myPid);
            logHandler.publish(new LogRecord(Level.INFO, s));

            try {
                while((s = reader.readLine()) != null) {
                    boolean matched = pattern.matcher(s).matches();
                    if (matched) {
                        logHandler.publish(new LogRecord(Level.INFO, s));
                    }
                }
            } catch (IOException e) {
                enabled = false;
                e.printStackTrace();
            }
        } finally {
            logcatProcess = null;
            if (logHandler != null) {
                logHandler.close();
            }
            if (reader != null)
                try { reader.close(); } catch (IOException e) {}
        }
    }
}
