package com.example.sound;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import com.example.sound.R;

import java.io.File;
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
    final String[] mPath = {"https://getsamplefiles.com/download/m4a/sample-3.m4a",
            "https://getsamplefiles.com/download/m4a/sample-4.m4a",
            "https://getsamplefiles.com/download/m4a/sample-5.m4a"};
    final int[] curr_song_id = {0};


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
        duration = findViewById(R.id.duration);


        //String[] mPath_ph = {R.};
        mPlayer = new MediaPlayer();

        try {
            mPlayer.setDataSource(mPath[curr_song_id[0]]);
            mPlayer.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        playButton.setOnClickListener(view -> playButton());
        repeatButton.setOnClickListener(view -> repeatButton());
        nextButton.setOnClickListener(view -> nextButton());
        prevButton.setOnClickListener(view -> prevButton());
    }

    protected void setNamePic (){
        int i = curr_song_id[0]+1;
        String url = "drawable/"+"pic"+i;
        int imageKey = getResources().getIdentifier(url,"drawable", getPackageName());
        songPh.setBackgroundResource(imageKey);
        Pattern pattern = Pattern.compile("m4a/(.*?).m4a");
        Matcher matcher = pattern.matcher(mPath[curr_song_id[0]]);
        if (matcher.find())
        {
            songName.setText(matcher.group(1));
        }
    }
    protected void playButton (){
        if (!mPlayer.isPlaying()) {
            playButton.setBackgroundResource(R.drawable.pause);
            mPlayer.start();
            setNamePic();
        }
        else if (mPlayer.isPlaying()) {
            playButton.setBackgroundResource(R.drawable.play);
            mPlayer.pause();
        }
    }

    protected void repeatButton (){
        if (mPlayer.isPlaying()) {
            if (!mPlayer.isLooping()) {
                repeatButton.setBackgroundResource(R.drawable.repeat_clicked);
                mPlayer.setLooping(true);
            }
            else {
                mPlayer.setLooping(false);
                repeatButton.setBackgroundResource(R.drawable.repeat);
            }
        }
    }

    protected void nextButton(){
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
            setNamePic();

            mPlayer.start();
            playButton.setBackgroundResource(R.drawable.pause);
        }
    }

    protected void prevButton(){
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
            setNamePic();
            mPlayer.start();
            playButton.setBackgroundResource(R.drawable.pause);
        }
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