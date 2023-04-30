package com.example.sound;

import android.media.AudioManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mPlayer;
    private ImageButton playButton, repeatButton, prevButton, nextButton;
    private SeekBar seekBar;
    private TextView duration, songName;
    private ImageView songPh;
    private double timeElapsed = 0;
    private double finalTime = 0;


    private Handler durationHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = findViewById(R.id.play);
        repeatButton = findViewById(R.id.repeat);
        prevButton = findViewById(R.id.prev);
        nextButton = findViewById(R.id.next);
        songName = findViewById(R.id.songName);
        songPh = findViewById(R.id.songPh);
        seekBar = findViewById(R.id.seekBar);

        String[] mPath = {"https://getsamplefiles.com/download/m4a/sample-3.m4a",
                "https://getsamplefiles.com/download/m4a/sample-4.m4a",
                "https://getsamplefiles.com/download/m4a/sample-5.m4a"};
        String[] mPath_ph = {"https://images-workbench.99static.com/eJZ_L80DvKAYfT00fADDUgjfjE0=/99designs-contests-attachments/85/85381/attachment_85381335",
        "https://images-workbench.99static.com/BWy_H3O1rX_XT_mxIRwpXbBrROU=/99designs-contests-attachments/85/85887/attachment_85887172",
        "https://images-workbench.99static.com/5Xrcjp0v9NiGTXLiVgeNr9jHeRA=/99designs-contests-attachments/99/99226/attachment_99226865"};
        mPlayer = new MediaPlayer();
        final int[] curr_song_id = {0};
        try {
            mPlayer.setDataSource(mPath[curr_song_id[0]]);
            mPlayer.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPlayer.isPlaying()) {
                    playButton.setBackgroundResource(R.drawable.pause);
                    //songPh.set(mPath_ph[0]);
                    mPlayer.start();
                    Pattern pattern = Pattern.compile("m4a/(.*?).m4a");
                    Matcher matcher = pattern.matcher(mPath[curr_song_id[0]]);
                    if (matcher.find())
                    {
                        songName.setText(matcher.group(1));
                    }

                }
                else if (mPlayer.isPlaying()) {
                    playButton.setBackgroundResource(R.drawable.play);

                    mPlayer.pause();
                }

            }
        });
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPlayer.isPlaying()) {
                    if (!mPlayer.isLooping())
                        mPlayer.setLooping(true);
                    else
                        mPlayer.setLooping(false);
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPath.length>1) {
                                        if (curr_song_id[0] < mPath.length-1)
                        curr_song_id[0] = curr_song_id[0] + 1;
                    else
                        curr_song_id[0] = 0;

                    mPlayer.release();
                    try {
                        mPlayer = new MediaPlayer();
                        mPlayer.setDataSource(mPath[curr_song_id[0]]);
                        mPlayer.prepare();

                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    Pattern pattern = Pattern.compile("m4a/(.*?).m4a");
                    Matcher matcher = pattern.matcher(mPath[curr_song_id[0]]);
                    if (matcher.find())
                    {
                        songName.setText(matcher.group(1));
                    }
                    mPlayer.start();
                    playButton.setBackgroundResource(R.drawable.pause);
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPath.length>1) {

                    if (curr_song_id[0] == 0)
                        curr_song_id[0] = mPath.length-1;
                    else
                        curr_song_id[0] = curr_song_id[0] - 1;

                    mPlayer.release();
                    try {
                        mPlayer = new MediaPlayer();
                        mPlayer.setDataSource(mPath[curr_song_id[0]]);
                        mPlayer.prepare();

                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    Pattern pattern = Pattern.compile("m4a/(.*?).m4a");
                    Matcher matcher = pattern.matcher(mPath[curr_song_id[0]]);
                    if (matcher.find())
                    {
                        songName.setText(matcher.group(1));
                    }
                    mPlayer.start();
                    playButton.setBackgroundResource(R.drawable.pause);
                }
            }
        });
    }

    protected void stopPlay()
    {
        mPlayer.stop();
        playButton.setBackgroundResource(R.drawable.play);
        try {
            mPlayer.prepare();
            mPlayer.seekTo(0);
        }
        catch (Throwable t) {
            Toast.makeText(this, t.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer.isPlaying()) {
            stopPlay();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            try {
                mPlayer.release();
                mPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}