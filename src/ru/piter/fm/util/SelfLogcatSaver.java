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
public class SelfLogcatSaver extends Thread {
    private static final String Tag = "SelfLogcatSaver";
    private static final String LOGFILE_PREFIX = "PiterFM";

    public SelfLogcatSaver() {
        super("SelfLogcatSaver");
        setDaemon(true);
    }

    public Process _deathPipe;

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        try {
            int myPid = android.os.Process.myPid();
            String s = String.format(Locale.US, "^..-.. ..:..:...... %5d .*", myPid);
            Pattern pattern = Pattern.compile(s);

            String logDir = Utils.LOG_DIR.getAbsolutePath();
            StatFs stat = new StatFs(logDir);

            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();

            long deathlogsz = 409600;
            long desiredlogsz = 80000000L;
            long totalsz = deathlogsz + desiredlogsz;

            int logssz = (int)Math.min(totalsz, availableBlocks * blockSize / 2);
            int numfiles = 4;
            if (logssz < 3000000) {
                Log.d(Tag, "too little free space");
                return;
            }
            logssz -= deathlogsz;

            String deathLogsDir = logDir;
            _deathPipe = Runtime.getRuntime().exec(new String[] {
                    "sh", "-c", "cat && logcat -v threadtime -f \"" + deathLogsDir + "/" + LOGFILE_PREFIX + "-death.log" + "\" -r100 -t1000"
            });

            FileHandler logHandler = new FileHandler(logDir + "/" + LOGFILE_PREFIX + "-%g.log", logssz / numfiles, numfiles);
            Formatter logFormatter = new Formatter() {
                @Override
                public String format(LogRecord r) {
                    String s = r.getMessage() + '\n';
                    return s;
                }
            };

            logHandler.setFormatter(logFormatter);

            Process process = Runtime.getRuntime().exec("logcat -v threadtime");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            s = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US).format(new Date())
                    + String.format(" %5d Logging started", myPid);
            logHandler.publish(new LogRecord(Level.INFO, s));

            String line;
            while ((line = reader.readLine()) != null) {
                boolean matched = pattern.matcher(line).matches();
                if (matched) {
                    logHandler.publish(new LogRecord(Level.INFO, line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
