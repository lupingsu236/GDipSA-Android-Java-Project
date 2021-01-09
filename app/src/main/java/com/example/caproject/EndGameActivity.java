package com.example.caproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

import java.io.IOException;

public class EndGameActivity extends AppCompatActivity implements Top3DialogFragment.ITop3DialogListener{

    Button homeBtn;
    Button leaderboardBtn;
    AnimationDrawable popperAnimation;
    SharedPreferences sharedPref;
    String name = "Placeholder";
    String gameDifficultyString = "";
    long millis;
    long firstTime;
    long secondTime;
    long thirdTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endgame);
        Context context = getApplicationContext();

        int gameDifficulty = getIntent().getIntExtra("gameMode", 0);

        switch(gameDifficulty) {
            case 6: gameDifficultyString = "Easy"; break;
            case 8: gameDifficultyString = "Normal"; break;
            case 10: gameDifficultyString = "Hard"; break;
        }
        //Get data from previous activity
        millis = getIntent().getLongExtra("millis", 0);
        int minutes = getIntent().getIntExtra("minutes", 0);
        int seconds = getIntent().getIntExtra("seconds", 0);

        //create alert dialog
        DialogFragment alert = new Top3DialogFragment();
        alert.setCancelable(false);


        //Set shared preferences to save scores
        sharedPref = context.getSharedPreferences(gameDifficultyString, Context.MODE_PRIVATE);
        firstTime = sharedPref.getLong("firstTime", 0);
        secondTime = sharedPref.getLong("secondTime", 0);
        thirdTime = sharedPref.getLong("thirdTime", 0);

        //Trigger dialog if requirements are met
        if (millis < firstTime || (firstTime == 0)) {
            alert.show(getSupportFragmentManager(), "Top3DialogFragment");
        }
        else if (millis < secondTime || (secondTime == 0)) {
            alert.show(getSupportFragmentManager(), "Top3DialogFragment");
        }
        else if (millis < thirdTime || (thirdTime == 0)) {
            alert.show(getSupportFragmentManager(), "Top3DialogFragment");
        }

        TextView timerMessageView = findViewById(R.id.timerMessageView);
        timerMessageView.setText(String.format("You only took %dmin %dseconds!", minutes, seconds));

        ImageView popper = findViewById(R.id.popperAnimation);
        loadGif(popper);

        MediaPlayer clap = MediaPlayer.create(this,R.raw.applause8);
        clap.start();

        homeBtn = (Button) findViewById(R.id.returnHomeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(view.getContext(), StartActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        leaderboardBtn = (Button) findViewById(R.id.leaderboardBtn);
        leaderboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(view.getContext(), LeaderboardActivity.class);
                intent.putExtra("difficulty", gameDifficultyString);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(EndGameActivity.this, StartActivity.class);
        startActivity(intent);
    }

    public void saveValues() {
        Context context = getApplicationContext();
        sharedPref = context.getSharedPreferences(gameDifficultyString, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (millis < firstTime || (firstTime == 0)) {
            editor.putString("thirdName", sharedPref.getString("secondName", "--"));
            editor.putLong("thirdTime", sharedPref.getLong("secondTime", 0));
            editor.putString("secondName", sharedPref.getString("firstName", "--"));
            editor.putLong("secondTime", sharedPref.getLong("firstTime", 0));
            editor.putString("firstName", name);
            editor.putLong("firstTime", millis);
        }
        else if (millis < secondTime || (secondTime == 0)) {
            editor.putString("thirdName", sharedPref.getString("secondName", "--"));
            editor.putLong("thirdTime", sharedPref.getLong("secondTime", 0));
            editor.putString("secondName", name);
            editor.putLong("secondTime", millis);
        }
        else if (millis < thirdTime || (thirdTime == 0)) {
            editor.putString("thirdName", name);
            editor.putLong("thirdTime", millis);
        }
        editor.commit();
    }

    public void loadGif(ImageView iv) {
        try {
            ImageDecoder.Source source =
                    ImageDecoder.createSource(getResources(), R.drawable.popper);

            Drawable drawable = ImageDecoder.decodeDrawable(source);
            iv.setImageDrawable(drawable);

            if (drawable instanceof AnimatedImageDrawable) {
                ((AnimatedImageDrawable) drawable).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "IOException: \n" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConfirmClick(String nameInput) {
        name = nameInput;
    }
}
