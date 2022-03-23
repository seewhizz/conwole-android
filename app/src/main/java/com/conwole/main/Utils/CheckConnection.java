package com.conwole.main.Utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.util.TimerTask;

public class CheckConnection extends TimerTask {
    private Context context;
    private Activity activity;
    public CheckConnection(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }
    public void run() {
        if(NetworkUtils.isNetworkAvailable(context)){
            //CONNECTED
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "Hello Internet", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            //DISCONNECTED
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "Bye Internet", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}