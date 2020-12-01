package com.keren_schlissel_app.beta;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Nitzan Dromi
 * the first activity. self transfer after 3 seconds
 */
public class MainActivity extends AppCompatActivity {
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer= new Timer();

        /**
         * this function transfer the user(?) to the next activity (register or login activity) after 3 seconds in this activity.
         * without any intervention from the user
         * @param TimerTask
         */
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent t = new Intent(MainActivity.this, Register_Login.class);
                startActivity(t);
                finish();
            }
        }, 3000);


    }
}
