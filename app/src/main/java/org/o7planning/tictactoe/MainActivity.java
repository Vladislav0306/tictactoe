package org.o7planning.tictactoe;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private SoundPool soundPool;
    private int soundID;
    boolean loaded = false;
    int num = 0;

    boolean gameActive = true;

    MediaPlayer mPlayer;
    // Player representation
    // 0 - X
    // 1 - O
    int activePlayer = 0;
    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    // State meanings:
    //    0 - X
    //    1 - O
    //    2 - Null
    // put all win positions in a 2D array
    int[][] winPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}};
    public static int counter = 0;



    // this function will be called every time a
    // players tap in an empty box of the grid
    public void playerTap(View view) {
        ImageView img = (ImageView) view;
        int tappedImage = Integer.parseInt(img.getTag().toString());

        // game reset function will be called
        // if someone wins or the boxes are full
        if (!gameActive) {
            gameReset(view);
            //Reset the counter
            counter = 0;
        }

        // if the tapped image is empty
        if (gameState[tappedImage] == 2) {
            // increase the counter
            // after every tap
            counter++;

            // check if its the last box
            if (counter == 9) {
                // reset the game
                gameActive = false;
            }

            // mark this position
            gameState[tappedImage] = activePlayer;

            // this will give a motion
            // effect to the image
            img.setTranslationY(-1000f);

            // change the active player
            // from 0 to 1 or 1 to 0
            if (activePlayer == 0) {
                // set the image of x
                img.setImageResource(R.drawable.x);

                activePlayer = 1;
                TextView status = findViewById(R.id.status);

                // change the status
                status.setText("O's Turn - Tap to play");
            } else {
                // set the image of o
                img.setImageResource(R.drawable.o);
                activePlayer = 0;
                TextView status = findViewById(R.id.status);

                // change the status
                status.setText("X's Turn - Tap to play");
            }
            img.animate().translationYBy(1000f).setDuration(300);
        }
        int flag = 0;
        // Check if any player has won if counter is > 4 as min 5 taps are
        // required to declare a winner
        if (counter > 4) {
            for (int[] winPosition : winPositions) {
                if (gameState[winPosition[0]] == gameState[winPosition[1]] &&
                        gameState[winPosition[1]] == gameState[winPosition[2]] &&
                        gameState[winPosition[0]] != 2) {
                    flag = 1;

                    // Somebody has won! - Find out who!
                    String winnerStr;

                    // game reset function be called
                    gameActive = false;
                    if (gameState[winPosition[0]] == 0) {
                        winnerStr = "X has won";
                    } else {
                        winnerStr = "O has won";
                    }
                    // Update the status bar for winner announcement
                    TextView status = findViewById(R.id.status);
                    status.setText(winnerStr);
                }
            }
            // set the status if the match draw
            if (counter == 9 && flag == 0) {
                TextView status = findViewById(R.id.status);
                status.setText("Match Draw");
            }
        }
    }

    // reset the game
    public void gameReset(View view) {
        gameActive = true;
        activePlayer = 0;
        //set all position to Null
        Arrays.fill(gameState, 2);
        // remove all the images from the boxes inside the grid
        ((ImageView) findViewById(R.id.imageView0)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView1)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView2)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView3)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView4)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView5)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView6)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView7)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView8)).setImageResource(0);

        TextView status = findViewById(R.id.status);
        status.setText("X's Turn - Tap to play");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //start service and play music
        startService(new Intent(MainActivity.this, SoundService.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Устанавливаем прослушивальшик нажатий на Textview
        View view0 = findViewById(R.id.imageView0);
        View view1 = findViewById(R.id.imageView1);
        View view2 = findViewById(R.id.imageView2);
        View view3 = findViewById(R.id.imageView3);
        View view4 = findViewById(R.id.imageView4);
        View view5 = findViewById(R.id.imageView5);
        View view6 = findViewById(R.id.imageView6);
        View view7 = findViewById(R.id.imageView7);
        View view8 = findViewById(R.id.imageView8);
        view0.setOnTouchListener(this);
        view1.setOnTouchListener(this);
        view2.setOnTouchListener(this);
        view3.setOnTouchListener(this);
        view4.setOnTouchListener(this);
        view5.setOnTouchListener(this);
        view6.setOnTouchListener(this);
        view7.setOnTouchListener(this);
        view8.setOnTouchListener(this);
        // Связываем кнопку громкости с нашим приложением
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //Создаем soundPool.
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        //устанавливаем call-back функцию, которая вызывается по
        //завершению загрузки файла в память
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
                Log.e("Test", "sampleId="+sampleId+" status="+status);
            }
        });

        //Загружаем звуки в память
        soundID = soundPool.load(this, R.raw.explosion, 1);
    }

    protected void onDestroy() {
        //stop service and stop music
        stopService(new Intent(MainActivity.this, SoundService.class));
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Getting the user sound settings
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            float actualVolume = (float) audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = actualVolume / maxVolume;
            // Is the sound loaded already?


            if (loaded) {
                num=num+1;
                soundPool.play(soundID, volume, volume, 1, 0, 1f);
            }
        }
        return false;
    }
}