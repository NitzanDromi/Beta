package com.example.beta;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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

import static android.provider.Telephony.Mms.Part.FILENAME;
import static com.example.beta.FBref.refRecfiles;
import static com.example.beta.FBref.refStor;

public class Matcon extends AppCompatActivity {
    int n;
    TextView tv_output;
    String fname,suf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matcon);

        tv_output=(TextView)findViewById(R.id.tvOutput);

        Intent a= getIntent();
        n=a.getExtras().getInt("recNum");
        fname=Integer.toString(n);
        suf=".txt";
        Toast.makeText(this, fname, Toast.LENGTH_SHORT).show();
        fname+=suf;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String st=item.getTitle().toString();
        if(st.equals("credits")){
            Intent a=new Intent(this, Credits.class);
            startActivity(a);
        }
        if(st.equals("menu")){
            Intent a=new Intent(this, tafritim.class);
            startActivity(a);
        }
        if(st.equals("settings")){
            Intent a=new Intent(this, Settings.class);
            startActivity(a);
        }
        if (st.equals("recipes")){
            Intent a=new Intent(this, recipes.class);
            startActivity(a);
        }
        return super.onOptionsItemSelected(item);

    }

    public void download(View view) throws IOException {
        final ProgressDialog pd=ProgressDialog.show(this,"Recipe download","downloading...",true);

        StorageReference refFile = refRecfiles.child(fname);

        final File localFile = new File(this.getFilesDir(), fname);
        refFile.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(Matcon.this, "Recipe download success", Toast.LENGTH_LONG).show();
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
}
