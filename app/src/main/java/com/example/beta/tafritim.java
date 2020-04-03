package com.example.beta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import static com.example.beta.FBref.refPlaces;
import static com.example.beta.FBref.refWeek;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class tafritim extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView lvMenu;
    ArrayList<String> stringList= new ArrayList<String>();
    ArrayAdapter<String> adp;
    String day="Sunday";
    String week;
    int wk=1;
    int wkmax=1;
    SeekBar seekBardays;
    TextView tvNumOfWeek;
    List<String> list = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tafritim);

        tvNumOfWeek=(TextView)findViewById(R.id.tvNumOfWeek);

        lvMenu=(ListView) findViewById(R.id.lvMenu);

        lvMenu.setOnItemClickListener(this);
        lvMenu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adp=new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,stringList);
        lvMenu.setAdapter(adp);

        seekBardays=(SeekBar) findViewById(R.id.seekBar);
        //int seekValue = seekBardays.getProgress();

        week=Integer.toString(wk);
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
                    //  default:day="Sunday";
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
            finish();
        }
        if(st.equals("credits")){
            Intent a=new Intent(this, Credits.class);
            startActivity(a);
            finish();
        }
        if(st.equals("settings")){
            Intent si = new Intent(tafritim.this,Settings.class);
            startActivity(si);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void NextWeek(View view) {

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

        if (wk<wkmax){
            wk++;
            week = Integer.toString(wk);
            tvNumOfWeek.setText(week);
            refWeek.setValue(wk);
            MainTafrit();
        }
        else{
            Toast.makeText(this, "this is the last week", Toast.LENGTH_SHORT).show();
        }

    }

    public void PreviousWeek(View view) {
        if (wk>1) {
            wk--;
            week = Integer.toString(wk);
            tvNumOfWeek.setText(week);
            refWeek.setValue(wk);
            MainTafrit();
        }
        else{
            Toast.makeText(this, "this is the first week", Toast.LENGTH_SHORT).show();
        }
    }
}
