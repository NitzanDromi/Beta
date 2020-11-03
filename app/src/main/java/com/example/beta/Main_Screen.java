package com.example.beta;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.beta.FBref.refAuth;
import static com.example.beta.FBref.refUsers;

public class Main_Screen extends AppCompatActivity {
    ImageView ivProfile, ivSupplements;
    TextView tvSup;
    Boolean female;
    User userInfo;
    String uidUserInfo="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__screen);

        ivProfile=(ImageView)findViewById(R.id.imvProfile);
        ivSupplements=(ImageView)findViewById(R.id.imvSupplements);
        tvSup=(TextView)findViewById(R.id.tvSupplements);

        FirebaseUser fbuser = refAuth.getCurrentUser();
        uidUserInfo = fbuser.getUid();
        Query query = refUsers.orderByChild("uid").equalTo(uidUserInfo);
        query.addListenerForSingleValueEvent(VELUserInfo);
    }

    /**
     * this function reads the necessary information about the user to this activity.
     * (the user's gender)
     */
    com.google.firebase.database.ValueEventListener VELUserInfo = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for (DataSnapshot data : dS.getChildren()) {
                    userInfo = data.getValue(User.class);
                    female = userInfo.getIsFemale();

                    if (female) {
                        ivProfile.setImageResource(R.drawable.women_id_icon);
                        ivSupplements.setImageResource(R.drawable.pregnancy_icon);

                    } else {
                        ivProfile.setImageResource(R.drawable.men_info_icon);
                        ivSupplements.setImageResource(R.drawable.men_sup_icon);
                    }
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    public void moveToTafritim(View view) {
        Intent    a = new Intent(this, tafritim.class);
        startActivity(a);
        finish();
    }

    public void moveToSubstitutes(View view) {
        Intent  t = new Intent(this, Substitutes.class);
        startActivity(t);
        finish();
    }

    public void moveToSession(View view) {
        Intent  g = new Intent(this, sessions.class);
        startActivity(g);
        finish();
    }

    public void moveToCredits(View view) {
        Intent   f = new Intent(this, Credits.class);
        startActivity(f);
        finish();
    }

    public void moveToRecipes(View view) {
        Intent h=new Intent(this, recipes.class);
        startActivity(h);
        finish();
    }

    public void moveToPersonalProfile(View view) {
        Intent  l = new Intent(this, Settings.class);
        startActivity(l);
        finish();
    }

    public void moveToSupplements(View view) {
        Intent   j = new Intent(this, tosafim.class);
        startActivity(j);
        finish();
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
        if(st.equals("מסך ראשי"))
            a=new Intent(this, Main_Screen.class);
        if (st.equals("פרטי הסדנה"))
            a = new Intent(this, sessions.class);
        if (st.equals("תפריט"))
            a = new Intent(this, tafritim.class);
        if (st.equals("מתכונים"))
            a = new Intent(this, recipes.class);
        if (st.equals("תוספי תזונה"))
            a = new Intent(this, tosafim.class);
        if (st.equals("תחליפים לצמחוניים וטבעוניים"))
            a = new Intent(this, Substitutes.class);
        if (st.equals("אודות"))
            a = new Intent(this, Credits.class);
        if (st.equals("פרופיל אישי"))
            a = new Intent(this, Settings.class);

        startActivity(a);
        finish();
        return super.onOptionsItemSelected(item);
    }
}