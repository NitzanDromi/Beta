package com.example.beta;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.beta.FBref.refRecfiles;

/**
 * @author Nitzan Dromi
 * an activity that presents the recipe the user chose in the recipes activity
 */

public class Matcon extends AppCompatActivity {
    int n;
    TextView tv_output;
    String fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matcon);

        tv_output=(TextView)findViewById(R.id.tv_Output);

        Intent a= getIntent();
        n=a.getExtras().getInt("recNum");
        fname=Integer.toString(n);
        fname+=".txt";

        download();

    }

    /**
     * this function uploads the recipe file (text file) from Firebase Storage
     * according to the recipe location from the previous activity (recipes activity)
     */
    public void download() {
        final ProgressDialog pd=ProgressDialog.show(this,"Recipe download","downloading...",true);

        StorageReference refFile = refRecfiles.child(fname);

        final File localFile = new File(this.getFilesDir(), fname);
        refFile.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(Matcon.this, "Recipe download success", Toast.LENGTH_LONG).show();

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
                    tv_output.setText(sb);
                    deleteFile(fname);
                } catch (FileNotFoundException e) {
                    Toast.makeText(Matcon.this, "File not downloaded yet", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.e("Matcon",e.toString());
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                if (((StorageException) exception).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    Toast.makeText(Matcon.this, "File not exist in storage", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Matcon.this, "Recipe download failed", Toast.LENGTH_LONG).show();
                    Log.e("Matcon", exception.toString());
                }
            }
        });
    }

    /**
     * this function is called if the user presses the "back" button on his device.
     * it assures the user's intention to leave the current screen.
     * if the user wants to exit, it sends him back to the recipes activity.
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage("Are you sure you want to close the recipe?");
        adb.setTitle("BACK");
        adb.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int whichButton) {
                Intent a=new Intent(Matcon.this, recipes.class);
                startActivity(a);
                dialogInterface.dismiss();
            }
        });
        adb.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int whichButton) {
                dialogInterface.cancel();
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
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
        if(st.equals("תחליפים לצמחוניים וטבעוניים")){
            Intent a=new Intent(this, Substitutes.class);
            startActivity(a);
        }
        return super.onOptionsItemSelected(item);

    }
}
