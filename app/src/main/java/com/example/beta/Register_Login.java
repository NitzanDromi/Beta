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
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import static com.example.beta.FBref.refAuth;
import static com.example.beta.FBref.refUsers;

public class Register_Login extends AppCompatActivity {

    private static final String TAG="MainActivity";

    TextView tvTitle, tvRegister, tvFemale, tvMale, tvPregnant;
    EditText etName, etPhone, etMail, etPass, etWeight, etId, etHeight;
    CheckBox cbStayconnect;
    Switch swMoF;
    ToggleButton tbPreg;
    Boolean Female, Preg;

    TextView mDisplayDate;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    Button btn;
    Spinner spFplace;
    String name, phone, email, password, id, weight, height, uid, date;
    User userdb;
    Boolean stayConnect, registered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__login);
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
        tvPregnant=(TextView) findViewById(R.id.tvPregnant);
        tbPreg =(ToggleButton) findViewById(R.id.tbPregnent);
        spFplace=(Spinner) findViewById(R.id.spPlace);

        btn=(Button)findViewById(R.id.btn);

        stayConnect=false;
        registered=true;

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

    private void regoption() {
        SpannableString ss = new SpannableString("Don't have an account?  Register here!");
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
                spFplace.setVisibility(View.VISIBLE);
                btn.setText("Register");
                registered=false;



                logoption();
            }
        };
        ss.setSpan(span, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(ss);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void updateUI(FirebaseUser currentUser) {
    }


    private void logoption() {
        SpannableString ss = new SpannableString("Already have an account?  Login here!");
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
                tvPregnant.setVisibility(View.INVISIBLE);
                tbPreg.setVisibility(View.INVISIBLE);
                btn.setText("Login");
                registered=true;
                regoption();
            }
        };
        ss.setSpan(span, 26, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(ss);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Logging in or Registering to the application
     * Using:   Firebase Auth with email & password
     *          Firebase Realtime database with the object User to the branch Users
     * If login or register process is Ok saving stay connect status & pass to next activity
     * <p>
     */
    public void logorreg(View view) {
        name=etName.getText().toString();
        phone=etPhone.getText().toString();
        email=etMail.getText().toString();
        password=etPass.getText().toString();
        id=etId.getText().toString();
        weight=etWeight.getText().toString();
        height=etHeight.getText().toString();

        if (registered) {

            final ProgressDialog pd=ProgressDialog.show(this,"Login","Connecting...",true);
            refAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                                SharedPreferences.Editor editor=settings.edit();
                                editor.putBoolean("stayConnect",cbStayconnect.isChecked());
                                editor.commit();
                                Log.d("Register_Login", "signinUserWithEmail:success");
                                Toast.makeText(Register_Login.this, "Login Success", Toast.LENGTH_LONG).show();
                                Intent si = new Intent(Register_Login.this,tafritim.class);
                                startActivity(si);
                            } else {
                                Log.d("Register_Login", "signinUserWithEmail:fail");
                                Toast.makeText(Register_Login.this, "e-mail or password are wrong!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {

            if ((!name.isEmpty()) && (!email.isEmpty()) && (!password.isEmpty()) && (!phone.isEmpty()) && (!id.isEmpty()) && (!date.isEmpty()) && (!weight.isEmpty()) && (!height.isEmpty())) {

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
                                    userdb = new User(name, email, password, phone, id, date, weight, height, Female, Preg, uid);
                                    refUsers.child(name).setValue(userdb);
                                    Toast.makeText(Register_Login.this, "Successful registration", Toast.LENGTH_LONG).show();
                                    Intent si = new Intent(Register_Login.this, tafritim.class);
                                    startActivity(si);
                                } else {
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
     * if the user is a female, she will have an option to choose if she is pregnant (in order to get supplements
     * worked in the version created in 22/1/20
     * @param view
     */
    public void MaleOrFemale(View view) {
        if (swMoF.isChecked()){
            tvPregnant.setVisibility(View.VISIBLE);
            tbPreg.setVisibility(View.VISIBLE);
            Female=true;
        }
        else{
            tvPregnant.setVisibility(View.INVISIBLE);
            tbPreg.setVisibility(View.INVISIBLE);
            Female=false;

        }
    }

    public void Pregnant(View view) {
        if (tbPreg.isChecked())
            Preg=true;
        else
            Preg=false;
    }
}
