package com.keren_schlissel_app.beta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.keren_schlissel_app.beta.FBref.refAuth;
import static com.keren_schlissel_app.beta.FBref.refSUPfiles;
import static com.keren_schlissel_app.beta.FBref.refUsers;

/**
 * an activity that shows the supplements for women/ men according to the user's gender
 */
public class tosafim extends AppCompatActivity {
    String uid, fname;
    User user;
    TextView tvoutput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tosafim);

        tvoutput=(TextView)findViewById(R.id.tvOutput);

        FirebaseUser fbuser = refAuth.getCurrentUser();
        uid = fbuser.getUid();
        Query query = refUsers.orderByChild("uid").equalTo(uid);
        query.addListenerForSingleValueEvent(VELSup);
    }

    /**
     * this function reads the necessary information about the user to this activity.
     * (the user's gender)
     */
    com.google.firebase.database.ValueEventListener VELSup = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for(DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);
                    if (user.getIsFemale())
                        fname = "supplements_female";
                    else
                        fname = "supplements_male";

                    fname += ".txt";

                    download();
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    /**
     * this function uploads the supplements file (text file) from Firebase Storage,
     * according to the user's gender
     */
    public void download() {
        final ProgressDialog pd=ProgressDialog.show(this,"התוספים בירידה","טוען...",true);

        StorageReference refFile = refSUPfiles.child(fname);

        final File localFile = new File(this.getFilesDir(), fname);
        refFile.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
              //  Toast.makeText(tosafim.this, "התוספים ירדו בהצלחה", Toast.LENGTH_SHORT).show();
                // String filePath = localFile.getPath();

                try {
                    InputStream is = openFileInput(fname);
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuffer sb = new StringBuffer();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    isr.close();
                    br.close();
                    tvoutput.setText(sb);
                    deleteFile(fname);
                } catch (FileNotFoundException e) {
                    Toast.makeText(tosafim.this, "קובץ התוספים עדיין לא ירד", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                  //  Log.e("supplements",e.toString());
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                if (((StorageException) exception).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    Toast.makeText(tosafim.this, "הקובץ לא קיים במאגר", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(tosafim.this, "הורדת התוספים נכשלה", Toast.LENGTH_LONG).show();
                 //   Log.e("supplements", exception.toString());
                }
            }
        });
    }

    /**
     * this function is called if the user presses the "back" button on his device.
     * it sends him back to the Main Screen activity.
     */
    @Override
    public void onBackPressed() {
        Intent a11=new Intent(this, Main_Screen.class);
        startActivity(a11);
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
     * @param item1
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item1) {
        String st1=item1.getTitle().toString();
        Intent a1=new Intent();
        if(st1.equals("מסך ראשי"))
            a1=new Intent(this, Main_Screen.class);
        if (st1.equals("פרטי הסדנה"))
            a1 = new Intent(this, sessions.class);
        if (st1.equals("תפריט"))
            a1 = new Intent(this, tafritim.class);
        if (st1.equals("מתכונים"))
            a1 = new Intent(this, recipes.class);
        if (st1.equals("תוספי תזונה"))
            a1= new Intent(this, tosafim.class);
        if (st1.equals("תחליפים לצמחוניים וטבעוניים"))
            a1 = new Intent(this, Substitutes.class);
        if (st1.equals("אודות"))
            a1 = new Intent(this, Credits.class);
        if (st1.equals("פרופיל אישי"))
            a1 = new Intent(this, Settings.class);

        startActivity(a1);
        finish();
        return super.onOptionsItemSelected(item1);
    }

}
