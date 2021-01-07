package com.example.caproject;

import android.content.Intent;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.IOException;

public class EndGameActivity extends AppCompatActivity {

    Button homeBtn;
    AnimationDrawable popperAnimation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endgame);

        ImageView popper = (ImageView) findViewById(R.id.popperAnimation);
        loadGif(popper);

        MediaPlayer ring= MediaPlayer.create(this,R.raw.applause8);
        ring.start();

        int minutes = getIntent().getIntExtra("minutes", 0);
        int seconds = getIntent().getIntExtra("seconds", 0);

        TextView timerMessageView = (TextView) findViewById(R.id.timerMessageView);
        timerMessageView.setText(String.format("You only took %dmin %dseconds!", minutes, seconds));

        homeBtn = (Button) findViewById(R.id.returnHomeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(view.getContext(), StartActivity.class);
                view.getContext().startActivity(intent);
            }
        });
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
