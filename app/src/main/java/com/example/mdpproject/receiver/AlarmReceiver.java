package com.example.mdpproject.receiver;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    public final static String ACTION_ALARM_SET = "com.example.mdpproject.receiver.ACTION_ALARM_SET";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        Log.i(TAG, "ALARM !!!!");
        Intent serviceIntent = new Intent(ACTION_ALARM_SET);
        context.sendBroadcast(serviceIntent);
    }
}