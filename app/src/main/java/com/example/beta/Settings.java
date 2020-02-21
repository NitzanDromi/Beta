package com.example.beta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
        if(st.equals("credits")){
            Intent a=new Intent(this, Credits.class);
            startActivity(a);
        }
        if(st.equals("menu")){
            Intent a=new Intent(this, tafritim.class);
            startActivity(a);
        }
        return super.onOptionsItemSelected(item);
    }
}
