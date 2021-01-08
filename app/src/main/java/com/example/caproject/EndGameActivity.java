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

import org.w3c.dom.Text;

import java.io.IOException;

public class EndGameActivity extends AppCompatActivity {

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

        //Build alert dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter your name for the leaderboard:");
        final EditText nameInput = new EditText(this);
        alert.setView(nameInput);
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                name = nameInput.getText().toString();
                return;
            }
        });

        //Set shared preferences to save scores
        sharedPref = context.getSharedPreferences(gameDifficultyString, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        firstTime = sharedPref.getLong("firstTime", 0);
        secondTime = sharedPref.getLong("secondTime", 0);
        thirdTime = sharedPref.getLong("thirdTime", 0);

        //Trigger dialog if requirements are met
        if (millis < firstTime || (firstTime == 0)) {
            editor.putLong("firstTime", millis);
            alert.show();
        }
        else if (millis < secondTime || (secondTime == 0)) {
            editor.putLong("secondTime", millis);
            alert.show();
        }
        else if (millis < thirdTime || (thirdTime == 0)) {
            editor.putLong("thirdTime", millis);
            alert.show();
        }
        editor.commit();

        TextView timerMessageView = (TextView) findViewById(R.id.timerMessageView);
        timerMessageView.setText(String.format("You only took %dmin %dseconds!", minutes, seconds));

        ImageView popper = (ImageView) findViewById(R.id.popperAnimation);
        loadGif(popper);

        MediaPlayer clap = MediaPlayer.create(this,R.raw.applause8);
        clap.start();

        homeBtn = (Button) findViewById(R.id.returnHomeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setName();
                finish();
                Intent intent = new Intent(view.getContext(), StartActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        leaderboardBtn = (Button) findViewById(R.id.leaderboardBtn);
        leaderboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setName();
                finish();
                Intent intent = new Intent(view.getContext(), LeaderboardActivity.class);
                intent.putExtra("difficulty", gameDifficultyString);
                view.getContext().startActivity(intent);
            }
        });
    }

    public void setName() {
        Context context = getApplicationContext();
        sharedPref = context.getSharedPreferences(gameDifficultyString, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (millis < firstTime || (firstTime == 0)) {
            editor.putString("firstName", name);
        }
        else if (millis < secondTime || (secondTime == 0)) {
            editor.putString("secondName", name);
        }
        else if (millis < thirdTime || (thirdTime == 0)) {
            editor.putString("thirdName", name);
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
}
