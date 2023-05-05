package com.example.sound;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.*;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import java.io.*;
import java.util.concurrent.TimeUnit;


public class recorder extends AppCompatActivity {
    AudioRecord recorder;
    //MediaPlayer mPlayer;
    Button recordButton, playButton;
    TextView timeRec, timeRec_txt;
    boolean isReading = false;
    private final int audio_format = AudioFormat.ENCODING_PCM_16BIT;
    private final int sample_rate = 32000;
    private final int channel = AudioFormat.CHANNEL_IN_MONO;
    private final int min_buffer_size = AudioRecord.getMinBufferSize(sample_rate,channel,audio_format);
    private Handler durationHandler = new Handler();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        recordButton = findViewById(R.id.record);
        playButton = findViewById(R.id.playRec);
        timeRec = findViewById(R.id.timeRec);
        timeRec_txt = findViewById(R.id.timeRec_txt);


        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            createRecorder();

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
        playButton.setOnClickListener(view->playRec());


    }

    private int curr_time;
    private final Runnable updateSeekBarTime = new Runnable() {
        @Override
        public void run() {
            if (isReading) {
                curr_time = curr_time + 100;

                @SuppressLint("DefaultLocale") String duration_millis = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((int) curr_time),
                        TimeUnit.MILLISECONDS.toSeconds((int) curr_time) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((int) curr_time))
                );
                timeRec.setText(duration_millis);
                durationHandler.postDelayed(this, 100);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createRecorder(){
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
    }
    PCMPlayer player;
    private void playRec(){

        player = new PCMPlayer();
        String fPath = getRecordingFilePath();
        player.init(fPath);
        player.start();
    }

    private void stopRec(){
        System.out.println("\nstopped rec");
        Toast.makeText(this, "Конец записи", Toast.LENGTH_SHORT).show();
        isReading = false;
        recorder.stop();
        recorder.release();

        recorder = null;
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (isReading) {
            stopRec();
        }
        if (player.mAudioTrack !=null){//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            player.stopPlay();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void recordButton(){
        if (recorder != null) {
            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                Thread readThread;
                if (recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    stopRec();
                    readThread = null;
                } else if (recorder.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
                    readThread = new Thread(readRecord);
                    System.out.println("\nrec");
                    Toast.makeText(this, "Начало записи", Toast.LENGTH_SHORT).show();
                    recorder.startRecording();
                    isReading = true;
                    readThread.start();
                    curr_time = 0;
                    timeRec_txt.setVisibility(View.VISIBLE);
                    durationHandler.postDelayed(updateSeekBarTime, 100);
                } else {
                    System.out.println("\nbruh exception 02");
                }
            }
        }else {
            createRecorder();
            recordButton();
        }


    }

    private final Runnable readRecord = new Runnable() {
        @Override
        public void run() {
            int data_length = min_buffer_size*2;
            byte[] data = new byte[data_length];
            String fPath = getRecordingFilePath();
            try (FileOutputStream fos= new FileOutputStream(fPath)) {
                while (isReading){
                   int read = recorder.read(data,0,data_length);
                   fos.write(data);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };
    private String getRecordingFilePath(){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File soundDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(soundDir,"RecFile"+".pcm");
        return file.getPath();
    }

    class PCMPlayer extends Thread {
        protected AudioTrack mAudioTrack;
        protected int mMiniBufferSize;
        protected byte[] mBuffer;
        File file;
        FileInputStream in;
        /**
         * @param filePath путь к файлу
         * */
        public void init(String filePath) {
            try {
                file = new File(filePath);
                in = new FileInputStream(file);

                /**
                 * Параметр 1: частота дискретизации Настроить в соответствии с частотой дискретизации записи.
                 * Параметр 2: аудиоканал
                 * Параметр 3: скорость передачи данных
                 * */
                mMiniBufferSize = AudioTrack.getMinBufferSize(sample_rate,
                        AudioFormat.CHANNEL_OUT_MONO, // CHANNEL_CONFIGURATION_MONO,
                        audio_format);

                mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sample_rate,
                        AudioFormat.CHANNEL_OUT_MONO, // CHANNEL_CONFIGURATION_MONO,
                        audio_format,
                        mMiniBufferSize,
                        AudioTrack.MODE_STREAM);

                mBuffer = new byte[mMiniBufferSize];

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void stopPlay(){
            mAudioTrack.stop();
            mAudioTrack = null;
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void run() {
            mAudioTrack.play();
            int read = -1 ;
            try {
                while ((read = in.read(mBuffer)) != -1) {
                    try {
                        mAudioTrack.write(mBuffer, 0, read);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            stopPlay();

        }
    }


//    private void PlayShortAudioFileViaAudioTrack()
//    {
//        String filePath = getRecordingFilePath();
//// We keep temporarily filePath globally as we have only two sample sounds now..
//        if (filePath==null)
//            return;
//
////Reading the file..
//        byte[] byteData = null;
//        File file = null;
//        file = new File(filePath); // for ex. path= "/sdcard/samplesound.pcm" or "/sdcard/samplesound.wav"
//        byteData = new byte[(int) file.length()];
//        FileInputStream in = null;
//        try {
//            in = new FileInputStream( file );
//            in.read( byteData );
//            in.close();
//
//        } catch (FileNotFoundException e) {
//// TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//// Set and push to audio track..
//
//        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, sample_rate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                audio_format, min_buffer_size, AudioTrack.MODE_STREAM);
//        if (at!=null) {
//            at.play();
//// Write the byte array to the track
//            at.write(byteData, 0, byteData.length);
//            at.stop();
//            at.release();
//        }
//        else
//            Log.d("TCAudio", "audio track is not initialised ");
//
//    }



//    private void getMicroPermit(){
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
//        == PackageManager.PERMISSION_DENIED){
//
//        }

//    }
}