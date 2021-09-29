package com.jp.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.konovalov.vad.Vad;
import com.konovalov.vad.VadConfig;

import java.util.LinkedList;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.VoiceRecorder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivityRecorder extends AppCompatActivity implements VoiceRecorder.Listener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = MainActivityRecorder.class.getName();
    private VadConfig.SampleRate DEFAULT_SAMPLE_RATE = VadConfig.SampleRate.SAMPLE_RATE_16K;
    private VadConfig.FrameSize DEFAULT_FRAME_SIZE = VadConfig.FrameSize.FRAME_SIZE_160;
    private VadConfig.Mode DEFAULT_MODE = VadConfig.Mode.VERY_AGGRESSIVE;

    private int DEFAULT_SILENCE_DURATION = 500;
    private int DEFAULT_VOICE_DURATION = 500;

    private final String SPINNER_SAMPLE_RATE_TAG = "sample_rate";
    private final String SPINNER_FRAME_SIZE_TAG = "frame_size";
    private final String SPINNER_MODE_TAG = "mode";

    private FloatingActionButton recordingActionButton;
    private TextView speechTextView;
    private Spinner sampleRateSpinner;
    private Spinner frameSpinner;
    private Spinner modeSpinner;

    private ArrayAdapter sampleRateAdapter;
    private ArrayAdapter frameAdapter;
    private ArrayAdapter modeAdapter;

    private VoiceRecorder recorder;
    private VadConfig config;
    private boolean isRecording = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder);

        config = VadConfig.newBuilder()
                .setSampleRate(DEFAULT_SAMPLE_RATE)
                .setFrameSize(DEFAULT_FRAME_SIZE)
                .setMode(DEFAULT_MODE)
                .setSilenceDurationMillis(DEFAULT_SILENCE_DURATION)
                .setVoiceDurationMillis(DEFAULT_VOICE_DURATION)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
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

        speechTextView = findViewById(R.id.speechTextView);
        sampleRateSpinner = findViewById(R.id.sampleRateSpinner);
        sampleRateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getSampleRates());
        sampleRateSpinner.setAdapter(sampleRateAdapter);
        sampleRateSpinner.setTag(SPINNER_SAMPLE_RATE_TAG);
        sampleRateSpinner.setSelection(getSampleRates().indexOf(DEFAULT_SAMPLE_RATE.name()), false);
        sampleRateSpinner.setOnItemSelectedListener(this);

        frameSpinner = findViewById(R.id.frameSampleRateSpinner);
        frameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getFrameSizes());
        frameSpinner.setAdapter(frameAdapter);
        frameSpinner.setTag(SPINNER_FRAME_SIZE_TAG);
        frameSpinner.setSelection(getFrameSizes().indexOf(DEFAULT_FRAME_SIZE.name()), false);
        frameSpinner.setOnItemSelectedListener(this);

        modeSpinner = findViewById(R.id.modeSpinner);
        modeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getModes());
        modeSpinner.setAdapter(modeAdapter);
        modeSpinner.setTag(SPINNER_MODE_TAG);
        modeSpinner.setSelection(getModes().indexOf(DEFAULT_MODE.name()), false);
        modeSpinner.setOnItemSelectedListener(this);

        recordingActionButton = findViewById(R.id.recordingActionButton);
        recordingActionButton.setOnClickListener(this);
        recordingActionButton.setEnabled(true);

        //MainActivityPermissionsDispatcher.activateAudioPermissionWithPermissionCheck(this);
    }

    private LinkedList<String> getSampleRates() {
        LinkedList<String> result = new LinkedList<>();
        for (VadConfig.SampleRate sampleRate : VadConfig.SampleRate.values()) {
            result.add(sampleRate.name());
        }
        return result;
    }

    private LinkedList<String> getFrameSizes() {
        LinkedList<String> result = new LinkedList<>();
        LinkedList<VadConfig.FrameSize> supportingFrameSizes = Vad.getValidFrameSize(config.getSampleRate());

        if (supportingFrameSizes != null) {
            for (VadConfig.FrameSize frameSize : supportingFrameSizes) {
                result.add(frameSize.name());
            }
        }

        return result;
    }

    private LinkedList<String> getModes() {
        LinkedList<String> result = new LinkedList<>();
        for (VadConfig.Mode mode : VadConfig.Mode.values()) {
            result.add(mode.name());
        }
        return result;
    }

    private void startRecording() {
        Log.i(TAG,"#### Iniciando a gravação!");
        isRecording = true;
        recorder.start();
        recordingActionButton.setImageResource(R.drawable.stop);
    }

    private void stopRecording() {
        Log.i(TAG,"#### Parando a gravação!");
        isRecording = false;
        recorder.stop();
        recordingActionButton.setImageResource(R.drawable.red_dot);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        stopRecording();

        switch (String.valueOf(adapterView.getTag())) {
            case SPINNER_SAMPLE_RATE_TAG:
                config.setSampleRate(VadConfig.SampleRate.valueOf(String.valueOf(sampleRateAdapter.getItem(position))));

                frameAdapter.clear();
                frameAdapter.addAll(getFrameSizes());
                frameAdapter.notifyDataSetChanged();
                frameSpinner.setSelection(0);

                config.setFrameSize(VadConfig.FrameSize.valueOf(String.valueOf(frameAdapter.getItem(0))));
                break;
            case SPINNER_FRAME_SIZE_TAG:
                config.setFrameSize(VadConfig.FrameSize.valueOf(String.valueOf(frameAdapter.getItem(position))));
                break;
            case SPINNER_MODE_TAG:
                config.setMode(VadConfig.Mode.valueOf(String.valueOf(modeAdapter.getItem(position))));
                break;
        }

        recorder.updateConfig(config);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    public void activateAudioPermission() {
        recordingActionButton.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    @Override
    public void onSpeechDetected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"#### Voz humana detectada!");
                speechTextView.setText(R.string.speech_detected);
            }
        });
    }

    @Override
    public void onNoiseDetected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"#### Voz humana não detectada!");
                speechTextView.setText(R.string.noise_detected);
            }
        });
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        //MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


}
