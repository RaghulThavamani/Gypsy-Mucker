package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    int SPLASH_TIME = 3000; //This is 3 seconds
    DatabaseReference myRefEmail,checkMechanicProfile;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do any action here. Now we are moving to next page
                final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null  && firebaseUser.isEmailVerified() ){customerAutoLogin();
                    ownerAutoLogin();
                    mechanicAutoLogin();}else {startActivity(new Intent(MainActivity.this,GmMainLoginPage.class));finish();}




                //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
            }
        }, SPLASH_TIME);
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

                                    startActivity(new Intent(MainActivity.this,MechanicHomePage.class));
                                    Toast.makeText(MainActivity.this,"Welcome..\uD83D\uDE1C" ,Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    startActivity(new Intent(MainActivity.this,ShowMechanicWasDeleted.class));
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

                        startActivity(new Intent(MainActivity.this,CustomerHomePage.class));
                        Toast.makeText(MainActivity.this,"Hi Welcome..\uD83D\uDE1C" ,Toast.LENGTH_LONG).show();
                        finish();
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

                        startActivity(new Intent(MainActivity.this,OwnerHomePage.class));
                        Toast.makeText(MainActivity.this,"Hi Owner Welcome..\uD83D\uDE1C" ,Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
    }
}
