package com.example.beta;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.beta.FBref.refAuth;
import static com.example.beta.FBref.refUsers;

public class sessions extends AppCompatActivity {
    TextView tvPlaces;
    String uidsess;
    User userplace, usersess;
    ListView lvParticipates;

    ArrayList<String> stringListSession= new ArrayList<String>();
    ArrayAdapter<String> adpsession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);
        tvPlaces=(TextView)findViewById(R.id.tvPlaces);
        lvParticipates=(ListView)findViewById(R.id.lvParticipates);

        adpsession=new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,stringListSession);
        lvParticipates.setAdapter(adpsession);

        FirebaseUser fbuser = refAuth.getCurrentUser();
        uidsess = fbuser.getUid();
        Query querysession = refUsers.orderByChild("uid").equalTo(uidsess);
        querysession.addListenerForSingleValueEvent(VELplaceinfo);
    }

    /**
     * this function gets the user's session information
     */
    com.google.firebase.database.ValueEventListener VELplaceinfo = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dSSessions) {
            if (dSSessions.exists()) {
                for(DataSnapshot dataSessions : dSSessions.getChildren()) {
                    usersess = dataSessions.getValue(User.class);
                    tvPlaces.setText("your sessions are in "+usersess.getPlaces());
                    Query queryParticipates = refUsers.orderByChild("places").equalTo(usersess.getPlaces());
                    queryParticipates.addListenerForSingleValueEvent(VELPARTICIPATES);
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    /**
     * this function sets in a listView all of the participates (and their phone number) in the user's session
     */
    com.google.firebase.database.ValueEventListener VELPARTICIPATES = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot data_snapshot) {
            if (data_snapshot.exists()) {
                stringListSession.clear();
                for(DataSnapshot dataplace : data_snapshot.getChildren()) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                        userplace=dataplace.getValue(User.class);
                        String tmp= userplace.getName()+" "+userplace.getLastName()+" - "+userplace.getPhone();
                        stringListSession.add(tmp);

                    }
                    adpsession=new ArrayAdapter<String>(sessions.this,R.layout.support_simple_spinner_dropdown_item,stringListSession);
                    lvParticipates.setAdapter(adpsession);
                }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    /**
     * this function is called if the user presses the "back" button on his device.
     * it sends him back to the Main Screen activity.
     */
    @Override
    public void onBackPressed() {
        Intent a22=new Intent(this, Main_Screen.class);
        startActivity(a22);
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
     * @param item2
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item2) {
        String st2=item2.getTitle().toString();
        Intent a2=new Intent();
        if(st2.equals("מסך ראשי"))
            a2=new Intent(this, Main_Screen.class);
        if (st2.equals("פרטי הסדנה"))
            a2 = new Intent(this, sessions.class);
        if (st2.equals("תפריט"))
            a2 = new Intent(this, tafritim.class);
        if (st2.equals("מתכונים"))
            a2 = new Intent(this, recipes.class);
        if (st2.equals("תוספי תזונה"))
            a2 = new Intent(this, tosafim.class);
        if (st2.equals("תחליפים לצמחוניים וטבעוניים"))
            a2 = new Intent(this, Substitutes.class);
        if (st2.equals("אודות"))
            a2 = new Intent(this, Credits.class);
        if (st2.equals("פרופיל אישי"))
            a2 = new Intent(this, Settings.class);

        startActivity(a2);
        finish();
        return super.onOptionsItemSelected(item2);
    }

}
