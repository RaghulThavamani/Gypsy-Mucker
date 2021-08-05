package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AdminApproveShop extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<OwnerDetails, HolderAdminApproveBusiness> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference, databaseReference2, myRefPassword,databaseReference3,
            myRefEmail,myRefLocation,databaseReference4,databaseReference5,databaseReference6;
    String imgValue;
    Double lt,lg;
    String declineEmail,declineShop,declineImgUrl;
    String approveEmail;
    private FirebaseAuth mAuth;
    GoogleMap mMap;
    long mLastClickTime = 0;

    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_shop);

        database = FirebaseDatabase.getInstance();
        myRefPassword = FirebaseDatabase.getInstance().getReference("TemporaryPassword").child("OwnerPassword");
        databaseReference = database.getReference("TemporaryOwnerRegistration");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("PermanentOwner");
        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<OwnerDetails>()
                        .setQuery(databaseReference, OwnerDetails.class)
                        .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<OwnerDetails, HolderAdminApproveBusiness>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderAdminApproveBusiness holderAdminApproveBusiness, int i, @NonNull OwnerDetails ownerDetails) {

                holderAdminApproveBusiness.ownerName.setText(ownerDetails.getOwnerName());
                holderAdminApproveBusiness.mobile.setText(ownerDetails.getOwnerMobile());
                holderAdminApproveBusiness.aadharNo.setText(ownerDetails.getOwnerAadharNum());
                holderAdminApproveBusiness.email.setText(ownerDetails.getOwnerEmail());

                holderAdminApproveBusiness.shopLicenseNO.setText(ownerDetails.getShopLicenseNo());
                holderAdminApproveBusiness.shopName.setText(ownerDetails.getShopName());
                holderAdminApproveBusiness.shopCity.setText(ownerDetails.getShopCity());
                holderAdminApproveBusiness.shopAdd.setText(ownerDetails.getShopAddress());
                holderAdminApproveBusiness.availability.setText(ownerDetails.getShopAvailability());
                holderAdminApproveBusiness.vehicleInfo.setText(ownerDetails.getShopVehicleInfo());
                holderAdminApproveBusiness.latitude.setText(ownerDetails.getShopLatitude());
                holderAdminApproveBusiness.longitude.setText(ownerDetails.getShopLongitude());
                holderAdminApproveBusiness.imageString.setText(ownerDetails.getShopImageURL());

                holderAdminApproveBusiness.viewCertificate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        String img = holderAdminApproveBusiness.imageString.getText().toString().trim();
                        imgValue = img;
                        certificateImageDialog(v);
                    }
                });

                holderAdminApproveBusiness.viewLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        String lat = holderAdminApproveBusiness.latitude.getText().toString();
                        String lng = holderAdminApproveBusiness.longitude.getText().toString();
                        lt = Double.valueOf(lat);
                        lg = Double.valueOf(lng);
                        locationDialog(v);

                    }
                });

                holderAdminApproveBusiness.decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      String em =  holderAdminApproveBusiness.email.getText().toString().trim();
                      declineEmail = em;
                      String sh = holderAdminApproveBusiness.shopName.getText().toString().trim();
                      declineShop = sh;
                      String img = holderAdminApproveBusiness.imageString.getText().toString().trim();
                      declineImgUrl = img;
                      declineShop(v);
                    }
                });

                holderAdminApproveBusiness.approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String em =  holderAdminApproveBusiness.email.getText().toString().trim();
                        approveEmail = em;
                        approveShop(v);
                    }
                });

            }

            @NonNull
            @Override
            public HolderAdminApproveBusiness onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_admin_approve_shop, parent, false);
                return new HolderAdminApproveBusiness(view);
            }
        };

        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void certificateImageDialog(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(AdminApproveShop.this);

        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_view_certificate,null);
        alert.setView(view1);
        ImageView licenseCertificateImg = view1.findViewById(R.id.licenseCertificateImg);
        final AlertDialog alertDialog = alert.create();

        Uri my_im = Uri.parse(imgValue);
        Glide.with(AdminApproveShop.this).load(my_im).into(licenseCertificateImg);

        alertDialog.show();
    }

    public void locationDialog(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(AdminApproveShop.this);

        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_view_location ,null);
        alert.setView(view1);
        FloatingActionButton normal = view1.findViewById(R.id.normal);
        FloatingActionButton satellite = view1.findViewById(R.id.satellite);

        final AlertDialog alertDialog = alert.create();

        MapView mapView = view1.findViewById(R.id.mapView);

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        MapsInitializer.initialize(this);

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
                    MarkerOptions markerOptions= new MarkerOptions().position(latLng).title("Shop is Here");
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    mMap.addMarker(markerOptions);




                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {


                            return false;
                        }
                    });
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

    public void declineShop(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(AdminApproveShop.this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_decline_shop, null);
        alert.setView(view1);

        final EditText editTextEmail = view1.findViewById(R.id.emailReject);
        final EditText editTextSubject = view1.findViewById(R.id.subject);
        final CheckBox checkBox1 = view1.findViewById(R.id.checkBox);
        final CheckBox checkBox2 = view1.findViewById(R.id.checkBox2);
        final CheckBox checkBox3 = view1.findViewById(R.id.checkBox3);
        final CheckBox checkBox4 = view1.findViewById(R.id.checkBox4);
        final CheckBox checkBox5 = view1.findViewById(R.id.checkBox5);

        ImageButton okBtn = view1.findViewById(R.id.okBtn);
        final AlertDialog alertDialog = alert.create();
        Toast.makeText(AdminApproveShop.this,declineEmail,Toast.LENGTH_LONG).show();
        editTextEmail.setText(declineEmail);
        editTextSubject.setText("Your request on GYPSY MUCKER to create a New Shop as Been Declined ");


        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String no1 = "";
                String no2 = "";
                String no3 = "";
                String no4 = "";
                String no5 = "";

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

                if (checkBox5.isChecked()) {
                    String con3 = checkBox5.getText().toString();
                    String s = con3;
                    no5 = s;
                }

                System.out.println(no1 + no2 + no3 + no4);

                if (!checkBox1.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked() && !checkBox4.isChecked() && !checkBox5.isChecked()) {
                    Toast.makeText(AdminApproveShop.this, "Click Any One Reason!", Toast.LENGTH_LONG).show();
                    return;

                }

                String recipient = editTextEmail.getText().toString();

                String subject = editTextSubject.getText().toString();


                final String finalNo = no1;
                final String finalNo1 = no2;
                final String finalNo2 = no3;
                final String finalNo3 = no4;
                final String finalNo4 = no5;

                String shopUP = declineShop.toUpperCase();

                final String message = "Your Request For Registering Shop\n '" + shopUP + "' \n will Be Decline Due to the reason of \n"
                        + finalNo + finalNo1 + finalNo2 + finalNo3 +"\n "+ finalNo4+"\n Registered again with correct values,\n Thank you,\n Have a Nice Day...";

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ recipient});
                Toast.makeText(AdminApproveShop.this, recipient, Toast.LENGTH_LONG).show();
                intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                intent.putExtra(Intent.EXTRA_TEXT,message);

                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent,"Choose an email client"));

                final String repEmail = declineEmail.replace(".",",");
                databaseReference5 = FirebaseDatabase.getInstance().getReference("TemporaryOwnerRegistration");
                databaseReference5.child(repEmail).removeValue();
                databaseReference6 = FirebaseDatabase.getInstance().getReference("TemporaryPassword").child("OwnerPassword");
                databaseReference6.child(repEmail).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        storageReference = FirebaseStorage.getInstance().getReference();
                        final StorageReference ref = storageReference.child(repEmail);
                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AdminApproveShop.this, "Good Work Chief..\uD83D\uDE0E", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                            }
                        });

                    }
                });


                alertDialog.dismiss();

            }
        });

        alertDialog.show();
    }

    public void approveShop(View view){

        final AlertDialog.Builder alert = new AlertDialog.Builder(AdminApproveShop.this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_approve_shop, null);
        alert.setView(view1);

        final TextView verifyEmail = view1.findViewById(R.id.verifyLink);

        verifyEmail.setClickable(true);

        String srcText = approveEmail;
        final android.content.ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Source Text", srcText);
        clipboardManager.setPrimaryClip(clipData);

        ImageButton cancelBtn = view1.findViewById(R.id.cancelBtn);
        ImageButton okBtn = view1.findViewById(R.id.okBtn);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration();
                alertDialog.dismiss();
            }

        });

        alertDialog.show();
    }

    public void registration(){
        String repEmail = approveEmail.replace(".",",");
        myRefPassword = FirebaseDatabase.getInstance().getReference("TemporaryPassword").child("OwnerPassword").child(repEmail);

        myRefPassword.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    String keys = data.getKey();
                    System.out.println(keys);


                    String pass = data.getValue().toString();

                    System.out.println(pass);
                    Toast.makeText(AdminApproveShop.this, pass+"\n"+approveEmail, Toast.LENGTH_LONG).show();

                    mAuth.createUserWithEmailAndPassword(approveEmail, pass)
                            .addOnCompleteListener(AdminApproveShop.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    Toast.makeText(AdminApproveShop.this, "Verification Send", Toast.LENGTH_LONG).show();
                                                    addShop();

                                                }else {
                                                    Toast.makeText(AdminApproveShop.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });



                                    } else {
                                        Toast.makeText(AdminApproveShop.this, "Registration Failed or Email-Id Already Registered", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addShop(){
        final OwnerDetails ownerDetails = new OwnerDetails();
        String repEmail = approveEmail.replace(".",",");
        databaseReference3 = FirebaseDatabase.getInstance().getReference("TemporaryOwnerRegistration");
        databaseReference3.child(repEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                    String keys = dataSnapshot.getKey();
                    System.out.println(keys);
                    final String sn = dataSnapshot.child("shopName").getValue().toString();
                    String sl = dataSnapshot.child("shopLicenseNo").getValue().toString();
                    String sc = dataSnapshot.child("shopCity").getValue().toString();
                    String sa = dataSnapshot.child("shopAddress").getValue().toString();
                    String on = dataSnapshot.child("ownerName").getValue().toString();
                    String om = dataSnapshot.child("ownerMobile").getValue().toString();
                    String oan = dataSnapshot.child("ownerAadharNum").getValue().toString();

                    final String oe = dataSnapshot.child("ownerEmail").getValue().toString();
                    final String sav = dataSnapshot.child("shopAvailability").getValue().toString();
                    final String sv = dataSnapshot.child("shopVehicleInfo").getValue().toString();
                    String si = dataSnapshot.child("shopImageURL").getValue().toString();
                    final String sla = dataSnapshot.child("shopLatitude").getValue().toString();
                    final String slo = dataSnapshot.child("shopLongitude").getValue().toString();

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    assert firebaseUser != null;
                    String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                ownerDetails.setId(id);
                ownerDetails.setShopLicenseNo(sl);
                ownerDetails.setShopName(sn);
                ownerDetails.setShopCity(sc);

                ownerDetails.setShopLatitude(sla);
                ownerDetails.setShopLongitude(slo);

                ownerDetails.setShopAddress(sa);
                ownerDetails.setOwnerName(on);
                ownerDetails.setOwnerMobile(om);
                ownerDetails.setOwnerAadharNum(oan);
                ownerDetails.setOwnerEmail(oe);
                ownerDetails.setShopAvailability(sav);
                ownerDetails.setShopVehicleInfo(sv);
                ownerDetails.setShopImageURL(si);

                final String repEmail = oe.replace(".",",");

                databaseReference2.child(repEmail).setValue(ownerDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        OwnerDetails ownerDetails1 = new OwnerDetails();
                        myRefLocation = FirebaseDatabase.getInstance().getReference().child("ShopLocation");
                        ownerDetails1.setShopLatitude(sla);
                        ownerDetails1.setShopLongitude(slo);
                        ownerDetails1.setShopAvailability(sav);
                        ownerDetails1.setShopVehicleInfo(sv);
                        ownerDetails1.setShopName(sn);
                        myRefLocation.push().setValue(ownerDetails1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                String dbEmail = oe;
                                OwnerDetails ownerRegistrationEmail = new OwnerDetails();
                                myRefEmail = FirebaseDatabase.getInstance().getReference("Email").child("OwnersEmail");
                                ownerRegistrationEmail.setOwnerEmail(dbEmail);
                                myRefEmail.push().setValue(ownerRegistrationEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        NotificationDetails notificationDetails = new NotificationDetails();
                                        String up = sn.toUpperCase();
                                        final String approveMsg = "Hi! Your Shop\t'"+up+"'\t as been Approved! \n Welcome to \nGypsy Mucker \nFamily!";

                                        databaseReference4 = FirebaseDatabase.getInstance().getReference("Notification");
                                        String time = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                        notificationDetails.setReason(approveMsg);
                                        notificationDetails.setTime(time);

                                        databaseReference4.child(repEmail).push().setValue(notificationDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                String shopUP = sn.toUpperCase();

                                                String subject = "Welcome to Grease monkey Family!!!";

                                                final String message = "Your Request For Registering Shop\n \t\t'" + shopUP + "' \n will Be Approved. \n"+"\n Verification mail reach you Soon!"
                                                        +"\n Explore your Business... \n Welcome to Gypsy Mucker Family!!!";

                                                Intent intent = new Intent(Intent.ACTION_SEND);
                                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ oe});
                                                intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                                                intent.putExtra(Intent.EXTRA_TEXT,message);

                                                intent.setType("message/rfc822");
                                                startActivity(Intent.createChooser(intent,"Choose an email client"));

                                                databaseReference5 = FirebaseDatabase.getInstance().getReference("TemporaryOwnerRegistration");
                                                databaseReference5.child(repEmail).removeValue();
                                                databaseReference6 = FirebaseDatabase.getInstance().getReference("TemporaryPassword").child("OwnerPassword");
                                                databaseReference6.child(repEmail).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(AdminApproveShop.this, "Good Work Chief..\uD83D\uDE0E", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });


                    Toast.makeText(AdminApproveShop.this, id, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
