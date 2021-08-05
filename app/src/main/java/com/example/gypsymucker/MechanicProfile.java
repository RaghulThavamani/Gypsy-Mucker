package com.example.gypsymucker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MechanicProfile extends AppCompatActivity {

    TextView shopName1,mechanicMobile1,mechanicName1,emailMechanic1,vehicleInfoMec11;
    ImageView imageUrl;
    DatabaseReference databaseReference2;
    FirebaseAuth mAuth;
    Button logout;

    long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_profile);


        shopName1 = (TextView)findViewById(R.id.shopName1);
        mechanicMobile1 = (TextView)findViewById(R.id.mechanicMobile1);
        mechanicName1 = (TextView)findViewById(R.id.mechanicName1);
        emailMechanic1 = (TextView)findViewById(R.id.emailMechanic1);
        vehicleInfoMec11 = (TextView)findViewById(R.id.vehicleInfoMec1);

        imageUrl=findViewById(R.id.imageUrl1);

        logout=findViewById(R.id.logout);


        mAuth = FirebaseAuth.getInstance();

        String emailMechanic = mAuth.getCurrentUser().getEmail().toString().trim();
        final String encodeEmail = emailMechanic.replace(".",",");

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                FirebaseAuth.getInstance().signOut();
                                finish();
                                Intent intent = new Intent(getApplicationContext(),GmMainLoginPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MechanicProfile.this);
                builder.setMessage("Are you sure? You want to logout!").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });


                databaseReference2 = FirebaseDatabase.getInstance().getReference("PermanentMechanic").child(encodeEmail);
                databaseReference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String shopName = dataSnapshot.child("shopName").getValue().toString();
                        String email = dataSnapshot.child("mechanicEmail").getValue().toString();
                        String aadharNo = dataSnapshot.child("mechanicAadharNum").getValue().toString();
                        String mechanicName = dataSnapshot.child("mechanicName").getValue().toString();
                        String mechanicMobile = dataSnapshot.child("mechanicMobile").getValue().toString();
                        String vehicleInfo = dataSnapshot.child("mechanicVehicleInfo").getValue().toString();
                        String image = dataSnapshot.child("mechanicImageUrl").getValue().toString();


                        emailMechanic1.setText(email);
                        mechanicName1.setText(mechanicName);
                        mechanicMobile1.setText(mechanicMobile);
                        vehicleInfoMec11.setText(vehicleInfo);
                        shopName1.setText(shopName);



                        Uri my_im = Uri.parse(image);
                        Glide.with(MechanicProfile.this).load(my_im).into(imageUrl);

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }

                });


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
