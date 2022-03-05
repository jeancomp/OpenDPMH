package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.konovalov.vad.VadConfig;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.digitalphenotyping.SaveActivity;

public class Voice implements VoiceRecorder.Listener {
    private final String TAG = Voice.class.getName();
    private Publisher publisher = PublisherFactory.createPublisher();
    private SaveActivity saveActivity = SaveActivity.getInstance();
    private static Voice instance = null;
    private Context context;

    private VadConfig.SampleRate DEFAULT_SAMPLE_RATE = VadConfig.SampleRate.SAMPLE_RATE_16K;
    private VadConfig.FrameSize DEFAULT_FRAME_SIZE = VadConfig.FrameSize.FRAME_SIZE_160;
    private VadConfig.Mode DEFAULT_MODE = VadConfig.Mode.VERY_AGGRESSIVE;

    private int DEFAULT_SILENCE_DURATION = 500;
    private int DEFAULT_VOICE_DURATION = 500;

    private static VoiceRecorder recorder;
    private VadConfig config;
    private boolean isRecording = false;

    public Voice(){ }

    public static Voice getInstance() {
        if (instance == null) {
            instance = new Voice();
        }
        return instance;
    }

    public void config(Context context){
        try {
            this.context = context;

            config = VadConfig.newBuilder()
                    .setSampleRate(DEFAULT_SAMPLE_RATE)
                    .setFrameSize(DEFAULT_FRAME_SIZE)
                    .setMode(DEFAULT_MODE)
                    .setSilenceDurationMillis(DEFAULT_SILENCE_DURATION)
                    .setVoiceDurationMillis(DEFAULT_VOICE_DURATION)
                    .build();

            recorder = new VoiceRecorder(this, config);
        }catch (Exception e){
            Log.e(TAG,"#### Error: " +e.getMessage());
        }
    }

    public void start() {
        if (!isRecording) {
            isRecording = true;
            startRecording();
        }
    }

    public void stop(){
        if(isRecording) {
            stopRecording();
        }
    }

    private void startRecording() {
        isRecording = true;
        recorder.getInstance().start();
    }

    private void stopRecording() {
        isRecording = false;
        recorder.getInstance().stop();
    }

    @Override
    public void onSpeechDetected() {
        new AddItemTask().execute();
    }

    @Override
    public void onNoiseDetected() { }

    private class AddItemTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            stop();

            String dataProcessorName = "Physical_Sociability";
            String alert = "Human_voice_detected";
            long stamp = System.currentTimeMillis();
            String str = String.valueOf(stamp);
            //Log.i("TEE","#### STR: " + str);
            Object[] valor = {dataProcessorName, alert, str};
            String[] atributte = {"data processor name", "message", "timestamp"};

            Message message = new Message();
            message.setServiceValue(valor);
            message.setAvailableAttributesList(atributte);
            message.setAvailableAttributes(3);
            message.setServiceName("audiodetected");

            publisher.addConnection(CDDL.getInstance().getConnection());
            publisher.publish(message);
            return null;
        }
    }

    public void publishMessage(Message m) {
        Log.i("message", "#### M: " + m);
        publisher.addConnection(CDDL.getInstance().getConnection());
        publisher.publish(m);
    }
}