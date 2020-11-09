package br.lsdi.digialphenotyping.virtualsensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "#######Intent recebida: " + intent.getAction());

//        if (intent.getAction().equals(SMS_RECEIVED)) {
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                Object[] pdus = (Object[])bundle.get("pdus");
//                final SmsMessage[] messages = new SmsMessage[pdus.length];
//                System.out.println(messages);
//                for (int i = 0; i < pdus.length; i++) {
//                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i], SMS_RECEIVED);
//                }
//                if (messages.length > -1) {
//                    //Log.i(TAG, "####Mensagem recebida: " + messages[0].getMessageBody());
//                }
//            }
//        }

        for(SmsMessage message : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            if (message == null) {
                Log.e(TAG, "####Mensagem nula: ");
                break;
            }
            Log.e(TAG, "####Mensagem recebida: " + message.getDisplayMessageBody());
        }
    }
}
