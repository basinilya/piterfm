package ru.piter.fm.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import ru.piter.fm.activities.RadioActivity;
import ru.piter.fm.prototype.R;
import ru.piter.fm.util.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 16.03.12
 * Time: 15:39
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseTask<T> extends AsyncTask<Object, Void, T> {

    public Context context;
    public ProgressDialog dialog;
    public Exception exception;
    public boolean isOnline = true;

    protected BaseTask() {
    }

    public BaseTask(Context context) {
        this.context = context;
        this.dialog = new ProgressDialog(context);
        dialog.setCanceledOnTouchOutside(true); // before ICS the default was false
    }

    @Override
    protected void onPreExecute() {
        if (!Utils.isInternetAvailable(context))
            isOnline = false;
        this.dialog.setMessage(context.getResources().getString(R.string.loading));
        this.dialog.show();

    }

    @Override
    protected T doInBackground(Object... objects) {
        try {
            return doWork(objects);
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(T result) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        if (exception == null) {
            onResult(result);
        } else {
            onError(exception);
        }
    }

    @SuppressLint("Override")
    protected final void onCancelled(T result) {
        this.onCancelled(); // not super
    }

    @Override
    protected void onCancelled() {
        if (dialog.isShowing()) dialog.dismiss();
    }

    public abstract T doWork(Object... objects) throws Exception;

    public abstract void onResult(T result);

    public abstract void onError(Exception e);


}
