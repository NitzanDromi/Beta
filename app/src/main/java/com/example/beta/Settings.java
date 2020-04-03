package com.example.beta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.beta.FBref.refAuth;
import static com.example.beta.FBref.refUsers;

public class Settings extends AppCompatActivity {
    String uid;
    User user;
    TextView tvname;
    EditText etweight, etheight;
    String name="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvname=(TextView)findViewById(R.id.tvname);
        etheight=(EditText) findViewById(R.id.etHeightt);
        etweight=(EditText) findViewById(R.id.etWeightt);

        FirebaseUser fbuser = refAuth.getCurrentUser();
        uid = fbuser.getUid();
        Query query = refUsers.orderByChild("uid").equalTo(uid);
        query.addListenerForSingleValueEvent(VEL);
    }

    com.google.firebase.database.ValueEventListener VEL = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for(DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);
                    tvname.setText("Welcome " + user.getName() + "!");
                    name=user.getName();
                    etweight.setText(user.getWeight());
                    etheight.setText(user.getHeight());
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
        if(st.equals("recipes")){
            Intent a=new Intent(this, recipes.class);
            startActivity(a);
        }
        if(st.equals("credits")){
            Intent a=new Intent(this, Credits.class);
            startActivity(a);
        }
        if(st.equals("menu")){
            Intent a=new Intent(this, tafritim.class);
            startActivity(a);
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateHeight(View view) {
        refUsers.child(user.getName()).child("height").removeValue();
        refUsers.child(user.getName()).child("height").setValue(etheight.getText().toString());
    }

    public void updateWeight(View view) {
        refUsers.child(name).child("weight").removeValue();
        refUsers.child(name).child("weight").setValue(etweight.getText().toString());
    }

}
