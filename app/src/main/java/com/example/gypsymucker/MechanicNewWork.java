package com.example.gypsymucker;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class MechanicNewWork extends Fragment implements ViewTreeObserver.OnWindowFocusChangeListener {
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<CustomerDetails, HolderMechanicNewWork> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    Query databaseReference;
    DatabaseReference databaseReference1;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;
    DatabaseReference databaseReference4;
    DatabaseReference databaseReference5;
    DatabaseReference databaseReference6;
    DatabaseReference databaseReference7;
    FirebaseAuth mAuth;
    GoogleMap mMap;
    Double lt,lg;
    String name,shop,repCusE,repMecE,cusEmail;

    long mLastClickTime = 0;

    public MechanicNewWork() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = lf.inflate(R.layout.fragment_mechanic_new_work, container, false); //pass the correct layout name for the fragment
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        final String email = mAuth.getCurrentUser().getEmail().toString();
        final String repEmail = email.replace(".",",");



        databaseReference = FirebaseDatabase.getInstance().getReference("RequestedCustomerData").orderByChild("requestTo").equalTo(repEmail);
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

               holderMechanicNewWork.rejectBtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(final View v) {
                       if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                           return;
                       }
                       mLastClickTime = SystemClock.elapsedRealtime();
                       String cusEmail1 = holderMechanicNewWork.email.getText().toString();
                       final String repCusEmail = cusEmail1.replace(".",",");
                       repCusE = repCusEmail;
                       cusEmail = cusEmail1;

                       repMecE = repEmail;

                       databaseReference3 = FirebaseDatabase.getInstance().getReference("PermanentMechanic").child(repEmail);
                       databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {


                               String mecName = dataSnapshot.child("mechanicName").getValue().toString();
                               final String shopName = dataSnapshot.child("shopName").getValue().toString();

                               name = mecName;
                               shop = shopName;
                               mechanicRejectCustomerDialog(v);


                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });


                   }
               });


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


               holderMechanicNewWork.acceptBtn.setOnClickListener(new View.OnClickListener() {
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

                                       final String em = holderMechanicNewWork.email.getText().toString();
                                       final String repCusEmail = em.replace(".",",");


                                       databaseReference2 = FirebaseDatabase.getInstance().getReference("Notification").child("Customer");
                                       String message = "Your request \n will be \n Accepted by \n"+email+"\n for more Info, \n Go to \n Service Hall Page";
                                       NotificationDetails notificationDetails = new NotificationDetails();
                                       String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                       notificationDetails.setReason(message);
                                       notificationDetails.setTime(date);
                                       databaseReference2.child(repCusEmail).push().setValue(notificationDetails);


                                       String  n = holderMechanicNewWork.name.getText().toString();
                                       String  e = holderMechanicNewWork.email.getText().toString();
                                       String  ve =holderMechanicNewWork.vehicle.getText().toString();
                                       String  m =holderMechanicNewWork.mobile.getText().toString();
                                       String  l = holderMechanicNewWork.licNo.getText().toString();
                                       String  lat =holderMechanicNewWork.lat.getText().toString();
                                       String  lng = holderMechanicNewWork.lng.getText().toString();

                                       CustomerDetails customerDetails1 = new CustomerDetails();
                                       customerDetails1.setCustomerName(n);
                                       customerDetails1.setEmail(e);
                                       customerDetails1.setVehicle(ve);
                                       customerDetails1.setMobile(m);
                                       customerDetails1.setLicenseNo(l);
                                       customerDetails1.setLatitude(Double.valueOf(lat));
                                       customerDetails1.setLongitude(Double.valueOf(lng));
                                       customerDetails1.setStatus("Accepted.");

                                       databaseReference4= FirebaseDatabase.getInstance().getReference("WorkBoard").child("AcceptedWork").child("Customer");
                                       databaseReference4.child(repEmail).push().setValue(customerDetails1);


                                       databaseReference3 = FirebaseDatabase.getInstance().getReference("PermanentMechanic").child(repEmail);
                                       databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
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

                                               MechanicDetails mechanicDetails = new MechanicDetails();
                                               mechanicDetails.setShopName(shopName);
                                               mechanicDetails.setMechanicEmail(email);
                                               mechanicDetails.setMechanicAadharNum(aadharNo);
                                               mechanicDetails.setMechanicName(mechanicName);
                                               mechanicDetails.setMechanicMobile(mechanicMobile);
                                               mechanicDetails.setMechanicVehicleInfo(vehicleInfo);
                                               mechanicDetails.setMechanicImageUrl(image);
                                               mechanicDetails.setUnionId(unionId);
                                               mechanicDetails.setUnionName(unionName);
                                               mechanicDetails.setStatus("Accepted.");

                                               databaseReference4= FirebaseDatabase.getInstance().getReference("WorkBoard").child("AcceptedWork").child("Mechanic");
                                               databaseReference4.child(repCusEmail).push().setValue(mechanicDetails);




                                               databaseReference1= FirebaseDatabase.getInstance().getReference("WorkBoard").child("PendingWork");
                                               databaseReference1.child(repCusEmail).removeValue();

                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                           }
                                       });


                                       databaseReference5 = FirebaseDatabase.getInstance().getReference("RequestedCustomerData");
                                       databaseReference5.orderByChild("email").equalTo(em).addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                               for(DataSnapshot data: dataSnapshot.getChildren()){

                                                   String keys=data.getKey();
                                                   Toast.makeText(getActivity(), keys, Toast.LENGTH_SHORT).show();
                                                   databaseReference5.child(keys).removeValue();

                                               }

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
                       builder.setMessage("Are you sure? you accept this request!").setPositiveButton("Yes", dialogClickListener)
                               .setNegativeButton("No", dialogClickListener).show();





                   }
               });



            }

            @NonNull
            @Override
            public HolderMechanicNewWork onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_mechanic_view_new_work, parent, false);
                return new HolderMechanicNewWork(view);
            }
        };

        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);




        // Inflate the layout for this fragment
        return view;
    }

    public void mechanicRejectCustomerDialog(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_mechanic_reject_customer_request, null);
        alert.setView(view1);

        final CheckBox checkBox1 = view1.findViewById(R.id.checkBox);
        final CheckBox checkBox2 = view1.findViewById(R.id.checkBox2);
        final CheckBox checkBox3 = view1.findViewById(R.id.checkBox3);
        final CheckBox checkBox4 = view1.findViewById(R.id.checkBox4);

        ImageButton sendBtn = view1.findViewById(R.id.sendBtn);
        final AlertDialog alertDialog = alert.create();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String no1 = "";
                String no2 = "";
                String no3 = "";
                String no4 = "";

                if (checkBox1.isChecked()) {
                    String con = checkBox1.getText().toString();
                    String s = con + "\n";
                    no1 = s;
                }
                if (checkBox2.isChecked()) {
                    String con1 = checkBox2.getText().toString();
                    String s = con1 + "\n";
                    no2 = s;
                }
                if (checkBox3.isChecked()) {
                    String con2 = checkBox3.getText().toString();
                    String s = con2 + "\n";
                    no3 = s;
                }
                if (checkBox4.isChecked()) {
                    String con3 = checkBox4.getText().toString();
                    String s = con3;
                    no4 = s;
                }



                System.out.println(no1 + no2 + no3 + no4);

                if (!checkBox1.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked() && !checkBox4.isChecked()) {
                    Toast.makeText(getActivity(), "Click Any One Reason!", Toast.LENGTH_LONG).show();
                    return;

                }


                final String finalNo = no1;
                final String finalNo1 = no2;
                final String finalNo2 = no3;
                final String finalNo3 = no4;


                String shopUP = name.toUpperCase();


                databaseReference2 = FirebaseDatabase.getInstance().getReference("Notification").child("Customer");
                String message = "Your Service \n request will \n be Rejected by \n'"+shopUP+"'\n from, \n'"+shop+"'\n Due to \n reason of, \n"
                        +finalNo+"\n"+finalNo1+"\n"+finalNo2+"\n"+finalNo3;
                NotificationDetails notificationDetails = new NotificationDetails();
                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                notificationDetails.setReason(message);
                notificationDetails.setTime(date);
                databaseReference2.child(repCusE).push().setValue(notificationDetails);



                databaseReference5 = FirebaseDatabase.getInstance().getReference("RequestedCustomerData");
                databaseReference5.orderByChild("requestTo").equalTo(repMecE).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){
                                databaseReference6 = FirebaseDatabase.getInstance().getReference("RequestedCustomerData");
                                databaseReference6.orderByChild("email").equalTo(cusEmail).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot data: dataSnapshot.getChildren()){

                                            String keys=data.getKey();
                                            Toast.makeText(getActivity(), keys, Toast.LENGTH_SHORT).show();
                                            databaseReference6.child(keys).removeValue();

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



                databaseReference1= FirebaseDatabase.getInstance().getReference("WorkBoard").child("PendingWork");
                databaseReference1.child(repCusE).child(repMecE).removeValue();

                Toast.makeText(getActivity(), "Requested Rejected!", Toast.LENGTH_SHORT).show();

                alertDialog.dismiss();

            }
        });

        alertDialog.show();
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
