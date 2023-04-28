package com.example.sound;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mPlayer;
    Button playButton, repeatButton, prevButton, ;
    SeekBar seekBar;
    TextView duration, name;
    ImageView image;
    private double timeElapsed = 0;
    private double finalTime = 0;

    private Handler durationHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = (Button) findViewById(R.id.play);
        repeatButton = (Button) findViewById(R.id.repeat);
        prevButton = (Button) findViewById(R.id.prev);
        stopButton = (Button) findViewById(R.id.next);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        duration = (TextView) findViewById(R.id.textView);
        name = (TextView) findViewById(R.id.textView2);
        image = (ImageView) findViewById(R.id.imageView);
        name.setText(getString(R.string.song));

        int resID = getResources().getIdentifier(getString(R.string.
                song), "drawable", getPackageName()); //R.drawable.russia
        image.setImageResource(resID);
        int resID2 = getResources().getIdentifier(getString(R.string.
                song), "raw", getPackageName()); //R.raw.russia
        mPlayer = MediaPlayer.create(this, resID2);
        mPlayer.setOnCompletionListener(new
                                                MediaPlayer.OnCompletionListener() {
                                                    @Override
                                                    public void onCompletion(MediaPlayer mp) {
                                                        stopPlay();
                                                    }
                                                });
        finalTime = mPlayer.getDuration();

        seekBar.setProgress(0);
        seekBar.setMax((int) finalTime);

        seekBar.setClickable(false);

        seekBar.setOnSeekBarChangeListener(new
                                                   SeekBar.OnSeekBarChangeListener() {
                                                       @Override
                                                       public void onProgressChanged(SeekBar
16
        seekBar, int i, boolean b) {
                                                           if (b) {
                                                               mPlayer.seekTo(i);

                                                               seekBar.setProgress(i);
                                                           }
                                                       }
                                                       @Override
                                                       public void onStartTrackingTouch(SeekBar seekBar) {
                                                       }
                                                       @Override
                                                       public void onStopTrackingTouch(SeekBar seekBar) {
                                                       }
                                                   });
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            timeElapsed = mPlayer.getCurrentPosition();

            seekBar.setProgress((int) timeElapsed);

            double timeRemaining = finalTime - timeElapsed;

            duration.setText(String.format(Locale.US, "%d min, %d sec",
                    TimeUnit.0,//,6(&21'6.toMinutes((long) timeRemaining),
                    TimeUnit.0,//,6(&21'6.toSeconds((long) timeRemaining)
                    -
                            TimeUnit.0,187(6.toSeconds(TimeUnit.0,//,6(&21'6.toMinutes((long)
                    timeRemaining)))
);
            durationHandler.postDelayed(this, 100);
        }
    }
}