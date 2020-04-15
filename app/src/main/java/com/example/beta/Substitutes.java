package com.example.beta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
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
    String uid, fname;
    User user;
    TextView tvOutPutSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substitutes);

        tvOutPutSub=(TextView)findViewById(R.id.tv_OutPut_sub);

        fname = "substitutes.txt";
        try {
            download();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void download() throws IOException {
        final ProgressDialog pd=ProgressDialog.show(this,"Substitutes download","downloading...",true);

        StorageReference refFile = refSUBfiles.child(fname);

        final File localFile = new File(this.getFilesDir(), fname);
        refFile.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(Substitutes.this, "Substitutes download success", Toast.LENGTH_LONG).show();
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

}
