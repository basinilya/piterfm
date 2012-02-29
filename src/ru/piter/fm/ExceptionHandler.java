package ru.piter.fm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 07.09.2010
 * Time: 23:58:06
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final File TRACE_DIR = new File(Environment.getExternalStorageDirectory() + "/piterfm/log");

    private Thread.UncaughtExceptionHandler defaultUEH;

    private Activity app = null;

    public ExceptionHandler(Activity app) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.app = app;
    }

    public void uncaughtException(Thread t, Throwable e) {
        StackTraceElement[] arr = e.getStackTrace();
        String report = e.toString() + "\n\n";
        report += "--------- Stack trace ---------\n\n";
        for (int i = 0; i < arr.length; i++) {
            report += "    " + arr[i].toString() + "\n";
        }
        report += "-------------------------------\n\n";

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        report += "--------- Cause ---------\n\n";
        Throwable cause = e.getCause();
        if (cause != null) {
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for (int i = 0; i < arr.length; i++) {
                report += "    " + arr[i].toString() + "\n";
            }
        }

        report += "-------------------------------\n\n";
        Log.d(getClass().getName(), "\n" + report);
        try {
            if (!TRACE_DIR.exists()) TRACE_DIR.mkdir();
            FileOutputStream trace = new FileOutputStream(new File(TRACE_DIR,"log.txt"));
            trace.write(report.getBytes());
            trace.flush();
            trace.close();

        } catch (IOException ioe) {
            e.printStackTrace();
        }

        Log.d(getClass().getName(), "Show error dialog");
        showErrorDialog(report);
        defaultUEH.uncaughtException(t, e);
    }


    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(app);
        builder.setMessage(message);
        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               app.finish();
            }
        });
        builder.setTitle("Exception!");
        builder.show();

    }

}
