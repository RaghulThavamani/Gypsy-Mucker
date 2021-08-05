package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class OwnerRegisterPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button nextBtn,regBtn;
    ViewFlipper viewFlipper;
    EditText licenseNo,shopName,shopCity,latitude,longitude,address,bOwnerName,bMobile,idNum,bEmail,bPassword,bCnfmPassword;
    ImageButton chooseBtn,locationBtn;
    ImageView licenseImg;

    Spinner dropDown,dropDown2;
    String[] availability={"   Mon-Fri | 9am-9pm","   24/7","   Mon-Sat | 9am-9pm","   Mon-Fri | 24Hrs","   Mon-Sat | 24Hrs"};
    String[] vehicle={"   Two Wheeler","   Four Wheeler","  Both Two & Four Wheeler"};

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    long mLastClickTime = 0;
    GoogleMap mMap;
    Geocoder geo;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference myRef,myRefPassword;
    FirebaseAuth mAuth;
    OwnerDetails ownerDetails;
    String shop;
    boolean checkEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_register_page);

        nextBtn = findViewById(R.id.nextBtn);
        viewFlipper = findViewById(R.id.viewFlipper);
        regBtn=findViewById(R.id.submitBtn);
        licenseNo=findViewById(R.id.licenseNo);
        shopName=findViewById(R.id.shopName);
        shopCity=findViewById(R.id.shopCity);
        latitude=findViewById(R.id.latitude);
        longitude=findViewById(R.id.longitude);
        address=findViewById(R.id.address);
        bOwnerName=findViewById(R.id.bOwnerName);
        idNum=findViewById(R.id.idNum);
        bMobile=findViewById(R.id.bMobile);
        bEmail=findViewById(R.id.bEmail);
        bPassword=findViewById(R.id.bPassword);
        bCnfmPassword=findViewById(R.id.bCnfmPassword);

        //spinner declaration for Availability
        dropDown=findViewById(R.id.dropDown);
        dropDown.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the Availability of shop Status list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,availability);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner1
        dropDown.setAdapter(aa);



        //spinner declaration for Vehicle
        dropDown2=findViewById(R.id.dropDown2);
        dropDown2.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the Vehicle info of shop
        ArrayAdapter aaa = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,vehicle);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner2
        dropDown2.setAdapter(aaa);


        //ImageButton declaration
        locationBtn=findViewById(R.id.locationBtn);

        //Button declaration
        chooseBtn=findViewById(R.id.chooseBtn);

        //ImageView declaration
        licenseImg=findViewById(R.id.licenseImg);

        //Firebase Storage instance
        storage = FirebaseStorage.getInstance();

        //Firebase Auth instance
        mAuth= FirebaseAuth.getInstance();


        //Firebase Database instance
        myRef = FirebaseDatabase.getInstance().getReference().child("TemporaryOwnerRegistration");


        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation(v);
            }
        });
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
               toast();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                toast4();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    //Set image to ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                licenseImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void toast(){

        String lin = licenseNo.getText().toString().trim();
        if(lin.isEmpty()){
            Toast.makeText(OwnerRegisterPage.this,"Enter License Number",Toast.LENGTH_LONG).show();return;}

        String shopN = shopName.getText().toString().trim();
        if(TextUtils.isEmpty(shopN)){
            Toast.makeText(OwnerRegisterPage.this,"Enter Shop Name",Toast.LENGTH_LONG).show();return;}

        String shopC = shopCity.getText().toString().trim();
        if(TextUtils.isEmpty(shopC)){
            Toast.makeText(OwnerRegisterPage.this,"Enter Shop City",Toast.LENGTH_LONG).show();return;}

        viewFlipper.setDisplayedChild(1);
        toast1();

    }

    public  void  toast1(){
        String add = address.getText().toString().trim();
        if(TextUtils.isEmpty(add)){
            Toast.makeText(OwnerRegisterPage.this,"Enter Address",Toast.LENGTH_LONG).show();return;}

        String ownName = bOwnerName.getText().toString().trim();
        if(TextUtils.isEmpty(ownName)){
            Toast.makeText(OwnerRegisterPage.this,"Enter Owner Name",Toast.LENGTH_LONG).show();return;}

        String mob = bMobile.getText().toString().trim();
        if(mob.isEmpty()){
            Toast.makeText(OwnerRegisterPage.this,"Enter Mobile Number",Toast.LENGTH_LONG).show();return;}

        if(mob.length() != 10){
            Toast.makeText(OwnerRegisterPage.this,"Enter Valid Mobile Number",Toast.LENGTH_LONG).show();return;}

        String aadhar = idNum.getText().toString().trim();
        if(aadhar.isEmpty()){
            Toast.makeText(OwnerRegisterPage.this,"Enter Aadhar Number",Toast.LENGTH_LONG).show();return;}

        if(aadhar.length() != 12){
            Toast.makeText(OwnerRegisterPage.this,"Enter Valid Aadhar Number",Toast.LENGTH_LONG).show();return;}

        viewFlipper.setDisplayedChild(2);
        toast2();
    }

    public  void  toast2(){
        if (filePath == null) {
            Toast.makeText(OwnerRegisterPage.this, "Choose Image..!", Toast.LENGTH_LONG).show(); return;
        }
        viewFlipper.setDisplayedChild(3);
        toast3();
    }

    public  void  toast3(){
        String lat = latitude.getText().toString().trim();
        if(TextUtils.isEmpty(lat)){
            Toast.makeText(OwnerRegisterPage.this,"Click Location Button To Add Location",Toast.LENGTH_LONG).show();return;}

        String lng = longitude.getText().toString().trim();
        if(TextUtils.isEmpty(lng)){
            Toast.makeText(OwnerRegisterPage.this,"Click Location Button To Add Location",Toast.LENGTH_LONG).show();return;}
        viewFlipper.setDisplayedChild(4);
        nextBtn.setVisibility(View.GONE);
        regBtn.setVisibility(View.VISIBLE);
    }

    public  void  toast4(){

        final String email = bEmail.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(OwnerRegisterPage.this,"Enter Email-Id",Toast.LENGTH_LONG).show();return; }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(OwnerRegisterPage.this, " Enter Valid Email-Id", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean check = !task.getResult().getSignInMethods().isEmpty();
                        checkEmail = check;
                        if(check){
                            Toast.makeText(OwnerRegisterPage.this,"Email-id Already Registered!",Toast.LENGTH_LONG).show();return;
                        }else {toast5();}

              }});

    }

    public void toast5(){

        String pass = bPassword.getText().toString().trim();
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(OwnerRegisterPage.this,"Enter Password",Toast.LENGTH_LONG).show();return;}

        if(pass.length() < 6){
            Toast.makeText(OwnerRegisterPage.this,"Password is Too Short!",Toast.LENGTH_LONG).show();return;}


        String cnfmPassword = bCnfmPassword.getText().toString().trim();
        if(TextUtils.isEmpty(cnfmPassword)){
            Toast.makeText(OwnerRegisterPage.this,"Enter Confirm Password",Toast.LENGTH_LONG).show();return;}

        if(!pass.equals(cnfmPassword)){
            Toast.makeText(OwnerRegisterPage.this,"Enter Correct Password",Toast.LENGTH_LONG).show();return;}

        ownerPassword();
    }

    private void ownerPassword() {
        String email = bEmail.getText().toString().trim();
        String pass = bPassword.getText().toString().trim();
        OwnerDetails ownerDetails1 = new OwnerDetails();
        ownerDetails1.setOwnerPassword(pass);
        myRefPassword = FirebaseDatabase.getInstance().getReference("TemporaryPassword");
        final String repEmail = email.replace(".",",");
        myRefPassword.child("OwnerPassword").child(repEmail).setValue(ownerDetails1);
        registerShopDetails();
    }

    public void getLocation(View view) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(OwnerRegisterPage.this);

        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_get_shop_location ,null);
        alert.setView(view1);
        ImageButton normal = view1.findViewById(R.id.normal);
        ImageButton satellite = view1.findViewById(R.id.satellite);
        Button getLocation = view1.findViewById(R.id.getLocation);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

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
                    geo = new Geocoder(OwnerRegisterPage.this, Locale.getDefault());

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            try {
                                if (geo == null)
                                    geo = new Geocoder(OwnerRegisterPage.this, Locale.getDefault());
                                List<Address> address = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                if (address.size() > 0) {
                                    mMap.addMarker(new MarkerOptions().position(latLng).title("Name:" + address.get(0).getCountryName()
                                            + ". Address:" + address.get(0).getAddressLine(0)));

                                }
                            } catch (IOException ex) {
                                if (ex != null)
                                    Toast.makeText(OwnerRegisterPage.this, "Error:" + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            double lat =marker.getPosition().latitude;
                            double lng =marker.getPosition().longitude;
                            latitude.setText(String.valueOf(lat));
                            longitude.setText(String.valueOf(lng));

                            return false;
                        }
                    });
                }
            }
        });


        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

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

    public void registerShopDetails(){


        final String email=bEmail.getText().toString().trim();
        String lat = latitude.getText().toString().trim();
        String lng = longitude.getText().toString().trim();
        String lin = licenseNo.getText().toString().trim();
        final String shopN = shopName.getText().toString().trim();
        shop = shopN;
        String shopC = shopCity.getText().toString().trim();
        String add = address.getText().toString().trim();
        final String ownName = bOwnerName.getText().toString().trim();
        String mob = bMobile.getText().toString().trim();
        String aadhar = idNum.getText().toString().trim();
        // String email = bEmail.getText().toString().trim();

        String availability = dropDown.getSelectedItem().toString().trim();
        String vehicle = dropDown2.getSelectedItem().toString().trim();

        ownerDetails = new OwnerDetails();

        ownerDetails.setShopLicenseNo(lin);
        ownerDetails.setShopName(shopN);
        ownerDetails.setShopCity(shopC);

        ownerDetails.setShopLatitude(lat);
        ownerDetails.setShopLongitude(lng);

        ownerDetails.setShopAddress(add);
        ownerDetails.setOwnerName(ownName);
        ownerDetails.setOwnerMobile(mob);
        ownerDetails.setOwnerAadharNum(aadhar);
        ownerDetails.setOwnerEmail(email);
        ownerDetails.setShopAvailability(availability);
        ownerDetails.setShopVehicleInfo(vehicle);



        if (filePath == null) {
            Toast.makeText(OwnerRegisterPage.this, "Choose Image..!", Toast.LENGTH_LONG).show(); return;
        }

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(OwnerRegisterPage.this);
            progressDialog.setTitle("Registering...");
            progressDialog.show();
            storageReference = FirebaseStorage.getInstance().getReference();
            final String repEmail = email.replace(".",",");
            final StorageReference ref = storageReference.child(repEmail);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            // StorageMetadata snapshotMetadata = taskSnapshot.getMetadata();

                            //to get image url from firebase storage...
                            Task<Uri> downloadUrl = ref.getDownloadUrl();
                            downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageReference = uri.toString();

                                    ownerDetails.setShopImageURL(imageReference);
                                    myRef.child(repEmail).setValue(ownerDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(OwnerRegisterPage.this,"Registered!",Toast.LENGTH_LONG).show();
                                            clear();
                                            regBtn.setVisibility(View.GONE);
                                            View v = null;
                                            ackDialog(v);

                                        }
                                    });


                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(OwnerRegisterPage.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Registering... " + (int) progress + "%");
                            progressDialog.setCanceledOnTouchOutside(false);
                        }
                    });
        }


    }

    public void clear(){
        bEmail.setText("");
        latitude.setText("");
        longitude.setText("");
        licenseNo.setText("");
        shopName.setText("");
       shopCity.setText("");
       address.setText("");
       bOwnerName.setText("");
       bMobile.setText("");
       idNum.setText("");
       bCnfmPassword.setText("");
       bPassword.setText("");

    }

    public void ackDialog(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(OwnerRegisterPage.this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_ack, null);
        alert.setView(view1);

        final TextView textView = view1.findViewById(R.id.notification);
        ImageButton okBtn = view1.findViewById(R.id.okBtn);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        String shopCaps = shop.toUpperCase();
        String data = "Your request for \n\n"+shopCaps+"\n\n will be Registered \n & We Approve it ASAP, \n Check your Email Address \n for further Process";
        textView.setText(data);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(),GmMainLoginPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        alertDialog.show();
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
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
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
