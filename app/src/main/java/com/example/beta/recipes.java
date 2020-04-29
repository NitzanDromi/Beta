package com.example.beta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.beta.FBref.refAuth;
import static com.example.beta.FBref.refUsers;
import static com.example.beta.FBref.refdinner;
import static com.example.beta.FBref.reflunch;

public class recipes extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Spinner spLunch, spDinner;
    Button btnLunch, btnDinner;
    int numl=0,numd=0;
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


        lst_lunch.clear();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item, lst_lunch);
        spLunch.setAdapter(arrayAdapter);
        spLunch.setOnItemSelectedListener(this);

        lst_dinner.clear();
        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item, lst_dinner);
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
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item, lst_lunch);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item, lst_dinner);
                arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                    }
                    else {
                        btnDinner.setText("הצג");
                        btnLunch.setText("הצג");
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
          //  numl=pos+1;
            strlunch = lst_lunch.get(pos);
        }
        else{
            if (spDinner.equals(parent)){
             //   numd=pos+1;
                strdinner=lst_dinner.get(pos);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "please choose a recipe", Toast.LENGTH_SHORT).show();
    }

    /**
     * this function is called when the user clicks on the button "show" (lunch recipe).
     * it calls a query in order to find the recipe in the firebase database using the recipe name and the reference reflunch.
     * @param view
     */
    public void lunchRecipe(View view) {
            Query query = reflunch.orderByChild("name").equalTo(strlunch);
            query.addListenerForSingleValueEvent(VEL);
    }

    /**
     * this function gets the correct recipe from the firebase database.
     * afterwards, it sends the user to the next activity in which the recipe will appear.
     */
    ValueEventListener VEL = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    recipe = data.getValue(Recipe.class);
                    Intent a = new Intent(recipes.this, Matcon.class);
                    a.putExtra("recNum", recipe.getLocation());
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
     * this function is called when the user clicks on the button "show" (dinner recipe).
     * it calls a query in order to find the recipe in the firebase database using the recipe name and the reference refdinner.
     * @param view
     */
    public void dinnerRecipe(View view) {
        Query query2 = refdinner.orderByChild("name").equalTo(strdinner);
        query2.addListenerForSingleValueEvent(VEL2);
    }

    /**
     * this function gets the correct recipe from the firebase database.
     * afterwards, it sends the user to the next activity in which the recipe will appear.
     */
    ValueEventListener VEL2 = new ValueEventListener() {
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
     * this function creates the menu options - the menu - main.xml
     * @param menu
     * @return ????????????????????????????????????????????
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * this function gets the user's choice from the menu and sends him to the appropriate activity (based on his choice...)
     * @param item
     * @return ???????????????????
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String st=item.getTitle().toString();
        if(st.equals("פרופיל אישי")){
            Intent a=new Intent(this, Settings.class);
            startActivity(a);
            finish();
        }
        if(st.equals("אודות")){
            Intent a=new Intent(this, Credits.class);
            startActivity(a);
            finish();
        }
        if(st.equals("תפריט")){
            Intent a=new Intent(this, tafritim.class);
            startActivity(a);
            finish();
        }
        if(st.equals("תוספי תזונה")){
            Intent a=new Intent(this, tosafim.class);
            startActivity(a);
        }
        if(st.equals("תחליפים לצמחוניים וטבעוניים")){
            Intent a=new Intent(this, Substitutes.class);
            startActivity(a);
        }
        return super.onOptionsItemSelected(item);
    }



}
