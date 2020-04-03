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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.beta.FBref.refdinner;
import static com.example.beta.FBref.reflunch;

public class recipiesList extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView lvLunch, lvDinner;
    int num;
    int location;

    Recipe recipe;

    List<String> lst = new ArrayList<String>();
    List<String> lst1 = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter,arrayAdapter1;
    int recNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipies_list);
        lvDinner=(ListView)findViewById(R.id.lvDinner);
        lvLunch=(ListView)findViewById(R.id.lvLunch);

        lst.clear();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(recipiesList.this, android.R.layout.simple_spinner_item, lst);
        lvLunch.setAdapter(arrayAdapter);
        lvLunch.getOnItemClickListener();
        lst1.clear();
        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(recipiesList.this, android.R.layout.simple_spinner_item, lst1);
        lvDinner.setAdapter(arrayAdapter1);
        lvDinner.getOnItemClickListener();

        reflunch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                lst.clear();
                for (DataSnapshot data : ds.getChildren()) {
                    String recipesname = (String) data.child("name").getValue();
                    lst.add(recipesname);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(recipiesList.this, android.R.layout.simple_spinner_item, lst);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lvLunch.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(recipiesList.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //  spLunch.setOnItemSelectedListener(this);


        refdinner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                lst1.clear();
                for (DataSnapshot data : ds.getChildren()){
                    String recipesname = (String) data.child("name").getValue();
                    lst1.add(recipesname);
                }
                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(recipiesList.this, android.R.layout.simple_spinner_item, lst1);
                arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lvDinner.setAdapter(arrayAdapter1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(recipiesList.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        num=position+1;
        String str= lst.get(position);
        if (lvLunch.equals(parent)){
            Query query = reflunch.orderByChild("name").equalTo(str);
            query.addListenerForSingleValueEvent(VEL);
        }
        else{
            if (lvDinner.equals(parent)){
                Query query = refdinner.orderByChild("name").equalTo(str);
                query.addListenerForSingleValueEvent(VEL);
            }
            else{
                Toast.makeText(this, "please choose a recipe", Toast.LENGTH_SHORT).show();
            }
        }

    }

    ValueEventListener VEL = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    recipe = data.getValue(Recipe.class);
                    Toast.makeText(recipiesList.this, "" + recipe.getLocation(), Toast.LENGTH_SHORT).show();
                   /* Intent a = new Intent(recipes.this, Matcon.class);
                    a.putExtra("recNum", recipe.getNum());
                    startActivity(a);
                    finish();*/
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String st=item.getTitle().toString();
        if(st.equals("settings")){
            Intent a=new Intent(this, Settings.class);
            startActivity(a);
            finish();
        }
        if(st.equals("credits")){
            Intent a=new Intent(this, Credits.class);
            startActivity(a);
            finish();
        }
        if(st.equals("menu")){
            Intent a=new Intent(this, tafritim.class);
            startActivity(a);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
