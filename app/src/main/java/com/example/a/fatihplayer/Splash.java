package com.example.a.fatihplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by elfay on 10/22/2017.
 */

public class Splash extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);



        Timer a = new Timer();
        a.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this,MusicActivity.class));
            }
        },3000);

    }
}