package com.example.beta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Credits extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String st=item.getTitle().toString();
        if(st.equals("recipes")){
            Intent a=new Intent(this, recipes.class);
            startActivity(a);
        }
        if(st.equals("menu")){
            Intent a=new Intent(this, tafritim.class);
            startActivity(a);
        }
        if(st.equals("settings")){
            Intent a=new Intent(this, Settings.class);
            startActivity(a);
        }
        return super.onOptionsItemSelected(item);
    }

}
