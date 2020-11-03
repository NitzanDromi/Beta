package com.example.beta;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.beta.FBref.refAuth;
import static com.example.beta.FBref.refUsers;
import static com.example.beta.FBref.refdinner;
import static com.example.beta.FBref.reflunch;

/**
 * @author Nitzan Dromi
 * an activity in which the user needs to choose a recipe
 */
public class recipes extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Spinner spLunch, spDinner;
    TextView spinner_lunch, spinner_dinner;
    Button btnLunch, btnDinner;
    String strlunch="",strdinner="";
    Recipe recipe, recipe2;

    List<String> lst_lunch = new ArrayList<String>();
    List<String> lst_dinner = new ArrayList<String>();


    String uid;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        spLunch= (Spinner) findViewById(R.id.spLunch);
        spDinner= (Spinner) findViewById(R.id.spDinner);

        btnDinner=(Button)findViewById(R.id.btnDinner);
        btnLunch=(Button)findViewById(R.id.btnLunch);


        spinner_lunch=(TextView)findViewById(R.id.tvChooseLunch);
        spinner_dinner=(TextView)findViewById(R.id.tvChooseDinner);


        lst_lunch.clear();
       // final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item, lst_lunch);
                final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(recipes.this, R.layout.spinner_lunch, lst_lunch);
        spLunch.setAdapter(arrayAdapter);
        spLunch.setOnItemSelectedListener(this);

       // arrayAdapter1=new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item,lst_dinner);

        lst_dinner.clear();
       // final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item, lst_dinner);
              final  ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(recipes.this, R.layout.spinner_dinner,lst_dinner);

        spDinner.setAdapter(arrayAdapter1);
        spDinner.setOnItemSelectedListener(this);

        /**
         * this function uploads the information from the firebase tree - recipes ->(branch) lunch - to a spinner
         * using the reference - reflunch and a Value event listener.
         */
        reflunch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                lst_lunch.clear();
                for (DataSnapshot data : ds.getChildren()) {
                    String recipesname = (String) data.child("name").getValue();
                    lst_lunch.add(recipesname);
                }
               // ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item, lst_lunch);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(recipes.this, R.layout.spinner_lunch,lst_lunch);
              //  arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                arrayAdapter.setDropDownViewResource(R.layout.spinner_lunch);
                spLunch.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(recipes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




        /**
         * this function uploads the information from the firebase tree - recipes ->(branch) dinner - to a spinner
         * using the reference - refdinner and a Value event listener.
         */
        refdinner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                lst_dinner.clear();
                for (DataSnapshot data : ds.getChildren()){
                    String recipesname = (String) data.child("name").getValue();
                    lst_dinner.add(recipesname);
                }
               // ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item, lst_dinner);

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(recipes.this, R.layout.spinner_dinner,lst_dinner);
                //arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                arrayAdapter1.setDropDownViewResource(R.layout.spinner_dinner);
                spDinner.setAdapter(arrayAdapter1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(recipes.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseUser fbuser = refAuth.getCurrentUser();
        uid = fbuser.getUid();
        Query query = refUsers.orderByChild("uid").equalTo(uid);
        query.addListenerForSingleValueEvent(VEL_USER);
    }

    /**
     * this function reads the necessary information about the user to this activity.
     * (the user's gender)
     */
    com.google.firebase.database.ValueEventListener VEL_USER = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for(DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);
                    if (user.getIsFemale()) {
                        btnDinner.setText("הציגי");
                        btnLunch.setText("הציגי");
                        spinner_dinner.setText("לחצי לבחירת מתכון");
                        spinner_lunch.setText("לחצי לבחירת מתכון");
                    }
                    else {
                        btnDinner.setText("הצג");
                        btnLunch.setText("הצג");
                         spinner_dinner.setText("לחץ לבחירת מתכון");
                        spinner_lunch.setText("לחץ לבחירת מתכון");
                    }
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    /**
     * this function is called when the user chooses an item from either one of the spinners.
     * it checks on which spinner the user clicked on gets the name of the recipe.
     * @param parent - parent is used in order to know on which spinner the user clicked
     * @param view
     * @param pos - used in order to get the correct value from the spinner (according to the user's choice)
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (spLunch.equals(parent)){
            strlunch = lst_lunch.get(pos);
            if (!strlunch.equals("רשימת המתכונים:"))
                spinner_lunch.setText(strlunch);
            if (!spinner_lunch.getText().equals("לחצי לבחירת מתכון")&& !spinner_lunch.getText().equals("לחץ לבחירת מתכון") && !spinner_lunch.getText().equals("רשימת המתכונים")){
                Query query = reflunch.orderByChild("name").equalTo(strlunch);
                query.addListenerForSingleValueEvent(VELrecipe1);
            }
        }
        else{
            if (spDinner.equals(parent)){
                strdinner=lst_dinner.get(pos);
                if (!strdinner.equals("רשימת המתכונים"))
                    spinner_dinner.setText(strdinner);
                if (!spinner_dinner.getText().equals("לחצי לבחירת מתכון")&& !spinner_dinner.getText().equals("לחץ לבחירת מתכון")
                        && !spinner_dinner.getText().equals("רשימת המתכונים:")) {
                    Query query2 = refdinner.orderByChild("name").equalTo(strdinner);
                    query2.addListenerForSingleValueEvent(VELrecipe2);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "לתשומת ליבך: עלייך לבחור מתכון", Toast.LENGTH_SHORT).show();
    }

    /**
     * this function is called when the user clicks on the button "show" (lunch recipe).
     * it calls a query in order to find the recipe in the firebase database using the recipe name and the reference reflunch.
     * @param view
     */
    public void lunchRecipe(View view) {
            Query query = reflunch.orderByChild("name").equalTo(strlunch);
            query.addListenerForSingleValueEvent(VELrecipe1);
    }

    /**
     * this function gets the correct recipe from the firebase database.
     * afterwards, it sends the user to the next activity in which the recipe will appear.
     */
    ValueEventListener VELrecipe1 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    recipe = data.getValue(Recipe.class);
                    if (recipe.getLocation()!=0) {
                        Intent a = new Intent(recipes.this, Matcon.class);
                        a.putExtra("recNum", recipe.getLocation());
                        startActivity(a);
                        finish();
                    }
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    /**
     * this function is called when the user clicks on the button "show" (dinner recipe).
     * it calls a query in order to find the recipe in the firebase database using the recipe name and the reference refdinner.
     * @param view
     */
    public void dinnerRecipe(View view) {
        Query query2 = refdinner.orderByChild("name").equalTo(strdinner);
        query2.addListenerForSingleValueEvent(VELrecipe2);
    }

    /**
     * this function gets the correct recipe from the firebase database.
     * afterwards, it sends the user to the next activity in which the recipe will appear.
     */
    ValueEventListener VELrecipe2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    recipe2 = data.getValue(Recipe.class);
                    Intent a = new Intent(recipes.this, Matcon.class);
                    a.putExtra("recNum", recipe2.getLocation());
                    startActivity(a);
                    finish();
                }
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
        Intent a66=new Intent(this, Main_Screen.class);
        startActivity(a66);
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
     * @param item6
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item6) {
        String st6=item6.getTitle().toString();
        Intent a6=new Intent();
        if(st6.equals("מסך ראשי"))
            a6=new Intent(this, Main_Screen.class);
        if (st6.equals("פרטי הסדנה"))
            a6 = new Intent(this, sessions.class);
        if (st6.equals("תפריט"))
            a6 = new Intent(this, tafritim.class);
        if (st6.equals("מתכונים"))
            a6 = new Intent(this, recipes.class);
        if (st6.equals("תוספי תזונה"))
            a6 = new Intent(this, tosafim.class);
        if (st6.equals("תחליפים לצמחוניים וטבעוניים"))
            a6 = new Intent(this, Substitutes.class);
        if (st6.equals("אודות"))
            a6 = new Intent(this, Credits.class);
        if (st6.equals("פרופיל אישי"))
            a6 = new Intent(this, Settings.class);

        startActivity(a6);
        finish();
        return super.onOptionsItemSelected(item6);
    }



}
