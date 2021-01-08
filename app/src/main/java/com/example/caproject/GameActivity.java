package com.example.caproject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements PauseDialogFragment.IPauseDialogListener {

    int numberOfPictures;
    ArrayList<String> chosenImages;
    Bitmap[] imageId;

    int flipCount = 0;
    int matches = 0;
    int prev_position = 0;
    int[] placeholderImg;
    int[] shuffledPosition;
    boolean[] flippedState;
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

        //Get information bundle from previous activity
        Bundle bundle = getIntent().getExtras();
        //Number of unique pictures
        numberOfPictures = bundle.getInt("gameDifficulty");
        //ArrayList<String> of image urls
        chosenImages = bundle.getStringArrayList("urlSelectedtoSend");
        //Initialize the array to hold the images
        imageId = new Bitmap[numberOfPictures*2];
        //Insert images into the array
        int index_counter = 0;
        for(String imgurl : chosenImages) {
            try {
                URL url= new URL(imgurl);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                imageId[index_counter] = image;
                index_counter += 1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Initialize all the other arrays
        initializeArrays();
        //shuffle the index-array for random image placement
        shuffledPosition = shuffle(shuffledPosition);
        //Music load
        MediaPlayer correctSound = MediaPlayer.create(this,R.raw.trimmedcorrect);
        //Set match view
        TextView matchCountTextView = findViewById(R.id.matchCountTextView);
        matchCountTextView.setText(String.format("Matches: %d/%d", 0, numberOfPictures));
        //Set timer
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

        //Set listener to pause button
        pauseBtn = findViewById(R.id.pauseBtn);
        pauseBtn.setOnClickListener(v -> pauseGame());

        //Set images to grid
        ImageAdapter adapter = new ImageAdapter(GameActivity.this, placeholderImg);
        GridView grid = findViewById(R.id.gameGrid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //start game, timer and music
                if (!gameStart) {
                    gameStart = true;
                    pauseBtn.setVisibility(View.VISIBLE);
                    startTime = System.currentTimeMillis();
                    timerHandler.post(timerRunnable);
                    Intent intent = new Intent(GameActivity.this, MusicService.class);
                    intent.setAction("START_BG_MUSIC");
                    startService(intent);
                }

                //only allow flipping if unflipped
                if (!flippedState[position]) {
                    if (flipCount == 0) {
                        flip(view, position);
                        flipCount = 1;
                        //save previous position for flipback
                        prev_position = position;
                    }
                    else if (flipCount == 1) {
                        flip(view, position);
                        boolean match = checkMatch(prev_position, position);
                        if (match) {
                            matches += 1;
                            correctSound.start();
                            flipCount = 0;
                            matchCountTextView.setText(String.format("Matches: %d/%d", matches, numberOfPictures));
                        }
                        else {
                            flipCount = 0;
                            flipback(parent, prev_position, position);
                        }
                    }
                }
                if (matches == numberOfPictures) {
                    gameStart = false;

                    //stop timer and music
                    timerHandler.removeCallbacks(timerRunnable);
                    Intent stopIntent = new Intent(GameActivity.this, MusicService.class);
                    stopService(stopIntent);

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

    public void initializeArrays() {
        //Array to pass to image adapter to initialize placeholder image
        placeholderImg = new int[numberOfPictures*2];
        //Array of paired-indexes to be shuffled
        shuffledPosition = new int[numberOfPictures*2];
        //Array to hold flip-state of the grid positions
        flippedState = new boolean[numberOfPictures*2];
        for(int i = 0; i < 2; i++) {
            //Doubled cause the images are in pairs
            for(int j = 0; j < numberOfPictures; j++) {
                //Set all pictures to the default placeholder
                placeholderImg[i*numberOfPictures + j] = R.drawable.catqnmark2;
                //Create an index-array of all images
                shuffledPosition[i*numberOfPictures + j] = j;
                //Initialize all flipped states to false
                flippedState[i*numberOfPictures + j] = false;
            }
        }
    }

    public void flip(View view, int position) {
        ImageView imgview = view.findViewById(R.id.grid_image);
        //instead of shuffling the bitmaps, we shuffle an array containing indexes
        //simply call the image at the shuffled index
        imgview.setImageBitmap(imageId[shuffledPosition[position]]);
        flippedState[position] = true;
    }

    Handler handler = new Handler();

    public void flipback(AdapterView<?> parent, int prev_position, int position) {
        ImageView imgview1 = parent.getChildAt(prev_position).findViewById(R.id.grid_image);
        imgview1.postDelayed(() -> {
            imgview1.setImageResource(placeholderImg[position]);
            flippedState[prev_position] = false;
        }, 500);

        ImageView imgview2 = parent.getChildAt(position).findViewById(R.id.grid_image);
        imgview2.postDelayed(() -> {
            imgview2.setImageResource(placeholderImg[position]);
            flippedState[position] = false;
        }, 500);
    }

    public boolean checkMatch(int prev_position, int position) {
        //shuffled indexes are paired
        if(shuffledPosition[prev_position] == shuffledPosition[position]) {
            return true;
        } else
            return false;
    }

    public void pauseGame() {
        //show dialog, update time elapsed so far, pause music
        timerHandler.removeCallbacks(timerRunnable);
        timeElapsed = timeElapsed + (System.currentTimeMillis() - startTime);

        Intent intent = new Intent(GameActivity.this, MusicService.class);
        intent.setAction("PAUSE_BG_MUSIC");
        startService(intent);

        DialogFragment dialog = new PauseDialogFragment();
        dialog.show(getSupportFragmentManager(), "PauseDialogFragment");
    }

    @Override
    public void onResumeGameClick(DialogFragment dialog) {
        //restart timer
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        //play music
        Intent intent = new Intent(GameActivity.this, MusicService.class);
        intent.setAction("RESUME_BG_MUSIC");
        startService(intent);

    }

    @Override
    public void onEndGameClick(DialogFragment dialog) {
        //stop music
        Intent stopIntent = new Intent(GameActivity.this, MusicService.class);
        stopService(stopIntent);

        //return to start activity
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseGame();
    }

    //override back button so that game is paused instead of going to previous activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            pauseGame();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void goToEndPage(){
        //send timing and start EndActivity
        long millis = (System.currentTimeMillis() - startTime) + timeElapsed;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        Intent intent = new Intent(this, EndGameActivity.class);
        intent.putExtra("minutes", minutes);
        intent.putExtra("seconds", seconds);
        startActivity(intent);
    }
}
