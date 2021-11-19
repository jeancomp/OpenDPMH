package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import static android.content.Context.ALARM_SERVICE;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.konovalov.vad.VadConfig;

import java.sql.Timestamp;
import java.util.Date;

import br.ufma.lsdi.cddl.message.Message;

public class Voice extends BroadcastReceiver implements VoiceRecorder.Listener {
    private final String TAG = Voice.class.getName();
    private Context context;
    private long frequency = 1000;
    private Send send;
    private VadConfig.SampleRate DEFAULT_SAMPLE_RATE = VadConfig.SampleRate.SAMPLE_RATE_16K;
    private VadConfig.FrameSize DEFAULT_FRAME_SIZE = VadConfig.FrameSize.FRAME_SIZE_160;
    private VadConfig.Mode DEFAULT_MODE = VadConfig.Mode.VERY_AGGRESSIVE;

    private int DEFAULT_SILENCE_DURATION = 500;
    private int DEFAULT_VOICE_DURATION = 500;

    private static VoiceRecorder recorder;
    private VadConfig config;
    private boolean isRecording = false;

    public void config(Context context){
        try {
            this.context = context;

            send = new Send();
            send.getInstance().setContext(context);

            config = VadConfig.newBuilder()
                    .setSampleRate(DEFAULT_SAMPLE_RATE)
                    .setFrameSize(DEFAULT_FRAME_SIZE)
                    .setMode(DEFAULT_MODE)
                    .setSilenceDurationMillis(DEFAULT_SILENCE_DURATION)
                    .setVoiceDurationMillis(DEFAULT_VOICE_DURATION)
                    .build();

            if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            recorder = new VoiceRecorder(this, config);
        }catch (Exception e){
            Log.e(TAG,"#### Error1: " +e.getMessage());
        }
    }

    public void startVoice() {
        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date(stamp.getTime());
        Log.i(TAG,"#### WORK EXECUTE AUDIO_SCHEDULE: " + date);

        if (!isRecording) {
            isRecording = true;
            startRecording();
        }
    }

    public void stopVoice(){
        if(isRecording) {
            stopRecording();
        }
    }

    private void startRecording() {
        Log.i(TAG,"#### Iniciando a gravação!");
        isRecording = true;
        recorder.start();
    }

    private void stopRecording() {
        Log.i(TAG,"#### Parando a gravação!");
        isRecording = false;
        recorder.stop();
    }

    @Override
    public void onSpeechDetected() {
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"#### Voz humana detectada!");
                }
            });*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.i(TAG,"#### Voz humana detectada!");
                String dataProcessorName = "Sociability";
                String alert = "Human voice detected";
                Timestamp stamp = new Timestamp(System.currentTimeMillis());
                Object[] valor = {dataProcessorName, alert, stamp};
                String[] atributte = {"data processor name", "message", "timestamp "};

                Message message = new Message();
                message.setServiceValue(valor);
                message.setAvailableAttributesList(atributte);
                message.setAvailableAttributes(3);
                message.setServiceName("Audio");

                send.getInstance().conectSociability(message);
            }
        }).start();
    }

    @Override
    public void onNoiseDetected() {
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"#### Voz humana não detectada!");
                }
            });*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.i(TAG,"#### Voz humana não detectada!");
            }
        }).start();
    }
//--------------------------------------------------------------------------------------------------
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.i("ALARM", "#### 1");
            this.context = context;
            startVoice();

            //stopV();
        }catch (Exception e){
            Log.e(TAG,"#### Error2: " +e.getMessage());
        }
    }

    public void stopV(){
        final long tempoDeEspera = this.frequency * 60 * 1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(tempoDeEspera);
                Log.i("ALARM", "#### 3");
                stopVoice();
            }
        }).start();
    }

    public void setAlarm(long frequency) {
        this.frequency = frequency;

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent i = new Intent(context, Voice.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), this.frequency * 60 * 1, pi); // Millisec * Second * Minute
    }

    public void desableAlarm(){
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, Voice.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
    }
//--------------------------------------------------------------------------------------------------

    public static class Send {
        Context context;
        Message message = new Message();
        private static Send instance = null;

        public Send() {
        }

        public static Send getInstance() {
            if (instance == null) {
                instance = new Send();
            }
            return instance;
        }

        public void setContext(Context c) {
            context = c;
        }

        public void conectSociability(Message m) {
            try {
                message = m;
                Intent intent = new Intent(context, Sociability.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                } else {
                    context.startService(intent);
                }
            } catch (Exception e) {
                Log.e("AlarmAudio", "#### Error: " + e.getMessage());
            }
        }


        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i("AlarmAudio", "#### Connection service MainService");
                Sociability.LocalBinder binder = (Sociability.LocalBinder) iBinder;
                Sociability myService = binder.getService();

                myService.onSensorDataArrived(message);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i("AlarmAudio", "#### Disconnection service MainService");
            }
        };
    }
}