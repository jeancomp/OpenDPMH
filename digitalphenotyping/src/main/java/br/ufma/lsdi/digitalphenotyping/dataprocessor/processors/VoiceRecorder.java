package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.CHANNEL_IN_STEREO;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import android.util.Log;

import com.konovalov.vad.Vad;
import com.konovalov.vad.VadConfig;
import com.konovalov.vad.VadListener;

/**
 * VoiceRecorder: responsável por identificar se o áudio possui voz humana
 */
public class VoiceRecorder {
    private static final String TAG = VoiceRecorder.class.getSimpleName();
    private static VoiceRecorder instance = null;
    private static final int PCM_CHANNEL = CHANNEL_IN_MONO;
    private static final int PCM_ENCODING_BIT = AudioFormat.ENCODING_PCM_16BIT;
    private Vad vad;
    private AudioRecord audioRecord;
    private static Listener callback;
    private static VadConfig config;
    private Thread thread;
    private boolean isListening = false;

    /*@RequiresPermission(Manifest.permission.RECORD_AUDIO)*/
    public VoiceRecorder(Listener callback, VadConfig config) {
        this.callback = callback;
        this.config = config;
        this.vad = new Vad(config);
    }

    public static VoiceRecorder getInstance() {
        if (instance == null) {
            instance = new VoiceRecorder(callback, config);
        }
        return instance;
    }

    public void updateConfig(VadConfig config) {
        vad.setConfig(config);
    }

    public void start() {
        try {
            stop();
            Log.i(TAG,"#### 00 - Iniciando a gravação!");
            audioRecord = createAudioRecord();
            if (audioRecord != null) {
                isListening = true;
                audioRecord.startRecording();
                thread = new Thread(new ProcessVoice());
                thread.start();
                vad.start();
            } else {
                Log.e(TAG, "Failed start Voice Recorder!");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void stop() {
        try {
            Log.i(TAG, "#### 00 - Parando a gravação!");
            isListening = false;
            if (thread != null) {
                thread.interrupt();
                thread = null;
            }
            if (audioRecord != null) {
                try {
                    audioRecord.stop();
                    audioRecord.release();
                } catch (Exception e) {
                    Log.e(TAG, "### Error stop AudioRecord ", e);
                }
                audioRecord = null;
            }
            if (vad != null) {
                vad.stop();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private AudioRecord createAudioRecord() {
        try {
            final int minBufSize = AudioRecord.getMinBufferSize(vad.getConfig().getSampleRate().getValue(), PCM_CHANNEL, PCM_ENCODING_BIT);

            if (minBufSize == AudioRecord.ERROR_BAD_VALUE) {
                return null;
            }

            final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, vad.getConfig().getSampleRate().getValue(), PCM_CHANNEL, PCM_ENCODING_BIT, minBufSize);

            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                return audioRecord;
            } else {
                audioRecord.release();
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "#### Error can't create AudioRecord ", e);
        }

        return null;
    }

    private int getNumberOfChannels() {
        switch (PCM_CHANNEL) {
            case CHANNEL_IN_MONO:
                return 1;
            case CHANNEL_IN_STEREO:
                return 2;
        }
        return 1;
    }

    private class ProcessVoice implements Runnable {

        @Override
        public void run() {
            try {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);

                while (!Thread.interrupted() && isListening && audioRecord != null) {
                    short[] buffer = new short[vad.getConfig().getFrameSize().getValue() * getNumberOfChannels() * 2];
                    audioRecord.read(buffer, 0, buffer.length);

                    detectSpeech(buffer);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        private void detectSpeech(short[] buffer) {
            vad.addContinuousSpeechListener(buffer, new VadListener() {
                @Override
                public void onSpeechDetected() {
                    callback.onSpeechDetected();
                }

                @Override
                public void onNoiseDetected() {
                    callback.onNoiseDetected();
                }
            });
        }
    }

    public interface Listener {
        void onSpeechDetected();

        void onNoiseDetected();
    }

}
