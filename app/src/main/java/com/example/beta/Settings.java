package com.example.beta;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.beta.FBref.refAuth;
import static com.example.beta.FBref.refImages;
import static com.example.beta.FBref.refUsers;

/**
 * an activity that shows personal information about the user
 */
public class Settings extends AppCompatActivity {
    String uiduser="", fullName="", name="", imBefore="checked", imAfter="checked", weight="";
    User user, userimage;
    TextView tvname;
    EditText etweight, etheight;
    int Gallery=1, count=0;
    ImageView ivBefore, ivAfter;
    ImageButton ibHeightChange, ibWeightChange;
    TextView tvBefore, tvAfter, tvweight;
    AlertDialog adImagebefore, adImageAfter;
    Boolean female;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ibWeightChange=(ImageButton) findViewById(R.id.imbtWeight);
        ibHeightChange=(ImageButton) findViewById(R.id.imbtHeight);
        ivBefore=(ImageView)findViewById(R.id.imageViewBeforeImage);
        ivAfter=(ImageView)findViewById(R.id.imageViewAfterImage);
        tvBefore=(TextView) findViewById(R.id.tvBefore);
        tvAfter=(TextView) findViewById(R.id.tvAfter);
        tvname=(TextView)findViewById(R.id.tvname);
        tvweight=(TextView)findViewById(R.id.tvbeginningweight);
        etheight=(EditText) findViewById(R.id.etHeightnow);
        etweight=(EditText) findViewById(R.id.etWeightnow);

        FirebaseUser fbuser = refAuth.getCurrentUser();
        uiduser = fbuser.getUid();
        Query query = refUsers.orderByChild("uid").equalTo(uiduser);
        query.addListenerForSingleValueEvent(VELUser);
    }

    /**
     * this function reads the necessary information about the user to this activity.
     * (the user's gender and name (personal and full name)
     * and whether the user already uploaded a before & an after image - if not, it puts the default image according to his gender)
     */
    com.google.firebase.database.ValueEventListener VELUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for(DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);
                    female=user.getIsFemale();
                    name=user.getName();
                    tvname.setText("שלום " + name + "!");

                    fullName=name+" "+user.getLastName();
                    tvweight.setText(user.getBeginningweight());
                    etweight.setText(user.getWeight());
                    etheight.setText(user.getHeight());

                    if (female) {
                        ibWeightChange.setImageResource(R.drawable.save_changes_women);
                        ibHeightChange.setImageResource(R.drawable.save_changes_women);
                    }
                    else{
                        ibWeightChange.setImageResource(R.drawable.save_changes_men);
                        ibHeightChange.setImageResource(R.drawable.save_changes_men);
                    }

                    if (user.getBeforeImage().equals("empty")){
                        imBefore="empty";
                        tvBefore.setVisibility(View.INVISIBLE);
                        if (female)
                            ivBefore.setImageResource(R.drawable.request_before_female);
                        else
                            ivBefore.setImageResource(R.drawable.request_before_male);
                    }
                    else {
                        try {
                            download();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (user.getAfterImage().equals("empty")){
                        imAfter="empty";
                        tvAfter.setVisibility(View.INVISIBLE);
                        if (female)
                            ivAfter.setImageResource(R.drawable.request_after_female);
                        else
                            ivAfter.setImageResource(R.drawable.request_after_male);
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

    /**
     * this function gets the image existing status.
     */
    com.google.firebase.database.ValueEventListener VELimage = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dSim) {
            if (dSim.exists()) {
                for(DataSnapshot dataim : dSim.getChildren()) {
                    userimage = dataim.getValue(User.class);
                    if (userimage.getBeforeImage().equals("empty")){
                       imBefore="empty";
                    }
                    else {
                        imBefore="checked";
                    }
                    if (userimage.getAfterImage().equals("empty"))
                        imAfter="empty";
                     else
                         imAfter="checked";
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    /**
     * this function updates the user's height in the Firebase Database
     * @param view
     */
    public void updateHeight(View view) {
        if (!etheight.getText().toString().isEmpty()) {
            refUsers.child(fullName).child("height").removeValue();
            refUsers.child(fullName).child("height").setValue(etheight.getText().toString());
            if(!Settings.this.isFinishing())
            {
                Toast.makeText(this, "השינויים נשמרו", Toast.LENGTH_SHORT).show();

            }
        }
        else
            Toast.makeText(this, "לתשומת ליבך: יש למלא את הגובה שלך", Toast.LENGTH_SHORT).show();

    }

    /**
     * this function updates the user's weight in the Firebase Database
     * @param view
     */
    public void updateWeight(View view) {
        if (!etweight.getText().toString().isEmpty()) {
            refUsers.child(fullName).child("weight").removeValue();
            refUsers.child(fullName).child("weight").setValue(etweight.getText().toString());
            if(!Settings.this.isFinishing())
            {
                Toast.makeText(this, "Changes are saved", Toast.LENGTH_SHORT).show();

            }
        }
        else
            Toast.makeText(this, "please, fill your weight", Toast.LENGTH_SHORT).show();

    }

    /**
     * this function checks the user's before image status from the Firebase Database
     * if there isn't a picture, it opens the gallery
     * if there is - it gives the user the option to replace it or delete it.
     * @param view
     */
    public void upload_before(View view) {
        Query q = refUsers.orderByChild("uid").equalTo(uiduser);
        q.addListenerForSingleValueEvent(VELimage);
        if (imBefore.equals("empty")){
            count = 1;
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, Gallery);
        }
        else {
            androidx.appcompat.app.AlertDialog.Builder alertDialogB = new AlertDialog.Builder(this);
            if (female)
                alertDialogB.setTitle("מה תרצי לעשות עם התמונה?");
            else
                alertDialogB.setTitle("מה תרצה לעשות עם התמונה?");
            alertDialogB.setPositiveButton("למחוק", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int whichButton) {
                    if (female)
                        ivBefore.setImageResource(R.drawable.request_before_female);
                    else
                        ivBefore.setImageResource(R.drawable.request_before_male);
                    imBefore="empty";
                    refUsers.child(fullName).child("beforeImage").removeValue();
                    refUsers.child(fullName).child("beforeImage").setValue("empty");
                    tvBefore.setVisibility(View.INVISIBLE);
                    dialogInterface.dismiss();
                }
            });
            alertDialogB.setNegativeButton("להחליף", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int whichButton) {
                    count = 1;
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Gallery);
                    dialogInterface.cancel();
                }
            });
            alertDialogB.setNeutralButton("ביטול", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int whichButton) {
                    dialogInterface.cancel();
                }
            });
            adImagebefore = alertDialogB.create();
            if(!Settings.this.isFinishing())
            {
                adImagebefore.show();
            }
        }
    }

    /**
     * this function checks the user's after image status from the Firebase Database
     * if there isn't a picture, it opens the gallery
     * if there is - it gives the user the option to replace it or delete it.
     * @param view
     */
    public void upload_after(View view) {
        Query q2 = refUsers.orderByChild("uid").equalTo(uiduser);
        q2.addListenerForSingleValueEvent(VELimage);
        if (imAfter.equals("empty")){
            count = 2;
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, Gallery);
        }
        else {
            androidx.appcompat.app.AlertDialog.Builder adbAfter= new AlertDialog.Builder(this);
            if (female)
                adbAfter.setTitle("מה תרצי לעשות עם התמונה?");
            else
                adbAfter.setTitle("מה תרצה לעשות עם התמונה?");
            adbAfter.setPositiveButton("למחוק", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int whichButton) {
                    if (female)
                        ivAfter.setImageResource(R.drawable.request_after_female);
                    else
                        ivAfter.setImageResource(R.drawable.request_after_male);
                    refUsers.child(fullName).child("afterImage").removeValue();
                    refUsers.child(fullName).child("afterImage").setValue("empty");
                    imAfter="empty";
                    tvAfter.setVisibility(View.INVISIBLE);
                    dialogInterface.dismiss();
                }
            });
            adbAfter.setNegativeButton("להחליף", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int whichButton) {
                    count = 2;
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Gallery);
                    dialogInterface.dismiss();
                }
            });
            adbAfter.setNeutralButton("ביטול", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int whichButton) {
                    dialogInterface.cancel();
                }
            });
            adImageAfter = adbAfter.create();
            if(!Settings.this.isFinishing())
            {
                adImageAfter.show();
            }
        }
    }

    /**
     * After the user chose an image from the gallery,
     * this function updates the before/after image status and uploads the selected image file to the firebase storage
     * @param requestCode   The call sign of the intent that requested the result
     * @param resultCode    A code that symbols the status of the result of the activity
     * @param data          The data returned
     */
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
                                        Toast.makeText(Settings.this, "התמונה הועלתה", Toast.LENGTH_LONG).show();

                                        refUsers.child(fullName).child("afterImage").removeValue();
                                        refUsers.child(fullName).child("afterImage").setValue("checked");
                                        imAfter="checked";


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
                                        Toast.makeText(Settings.this, "העלאה נכשלה", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    else {

                        final ProgressDialog pd = ProgressDialog.show(this, "Upload image", "Uploading...", true);
                        StorageReference refImg = refImages.child(name + "_before.jpg");
                        refImg.putFile(file)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        pd.dismiss();
                                        Toast.makeText(Settings.this, "התמונה הועלתה", Toast.LENGTH_LONG).show();

                                        refUsers.child(fullName).child("beforeImage").removeValue();
                                        refUsers.child(fullName).child("beforeImage").setValue("checked");
                                        imBefore="checked";

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
                                        Toast.makeText(Settings.this, "העלאה נכשלה", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(this, "לא נבחרה תמונה", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * this function downloads the 'before' image (image file) from Firebase Storage to a local file
     * and presents the image
     * @throws IOException
     */
    public void download() throws IOException{
        tvBefore.setVisibility(View.VISIBLE);

        StorageReference refImg = refImages.child(name+"_before.jpg");

        final File localFile = File.createTempFile(name,"_beforejpg");
        refImg.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                String filePath = localFile.getPath();
                Bitmap bitmapImage = BitmapFactory.decodeFile(filePath);
                ivBefore.setImageBitmap(bitmapImage);
                ivBefore.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                 Toast.makeText(Settings.this, "הורדת התמונה נכשלה", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * this function downloads the 'after' image (image file) from Firebase Storage to a local file
     * and presents the image
     * @throws IOException
     */
    public void download1() throws IOException{
        tvAfter.setVisibility(View.VISIBLE);

        StorageReference refImg = refImages.child(name+"_after.jpg");

        final File localFile = File.createTempFile(name,"_afterjpg");
        refImg.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                String filePath = localFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                ivAfter.setImageBitmap(bitmap);
                ivAfter.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                 Toast.makeText(Settings.this, "הורדת התמונה נכשלה", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * this function is called if the user presses the "back" button on his device.
     * it sends him back to the Main Screen activity.
     */
    @Override
    public void onBackPressed() {
        Intent a33=new Intent(this, Main_Screen.class);
        startActivity(a33);
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
     * @param item3
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item3) {
        String st3=item3.getTitle().toString();
        Intent a3=new Intent();
        if(st3.equals("מסך ראשי"))
            a3=new Intent(this, Main_Screen.class);
        if (st3.equals("פרטי הסדנה"))
            a3 = new Intent(this, sessions.class);
        if (st3.equals("תפריט"))
            a3 = new Intent(this, tafritim.class);
        if (st3.equals("מתכונים"))
            a3 = new Intent(this, recipes.class);
        if (st3.equals("תוספי תזונה"))
            a3 = new Intent(this, tosafim.class);
        if (st3.equals("תחליפים לצמחוניים וטבעוניים"))
            a3 = new Intent(this, Substitutes.class);
        if (st3.equals("אודות"))
            a3 = new Intent(this, Credits.class);
        if (st3.equals("פרופיל אישי"))
            a3 = new Intent(this, Settings.class);

        startActivity(a3);
        finish();
        return super.onOptionsItemSelected(item3);
    }

}
