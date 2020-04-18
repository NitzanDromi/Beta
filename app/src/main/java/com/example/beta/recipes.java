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

    List<String> lst = new ArrayList<String>();
    List<String> lst1 = new ArrayList<String>();

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


        lst.clear();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item, lst);
        spLunch.setAdapter(arrayAdapter);
        spLunch.setOnItemSelectedListener(this);

        lst1.clear();
        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item, lst1);
        spDinner.setAdapter(arrayAdapter1);
        spDinner.setOnItemSelectedListener(this);

        reflunch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                lst.clear();
                for (DataSnapshot data : ds.getChildren()) {
                    String recipesname = (String) data.child("name").getValue();
                    lst.add(recipesname);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item, lst);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spLunch.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(recipes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        refdinner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                lst1.clear();
                for (DataSnapshot data : ds.getChildren()){
                    String recipesname = (String) data.child("name").getValue();
                    lst1.add(recipesname);
                }
                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(recipes.this, android.R.layout.simple_spinner_item, lst1);
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (spLunch.equals(parent)){
            numl=pos+1;
            strlunch = lst.get(pos);
        }
        else{
            if (spDinner.equals(parent)){
                numd=pos+1;
                strdinner=lst1.get(pos);
            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "please choose a recipe", Toast.LENGTH_SHORT).show();
    }

    public void showReclunch(View view) {
            Query query = reflunch.orderByChild("name").equalTo(strlunch);
            query.addListenerForSingleValueEvent(VEL);
    }

    public void showRecdiner(View view) {
            Query query2 = refdinner.orderByChild("name").equalTo(strdinner);
            query2.addListenerForSingleValueEvent(VEL2);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String st=item.getTitle().toString();
        if(st.equals("הגדרות")){
            Intent a=new Intent(this, Settings.class);
            startActivity(a);
            finish();
        }
        if(st.equals("קרדיטים")){
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


    public void dinnerRecipe(View view) {
        Query query2 = refdinner.orderByChild("name").equalTo(strdinner);
        query2.addListenerForSingleValueEvent(VEL2);
    }
}
