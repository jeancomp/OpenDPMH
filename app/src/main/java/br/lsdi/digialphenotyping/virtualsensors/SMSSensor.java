package br.lsdi.digialphenotyping.virtualsensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

// BroadcastReceiver: é um componente android que permite registrar eventos no sistema ou aplicativos
// 1- É registrado um receiver no AndroidManifest, com permissão para escutar sms recebido no dispositivo móvel
// 2- Para implementar a classe  para receber SMS, precisa ser extendida do BroadcastReceiver
// 3- Se o evento para o qual o BroadcastReceiver foi registrado acontecer, o onReceive() método do
//      receptor é chamado pelo sistema Android.
public class SMSSensor extends BroadcastReceiver {
    private static final String TAG = SMSSensor.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "#######Intent recebida: " + intent.getAction());

        for(SmsMessage message : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            if (message == null) {
                Log.e(TAG, "####Mensagem nula: ");
                break;
            }
            Log.e(TAG, "####Mensagem recebida: " + message.getDisplayMessageBody());
        }
    }
}
