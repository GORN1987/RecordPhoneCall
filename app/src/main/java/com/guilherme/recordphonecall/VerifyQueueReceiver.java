package com.guilherme.recordphonecall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class VerifyQueueReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String resultCode=intent.getStringExtra("informExecution");
        if (resultCode != null ){
            Toast.makeText(context,resultCode,Toast.LENGTH_SHORT).show();
            BroadcastObserver obs = BroadcastObserver.getIntance();
            obs.change();
        }
    }
}
