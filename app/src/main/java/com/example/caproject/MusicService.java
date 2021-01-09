package com.example.caproject;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    MediaPlayer player;

    public MusicService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action!=null) {
            switch (action) {
                case "START_BG_MUSIC":
                    player = MediaPlayer.create(this, R.raw.wish_you_were_here);
                    player.setLooping(true);
                    player.setOnPreparedListener(mp -> player.start());
                    break;
                case "RESUME_BG_MUSIC":
                    if(player!=null) {
                        player.start();
                    }
                    break;
                case "PAUSE_BG_MUSIC":
                    if (player!=null) {
                        player.pause();
                    }
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