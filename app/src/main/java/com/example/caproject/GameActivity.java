package com.example.caproject;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements PauseDialogFragment.IPauseDialogListener {
    int[] imageId = {
            R.drawable.afraid,
            R.drawable.full,
            R.drawable.hug,
            R.drawable.laugh,
            R.drawable.no_way,
            R.drawable.peep,
            R.drawable.snore,
            R.drawable.stop,
            R.drawable.tired,
            R.drawable.what,
            R.drawable.afraid,
            R.drawable.full,
            R.drawable.hug,
            R.drawable.laugh,
            R.drawable.no_way,
            R.drawable.peep,
            R.drawable.snore,
            R.drawable.stop,
            R.drawable.tired,
            R.drawable.what
    };
    int flipCount = 0;
    int matches = 0;
    int prev_position = 0;
    int numberOfPictures = 10;
    int[] placeholderImg = new int[numberOfPictures*2];
    int[] position = new int[numberOfPictures*2];
    int[] shuffledImages = new int[numberOfPictures*2];
    boolean[] flippedState = new boolean[numberOfPictures*2];
    boolean gameStart = false;
    long startTime = 0;
    long timeElapsed = 0;
    ImageButton pauseBtn;
    Handler timerHandler;
    Runnable timerRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        for(int i = 0; i < 2; i++) {
            //Doubled cause the images are in pairs
            for(int j = 0; j < numberOfPictures; j++) {
                //Set all pictures to the default placeholder
                placeholderImg[i*numberOfPictures + j] = R.drawable.catqnmark2;
                //Create an index-array of all images
                position[i*numberOfPictures + j] = i*numberOfPictures + j;
                //Initialize all flipped states to false
                flippedState[i*numberOfPictures + j] = false;
            }
        }
        //shuffle the index-array for random image placement
        position = shuffle(position);
        //shuffle images using the shuffled index-array
        for(int i = 0; i < imageId.length; i++) {
            shuffledImages[position[i]] = imageId[i];
        }

        //Music load
        MediaPlayer correctSound = MediaPlayer.create(this,R.raw.trimmedcorrect);

        TextView matchCountTextView = findViewById(R.id.matchCountTextView);
        TextView timerTextView = findViewById(R.id.timerTextView);

        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                //timeElapsed stores previous time recorded prior to pause being clicked
                //timeElapsed = 0 if pause has never been triggered
                long millis = (System.currentTimeMillis() - startTime) + timeElapsed;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                timerTextView.setText(String.format("%d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 500);
            }
        };

        //Pause game manually
        pauseBtn = findViewById(R.id.pauseBtn);
        pauseBtn.setOnClickListener(v -> pauseGame());

        //Set images to grid
        ImageAdapter adapter = new ImageAdapter(GameActivity.this, placeholderImg);
        GridView grid=(GridView)findViewById(R.id.gameGrid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //start game and timer
                if (!gameStart) {
                    gameStart = true;
                    pauseBtn.setVisibility(View.VISIBLE);
                    startTime = System.currentTimeMillis();
                    timerHandler.post(timerRunnable);
                }
                //only allow flipping if unflipped
                if (flippedState[position] == false) {
                    if (flipCount == 0) {
                        flip(view, position);
                        flipCount = 1;
                        prev_position = position;
                    }
                    else if (flipCount == 1) {
                        flip(view, position);
                        boolean match = checkMatch(prev_position, position);
                        if (match) {
                            matches += 1;
                            correctSound.start();
                            flipCount = 0;
                            matchCountTextView.setText(String.format("Matches: %d/10", matches));
                        }
                        else {
                            flipCount = 0;
                            flipback(parent, prev_position, position);
                        }
                    }
                }
                if (matches == numberOfPictures) {
                    gameStart = false;
                    timerHandler.removeCallbacks(timerRunnable);
                    goToEndPage();
                }
            }
        });


    }

    public int[] shuffle(int[] position) {
        int[] shuffled = position;
        Random rand = new Random();
        for(int i = 0; i < shuffled.length; i++) {
            int randomIndexToSwap = rand.nextInt(shuffled.length);
            int temp = shuffled[randomIndexToSwap];
            shuffled[randomIndexToSwap] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    public void flip(View view, int position) {
        ImageView imgview = (ImageView) view.findViewById(R.id.grid_image);
        imgview.setImageResource(shuffledImages[position]);
        flippedState[position] = true;
    }

    Handler handler = new Handler();

    public void flipback(AdapterView<?> parent, int prev_position, int position) {
        ImageView imgview1 = (ImageView) parent.getChildAt(prev_position).findViewById(R.id.grid_image);
        imgview1.postDelayed(new Runnable() {
            @Override
            public void run() {
                imgview1.setImageResource(placeholderImg[position]);
                flippedState[prev_position] = false;
            }
        }, 500);

        ImageView imgview2 = (ImageView) parent.getChildAt(position).findViewById(R.id.grid_image);
        imgview2.postDelayed(new Runnable() {
            @Override
            public void run() {
                imgview2.setImageResource(placeholderImg[position]);
                flippedState[position] = false;
            }
        }, 500);
    }

    public boolean checkMatch(int prev_position, int position) {
        if(shuffledImages[prev_position] == shuffledImages[position]) {return true;}
        else return false;
    }

    public void pauseGame() {
        //show dialog and update time elapsed so far
        timerHandler.removeCallbacks(timerRunnable);
        timeElapsed = timeElapsed + (System.currentTimeMillis() - startTime);
        DialogFragment dialog = new PauseDialogFragment();
       // dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "PauseDialogFragment");

    }

    @Override
    public void onDialoguePositiveClick(DialogFragment dialog) {
        //restart timer
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseGame();
    }

    public void goToEndPage(){
        Intent intent = new Intent(this, EndGameActivity.class);
        startActivity(intent);
    }
}
