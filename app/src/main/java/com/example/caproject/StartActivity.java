package com.example.caproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartActivity extends AppCompatActivity implements View.OnClickListener
{
    Map<String, Boolean> difficultyBtnClickState = new HashMap<>();
    String difficultyChosen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // all buttons are not clicked when the app is first launched
        difficultyBtnClickState.put("easy", false);
        difficultyBtnClickState.put("normal", false);
        difficultyBtnClickState.put("hard", false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button easyBtn = findViewById(R.id.easyBtn);
        easyBtn.setOnClickListener(this);
        Button normalBtn = findViewById(R.id.normalBtn);
        normalBtn.setOnClickListener(this);
        Button hardBtn = findViewById(R.id.hardBtn);
        hardBtn.setOnClickListener(this);
        Button startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        Button easyBtn = findViewById(R.id.easyBtn);
        Button normalBtn = findViewById(R.id.normalBtn);
        Button hardBtn = findViewById(R.id.hardBtn);

        int id = view.getId();

        if (id == R.id.easyBtn)
        {
            if (difficultyBtnClickState.get("easy"))
            {
                easyBtn.setBackground(getDrawable(R.drawable.difficulty_button_shape));
                difficultyBtnClickState.put("easy", false);
                difficultyChosen = null;
            }
            else
            {
                easyBtn.setBackground(getDrawable(R.drawable.difficulty_button_shape_clicked));
                normalBtn.setBackground(getDrawable(R.drawable.difficulty_button_shape));
                hardBtn.setBackground(getDrawable(R.drawable.difficulty_button_shape));
                difficultyBtnClickState.put("easy", true);
                difficultyBtnClickState.put("normal", false);
                difficultyBtnClickState.put("hard", false);
                difficultyChosen = "easy";
            }
        }
        else if (id == R.id.normalBtn)
        {
            if (difficultyBtnClickState.get("normal"))
            {
                normalBtn.setBackground(getDrawable(R.drawable.difficulty_button_shape));
                difficultyBtnClickState.put("normal", false);
                difficultyChosen = null;
            }
            else
            {
                easyBtn.setBackground(getDrawable(R.drawable.difficulty_button_shape));
                normalBtn.setBackground(getDrawable(R.drawable.difficulty_button_shape_clicked));
                hardBtn.setBackground(getDrawable(R.drawable.difficulty_button_shape));
                difficultyBtnClickState.put("easy", false);
                difficultyBtnClickState.put("normal", true);
                difficultyBtnClickState.put("hard", false);
                difficultyChosen = "normal";
            }
        }
        else if (id == R.id.hardBtn)
        {
            if (difficultyBtnClickState.get("hard"))
            {
                hardBtn.setBackground(getDrawable(R.drawable.difficulty_button_shape));
                difficultyBtnClickState.put("hard", false);
                difficultyChosen = null;
            }
            else
            {
                easyBtn.setBackground(getDrawable(R.drawable.difficulty_button_shape));
                normalBtn.setBackground(getDrawable(R.drawable.difficulty_button_shape));
                hardBtn.setBackground(getDrawable(R.drawable.difficulty_button_shape_clicked));
                difficultyBtnClickState.put("easy", false);
                difficultyBtnClickState.put("normal", false);
                difficultyBtnClickState.put("hard", true);
                difficultyChosen = "hard";
            }
        }

        else if (id == R.id.startBtn)
        {
            if (difficultyChosen == null)
                Toast.makeText(this, "Please select a difficulty before starting",
                        Toast.LENGTH_SHORT).show();
            else
            {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("difficulty", difficultyChosen);
                startActivity(intent);
            }
        }
    }
}