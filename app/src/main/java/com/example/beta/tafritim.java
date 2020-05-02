package com.example.beta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.beta.FBref.refMenu;
import static com.example.beta.FBref.refSentence;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * an activity that presents the menus.
 */
public class tafritim extends AppCompatActivity {
    TextView tvNumOfWeek, tvSentence;
    ListView lvMenu;

    ArrayList<String> stringList= new ArrayList<String>();
    ArrayAdapter<String> adp;

    String day="Sunday", week;
    int wk=1, wkmax=1;

    SeekBar seekBardays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tafritim);

        tvNumOfWeek=(TextView)findViewById(R.id.tvNumOfWeek);
        tvSentence=(TextView)findViewById(R.id.tvSentence);

        lvMenu=(ListView) findViewById(R.id.lvMenu);

        adp=new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,stringList);
        lvMenu.setAdapter(adp);

        seekBardays=(SeekBar) findViewById(R.id.seekBar);

        week=Integer.toString(wk);
        /**
         * this function checks how many weeks are in the database in order to know the maximal week
         */
        refMenu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    wkmax=Integer.parseInt(ds.getKey(), (int) ds.getChildrenCount());
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        MainTafrit();

    }

    /**
     * this function is called in order to set the appropriate menu.
     * the default option (when called from the oncreate) is the first week and Sunday
     */
    public void MainTafrit (){
        DatabaseReference refDay = refMenu.child(week).child(day);

        // Read from the database
        refDay.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                stringList.clear();
                for (DataSnapshot data : ds.getChildren()){
                    String tmp=data.getValue(String.class);
                    stringList.add(tmp);

                }
                adp=new ArrayAdapter<String>(tafritim.this,R.layout.my_lv,stringList);
                lvMenu.setAdapter(adp);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        seekBardays.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress){
                    case 3: day="Sunday";break;
                    case 2: day="Monday and Tuesday";break;
                    case 1: day="Wednesday and Thursday";break;
                    case 0: day="Friday and Saturday";break;
                }
                DatabaseReference refDay = refMenu.child(week).child(day);
                // Read from the database
                refDay.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot ds) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        stringList.clear();
                        for (DataSnapshot data : ds.getChildren()){
                            String tmp=data.getValue(String.class);
                            stringList.add(tmp);

                        }
                        adp=new ArrayAdapter<String>(tafritim.this,R.layout.my_lv,stringList);
                        lvMenu.setAdapter(adp);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * this function changes the screen into the next week's menu and information (unless it's the last one)
     * @param view
     */
    public void NextWeek(View view) {
        if (wk<wkmax){
            wk++;
            week = Integer.toString(wk);
            tvNumOfWeek.setText(week);
            refSentence.child(week).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String tmp=dataSnapshot.getValue(String.class);
                    tvSentence.setText(tmp);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });
            MainTafrit();
        }
        else{
            Toast.makeText(this, "this is the last week", Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * this function changes the screen into the previous week's menu and information (unless it's the first one)
     * @param view
     */
    public void PreviousWeek(View view) {
        if (wk>1) {
            wk--;
            week = Integer.toString(wk);
            tvNumOfWeek.setText(week);
            refSentence.child(week).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                        String tmp=dataSnapshot.getValue(String.class);
                        tvSentence.setText(tmp);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });
            MainTafrit();
        }
        else{
            Toast.makeText(this, "this is the first week", Toast.LENGTH_SHORT).show();
        }
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
        if(st.equals("מתכונים")){
            Intent a=new Intent(this, recipes.class);
            startActivity(a);
            finish();
        }
        else {
            if (st.equals("פרטי הסדנה")) {
                Intent  a = new Intent(this, sessions.class);
                startActivity(a);
                finish();
            }
            else {
                if (st.equals("פרופיל אישי")) {
                    Intent    a = new Intent(this, Settings.class);
                    startActivity(a);
                    finish();
                }
                else {
                    if (st.equals("תוספי תזונה")) {
                        Intent   a = new Intent(this, tosafim.class);
                        startActivity(a);
                        finish();
                    }
                    else {
                        if (st.equals("תחליפים לצמחוניים וטבעוניים")){
                            Intent  a = new Intent(this, Substitutes.class);
                            startActivity(a);
                            finish();}

                        else
                        if (st.equals("אודות")) {
                            Intent   a = new Intent(this, Credits.class);
                            startActivity(a);
                            finish();
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);

    }
}
