package ru.piter.fm.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 10.09.2010
 * Time: 0:51:44
 * To change this template use File | Settings | File Templates.
 */
public class EventsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();


        if (action.equals("ru.piter.fm.action.ERROR")) {
            Toast.makeText(context, "Refreshing...", Toast.LENGTH_SHORT).show();
        }else if (action.equals("android.intent.action.CALL")){
            
        }


    }
}
