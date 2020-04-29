package com.example.beta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import static com.example.beta.FBref.refAuth;
import static com.example.beta.FBref.refSUBfiles;
import static com.example.beta.FBref.refSUPfiles;
import static com.example.beta.FBref.refUsers;

public class Substitutes extends AppCompatActivity {
    String fname;
    TextView tvOutPutSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substitutes);

        tvOutPutSub=(TextView)findViewById(R.id.tv_OutPut_sub);

        fname = "substitutes.txt";

        download();

    }

    /**
     * this function uploads the substitutes file (text file) from Firebase Storage
     */
    public void download() {
        final ProgressDialog pd=ProgressDialog.show(this,"Substitutes download","downloading...",true);

        StorageReference refFile = refSUBfiles.child(fname);

        final File localFile = new File(this.getFilesDir(), fname);
        refFile.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(Substitutes.this, "Substitutes download success", Toast.LENGTH_LONG).show();

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
                    tvOutPutSub.setText(sb);
                    deleteFile(fname);
                } catch (FileNotFoundException e) {
                    Toast.makeText(Substitutes.this, "File not downloaded yet", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.e("Substitutes",e.toString());
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                if (((StorageException) exception).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    Toast.makeText(Substitutes.this, "File not exist in storage", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Substitutes.this, "Substitutes download failed", Toast.LENGTH_LONG).show();
                    Log.e("Substitutes", exception.toString());
                }
            }
        });
    }

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
        if(st.equals("אודות")){
            Intent a=new Intent(this, Credits.class);
            startActivity(a);
        }
        if(st.equals("תפריט")){
            Intent a=new Intent(this, tafritim.class);
            startActivity(a);
        }
        if(st.equals("פרופיל אישי")){
            Intent a=new Intent(this, Settings.class);
            startActivity(a);
        }
        if (st.equals("מתכונים")){
            Intent a=new Intent(this, recipes.class);
            startActivity(a);
        }
        if(st.equals("תוספי תזונה")){
            Intent a=new Intent(this, tosafim.class);
            startActivity(a);
        }
        return super.onOptionsItemSelected(item);

    }

}
