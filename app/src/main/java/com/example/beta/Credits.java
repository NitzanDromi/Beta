package com.example.beta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String st=item.getTitle().toString();
        Intent a=new Intent();
        if(st.equals("מתכונים"))
            a=new Intent(this, recipes.class);
        else {
            if (st.equals("פרטי הסדנה"))
                a = new Intent(this, sessions.class);
            else {
                if (st.equals("תפריט"))
                    a = new Intent(this, tafritim.class);
                else {
                    if (st.equals("תוספי תזונה"))
                        a = new Intent(this, tosafim.class);
                    else {
                        if (st.equals("תחליפים לצמחוניים וטבעוניים"))
                            a = new Intent(this, Substitutes.class);

                        else
                        if (st.equals("פרופיל אישי"))
                            a = new Intent(this, Settings.class);
                    }
                }
            }
        }
        startActivity(a);
        finish();
        return super.onOptionsItemSelected(item);
    }

}
