package com.example.beta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.beta.FBref.refAuth;
import static com.example.beta.FBref.refImages;
import static com.example.beta.FBref.refUsers;

public class Settings extends AppCompatActivity {
    String uid;
    User user;
    TextView tvname;
    EditText etweight, etheight;
    String name="";
    int Gallery=1, count=0;
    ImageButton ibBefore, ibAfter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ibBefore=(ImageButton)findViewById(R.id.imageButtonBeforeImage);
        ibAfter=(ImageButton)findViewById(R.id.imageButtonAfterImage);

        tvname=(TextView)findViewById(R.id.tvname);
        etheight=(EditText) findViewById(R.id.etHeightt);
        etweight=(EditText) findViewById(R.id.etWeightt);

        FirebaseUser fbuser = refAuth.getCurrentUser();
        uid = fbuser.getUid();
        Query query = refUsers.orderByChild("uid").equalTo(uid);
        query.addListenerForSingleValueEvent(VEL);

     /*   try {
            download();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            download1();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
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
                    if (user.getBeforeImage().equals("empty")){
                        if (user.getIsFemale())
                        ibBefore.setImageResource(R.drawable.request_before_female);
                        else
                            ibBefore.setImageResource(R.drawable.request_before_male);
                    }
                    else {
                        if (user.getAfterImage().equals("empty")){
                            if (user.getIsFemale())
                                ibAfter.setImageResource(R.drawable.request_after_female);
                            else
                                ibAfter.setImageResource(R.drawable.request_after_male);
                        }
                        else {
                            try {
                                download1();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            download();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (user.getAfterImage().equals("empty")){
                        if (user.getIsFemale())
                            ibAfter.setImageResource(R.drawable.request_after_female);
                        else
                            ibAfter.setImageResource(R.drawable.request_after_male);
                    }
                    else {
                        try {
                            download1();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };


    public void updateHeight(View view) {
        refUsers.child(user.getName()).child("height").removeValue();
        refUsers.child(user.getName()).child("height").setValue(etheight.getText().toString());
    }

    public void updateWeight(View view) {
        refUsers.child(name).child("weight").removeValue();
        refUsers.child(name).child("weight").setValue(etweight.getText().toString());
    }


    public void upload_before(View view) {
        count=1;
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Gallery);
    }

    public void upload_after(View view) {
        count=2;
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Gallery);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Gallery) {
                Uri file = data.getData();
                if (file != null) {
                    if(count==2){
                        final ProgressDialog pd=ProgressDialog.show(this,"Upload image","Uploading...",true);
                        StorageReference refImg = refImages.child(name+"_after.jpg");
                        refImg.putFile(file)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        pd.dismiss();
                                        Toast.makeText(Settings.this, "Image Uploaded", Toast.LENGTH_LONG).show();

                                        refUsers.child(user.getName()).child("afterImage").removeValue();
                                        refUsers.child(user.getName()).child("afterImage").setValue("checked");

                                        try {
                                            download1();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        pd.dismiss();
                                        Toast.makeText(Settings.this, "Upload failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    else {

                        final ProgressDialog pd = ProgressDialog.show(this, "Upload image", "Uploading...", true);
                        StorageReference refImg = refImages.child(name + ".jpg");
                        refImg.putFile(file)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        pd.dismiss();
                                        Toast.makeText(Settings.this, "Image Uploaded", Toast.LENGTH_LONG).show();

                                        refUsers.child(user.getName()).child("beforeImage").removeValue();
                                        refUsers.child(user.getName()).child("beforeImage").setValue("checked");

                                        try {
                                            download();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        pd.dismiss();
                                        Toast.makeText(Settings.this, "Upload failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(this, "No Image was selected", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void download() throws IOException{
        StorageReference refImg = refImages.child(name+".jpg");

        final File localFile = File.createTempFile(name,"jpg");
        refImg.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                String filePath = localFile.getPath();
                Bitmap bitmapImage = BitmapFactory.decodeFile(filePath);
               // int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
                //Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
                //ibBefore.setImageBitmap(scaled);
                ibBefore.setImageBitmap(bitmapImage);
                ibBefore.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Toast.makeText(infoTeacher.this, "Image download failed", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void download1() throws IOException{

        StorageReference refImg = refImages.child(name+"_after.jpg");

        final File localFile = File.createTempFile(name,"_afterjpg");
        refImg.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                String filePath = localFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                ibAfter.setImageBitmap(bitmap);
                ibAfter.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Toast.makeText(infoTeacher.this, "Image download failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String st=item.getTitle().toString();
        if(st.equals("מתכונים")){
            Intent a=new Intent(this, recipes.class);
            startActivity(a);
        }
        if(st.equals("קרדיטים")){
            Intent a=new Intent(this, Credits.class);
            startActivity(a);
        }
        if(st.equals("תפריט")){
            Intent a=new Intent(this, tafritim.class);
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
