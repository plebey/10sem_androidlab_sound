package com.example.sound;

import android.media.AudioManager;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mPlayer;
    private Button playButton, repeatButton, prevButton;
    private SeekBar seekBar;
    private TextView duration, song_name;
    private ImageView image;
    private double timeElapsed = 0;
    private double finalTime = 0;
    boolean isPlaying = false;

    private Handler durationHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button playButton = findViewById(R.id.play);
        final Button repeatButton = findViewById(R.id.repeat);
        final Button prevButton = findViewById(R.id.prev);
        final TextView songName = findViewById(R.id.songName);
        mPlayer = new MediaPlayer();


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    playButton.setText(R.string.pause);
                    isPlaying = !isPlaying;
                    String mPath = "https://getsamplefiles.com/download/m4a/sample-1.m4a";
                    try {
                        mPlayer.setDataSource(mPath);
                        mPlayer.prepare();
                        mPlayer.start();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }
                else if (isPlaying) {
                    playButton.setText(R.string.play);
                    isPlaying = !isPlaying;
                }

            }
        });
    }

}