package com.example.beta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent t = new Intent(MainActivity.this, Register_Login.class);
                startActivity(t);
                finish();
            }
        }, 5000);


    }
}
