package br.lsdi.digialphenotyping.virtualsensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    VirtualSensorInterface onNewMessageListener;

    public SmsBroadcastReceiver() {
    }

    public SmsBroadcastReceiver(VirtualSensorInterface onNewMessageListener) {
        this.onNewMessageListener = onNewMessageListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                if (status != null)
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            // Obtenha o conteúdo da mensagem SMS
                            String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                            // Extraia o código único da mensagem e conclua a verificação
                            // enviando o código de volta ao seu servidor.
                            if (!TextUtils.isEmpty(message)) {
                                String activationCode = null;
                                Pattern p = Pattern.compile("your pattern like \\b\\d{4}\\b");
                                Matcher m = p.matcher(message);
                                if (m.find()) {
                                    activationCode = (m.group(0));  // A substring correspondente
                                }

                                if (onNewMessageListener != null && !TextUtils.isEmpty(activationCode))
                                    onNewMessageListener.onNewMessageReceived(activationCode);
                            }
                            break;
                        case CommonStatusCodes.TIMEOUT:
                            // Tempo limite de espera por SMS (5 minutos)
                            // Lidar com o erro ...
                            break;
                    }
            }
        }
    }
}
