package edu.washington.clgan.awty;

/**
 * Created by GanHome on 2/19/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getExtras().getString("message");
        String phoneNum = intent.getExtras().getString("phone");
        //Toast.makeText(context, phoneNum + ": " + message, Toast.LENGTH_SHORT).show();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNum, null, message, null, null);
    }
}