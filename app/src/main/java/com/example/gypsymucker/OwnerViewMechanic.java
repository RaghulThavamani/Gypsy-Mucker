package com.example.gypsymucker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class OwnerViewMechanic extends Fragment {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<MechanicDetails, HolderOwnerViewMechanic> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    Query databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference review;
    DatabaseReference databaseReference3,databaseReference4;
    FirebaseAuth mAuth;
    long mLastClickTime = 0;

    FirebaseAuth auth;


    public OwnerViewMechanic() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        final View view = lf.inflate(R.layout.fragment_owner_view_mechanic, container, false); //pass the correct layout name for the fragment


        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        auth = FirebaseAuth.getInstance();

        databaseReference2=FirebaseDatabase.getInstance().getReference().child("PermanentOwner");

        final String email = mAuth.getCurrentUser().getEmail().toString().trim();
        final String repEmail = email.replace(".",",");



        databaseReference2.child(repEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                    String keys=dataSnapshot.getKey();
                    System.out.println(keys);
                    final String shopName1=dataSnapshot.child("shopName").getValue().toString();

                    databaseReference=database.getReference("PermanentMechanic").orderByChild("shopName").equalTo(shopName1);

                FirebaseRecyclerOptions options =
                        new FirebaseRecyclerOptions.Builder<MechanicDetails>()
                                .setQuery(databaseReference, MechanicDetails.class)
                                .build();

                recyclerAdapter = new FirebaseRecyclerAdapter<MechanicDetails, HolderOwnerViewMechanic>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final HolderOwnerViewMechanic holderOwnerViewMechanic, int i, @NonNull final MechanicDetails mechanicDetails) {

                        holderOwnerViewMechanic.unionId.setText(mechanicDetails.getUnionId());
                        holderOwnerViewMechanic.unionName.setText(mechanicDetails.getUnionName());
                        holderOwnerViewMechanic.mechanicName.setText(mechanicDetails.getMechanicName());
                        holderOwnerViewMechanic.mechanicMobile.setText(mechanicDetails.getMechanicMobile());
                        holderOwnerViewMechanic.emailMechanic.setText(mechanicDetails.getMechanicEmail());
                        holderOwnerViewMechanic.aadharNoMechanic.setText(mechanicDetails.getMechanicAadharNum());
                        holderOwnerViewMechanic.vehicleInfoMec11.setText(mechanicDetails.getMechanicVehicleInfo());
                        holderOwnerViewMechanic.shopName.setText(mechanicDetails.getShopName());


                        String im=mechanicDetails.getMechanicImageUrl().toString().trim();
                        Uri my_im = Uri.parse(im);
                        Glide.with(getActivity()).load(my_im).into(holderOwnerViewMechanic.imageUrl);

                        holderOwnerViewMechanic.update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                                //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                // Setting Dialog Title
                                alertDialog.setTitle("PASSWORD");

                                // Setting Dialog Message
                                alertDialog.setMessage("Enter Password");
                                final EditText input = new EditText(getActivity());
                                input.setInputType(InputType.TYPE_CLASS_TEXT |
                                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                input.setLayoutParams(lp);
                                alertDialog.setView(input);
                                // Setting Positive "Yes" Button
                                alertDialog.setPositiveButton("YES",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int which) {

                                                String password = input.getText().toString().trim();
                                                if(TextUtils.isEmpty(password)){
                                                    Toast.makeText(getActivity(),"Enter Password!", Toast.LENGTH_SHORT).show(); return;
                                                }

                                                mAuth.signInWithEmailAndPassword(email, password)
                                                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {

                                                                    // Write your code here to execute after dialog
                                                                    Toast.makeText(getActivity(),"Password Matched", Toast.LENGTH_SHORT).show();
                                                                    String email = holderOwnerViewMechanic.emailMechanic.getText().toString();
                                                                    Intent intent = new Intent(getActivity(),OwnerUpdateMechanicDetails.class);
                                                                    intent.putExtra("Email",email);
                                                                    startActivity(intent);

                                                                }else {
                                                                    Toast.makeText(getActivity(),"Password Not Matched!",Toast.LENGTH_LONG).show();

                                                                }

                                                            }
                                                        });
                                            }
                                        });
                                //Setting Negative "NO" Button
                                alertDialog.setNegativeButton("NO",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Write your code here to execute after dialog
                                                dialog.cancel();
                                            }
                                        });
                                // Showing Alert Message
                                alertDialog.show();


                            }
                        });

                        holderOwnerViewMechanic.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                                //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                // Setting Dialog Title
                                alertDialog.setTitle("PASSWORD");

                                // Setting Dialog Message
                                alertDialog.setMessage("Enter Password");
                                final EditText input = new EditText(getActivity());
                                input.setInputType(InputType.TYPE_CLASS_TEXT |
                                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                input.setLayoutParams(lp);
                                alertDialog.setView(input);
                                //alertDialog.setView(input);
                                // Setting Positive "Yes" Button
                                alertDialog.setPositiveButton("YES",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int which) {



                                                String password = input.getText().toString().trim();
                                                if(TextUtils.isEmpty(password)){
                                                    Toast.makeText(getActivity(),"Enter Password!", Toast.LENGTH_SHORT).show(); return;
                                                }

                                                mAuth.signInWithEmailAndPassword(email, password)
                                                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {

                                                                    String mecEmail = holderOwnerViewMechanic.emailMechanic.getText().toString();
                                                                    final String repMecEmail = mecEmail.replace(".",",");
                                                                    databaseReference3 = FirebaseDatabase.getInstance().getReference("PermanentMechanic").child(repMecEmail);
                                                                    databaseReference3.addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                                            String shopName = dataSnapshot.child("shopName").getValue().toString();
                                                                            String email = dataSnapshot.child("mechanicEmail").getValue().toString();
                                                                            String aadharNo = dataSnapshot.child("mechanicAadharNum").getValue().toString();
                                                                            String mechanicName = dataSnapshot.child("mechanicName").getValue().toString();
                                                                            String mechanicMobile = dataSnapshot.child("mechanicMobile").getValue().toString();
                                                                            String vehicleInfo = dataSnapshot.child("mechanicVehicleInfo").getValue().toString();
                                                                            String image = dataSnapshot.child("mechanicImageUrl").getValue().toString();
                                                                            String id = dataSnapshot.child("id").getValue().toString();

                                                                            MechanicDetails mechanicDetails1 = new MechanicDetails();
                                                                            mechanicDetails1.setMechanicMobile(mechanicMobile);
                                                                            mechanicDetails1.setMechanicName(mechanicName);
                                                                            mechanicDetails1.setMechanicAadharNum(aadharNo);
                                                                            mechanicDetails1.setShopName(shopName);
                                                                            mechanicDetails1.setMechanicEmail(email);
                                                                            mechanicDetails1.setMechanicVehicleInfo(vehicleInfo);
                                                                            mechanicDetails1.setMechanicImageUrl(image);
                                                                            mechanicDetails1.setId(id);
                                                                            mechanicDetails1.setConfirmation("Pending.");
                                                                            final String email1 = mAuth.getCurrentUser().getEmail().toString().trim();
                                                                            mechanicDetails1.setOwnerEmail(email1);
                                                                            String time = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                                                            mechanicDetails1.setTime(time);




                                                                            databaseReference4 = FirebaseDatabase.getInstance().getReference("DeleteMechanicProfile").child(repMecEmail);
                                                                            databaseReference4.setValue(mechanicDetails1);
                                                                            Toast.makeText(getActivity(), "Delete Request Send!", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError error) {

                                                                        }

                                                                    });


                                                                }else {
                                                                    Toast.makeText(getActivity(),"Password Not Matched!",Toast.LENGTH_LONG).show();

                                                                }

                                                            }
                                                        });
                                            }
                                        });
                                //Setting Negative "NO" Button
                                alertDialog.setNegativeButton("NO",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Write your code here to execute after dialog
                                                dialog.cancel();
                                            }
                                        });
                                // Showing Alert Message
                                alertDialog.show();




                            }
                        });
                    }
                    @NonNull
                    @Override
                    public HolderOwnerViewMechanic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.view_owner_view_mechanic, parent, false);
                        return new HolderOwnerViewMechanic(view);
                    }
                };

                recyclerAdapter.notifyDataSetChanged();
                recyclerAdapter.startListening();
                recyclerView.setAdapter(recyclerAdapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            } });
        return view;
    }
}
