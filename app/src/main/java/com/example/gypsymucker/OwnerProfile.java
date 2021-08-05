package com.example.gypsymucker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;


public class OwnerProfile extends Fragment implements ViewTreeObserver.OnWindowFocusChangeListener {
    TextView shopLicenseNO,shopName,shopCity,shopAdd,ownerName,mobile,aadharNo,email,availability,vehicle;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    Button exit;
    public OwnerProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_owner_profile, container, false); //pass the correct layout name for the fragment
        shopName=view.findViewById(R.id.shopName);
        shopCity=view.findViewById(R.id.shopCity);
        shopAdd=view.findViewById(R.id.shopAdd);
        ownerName=view.findViewById(R.id.ownerName);
        mobile=view.findViewById(R.id.mobile);
        email=view.findViewById(R.id.email);
        availability=view.findViewById(R.id.availability);
        vehicle=view.findViewById(R.id.vehicleInfo);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                mAuth.signOut();
                                getActivity().finish();
                                Intent intent = new Intent(getActivity(),GmMainLoginPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure? You want logout!").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        databaseReference=FirebaseDatabase.getInstance().getReference().child("PermanentOwner");

        String em = mAuth.getCurrentUser().getEmail().toString().trim();
        String email1 = em.replace(".",",");

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Wait while a second...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        databaseReference.child(email1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    progressDialog.dismiss();
                    String shopName1=dataSnapshot.child("shopName").getValue().toString();
                    String shopCity1=dataSnapshot.child("shopCity").getValue().toString();
                    String address=dataSnapshot.child("shopAddress").getValue().toString();
                    String ownerName1=dataSnapshot.child("ownerName").getValue().toString();
                    String mobile1=dataSnapshot.child("ownerMobile").getValue().toString();
                    String aadharNum=dataSnapshot.child("ownerAadharNum").getValue().toString();
                    String email1=dataSnapshot.child("ownerEmail").getValue().toString();
                    String availability1=dataSnapshot.child("shopAvailability").getValue().toString();
                    String vehicleInfo=dataSnapshot.child("shopVehicleInfo").getValue().toString();

                    shopName.setText(shopName1);
                    shopCity.setText(shopCity1);
                    shopAdd.setText(address);
                    ownerName.setText(ownerName1);
                    mobile.setText(mobile1);
                    email.setText(email1);
                    availability.setText(availability1);
                    vehicle.setText(vehicleInfo);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            } });


        return view;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onMultiWindowModeChanged(hasFocus);
        if (hasFocus) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(
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


