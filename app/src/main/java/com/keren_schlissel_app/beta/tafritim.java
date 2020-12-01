package com.keren_schlissel_app.beta;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.keren_schlissel_app.beta.FBref.refMenu;
import static com.keren_schlissel_app.beta.FBref.refSentence;

/**
 * an activity that presents the menus.
 */
public class tafritim extends AppCompatActivity {
    TextView tvNumOfWeek, tvSentence;
    ListView lvMenu;

    ArrayList<String> stringList= new ArrayList<String>();
    ArrayAdapter<String> adp;

    String day="Sunday", week;
    int wk=1;
        int wkmax=1;

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

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //wkmax=Integer.parseInt(ds.getKey(), (int) ds.getChildrenCount());
                    wkmax=(int) ds.getChildrenCount();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        MainTafrit();

    }

    /**
     * this function is called if the user presses the "back" button on his device.
     * it sends him back to the Main Screen activity.
     */
    @Override
    public void onBackPressed() {
        Intent a55=new Intent(this, Main_Screen.class);
        startActivity(a55);
    }

    /**
     * this function is called in order to set the appropriate menu.
     * the default option (when called from the onCreate) is the first week and Sunday
     */
    public void MainTafrit (){
        DatabaseReference refDay = refMenu.child(week).child(day);

        refDay.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
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
                refDay.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot ds) {
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

    public void Sunday(View view) {
        seekBardays.setProgress(3);
    }

    public void MondayTuesday(View view) {
        seekBardays.setProgress(2);
    }

    public void WednesdayThursday(View view) {
        seekBardays.setProgress(1);
    }

    public void FridaySaturday(View view) {
        seekBardays.setProgress(0);
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

                    String tmp=dataSnapshot.getValue(String.class);
                    tvSentence.setText(tmp);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
            MainTafrit();
        }
        else{
            Toast.makeText(this, "זהו השבוע האחרון במאגר", Toast.LENGTH_SHORT).show();
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
                        String tmp=dataSnapshot.getValue(String.class);
                        tvSentence.setText(tmp);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
            MainTafrit();
        }
        else{
            Toast.makeText(this, "זהו השבוע הראשון", Toast.LENGTH_SHORT).show();
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
     * @param item5
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item5) {
        String st5=item5.getTitle().toString();
        Intent a5=new Intent();
        if(st5.equals("מסך ראשי"))
            a5=new Intent(this, Main_Screen.class);
        if (st5.equals("פרטי הסדנה"))
            a5 = new Intent(this, sessions.class);
        if (st5.equals("תפריט"))
            a5 = new Intent(this, tafritim.class);
        if (st5.equals("מתכונים"))
            a5 = new Intent(this, recipes.class);
        if (st5.equals("תוספי תזונה"))
            a5 = new Intent(this, tosafim.class);
        if (st5.equals("תחליפים לצמחוניים וטבעוניים"))
            a5 = new Intent(this, Substitutes.class);
        if (st5.equals("אודות"))
            a5 = new Intent(this, Credits.class);
        if (st5.equals("פרופיל אישי"))
            a5 = new Intent(this, Settings.class);

        startActivity(a5);
        finish();
        return super.onOptionsItemSelected(item5);

    }


}
