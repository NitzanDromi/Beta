package com.example.beta;

/**
 * @author		Nitzan Dromi <address @nitzandr13@gmail.com>
 * @version	    1.2(current version number of program)
 * @since		29/12/2019 (the date of the package the class was added)
 * Beta version of the application.
 * has:
 * main activity (contains the app icon image - "loading the app" activity)
 * login/ register activity (the same activity, it's purpose can change)
 * menus activity
 * class for all the variables related to FireBase
 * class for the User's tree in FireBase
 * recipes activity
 * matcon (single recipe) activity (the user can reach to it only through the recipes activity)
 * substitutes activity
 * tosafim (supplements) activity
 * credits activity
 * and a main menu - in order to easily move from one activity to the other
 */


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static com.example.beta.FBref.refAuth;
import static com.example.beta.FBref.refPlaces;
import static com.example.beta.FBref.refUsers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Register_Login extends AppCompatActivity {

    private static final String TAG="MainActivity";

    TextView tvTitle, tvRegister, tvFemale, tvMale;
    EditText etName, etLastName, etPhone, etMail, etWeight, etId, etHeight;
    CheckBox cbStayconnect;
    Switch swMoF;
    Boolean isFemale= false;

    TextView mDisplayDate;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    Button btn;

    List<String> titleList = new ArrayList<String>();

    Spinner spFplace;

    User userdb;

    String mVerificationId, code,lastName="", fstName="", phone="+972", phoneInput="", email="", id="",currentWeight="", weight="", height="", uid="", date="", places="", beforeImage="empty", afterImage="empty";
    Boolean stayConnect, registered=false, isUID = false;
    Boolean mVerificationInProgress = false;

    AlertDialog ad, adCode;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    ValueEventListener usersListener;
    FirebaseUser user;

    Boolean firstRun=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__login);

        tvTitle=(TextView) findViewById(R.id.tvTitle);
        etHeight=(EditText) findViewById(R.id.etHeight);
        etName=(EditText)findViewById(R.id.etName);
        etLastName=(EditText)findViewById(R.id.etLastName);
        etMail=(EditText)findViewById(R.id.etMail);
        etPhone=(EditText)findViewById(R.id.etPhone);
        etWeight= (EditText) findViewById(R.id.etWeight);
        etId=(EditText)findViewById(R.id.etId);
        cbStayconnect=(CheckBox)findViewById(R.id.cbStayConnect);
        tvRegister=(TextView) findViewById(R.id.tvRegister);

        tvFemale=(TextView) findViewById(R.id.tvFemale);
        tvMale = (TextView) findViewById(R.id.tvMale);
        swMoF=(Switch) findViewById(R.id.switchFM);

        btn=(Button)findViewById(R.id.btn);

        spFplace=(Spinner) findViewById(R.id.spPlace);

        /**
         * this function uploads the information from the firebase tree - Places - to a spinner
         * using the reference - refPlaces and a Value event listener.
         */
        refPlaces.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                titleList.clear();

                for (DataSnapshot data : ds.getChildren()){
                    String info=data.getValue().toString();
                    titleList.add(info);
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
 * date picker - in order to choose the user's birth day (to know his age).
 * ended programming in 22/1 - the program works.(?)
 */
        mDisplayDate=(TextView)findViewById(R.id.tvBDate);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                year=year-15;
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Register_Login.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        /**
         * after the dialog is opened, this function gets the date the user chose.
         * if the user is under the minimal age to use this app (under 15), the function sets a toast to tell him that
         */
        mDateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
               // Log.d(TAG, "onDataSet: dd/mm/yyyy: "+ dayOfMonth +"/" + month +"/" +year );
                date = dayOfMonth +"/" + month +"/" +year;
                int currentYear=Calendar.getInstance().get(Calendar.YEAR);
                if ((currentYear-year)>=15)
                    mDisplayDate.setText(date);
                else
                    Toast.makeText(Register_Login.this, "You are too young to use this application!", Toast.LENGTH_SHORT).show();
            }
        };


        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        firstRun=settings.getBoolean("firstRun",true);
        stayConnect = false;
    /**
     * this condition checks if this is the first run on the user's device
     * if so, the function sends th user directly to the registration option
     * if not, it sends him to the login option
     */
        if (firstRun) {
            isUID=false;
            onVerificationStateChanged();
            regOption();
        }
        else {
            isUID=true;
            registered = true;
            onVerificationStateChanged();
            logOption();
        }
    }

    /**
     * on activity start - if the user exists & asked to  be remembered -transfer to the next activity (tafritim)
     */
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
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (stayConnect) finish();
    }

    /**
     * this function is called when the user is in the login option but he needs to register
     * OR
     * when the application is running for the first time in the user's device
     * the function "changes" the screen for the register option.
     */
    private void regOption() {
        tvTitle.setText("Register");
        etHeight.setVisibility(View.VISIBLE);
        mDisplayDate.setVisibility(View.VISIBLE);
        etId.setVisibility(View.VISIBLE);
        etWeight.setVisibility(View.VISIBLE);
        etLastName.setVisibility(View.VISIBLE);
        etName.setVisibility(View.VISIBLE);
        tvFemale.setVisibility(View.VISIBLE);
        tvMale.setVisibility(View.VISIBLE);
        swMoF.setVisibility(View.VISIBLE);
        spFplace.setVisibility(View.VISIBLE);
        etMail.setVisibility(View.VISIBLE);
        btn.setText("Register");

        SpannableString spannableString = new SpannableString("Already have an account?  Login here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                registered=true;
                isUID=true;
                logOption();
            }
        };
        spannableString.setSpan(span, 26, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(spannableString);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * this function is called when the user is in the register option but he needs to log in.
     * the function "changes" the screen for the login option
     * It is also the default option (the Login activity) unless the user is entering the app for the first time
     */
    private void logOption() {
        tvTitle.setText("Login");
        etWeight.setVisibility(View.INVISIBLE);
        etHeight.setVisibility(View.INVISIBLE);
        etId.setVisibility(View.INVISIBLE);
        mDisplayDate.setVisibility(View.INVISIBLE);
        etName.setVisibility(View.INVISIBLE);
        etLastName.setVisibility(View.INVISIBLE);
        tvFemale.setVisibility(View.INVISIBLE);
        tvMale.setVisibility(View.INVISIBLE);
        swMoF.setVisibility(View.INVISIBLE);
        spFplace.setVisibility(View.INVISIBLE);
        etMail.setVisibility(View.INVISIBLE);
        btn.setText("Login");
        registered=true;

        SpannableString spannableString = new SpannableString("Don't have an account?  Register here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                isUID=false;
                registered=false;
                regOption();
            }
        };
        spannableString.setSpan(span, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(spannableString);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Logging in or Registering to the application
     * Using:   Firebase Auth with phone and sms code
     *          Firebase Realtime database with the object User to the branch Users
     * If login or register process is Ok saving stay connect status & pass to next activity
     * @param view
     */
    public void logOrReg(View view) {
        phoneInput=etPhone.getText().toString();
        if (registered) {
            if (!phoneInput.isEmpty()) {

                if ((phoneInput.length() != 10) || (!phoneInput.substring(0, 2).equals("05")) || Pattern.matches("[a-zA-Z]+", phoneInput) == true) {
                    etPhone.setError("invalid phone number");
                } else {
                    if (phone.equals("+972"))
                        for (int x = 1; x <= 9; x++)
                            phone = phone + phoneInput.charAt(x);

                    startPhoneNumberVerification(phone);
                    onVerificationStateChanged();

                    AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    final EditText et = new EditText(this);
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                    adb.setMessage("enter the code you received");
                    adb.setTitle("Authentication");
                    adb.setView(et);
                    adb.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int whichButton) {
                            code = et.getText().toString();
                            if (!code.isEmpty())
                                verifyPhoneNumberWithCode(mVerificationId, code);
                            dialogInterface.dismiss();
                        }
                    });
                    adb.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int whichButton) {
                            dialogInterface.cancel();
                        }
                    });
                    adCode = adb.create();
                    adCode.show();
                }
            }else {
                Toast.makeText(Register_Login.this, "Please, enter your phone number.", Toast.LENGTH_LONG).show();
            }

        }
        else {
            fstName=etName.getText().toString();
            lastName=etLastName.getText().toString();
            phoneInput=etPhone.getText().toString();
            id=etId.getText().toString();
            weight=etWeight.getText().toString();
            currentWeight=weight;
            height=etHeight.getText().toString();
            places=spFplace.getSelectedItem().toString();
            email=etMail.getText().toString();
            if (swMoF.isChecked()){
                isFemale=true;
            }
            else{
                isFemale=false;

            }

            if ((!fstName.isEmpty()) && (!email.isEmpty()) &&(!lastName.isEmpty())&&
                    (!phoneInput.isEmpty()) && (!id.isEmpty()) && (!date.isEmpty()) && (!weight.isEmpty()) && (!height.isEmpty())) {

                if (places.equals("select your meetings location")) {
                        Toast.makeText(this, "please choose your meetings location", Toast.LENGTH_SHORT).show();
                } else {
                    if (Pattern.matches("[a-zA-Z]+", id) == true || id.length() != 9) {
                        etId.setError("invalid id");
                    } else {
                        if (((!email.endsWith(".com")) || (!email.endsWith(".il"))) && (email.indexOf("@") == (-1)))
                            etMail.setError("invalid e-mail");
                        else {
                            if ((phoneInput.length() != 10) || (!phoneInput.substring(0, 2).equals("05")) || Pattern.matches("[a-zA-Z]+", phoneInput) == true) {
                                etPhone.setError("invalid phone number");
                            } else {
                                if (phone.equals("+972")) {
                                    for (int x = 1; x <= 9; x++)
                                        phone = phone + phoneInput.charAt(x);
                                }

                                startPhoneNumberVerification(phone);
                                onVerificationStateChanged();

                                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                                final EditText et = new EditText(this);
                                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                                adb.setMessage("enter the code you received");
                                adb.setTitle("Authentication");
                                adb.setView(et);
                                adb.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int whichButton) {
                                            code = et.getText().toString();
                                            if (!code.isEmpty())
                                                verifyPhoneNumberWithCode(mVerificationId, code);
                                            dialogInterface.dismiss();
                                        }
                                });
                                adb.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int whichButton) {
                                            dialogInterface.cancel();
                                        }
                                });
                                ad = adb.create();
                                ad.show();
                            }
                        }
                    }
                }
            } else {
                    Toast.makeText(Register_Login.this, "Please, fill all the necessary details.", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * this function is called when the user wants to sign up (register) or sign in (login).
     * the function sends sms to his phone with a verification code.
     * @param	phoneNumber the user's phone number. The SMS is sent to this phone number.
     */

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,           // Phone number to verify
                40,                 // Timeout duration
                TimeUnit.SECONDS,      // Unit of timeout
                this,          // Activity (for callback binding)
                mCallbacks);          // OnVerificationStateChangedCallbacks
        mVerificationInProgress = true;
    }

    /**
     * this function is called in order to check if the code the user wrote is the code he received and create a credential.
     * if he wrote a right code, "signInWithPhoneAuthCredential" function is called.
     * @param	code the code that the user received
     * @param verificationId a verification identity to connect with firebase servers.
     */
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    /**
     * this function is called to sign in or sign up the user.
     * if the credential is proper the user is signs in and he sent to the next activity
     * @param	credential is a credential that everything was right and the user can sign in.
     */
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
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
                                userdb = new User(fstName, lastName, email, phone, id, date, weight, weight, height, isFemale, places, uid, afterImage, beforeImage);
                                refUsers.child(fstName + " " + lastName).setValue(userdb);
                            }
                            setUsersListener();
                        }

                        else {
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Register_Login.this, "wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * this function connect the current user with his information in the database by checking his uid,
     * in order to check his status and sent him to the next activity.
     */

    public void setUsersListener() {
        user = refAuth.getCurrentUser();
        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (user.getUid().equals(data.getValue(User.class).getUid())){
                        if (registered)
                            adCode.dismiss();
                        else
                            ad.dismiss();;
                        Intent si = new Intent(Register_Login.this, tafritim.class);
                        startActivity(si);
                        finish();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        refUsers.addValueEventListener(usersListener);

    }

    /**
     * this function checks the status of the verification, if it's completed, failed or inProgress.
     */
    private void onVerificationStateChanged() {
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
                    etPhone.setError("Invalid phone number");
                } else {
                    if (e instanceof FirebaseTooManyRequestsException) {
                    }
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
            }
        };
    }
}
