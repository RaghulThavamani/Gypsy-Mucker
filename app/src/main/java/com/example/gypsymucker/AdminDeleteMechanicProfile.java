package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AdminDeleteMechanicProfile extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<MechanicDetails, HolderAdminApproveMechanic> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference,databaseReference1,databaseReference2,databaseReference3,databaseReference4,databaseReference5,databaseReference6,databaseReference7;
    FirebaseAuth firebaseAuth;

    String repMec;
    long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_mechanic_profile);


        databaseReference = FirebaseDatabase.getInstance().getReference("DeleteMechanicProfile");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        firebaseAuth = FirebaseAuth.getInstance();




        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<MechanicDetails>()
                        .setQuery(databaseReference, MechanicDetails.class)
                        .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<MechanicDetails, HolderAdminApproveMechanic>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderAdminApproveMechanic holderAdminApproveMechanic, int i, @NonNull final MechanicDetails mechanicDetails) {


                holderAdminApproveMechanic.mechanicMobile.setText(mechanicDetails.getMechanicMobile());
                holderAdminApproveMechanic.emailMechanic.setText(mechanicDetails.getMechanicEmail());
                holderAdminApproveMechanic.aadharNoMechanic.setText(mechanicDetails.getMechanicAadharNum());
                holderAdminApproveMechanic.mechanicName.setText(mechanicDetails.getMechanicName());
                holderAdminApproveMechanic.vehicleInfoMec.setText(mechanicDetails.getMechanicVehicleInfo());
                holderAdminApproveMechanic.id.setText(mechanicDetails.getId());
                holderAdminApproveMechanic.shopName1.setText(mechanicDetails.getShopName());
                holderAdminApproveMechanic.confirmationText.setText(mechanicDetails.getConfirmation());
                holderAdminApproveMechanic.ownerEmail.setText(mechanicDetails.getOwnerEmail());
                holderAdminApproveMechanic.date.setText(mechanicDetails.getTime());


                if(mechanicDetails.getConfirmation().equals("Accepted.")){
                    holderAdminApproveMechanic.acceptedDate.setVisibility(View.VISIBLE);
                    holderAdminApproveMechanic.acceptedDate.setText(mechanicDetails.getAcceptedDate());
                    holderAdminApproveMechanic.confirmation.setVisibility(View.GONE);
                    holderAdminApproveMechanic.deleteProfile.setVisibility(View.VISIBLE);
                }


                String im = mechanicDetails.getMechanicImageUrl().toString().trim();
                Uri my_im = Uri.parse(im);
                Glide.with(AdminDeleteMechanicProfile.this).load(my_im).into(holderAdminApproveMechanic.imageUrl);


                holderAdminApproveMechanic.confirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        String name = holderAdminApproveMechanic.mechanicName.getText().toString().toUpperCase();
                        String repMecEmail = holderAdminApproveMechanic.emailMechanic.getText().toString().replace(".",",");
                        String repEmail = holderAdminApproveMechanic.ownerEmail.getText().toString().replace(".",",");

                        String content = "Are you sure \n you want remove \nmechanic\n"+name+"\n from your shop";

                        databaseReference2 = FirebaseDatabase.getInstance().getReference("Notification");
                        String time = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        NotificationDetails notificationDetails=new NotificationDetails();
                        notificationDetails.setReason(content);
                        notificationDetails.setTime(time);
                        notificationDetails.setMechanicName(repMecEmail);
                        notificationDetails.setConfirmation("Enable");
                        databaseReference2.child(repEmail).push().setValue(notificationDetails);
                        Toast.makeText(AdminDeleteMechanicProfile.this, "Message Send!", Toast.LENGTH_SHORT).show();

                    }
                });


                holderAdminApproveMechanic.deleteProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                      //  Toast.makeText(AdminDeleteMechanicProfile.this, "Vannakam d maple waiting to dlt code...", Toast.LENGTH_SHORT).show();

                        String MecEmail = holderAdminApproveMechanic.emailMechanic.getText().toString();

                      String repMecEmail = holderAdminApproveMechanic.emailMechanic.getText().toString().replace(".",",");




                        databaseReference3 = FirebaseDatabase.getInstance().getReference("PermanentMechanic");
                        databaseReference3.child(repMecEmail).removeValue();
                        databaseReference4 = FirebaseDatabase.getInstance().getReference("Notification").child("Mechanic");
                        databaseReference4.child(repMecEmail).removeValue();
                        databaseReference5 = FirebaseDatabase.getInstance().getReference().child("Review");
                        databaseReference5.child(repMecEmail).removeValue();
                        databaseReference6 = FirebaseDatabase.getInstance().getReference("MechanicCompletedWork");
                        databaseReference6.child(repMecEmail).removeValue();



                        databaseReference7=FirebaseDatabase.getInstance().getReference().child("DeleteMechanicProfile");

                        databaseReference7.child(repMecEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists()){

                                        String keys = dataSnapshot.getKey().toString();
                                        databaseReference7=FirebaseDatabase.getInstance().getReference().child("DeleteMechanicProfile");
                                        databaseReference7.child(keys).removeValue();
                                        Toast.makeText(AdminDeleteMechanicProfile.this,"Delete Completed!", Toast.LENGTH_SHORT).show();
                                    }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            } });
                    }
                });



            }

            @NonNull
            @Override
            public HolderAdminApproveMechanic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_admin_delete_mechanic_profile, parent, false);
                return new HolderAdminApproveMechanic(view);
            }
        };

        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);

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
