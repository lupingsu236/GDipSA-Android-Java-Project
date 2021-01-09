package com.example.caproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LeaderboardActivity extends AppCompatActivity {
    //This is important
    Button homeBtn2;
    SharedPreferences sharedPref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Context context = getApplicationContext();

        String difficulty = getIntent().getStringExtra("difficulty");
        sharedPref = context.getSharedPreferences(difficulty, Context.MODE_PRIVATE);
        long firstTime = sharedPref.getLong("firstTime", 0);
        long secondTime = sharedPref.getLong("secondTime", 0);
        long thirdTime = sharedPref.getLong("thirdTime", 0);

        String firstName = sharedPref.getString("firstName", "--");
        String secondName = sharedPref.getString("secondName", "--");
        String thirdName = sharedPref.getString("thirdName", "--");

        long millis = getIntent().getLongExtra("millis", 0);
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        TextView yourScore = (TextView) findViewById(R.id.yourScore);
        yourScore.setText(String.format("Your score is %d min %d sec", minutes, seconds));

        TextView difficultyView = (TextView) findViewById(R.id.difficultyLevel);
        difficultyView.setText("Leaderboard: " + difficulty);

        TextView firstTimeView = (TextView) findViewById(R.id.firstTime);
        firstTimeView.setText(displayAsTime(firstTime));
        TextView secondTimeView = (TextView) findViewById(R.id.secondTime);
        secondTimeView.setText(displayAsTime(secondTime));
        TextView thirdTimeView = (TextView) findViewById(R.id.thirdTime);
        thirdTimeView.setText(displayAsTime(thirdTime));

        TextView firstNameView = (TextView) findViewById(R.id.firstName);
        firstNameView.setText(String.format("1. " + firstName));
        TextView secondNameView = (TextView) findViewById(R.id.secondName);
        secondNameView.setText(String.format("2. " + secondName));
        TextView thirdNameView = (TextView) findViewById(R.id.thirdName);
        thirdNameView.setText(String.format("3. " + thirdName));

        homeBtn2 = (Button) findViewById(R.id.returnHomeBtn2);
        homeBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(view.getContext(), StartActivity.class);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(LeaderboardActivity.this, StartActivity.class);
        startActivity(intent);
    }

    public String displayAsTime(long millis) {
        if (millis == 0) return "--";
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d min %d sec", minutes, seconds);
    }
}
