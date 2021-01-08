package com.example.caproject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;

public class MusicService extends Service {
    MediaPlayer player;

    public MusicService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        player = MediaPlayer.create(this, R.raw.wish_you_were_here);
        player.setLooping(true);
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (action!=null) {
            switch (action) {
                case "START_BG_MUSIC":
                case "RESUME_BG_MUSIC":
                    player.start();
                    break;
                case "PAUSE_BG_MUSIC":
                    player.pause();
                    break;
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (player!=null) {
                player.stop();
                player.reset();
                player.release();
                player = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}