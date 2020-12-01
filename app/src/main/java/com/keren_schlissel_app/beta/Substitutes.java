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

import static com.keren_schlissel_app.beta.FBref.refSUBfiles;
/**
 * @author Nitzan Dromi
 * an activity that presents substitutes for vegans or vegiterians
 */
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
        final ProgressDialog pd_sub=ProgressDialog.show(this,"התחליפים בהורדה","טוען...",true);

        StorageReference refFile = refSUBfiles.child(fname);

        final File localFile = new File(this.getFilesDir(), fname);
        refFile.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                pd_sub.dismiss();
             //   Toast.makeText(Substitutes.this, "תחליפי התזונה ירדו בהצלחה", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(Substitutes.this, "קובץ התחליפים עדיין לא ירד", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                   // Log.e("Substitutes",e.toString());
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd_sub.dismiss();
                if (((StorageException) exception).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    Toast.makeText(Substitutes.this, "הקובץ לא קיים במאגר", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Substitutes.this, "הורדת התחליפים נכשלה", Toast.LENGTH_LONG).show();
               //     Log.e("Substitutes", exception.toString());
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
        Intent a44=new Intent(this, Main_Screen.class);
        startActivity(a44);
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
     * @param item4
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item4) {
        String st4=item4.getTitle().toString();
        Intent a4=new Intent();
        if(st4.equals("מסך ראשי"))
            a4=new Intent(this, Main_Screen.class);
        if (st4.equals("פרטי הסדנה"))
            a4 = new Intent(this, sessions.class);
        if (st4.equals("תפריט"))
            a4 = new Intent(this, tafritim.class);
        if (st4.equals("מתכונים"))
            a4 = new Intent(this, recipes.class);
        if (st4.equals("תוספי תזונה"))
            a4 = new Intent(this, tosafim.class);
        if (st4.equals("תחליפים לצמחוניים וטבעוניים"))
            a4 = new Intent(this, Substitutes.class);
        if (st4.equals("אודות"))
            a4 = new Intent(this, Credits.class);
        if (st4.equals("פרופיל אישי"))
            a4 = new Intent(this, Settings.class);

        startActivity(a4);
        finish();
        return super.onOptionsItemSelected(item4);
    }

}
