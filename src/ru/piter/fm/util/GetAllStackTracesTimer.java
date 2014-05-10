/**
 * 
 */
package ru.piter.fm.util;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

/**
 * @author Ilya Basin
 * 
 */
public class GetAllStackTracesTimer extends TimerTask {
    private static final String TAG = "GetAllStackTracesTimer";

    public GetAllStackTracesTimer() {
        long period = 300000;
        new Timer(TAG, true).scheduleAtFixedRate(this, period, period);
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        sb.append("all stack traces:\n");
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            Thread t = entry.getKey();
            StackTraceElement[] stack = entry.getValue();
            sb.append("\t" + "Thread [" + t.getName() + "]");
            for (StackTraceElement el : stack) {
                sb.append("\n\t\t").append(el.toString());
            }
            sb.append("\n\n");
        }
        Log.d(TAG, sb.toString());
    }
}
