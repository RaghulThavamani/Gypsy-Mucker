package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class CustomerRegisterPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ViewFlipper viewFlipper;
    Button nextBtn,preBtn,regBtn;
    long mLastClickTime = 0;
    EditText userName,mobile,licNo,email,password,confirmPassword;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference,databaseReference2;

    Spinner dropDown;
    String[] vehicle={"   Two Wheeler","   Four Wheeler"};
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register_page);

        viewFlipper = findViewById(R.id.viewFlipper);
        nextBtn = findViewById(R.id.nextBtn);

        userName=findViewById(R.id.cUserName);
        mobile=findViewById(R.id.cMobile);
        licNo=findViewById(R.id.cLicenseNo);
        email=findViewById(R.id.cEmail);
        password=findViewById(R.id.cPassword);
        confirmPassword=findViewById(R.id.cConfirmPassword);

        regBtn=findViewById(R.id.submitBtn);
        preBtn=findViewById(R.id.preBtn);

        dropDown=findViewById(R.id.vehicleInfoDropDown);
        dropDown.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the Availability of shop Status list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,vehicle);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner1
        dropDown.setAdapter(aa);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("PermanentCustomer");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Email").child("CustomerEmail");

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                toast1();


            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                toast2();
            }
        });

        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                viewFlipper.showPrevious();
                regBtn.setVisibility(View.GONE);
                preBtn.setVisibility(View.GONE);
                nextBtn.setVisibility(View.VISIBLE);
            }
        });
    }
    public void toast1(){

        String user= userName.getText().toString().trim();
        if(TextUtils.isEmpty(user)){
            Toast.makeText(CustomerRegisterPage.this,"Enter User Name",Toast.LENGTH_LONG).show();return;}

        String mob = mobile.getText().toString().trim();
        if(mob.isEmpty()){
            Toast.makeText(CustomerRegisterPage.this,"Enter Mobile Number",Toast.LENGTH_LONG).show();return;}

        if(mob.length() != 10){
            Toast.makeText(CustomerRegisterPage.this,"Enter Valid Mobile Number",Toast.LENGTH_LONG).show();return;}

        String lin = licNo.getText().toString().trim();
        if(lin.isEmpty()){
            Toast.makeText(CustomerRegisterPage.this,"Enter License Number",Toast.LENGTH_LONG).show();return;}
        viewFlipper.showNext();
        viewFlipper.stopFlipping();
        nextBtn.setVisibility(View.GONE);
        regBtn.setVisibility(View.VISIBLE);
        preBtn.setVisibility(View.VISIBLE);

    }

    public void toast2() {
        String cEmail = email.getText().toString().trim();
        if (TextUtils.isEmpty(cEmail)) {
            Toast.makeText(CustomerRegisterPage.this, "Enter Email-Id", Toast.LENGTH_LONG).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(cEmail).matches()) {
            Toast.makeText(CustomerRegisterPage.this, " Enter Valid Email-Id", Toast.LENGTH_LONG).show();
            return;
        }

        String pass = password.getText().toString().trim();
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(CustomerRegisterPage.this, "Enter Password", Toast.LENGTH_LONG).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(CustomerRegisterPage.this, "Password is Too Short!", Toast.LENGTH_LONG).show();
            return;
        }


        String cnfmPassword = confirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(cnfmPassword)) {
            Toast.makeText(CustomerRegisterPage.this, "Enter Confirm Password", Toast.LENGTH_LONG).show();
            return;
        }

        if (!pass.equals(cnfmPassword)) {
            Toast.makeText(CustomerRegisterPage.this, "Enter Correct Password", Toast.LENGTH_LONG).show();
            return;
        }
        registration();

    }

    public void registration(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registering...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        String cEmail = email.getText().toString().trim();
        if(TextUtils.isEmpty(cEmail)){
            Toast.makeText(CustomerRegisterPage.this,"Enter Email-Id",Toast.LENGTH_LONG).show();return; }

        String pass = password.getText().toString().trim();
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(CustomerRegisterPage.this,"Enter Password",Toast.LENGTH_LONG).show();return;}

        if(pass.length() < 6){
            Toast.makeText(CustomerRegisterPage.this,"Password is Too Short!",Toast.LENGTH_LONG).show();return;}


        String cnfmPassword = confirmPassword.getText().toString().trim();
        if(TextUtils.isEmpty(cnfmPassword)){
            Toast.makeText(CustomerRegisterPage.this,"Enter Confirm Password",Toast.LENGTH_LONG).show();return;}

        if(!pass.equals(cnfmPassword)){
            Toast.makeText(CustomerRegisterPage.this,"Enter Correct Password",Toast.LENGTH_LONG).show();return;}

        if(cnfmPassword.equals(pass)) {
            mAuth.createUserWithEmailAndPassword(cEmail, pass)
                    .addOnCompleteListener(CustomerRegisterPage.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            addCustomerData();
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(getApplicationContext(),GmMainLoginPage.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            Toast.makeText(CustomerRegisterPage.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                        }else {
                                            Toast.makeText(CustomerRegisterPage.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss(); return;
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(CustomerRegisterPage.this, "Registration Failed or Email-Id Already Registered", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                return;

                            }
                        }});
        }

    }

    public void addCustomerData(){
        CustomerDetails customerDetails= new CustomerDetails();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


        String user= userName.getText().toString().trim();
        String mob = mobile.getText().toString().trim();
        String lin = licNo.getText().toString().trim();
        String Email = email.getText().toString().trim();
        String vehicle = dropDown.getSelectedItem().toString().trim();

        customerDetails.setCustomerName(user);
        customerDetails.setMobile(mob);
        customerDetails.setLicenseNo(lin);
        customerDetails.setEmail(Email);
        customerDetails.setVehicle(vehicle);
        customerDetails.setId(id);

        String repCusEmail = Email.replace(".",",");

        databaseReference.child(repCusEmail).setValue(customerDetails);

        CustomerDetails customerDetails1 = new CustomerDetails();
        customerDetails1.setEmail(Email);
        databaseReference2.push().setValue(customerDetails1);


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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
