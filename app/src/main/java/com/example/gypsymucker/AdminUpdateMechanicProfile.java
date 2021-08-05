package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminUpdateMechanicProfile extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<MechanicDetails, HolderAdminApproveMechanic> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference,databaseReference1,databaseReference2,databaseReference3;

    String repMec;
    long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_mechanic_profile);



        databaseReference = FirebaseDatabase.getInstance().getReference("UpdateMechanicProfile");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);





        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<MechanicDetails>()
                        .setQuery(databaseReference, MechanicDetails.class)
                        .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<MechanicDetails, HolderAdminApproveMechanic>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderAdminApproveMechanic holderAdminApproveMechanic, int i, @NonNull final MechanicDetails mechanicDetails) {

                holderAdminApproveMechanic.unionId.setText(mechanicDetails.getUnionId());
                holderAdminApproveMechanic.unionName.setText(mechanicDetails.getUnionName());
                holderAdminApproveMechanic.mechanicMobile.setText(mechanicDetails.getMechanicMobile());
                holderAdminApproveMechanic.emailMechanic.setText(mechanicDetails.getMechanicEmail());
                holderAdminApproveMechanic.mechanicImageUrl.setText(mechanicDetails.getMechanicImageUrl());

                String im = mechanicDetails.getMechanicImageUrl().toString().trim();
                Uri my_im = Uri.parse(im);
                Glide.with(AdminUpdateMechanicProfile.this).load(my_im).into(holderAdminApproveMechanic.imageUrl);


                holderAdminApproveMechanic.existingProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        String email = holderAdminApproveMechanic.emailMechanic.getText().toString().trim();
                        repMec = email.replace(".",",");
                        existingProfile(v);
                    }
                });


                holderAdminApproveMechanic.updateProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        String email = holderAdminApproveMechanic.emailMechanic.getText().toString().trim();
                        String repMecHere = email.replace(".",",");

                        String un = holderAdminApproveMechanic.unionId.getText().toString();
                        String unName = holderAdminApproveMechanic.unionName.getText().toString();
                        String mob = holderAdminApproveMechanic.mechanicMobile.getText().toString();
                        String imageUrl = holderAdminApproveMechanic.mechanicImageUrl.getText().toString();

                        databaseReference2 = FirebaseDatabase.getInstance().getReference("PermanentMechanic").child(repMecHere);
                        databaseReference2.child("unionId").setValue(un);
                        databaseReference2.child("unionName").setValue(unName);
                        databaseReference2.child("mechanicMobile").setValue(mob);
                        databaseReference2.child("mechanicImageUrl").setValue(imageUrl);

                        databaseReference3 = FirebaseDatabase.getInstance().getReference("UpdateMechanicProfile").child(repMecHere);
                        databaseReference3.removeValue();

                        Toast.makeText(AdminUpdateMechanicProfile.this, "Updated Successful..\uD83D\uDC4D", Toast.LENGTH_SHORT).show();




                    }
                });
            }

            @NonNull
            @Override
            public HolderAdminApproveMechanic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_admin_update_mechanic_profile, parent, false);
                return new HolderAdminApproveMechanic(view);
            }
        };

        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);

    }

    public void existingProfile(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_view_mechanic_existing_profile, null);
        alert.setView(view1);

        final TextView shopName1,mechanicMobile1,mechanicName1,emailMechanic1,vehicleInfoMec11,unionId1,unionName1,aadharNum;
        final ImageView imageUrl;

        shopName1 = (TextView)view1.findViewById(R.id.shopName1);
        mechanicMobile1 = (TextView)view1.findViewById(R.id.mobileMechanic1);
        mechanicName1 = (TextView)view1.findViewById(R.id.mechanicName);
        emailMechanic1 = (TextView)view1.findViewById(R.id.emailMechanic);
        vehicleInfoMec11 = (TextView)view1.findViewById(R.id.vehicleInfoMec);
        unionId1 = (TextView)view1.findViewById(R.id.unionId);
        unionName1 = (TextView)view1.findViewById(R.id.unionName);
        aadharNum = (TextView)view1.findViewById(R.id.aadharNoMechanic);
        imageUrl=view1.findViewById(R.id.imageUrl1);


        final AlertDialog alertDialog = alert.create();
        databaseReference2 = FirebaseDatabase.getInstance().getReference("PermanentMechanic").child(repMec);
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
                String unionId = dataSnapshot.child("unionId").getValue().toString();
                String unionName = dataSnapshot.child("unionName").getValue().toString();



                emailMechanic1.setText(email);
                mechanicName1.setText(mechanicName);
                mechanicMobile1.setText(mechanicMobile);
                vehicleInfoMec11.setText(vehicleInfo);
                shopName1.setText(shopName);
                aadharNum.setText(aadharNo);
                unionId1.setText(unionId);
                unionName1.setText(unionName);


                Uri my_im = Uri.parse(image);
                Glide.with(AdminUpdateMechanicProfile.this).load(my_im).into(imageUrl);

            }

            @Override
            public void onCancelled(DatabaseError error) {

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
