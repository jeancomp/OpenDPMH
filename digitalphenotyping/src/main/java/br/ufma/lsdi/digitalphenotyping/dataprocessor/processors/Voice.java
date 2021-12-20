package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.content.Context;
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
            saveActivity.getInstance().getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.i(TAG,"#### Voz humana detectada!");
                    String dataProcessorName = "Sociability";
                    String alert = "Human voice detected";
                    long stamp = System.currentTimeMillis();
                    Object[] valor = {dataProcessorName, alert, stamp};
                    String[] atributte = {"data processor name", "message", "timestamp"};

                    Message message = new Message();
                    message.setServiceValue(valor);
                    message.setAvailableAttributesList(atributte);
                    message.setAvailableAttributes(3);
                    message.setServiceName("audiodetected");

                    publishMessage(message);
                }
            });
    }

    @Override
    public void onNoiseDetected() {
            saveActivity.getInstance().getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
    }

    public void publishMessage(Message m) {
        publisher.addConnection(CDDL.getInstance().getConnection());
        publisher.publish(m);
    }
}