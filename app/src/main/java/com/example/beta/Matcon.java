package com.example.beta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

import static com.example.beta.FBref.refRecfiles;
import static com.example.beta.FBref.refRecipesImages;

/**
 * @author Nitzan Dromi
 * an activity that presents the recipe the user chose in the recipes activity
 */

public class Matcon extends AppCompatActivity {
    int n;
    TextView tv_output;
    String fname;
    ImageView ivRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matcon);

        tv_output=(TextView)findViewById(R.id.tv_Output);
        ivRecipe=(ImageView)findViewById(R.id.imageViewRecipe);

        Intent a= getIntent();
        n=a.getExtras().getInt("recNum");
        fname=Integer.toString(n);
        fname+=".txt";

        downloadRecipe();
        try {
            downloadImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * this function downloads the recipe image (image file) from Firebase Storage to a local file
     * and presents the image
     * @throws IOException
     */
    public void downloadImage() throws IOException{
        StorageReference refRecImg = refRecipesImages.child(Integer.toString(n)+".jpg");

        final File localFile = File.createTempFile(Integer.toString(n)+"tmp","jpg");
        refRecImg.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                String filePath = localFile.getPath();
                Bitmap bitmapImage = BitmapFactory.decodeFile(filePath);
                ivRecipe.setImageBitmap(bitmapImage);
                ivRecipe.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Matcon.this, "תמונת המתכון לא מצליחה לעלות", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * this function uploads the recipe file (text file) from Firebase Storage
     * according to the recipe location from the previous activity (recipes activity)
     */
    public void downloadRecipe() {
        final ProgressDialog pd=ProgressDialog.show(this,"המתכון יורד","טוען...",true);

        StorageReference refFile = refRecfiles.child(fname);

        final File localFile = new File(this.getFilesDir(), fname);
        refFile.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(Matcon.this, "המתכון ירד בהצלחה", Toast.LENGTH_LONG).show();

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
                    Toast.makeText(Matcon.this, "המתכון עדיין לא עלה", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.e("Matcon",e.toString());
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                if (((StorageException) exception).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    Toast.makeText(Matcon.this, "קובץ לא קיים בזיכרון", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Matcon.this, "המתכון נכשל לעלות", Toast.LENGTH_LONG).show();
                    Log.e("Matcon", exception.toString());
                }
            }
        });
    }

    /**
     * this function is called if the user presses the "back" button on his device.
     * it sends him back to the recipes activity.
     */
    @Override
    public void onBackPressed() {
        Intent a77=new Intent(Matcon.this, recipes.class);
        startActivity(a77);
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
     * @param item7
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item7) {
        String st7=item7.getTitle().toString();
        Intent a7=new Intent();
        if(st7.equals("מסך ראשי"))
            a7=new Intent(this, Main_Screen.class);
        if (st7.equals("פרטי הסדנה"))
            a7 = new Intent(this, sessions.class);
        if (st7.equals("תפריט"))
            a7 = new Intent(this, tafritim.class);
        if (st7.equals("מתכונים"))
            a7 = new Intent(this, recipes.class);
        if (st7.equals("תוספי תזונה"))
            a7 = new Intent(this, tosafim.class);
        if (st7.equals("תחליפים לצמחוניים וטבעוניים"))
            a7 = new Intent(this, Substitutes.class);
        if (st7.equals("אודות"))
            a7 = new Intent(this, Credits.class);
        if (st7.equals("פרופיל אישי"))
            a7 = new Intent(this, Settings.class);

        startActivity(a7);
        finish();

        return super.onOptionsItemSelected(item7);

    }
}
