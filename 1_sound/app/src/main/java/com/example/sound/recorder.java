package com.example.sound;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class recorder extends AppCompatActivity {
    private static int MICROPHONE_PERMISSION_CODE = 200;
    AudioRecord recorder;
    Button recordButton, playButton;
    TextView timeRec;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        recordButton = findViewById(R.id.record);
        playButton = findViewById(R.id.playRec);
        timeRec = findViewById(R.id.timeRec);



        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            int audio_format = AudioFormat.ENCODING_PCM_16BIT;
                            int sample_rate = 32000;
                            int channel = AudioFormat.CHANNEL_IN_MONO;
                            int min_buffer_size = AudioRecord.getMinBufferSize(sample_rate,channel,audio_format);
                            try {
                                recorder = new AudioRecord.Builder()
                                        .setAudioSource(MediaRecorder.AudioSource.MIC)
                                        .setAudioFormat(new AudioFormat.Builder()
                                                .setEncoding(audio_format)
                                                .setSampleRate(sample_rate)
                                                .setChannelMask(channel)
                                                .build())
                                        .setBufferSizeInBytes(4 * min_buffer_size)
                                        .build();
                            } catch (SecurityException e){
                                System.out.println("bruh exception 01");
                            }
                        } else {
                            return;
                        }
                    }
                }
        );
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
        recordButton.setOnClickListener(view->recordButton());
    }

    protected void recordButton(){
        if (recorder.getState()==AudioRecord.STATE_INITIALIZED){
            if (recorder.getRecordingState()==AudioRecord.RECORDSTATE_RECORDING)
            {
                System.out.println("\nstoped rec");
                recorder.stop();
            }else if (recorder.getRecordingState()==AudioRecord.RECORDSTATE_STOPPED){
                System.out.println("\nrec");
                recorder.startRecording();
            }else {
                System.out.println("\nbruh exception 02");
            }
        }


    }



    private void getMicroPermit(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        == PackageManager.PERMISSION_DENIED){

        }

    }
}