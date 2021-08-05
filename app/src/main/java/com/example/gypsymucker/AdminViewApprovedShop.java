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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;

public class AdminViewApprovedShop extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<OwnerDetails, HolderAdminApproveBusiness> recyclerAdapter;
    OwnerDetails ownerDetails;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    Query mecList;
    String imgValue;
    Double lt,lg;
    String emailOwnerForDialog;
    String shop;
    private FirebaseAuth mAuth;
    GoogleMap mMap;
    long mLastClickTime = 0;

    FirebaseRecyclerAdapter<MechanicDetails, HolderAdminApproveMechanic> recyclerAdapter1;
    RecyclerView.LayoutManager layoutManager1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_approved_shop);

        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("PermanentOwner");
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

                holderAdminApproveBusiness.warning.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        String email = holderAdminApproveBusiness.email.getText().toString();
                        emailOwnerForDialog = email;
                        message(v);
                    }
                });


                holderAdminApproveBusiness.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        String shopName = holderAdminApproveBusiness.shopName.getText().toString();
                        shop = shopName;
                        mechanicList(v);
                    }
                });



            }
            @NonNull
            @Override
            public HolderAdminApproveBusiness onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_admin_view_approved_shop, parent, false);
                return new HolderAdminApproveBusiness(view);
            }
        };
        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);
    }
    public void certificateImageDialog(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(AdminViewApprovedShop.this);

        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_view_certificate,null);
        alert.setView(view1);
        ImageView licenseCertificateImg = view1.findViewById(R.id.licenseCertificateImg);
        final AlertDialog alertDialog = alert.create();

        Uri my_im = Uri.parse(imgValue);
        Glide.with(AdminViewApprovedShop.this).load(my_im).into(licenseCertificateImg);

        alertDialog.show();
    }

    public void locationDialog(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(AdminViewApprovedShop.this);

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

    public void message(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(AdminViewApprovedShop.this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_admin_send_message_to_shop,null);
        alert.setView(view1);
        TextView emailAdmin = view1.findViewById(R.id.emailAdmin);
        TextView emailOwner = view1.findViewById(R.id.emailOwner);
        final EditText message = view1.findViewById(R.id.message);
        Button sendMessage = view1.findViewById(R.id.sendMessage);

        final AlertDialog alertDialog = alert.create();

        emailAdmin.setText("gypsymucker@gmail.com");
        emailOwner.setText(emailOwnerForDialog);


        final String repEmail = emailOwnerForDialog.replace(".",",");
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                final String messageContent = message.getText().toString().trim();
                if(TextUtils.isEmpty(messageContent)){
                    Toast.makeText(AdminViewApprovedShop.this, "Enter Message!", Toast.LENGTH_SHORT).show(); return;
                }

                String content = "From Admin"+"\n"+"gypsymucker@gmail.com"+"\n"+messageContent;

                databaseReference2 = FirebaseDatabase.getInstance().getReference("Notification");
                String time = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                NotificationDetails notificationDetails=new NotificationDetails();
                notificationDetails.setReason(content);
                notificationDetails.setTime(time);
                databaseReference2.child(repEmail).push().setValue(notificationDetails);
                message.setText("");
                Toast.makeText(AdminViewApprovedShop.this, "Message Send!", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public void mechanicList(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_admin_view_mechanic_under_the_shop, null);
        alert.setView(view1);

        final  RecyclerView recyclerView = view1.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager1 = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager1);

        final AlertDialog alertDialog = alert.create();

        mecList = FirebaseDatabase.getInstance().getReference("PermanentMechanic").orderByChild("shopName").equalTo(shop);
        FirebaseRecyclerOptions options1 =
                new FirebaseRecyclerOptions.Builder<MechanicDetails>()
                        .setQuery(mecList, MechanicDetails.class)
                        .build();
        recyclerAdapter1 = new FirebaseRecyclerAdapter<MechanicDetails, HolderAdminApproveMechanic>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull HolderAdminApproveMechanic holderAdminApproveMechanic, int i, @NonNull MechanicDetails mechanicDetails) {
                holderAdminApproveMechanic.mechanicName.setText(mechanicDetails.getMechanicName());
                holderAdminApproveMechanic.vehicleInfoMec.setText(mechanicDetails.getMechanicVehicleInfo());
                String im=mechanicDetails.getMechanicImageUrl().toString().trim();
                Uri my_im = Uri.parse(im);
                Glide.with(AdminViewApprovedShop.this).load(my_im).into(holderAdminApproveMechanic.imageUrl);

            }

            @NonNull
            @Override
            public HolderAdminApproveMechanic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_admin_view_mechanic_under_the_shop, parent, false);
                return new HolderAdminApproveMechanic(view);
            }
        };


        recyclerAdapter1.notifyDataSetChanged();
        recyclerAdapter1.startListening();
        recyclerView.setAdapter(recyclerAdapter1);

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
