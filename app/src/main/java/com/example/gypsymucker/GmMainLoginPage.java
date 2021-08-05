package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

public class GmMainLoginPage extends AppCompatActivity {

    EditText mLoginEmail,mLoginPassword;
    RadioGroup radioGrp;
    RadioButton cusRadioBtn,gmRadioBtn,gmOwnerRadioBtn;
    Button mLoginBtn;
    DatabaseReference myRefEmail,checkMechanicProfile;

    private FirebaseAuth mAuth;
    long mLastClickTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gm_main_login_page);
        mAuth = FirebaseAuth.getInstance();


        customerAutoLogin();
        ownerAutoLogin();
        mechanicAutoLogin();

        cusRadioBtn = findViewById(R.id.cusRadioBtn);
        gmRadioBtn = findViewById(R.id.gmRadioBtn);
        gmOwnerRadioBtn= findViewById(R.id.gmOwnerRadioBtn);
        radioGrp = findViewById(R.id.radioGrp);
        mLoginBtn = findViewById(R.id.mLoginBtn);
        mLoginEmail=findViewById(R.id.mLoginEmail);
        mLoginPassword=findViewById(R.id.mLoginPassword);



        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(gmOwnerRadioBtn.isChecked()){
                    ownerToast();
                }
                else if (gmRadioBtn.isChecked()){
                    mechanicToast();
                }
                else if (cusRadioBtn.isChecked()){
                    customerToast();
                }


            }
        });


    }


    public void customerToast(){
        final String email=mLoginEmail.getText().toString().trim();
        final String password=mLoginPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(GmMainLoginPage.this,"Please Enter Email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(GmMainLoginPage.this,"Please Enter Password",Toast.LENGTH_LONG).show();
            return;
        }

        if(password.length()<6){
            Toast.makeText(GmMainLoginPage.this,"Password is Too Short",Toast.LENGTH_LONG).show();
            return;
        }
        customerLogin();

    }

    public void customerLogin(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login Processing...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        final String email=mLoginEmail.getText().toString().trim();
        final String password=mLoginPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(GmMainLoginPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            if (firebaseUser != null  && firebaseUser.isEmailVerified() ){


                                myRefEmail = FirebaseDatabase.getInstance().getReference("Email").child("CustomerEmail");


                                myRefEmail.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                String keys = data.child("email").getValue().toString();
                                                System.out.println("Login Page Email:" + keys);
                                                if (keys.equals(email)) {
                                                    startActivity(new Intent(GmMainLoginPage.this, CustomerHomePage.class));
                                                    Toast.makeText(GmMainLoginPage.this, "Hi Welcome..\uD83E\uDD29", Toast.LENGTH_LONG).show();
                                                    progressDialog.dismiss();
                                                    mLoginEmail.setText("");
                                                    mLoginPassword.setText("");
                                                }
                                            }
                                        }else {
                                            Toast.makeText(GmMainLoginPage.this, "Who are You..\uD83D\uDE21", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                Toast.makeText(GmMainLoginPage.this, "Verify your Email Address!", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }else {
                            Toast.makeText(GmMainLoginPage.this,task.getException().getMessage().toString()+"\n So Register Now",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }

                    }
                });

    }

    public void mechanicToast(){
        final String email=mLoginEmail.getText().toString().trim();
        final String password=mLoginPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(GmMainLoginPage.this,"Please Enter Email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(GmMainLoginPage.this,"Please Enter Password",Toast.LENGTH_LONG).show();
            return;
        }

        if(password.length()<6){
            Toast.makeText(GmMainLoginPage.this,"Password is Too Short",Toast.LENGTH_LONG).show();
            return;
        }
        mechanicLogin();

    }

    public void mechanicLogin(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login Processing...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        final String email=mLoginEmail.getText().toString().trim();
        final String password=mLoginPassword.getText().toString().trim();
        final String repEmail = email.replace(".",",");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(GmMainLoginPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            if (firebaseUser != null  && firebaseUser.isEmailVerified() ){


                                myRefEmail = FirebaseDatabase.getInstance().getReference("Email").child("MechanicEmail");
                                myRefEmail.orderByChild("mechanicEmail").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            for (DataSnapshot data : dataSnapshot.getChildren()) {

                                                String keys = data.child("mechanicEmail").getValue().toString();
                                                if (keys.equals(email)) {

                                                    checkMechanicProfile = FirebaseDatabase.getInstance().getReference("PermanentMechanic").child(repEmail);
                                                    checkMechanicProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {


                                                                progressDialog.dismiss();
                                                                startActivity(new Intent(GmMainLoginPage.this, MechanicHomePage.class));
                                                                Toast.makeText(GmMainLoginPage.this, "Are you ready to jump..\uD83D\uDE1C", Toast.LENGTH_LONG).show();
                                                                mLoginEmail.setText("");
                                                                mLoginPassword.setText("");

                                                            } else {
                                                                progressDialog.dismiss();
                                                                startActivity(new Intent(GmMainLoginPage.this,ShowMechanicWasDeleted.class));
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });






                                                }
                                            }

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(GmMainLoginPage.this, "Who are You..\uD83D\uDE21", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });



                            } else {progressDialog.dismiss();
                                Toast.makeText(GmMainLoginPage.this, "Verify your Email Address!", Toast.LENGTH_LONG).show();
                            }
                        }else {progressDialog.dismiss();
                            Toast.makeText(GmMainLoginPage.this,task.getException().getMessage().toString()+"\n So Register Now",Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }

    public void ownerToast(){
        final String email=mLoginEmail.getText().toString().trim();
        final String password=mLoginPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(GmMainLoginPage.this,"Please Enter Email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(GmMainLoginPage.this,"Please Enter Password",Toast.LENGTH_LONG).show();
            return;
        }

        if(password.length()<6){
            Toast.makeText(GmMainLoginPage.this,"Password is Too Short",Toast.LENGTH_LONG).show();
            return;
        }
        ownerLogin();

    }

    public void ownerLogin(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login Processing...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        final String email=mLoginEmail.getText().toString().trim();
        final String password=mLoginPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(GmMainLoginPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            if (firebaseUser != null  && firebaseUser.isEmailVerified() ){


                                myRefEmail = FirebaseDatabase.getInstance().getReference("Email").child("OwnersEmail");


                                myRefEmail.orderByChild("ownerEmail").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                String keys = data.child("ownerEmail").getValue().toString();
                                                System.out.println("Login Page Email:" + keys);
                                                if (keys.equals(email)) {
                                                    startActivity(new Intent(GmMainLoginPage.this, OwnerHomePage.class));
                                                    Toast.makeText(GmMainLoginPage.this, "Hi Monkeys Owner..\uD83D\uDE1C", Toast.LENGTH_LONG).show();
                                                    progressDialog.dismiss();
                                                    mLoginEmail.setText("");
                                                    mLoginPassword.setText("");
                                                }
                                            }
                                        }else {
                                            Toast.makeText(GmMainLoginPage.this, "Who are You..\uD83D\uDE21", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                Toast.makeText(GmMainLoginPage.this, "Verify your Email Address!", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }else {
                            Toast.makeText(GmMainLoginPage.this,task.getException().getMessage().toString()+"\n So Register Now",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }

                    }
                });

    }

    public void mechanicAutoLogin(){
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();


        if (firebaseUser != null  && firebaseUser.isEmailVerified() ){

           final String em = mAuth.getCurrentUser().getEmail();
            final String repEmail = em.replace(".",",");

                        myRefEmail = FirebaseDatabase.getInstance().getReference("Email").child("MechanicEmail");
                        myRefEmail.orderByChild("mechanicEmail").equalTo(em).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    checkMechanicProfile = FirebaseDatabase.getInstance().getReference("PermanentMechanic").child(repEmail);
                                    checkMechanicProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {

                                    startActivity(new Intent(GmMainLoginPage.this,MechanicHomePage.class));
                                    Toast.makeText(GmMainLoginPage.this,"Welcome..\uD83D\uDE1C" ,Toast.LENGTH_LONG).show();
                                            } else {
                                                startActivity(new Intent(GmMainLoginPage.this,ShowMechanicWasDeleted.class));
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });












        }

    }

    public void customerAutoLogin(){

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();


        if (firebaseUser != null  && firebaseUser.isEmailVerified() ){

            String em = mAuth.getCurrentUser().getEmail();
            myRefEmail = FirebaseDatabase.getInstance().getReference("Email").child("CustomerEmail");


            myRefEmail.orderByChild("email").equalTo(em).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        String keys = data.getKey();
                        System.out.println(keys);

                        startActivity(new Intent(GmMainLoginPage.this,CustomerHomePage.class));
                        Toast.makeText(GmMainLoginPage.this,"Hi Welcome..\uD83D\uDE1C" ,Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
    }

    public void ownerAutoLogin(){
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();


        if (firebaseUser != null  && firebaseUser.isEmailVerified() ){

            String em = mAuth.getCurrentUser().getEmail();
            myRefEmail = FirebaseDatabase.getInstance().getReference("Email").child("OwnersEmail");


            myRefEmail.orderByChild("ownerEmail").equalTo(em).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        String keys = data.getKey();
                        System.out.println(keys);

                        startActivity(new Intent(GmMainLoginPage.this,OwnerHomePage.class));
                        Toast.makeText(GmMainLoginPage.this,"Hi Owner Welcome..\uD83D\uDE1C" ,Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
    }

    public void directTOForgotIntent(View view){
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        startActivity(new Intent(GmMainLoginPage.this,GmForgotPasswordPage.class));
    }

    public void directTORegisterIntent(View view) {

        if(gmOwnerRadioBtn.isChecked()){
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            startActivity(new Intent(GmMainLoginPage.this,OwnerRegisterPage.class));
        }
        else if (cusRadioBtn.isChecked()){
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            startActivity(new Intent(GmMainLoginPage.this,CustomerRegisterPage.class));
        }
        else if (gmRadioBtn.isChecked()){
            Toast.makeText(this, "Only Owner can Add Mechanic", Toast.LENGTH_SHORT).show();
        }

    }

    public void directTOAdminLogin(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        final androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_admin_login, null);
        alert.setView(view1);

        final EditText mAdminEmail = view1.findViewById(R.id.mAdminEmail);
        final EditText mAdminPassword = view1.findViewById(R.id.mAdminPassword);
        final Button mLoginBtn = view1.findViewById(R.id.mLoginBtn);

        final androidx.appcompat.app.AlertDialog alertDialog = alert.create();


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                final String email = mAdminEmail.getText().toString().trim();
                final String password = mAdminPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(GmMainLoginPage.this,"Enter Email!", Toast.LENGTH_SHORT).show(); return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(GmMainLoginPage.this,"Enter Password!", Toast.LENGTH_SHORT).show(); return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(GmMainLoginPage.this);
                progressDialog.setTitle("Login Processing...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(GmMainLoginPage.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    myRefEmail = FirebaseDatabase.getInstance().getReference("Email").child("Admin");
                                    myRefEmail.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()) {
                                                String keys = dataSnapshot.child("email").getValue().toString();
                                                if (keys.equals(email)) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(GmMainLoginPage.this,"Welcome Chief!!!",Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(GmMainLoginPage.this,GmAdminHomePage.class));
                                                    mAdminEmail.setText("");
                                                    mAdminPassword.setText("");
                                                    alertDialog.dismiss();
                                                }else {Toast.makeText(GmMainLoginPage.this,"You are Not Permitted!",Toast.LENGTH_LONG).show();progressDialog.dismiss();
                                                mAdminEmail.setText("");
                                                mAdminPassword.setText("");
                                                alertDialog.dismiss();
                                                }


                                            }

                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }else {
                                    Toast.makeText(GmMainLoginPage.this,task.getException().getMessage().toString(),Toast.LENGTH_LONG).show();progressDialog.dismiss();
                                    mAdminEmail.setText("");
                                    mAdminPassword.setText("");
                                    alertDialog.dismiss();
                                }

                            }
                        });
            }
        });


        alertDialog.show();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }


}
