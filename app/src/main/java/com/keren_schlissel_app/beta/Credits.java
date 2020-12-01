package com.keren_schlissel_app.beta;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Nitzan Dromi
 * an activity that presents the credits for the application author and information about the manager of the sessions
 */

public class Credits extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
    }

    /**
     * this function is called if the user presses the "back" button on his device.
     * it sends him back to the Main Screen activity.
     */
    @Override
    public void onBackPressed() {
        Intent a88=new Intent(this, Main_Screen.class);
        startActivity(a88);
    }

    /**
     * this function creates the menu options - the menu - main.xml
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * this function gets the user's choice from the menu and sends him to the appropriate activity (based on his choice...)
     * @param item8
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item8) {
        String st8=item8.getTitle().toString();
        Intent a8=new Intent();
        if(st8.equals("מסך ראשי"))
            a8=new Intent(this, Main_Screen.class);
        if (st8.equals("פרטי הסדנה"))
            a8 = new Intent(this, sessions.class);
        if (st8.equals("תפריט"))
            a8 = new Intent(this, tafritim.class);
        if (st8.equals("מתכונים"))
            a8 = new Intent(this, recipes.class);
        if (st8.equals("תוספי תזונה"))
            a8 = new Intent(this, tosafim.class);
        if (st8.equals("תחליפים לצמחוניים וטבעוניים"))
            a8 = new Intent(this, Substitutes.class);
        if (st8.equals("אודות"))
            a8 = new Intent(this, Credits.class);
        if (st8.equals("פרופיל אישי"))
            a8 = new Intent(this, Settings.class);

        startActivity(a8);
        finish();
        return super.onOptionsItemSelected(item8);
    }

}
