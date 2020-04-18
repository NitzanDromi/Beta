package com.example.beta;

/**
 * @author		Nitzan Dromi <address @nitzandr13@gmail.com>
 * @version	1.2(current version number of program)
 * @since		29/01/2020 (the date of the package the class was added)
 * Beta version of the application.
 * has:
 * login/ register activity (the same activity, it's purpose can change) - xml is done, program is in progress.
 * menus activity (currently empty)
 * class for all the variables related to FireBase
 * class for the User's tree in FireBase
 */


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static com.example.beta.FBref.refAuth;
import static com.example.beta.FBref.refPlaces;
import static com.example.beta.FBref.refUsers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Register_Login extends AppCompatActivity {

    private static final String TAG="MainActivity";

    TextView tvTitle, tvRegister, tvFemale, tvMale;
    EditText etName, etPhone, etMail, etPass, etWeight, etId, etHeight;
    CheckBox cbStayconnect;
    Switch swMoF;
    Boolean isFemale= false;

    TextView mDisplayDate;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    Button btn;

    List<String> titleList = new ArrayList<String>();

    Spinner spFplace;

    User userdb, currentUser;

    String mVerificationId, code, name, phone, email, password, id, weight, height, uid="", date, places, beforeImage="empty", afterImage="empty";
    Boolean stayConnect, registered, isUID = false, status;

    AlertDialog ad;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    Boolean mVerificationInProgress = false;
    ValueEventListener usersListener;
    ProgressDialog progressDialog;
    FirebaseUser user;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__login);

        spFplace=(Spinner) findViewById(R.id.spPlace);

        refPlaces.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                titleList.clear();

                for (DataSnapshot data : ds.getChildren()){
                    String titlename=data.getKey();
                    titleList.add(titlename);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Register_Login.this, android.R.layout.simple_spinner_item, titleList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFplace.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Register_Login.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

/**
 * date picker - in order to choose the date of birth of each user.
 * ended programming in 22/1 - the program works.
 */
        mDisplayDate=(TextView)findViewById(R.id.tvBDate);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Register_Login.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month= month+1;
                Log.d(TAG, "onDataSet: dd/mm/yyyy: "+ dayOfMonth +"/" + month +"/" +year );
                date = dayOfMonth +"/" + month +"/" +year;
                mDisplayDate.setText(date);
            }
        };


        tvTitle=(TextView) findViewById(R.id.tvTitle);
        etHeight=(EditText) findViewById(R.id.etHeight);
        etName=(EditText)findViewById(R.id.etName);
        etMail=(EditText)findViewById(R.id.etMail);
        etPass=(EditText)findViewById(R.id.etPass);
        etPhone=(EditText)findViewById(R.id.etPhone);
        etWeight= (EditText) findViewById(R.id.etWeight);
        etId=(EditText)findViewById(R.id.etId);
        cbStayconnect=(CheckBox)findViewById(R.id.cbStayConnect);
        tvRegister=(TextView) findViewById(R.id.tvRegister);

        tvFemale=(TextView) findViewById(R.id.tvFemale);
        tvMale = (TextView) findViewById(R.id.tvMale);
        swMoF=(Switch) findViewById(R.id.switchFM);

        btn=(Button)findViewById(R.id.btn);


        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spFplace.setAdapter(adapter);
        //spFplace.setOnItemClickListener(this);


        stayConnect=false;
        registered=true;


        onVerificationStateChanged();
        regoption();


    }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        Boolean isChecked=settings.getBoolean("stayConnect",false);
        Intent si = new Intent(Register_Login.this,tafritim.class);
        if (refAuth.getCurrentUser()!=null && isChecked) {
            stayConnect=true;
            startActivity(si);
        }
    }

    /**
     * On activity pause - If logged in & asked to be remembered - kill activity.
     * <p>
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (stayConnect) finish();
    }

    private void regOption() {
        SpannableString spannableString = new SpannableString("Don't have an account?  Register here!");

        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                tvTitle.setText("Register");
                etHeight.setVisibility(View.VISIBLE);
                mDisplayDate.setVisibility(View.VISIBLE);
                etId.setVisibility(View.VISIBLE);
                etWeight.setVisibility(View.VISIBLE);
                etPhone.setVisibility(View.VISIBLE);
                etName.setVisibility(View.VISIBLE);
                tvFemale.setVisibility(View.VISIBLE);
                tvMale.setVisibility(View.VISIBLE);
                swMoF.setVisibility(View.VISIBLE);
                spFplace.setVisibility(View.VISIBLE);
                btn.setText("Register");
                registered=false;
                isUID=false;
                logOption();
            }
        };
        spannableString.setSpan(span, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(spannableString);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void logOption() {
        SpannableString spannableString = new SpannableString("Already have an account?  Login here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                tvTitle.setText("Login");
                etWeight.setVisibility(View.INVISIBLE);
                etHeight.setVisibility(View.INVISIBLE);
                etId.setVisibility(View.INVISIBLE);
                mDisplayDate.setVisibility(View.INVISIBLE);
                etName.setVisibility(View.INVISIBLE);
                etPhone.setVisibility(View.INVISIBLE);
                tvFemale.setVisibility(View.INVISIBLE);
                tvMale.setVisibility(View.INVISIBLE);
                swMoF.setVisibility(View.INVISIBLE);
                spFplace.setVisibility(View.INVISIBLE);
                btn.setText("Login");
                isUID=true;
                registered=true;
                regOption();
            }
        };
        spannableString.setSpan(span, 26, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(spannableString);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Logging in or Registering to the application
     * Using:   Firebase Auth with email & password
     *          Firebase Realtime database with the object User to the branch Users
     * If login or register process is Ok saving stay connect status & pass to next activity
     * <p>
     */
    public void logOrReg(View view) {
        email=etMail.getText().toString();
        password=etPass.getText().toString();

        if (registered) {
            if ((!password.isEmpty()) && (!email.isEmpty())) {

                final ProgressDialog pd = ProgressDialog.show(this, "Login", "Connecting...", true);
                refAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pd.dismiss();
                                if (task.isSuccessful()) {
                                    SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putBoolean("stayConnect", cbStayconnect.isChecked());
                                    editor.commit();
                                    Log.d("Register_Login", "signinUserWithEmail:success");
                                    Toast.makeText(Register_Login.this, "Login Success", Toast.LENGTH_LONG).show();
                                    Intent si = new Intent(Register_Login.this, tafritim.class);
                                    startActivity(si);
                                    finish();
                                } else {
                                    Log.d("Register_Login", "signinUserWithEmail:fail");
                                    Toast.makeText(Register_Login.this, "e-mail or password are wrong!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(Register_Login.this, "Please, fill all the necessary details.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            name=etName.getText().toString();
            phone=etPhone.getText().toString();
            id=etId.getText().toString();
            weight=etWeight.getText().toString();
            height=etHeight.getText().toString();
            places=spFplace.getSelectedItem().toString();


            if ((!name.isEmpty()) && (!email.isEmpty()) && (!password.isEmpty()) && (!phone.isEmpty()) && (!id.isEmpty()) && (!date.isEmpty()) && (!weight.isEmpty()) && (!height.isEmpty())) {

            /*    startPhoneNumberVerification(phone);
                onVerificationStateChanged();
                ProgressDialog.show(this, "login", "connecting.. ", true);
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                final EditText edittext = new EditText(this);
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                adb.setMessage("enter the code");
                adb.setTitle("Authentication");
                adb.setView(edittext);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        code = edittext.getText().toString();
                        if (!code.isEmpty())
                            verifyPhoneNumberWithCode(mVerificationId, code);
                    }
                });
                adb.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                ad = adb.create();
                ad.show();*/

                final ProgressDialog pd = ProgressDialog.show(this, "Register", "Registering...", true);
                refAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pd.dismiss();
                                if (task.isSuccessful()) {
                                    SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putBoolean("stayConnect", cbStayconnect.isChecked());
                                    editor.commit();
                                    Log.d("Register_Login", "createUserWithEmail:success");
                                    FirebaseUser user = refAuth.getCurrentUser();
                                    uid = user.getUid();
                                    userdb = new User(name, email, password, phone, id, date, weight, height, isFemale, places, uid, afterImage, beforeImage);
                                    refUsers.child(name).setValue(userdb);
                                    Toast.makeText(Register_Login.this, "Successful registration", Toast.LENGTH_LONG).show();
                                    Intent si = new Intent(Register_Login.this, tafritim.class);
                                    startActivity(si);
                                }
                                else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                        Toast.makeText(Register_Login.this, "User with e-mail already exist!", Toast.LENGTH_LONG).show();
                                    else {
                                        Log.w("Register_Login", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(Register_Login.this, "User create failed.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });

            }else{
                Toast.makeText(Register_Login.this, "Please, fill all the necessary details.", Toast.LENGTH_LONG).show();
            }
        }

    }


    /**
     * this function is called when the user wants to login.
     * the function sends sms to his phone number with a verification code.
     *
   //  * @param	phoneNumber the phone number of the user. The SMS is sent to this phone number.
     */
/*
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        mVerificationInProgress = true;
    }
*/
    /**
     * this function is called to check if the code the user wrote is the code he received and create a credential.
     * if he wrote a right code, "signInWithPhoneAuthCredential" function is called.
   //  * @param	code the code that the
     //* @param verificationId a verification identity to connect with firebase servers.
     */
   /* private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }*/

    /**
     * this function is called to sign in the user.
     * if the credential is proper the user is signs in and he sent to the next activity, depends on his status (worker or manager)
   //  * @param	credential a credential that everything was right and he can sign in.
     */
   /* private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        refAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("stayConnect", cbStayconnect.isChecked());
                            editor.putBoolean("firstRun", false);
                            editor.commit();

                            FirebaseUser user = refAuth.getCurrentUser();
                            uid = user.getUid();
                            if (!isUID) {
                                refUsers.child(name).child("uid").setValue(uid);
                            }
                            setUsersListener();
                        }

                        else {
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Register_Login.this, "wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }*/

    /**
     * this function checks the status of the verification, if it's completed, failed or inProgress.
     */
  /*  private void onVerificationStateChanged() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                mVerificationInProgress = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(Register_Login.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                   // etCode.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) { }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
            }
        };
    }
*/
    /**
     * this function connect the current user with his information in the database by checking his uid,
     * in purpose to check his status and sent him to the right activity.
     */

   /* public void setUsersListener() {
        user = refAuth.getCurrentUser();
        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (user.getUid().equals(data.getValue(User.class).getUid())){
                        currentUser=data.getValue(User.class);
                        if (progressDialog!=null) progressDialog.dismiss();
                            Intent si = new Intent(Register_Login.this, tafritim.class);
                            startActivity(si);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (progressDialog!=null) progressDialog.dismiss();
            }
        };
    }
    */

    /**
     * if the user is a female, she will have an option to choose if she is pregnant (in order to get supplements) = changed ittttt
     * worked in the version created in 22/1/20
     * @param view
     */
    public void MaleOrFemale(View view) {
        if (swMoF.isChecked()){
            isFemale=true;
        }
        else{
            isFemale=false;

        }
    }

  /*  public void showDataInSpinner(ArrayList<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, data
        ); //Create the Adapter to set the data
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //Set the layout resource to create the drop down views.
        spFplace.setAdapter(adapter); //Set the data to the spinner
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Read from the database
        refPlaces.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String tmp = ds.getValue(String.class);
                Log.d(TAG, "Value is: " + tmp);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }*/


}
