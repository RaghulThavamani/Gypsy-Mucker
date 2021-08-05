package com.example.gypsymucker;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class MechanicAcceptedWork extends Fragment {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<CustomerDetails, HolderMechanicNewWork> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    Query databaseReference;
    DatabaseReference databaseReference1,databaseReference8;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;
    DatabaseReference databaseReference4;
    DatabaseReference databaseReference5;
    DatabaseReference databaseReference6;
    DatabaseReference databaseReference7;
    FirebaseAuth mAuth;
    GoogleMap mMap;
    Double lt,lg;

    long mLastClickTime = 0;

    public MechanicAcceptedWork() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = lf.inflate(R.layout.fragment_mechanic_accepted_work, container, false); //pass the correct layout name for the fragment

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        final String mecEmail = mAuth.getCurrentUser().getEmail().toString();
        final String repEmail = mecEmail.replace(".",",");
        databaseReference = FirebaseDatabase.getInstance().getReference("WorkBoard").child("AcceptedWork").child("Customer").child(repEmail);



        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<CustomerDetails>()
                        .setQuery(databaseReference, CustomerDetails.class)
                        .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<CustomerDetails, HolderMechanicNewWork>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderMechanicNewWork holderMechanicNewWork, int i, @NonNull CustomerDetails customerDetails) {

                holderMechanicNewWork.email.setText(customerDetails.getEmail().toString());
                holderMechanicNewWork.lat.setText(customerDetails.getLatitude().toString());
                holderMechanicNewWork.lng.setText(customerDetails.getLongitude().toString());
                holderMechanicNewWork.status.setText(customerDetails.getStatus().toString());
                holderMechanicNewWork.name.setText(customerDetails.getCustomerName().toString());
                holderMechanicNewWork.mobile.setText(customerDetails.getMobile().toString());
                holderMechanicNewWork.vehicle.setText(customerDetails.getVehicle().toString());
                holderMechanicNewWork.licNo.setText(customerDetails.getLicenseNo().toString());


                holderMechanicNewWork.getCusLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        String lat1 =  holderMechanicNewWork.lat.getText().toString();
                        lt = Double.valueOf(lat1);
                        String lng1 =  holderMechanicNewWork.lng.getText().toString();
                        lg = Double.valueOf(lng1);

                        getLocation(v);
                    }
                });


                holderMechanicNewWork.serviceCompleted.setOnClickListener(new View.OnClickListener() {
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

                                        final String cusEmail = holderMechanicNewWork.email.getText().toString().trim();
                                        final String repCusEmail = cusEmail.replace(".",",");


                                        databaseReference1 = FirebaseDatabase.getInstance().getReference("WorkBoard").child("AcceptedWork").child("Mechanic").child(repCusEmail);
                                        databaseReference1.orderByChild("mechanicEmail").equalTo(mecEmail).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot data: dataSnapshot.getChildren()){

                                                    String keys=data.getKey();
                                                    databaseReference2 = FirebaseDatabase.getInstance().getReference("WorkBoard").child("AcceptedWork").child("Mechanic").child(repCusEmail);
                                                    databaseReference2.child(keys).child("status").setValue("Completed.");


                                                    databaseReference3 = FirebaseDatabase.getInstance().getReference("WorkBoard").child("AcceptedWork").child("Customer").child(repEmail);
                                                    databaseReference3.orderByChild("email").equalTo(cusEmail).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for(DataSnapshot data: dataSnapshot.getChildren()){

                                                                String keys=data.getKey();
                                                                databaseReference4 = FirebaseDatabase.getInstance().getReference("WorkBoard").child("AcceptedWork").child("Customer").child(repEmail);
                                                                databaseReference4.child(keys).removeValue();

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


                                        databaseReference5 = FirebaseDatabase.getInstance().getReference("PermanentMechanic").child(repEmail);
                                        databaseReference5.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {


                                                String mecName = dataSnapshot.child("mechanicName").getValue().toString();
                                                String shopName = dataSnapshot.child("shopName").getValue().toString();

                                                final NotificationDetails notificationDetails = new NotificationDetails();
                                                final String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                                String cusName = holderMechanicNewWork.name.getText().toString().trim();
                                                notificationDetails.setCustomerName(cusName);
                                                notificationDetails.setTime(date);
                                                notificationDetails.setMechanicName(mecName);
                                                String vehicleInfo = holderMechanicNewWork.vehicle.getText().toString().trim();
                                                notificationDetails.setVehicleInfo(vehicleInfo);

                                                databaseReference6 = FirebaseDatabase.getInstance().getReference("MechanicCompletedWork").child(repEmail);
                                                databaseReference6.push().setValue(notificationDetails);

                                                databaseReference7 = FirebaseDatabase.getInstance().getReference("Notification").child("Customer");
                                                String message1 = "Your requested \n service will be \n Completed by \n'"+mecName+"'\n for more Info \n go to Service Hall page & \n review \n a Service.";
                                                NotificationDetails notificationDetails1 = new NotificationDetails();
                                                notificationDetails1.setReason(message1);
                                                notificationDetails1.setTime(date);
                                                databaseReference7.child(repCusEmail).push().setValue(notificationDetails1);


                                             /*   databaseReference8 = FirebaseDatabase.getInstance().getReference("PermanentOwner");
                                                databaseReference8.orderByChild("shopName").equalTo(shopName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for(DataSnapshot data: dataSnapshot.getChildren()) {
                                                            String keys=data.getKey();
                                                            databaseReference6 = FirebaseDatabase.getInstance().getReference("MechanicCompletedWork").child(keys);
                                                            databaseReference6.push().setValue(notificationDetails);

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });  */



                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Are you sure? You Completed the work?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();









                    }
                });


                holderMechanicNewWork.call.setOnClickListener(new View.OnClickListener() {
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
                                        String call = String.valueOf(holderMechanicNewWork.mobile.getText());
                                        try {
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:" + call));
                                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                return;
                                            }
                                            startActivity(callIntent);
                                        } catch (ActivityNotFoundException activityException) {
                                            Log.e("Calling a Phone Number", "Call failed", activityException);
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("You want to make a call?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();




                    }
                });



            }

            @NonNull
            @Override
            public HolderMechanicNewWork onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_mechanic_view_accepted_work, parent, false);
                return new HolderMechanicNewWork(view);
            }
        };

        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);



        // Inflate the layout for this fragment
        return view;
    }

    public void getLocation(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_view_location ,null);
        alert.setView(view1);
        FloatingActionButton normal = view1.findViewById(R.id.normal);
        FloatingActionButton satellite = view1.findViewById(R.id.satellite);
        FloatingActionButton getLocation = view1.findViewById(R.id.getLocation);
        final AlertDialog alertDialog = alert.create();

        MapView mapView = view1.findViewById(R.id.mapView);

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

        MapsInitializer.initialize(getActivity());

        mapView.onCreate(alertDialog.onSaveInstanceState());
        mapView.onResume();


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mMap = googleMap;

                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                if (mMap != null) {


                    LatLng latLng = new LatLng(lt,lg);
                    MarkerOptions markerOptions= new MarkerOptions().position(latLng).title("Customer is Here");
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    mMap.addMarker(markerOptions);


                }
            }
        });


        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null)
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });


        satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null)
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });




        alertDialog.show();
    }
}
