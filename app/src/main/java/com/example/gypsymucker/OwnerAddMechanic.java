package com.example.gypsymucker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class OwnerAddMechanic extends Fragment implements ViewTreeObserver.OnWindowFocusChangeListener {

    ViewFlipper viewFlipper;
    EditText unionId, unionName, mobile, mechanicName, aadharNo, email, password, cnfrmPassword;
    ImageView imageUrl;
    Button regBtn,nxtBtn;
    ImageButton chooseImage;
    String[] vehicle = {"   Two Wheeler", "   Four Wheeler", "  Both Two & Four Wheeler"};
    Spinner spinner;
    long mLastClickTime = 0;
    DatabaseReference myRef,myRef1,myRefPassword,databaseReference4,myRefEmail2;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    String name,shop,Oname;

    public OwnerAddMechanic() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_owner_add_mechanic, container, false); //pass the correct layout name for the fragment
        unionId = view.findViewById(R.id.unionId);
        unionName = view.findViewById(R.id.unionName);
        mechanicName = view.findViewById(R.id.mechanicName);
        mobile = view.findViewById(R.id.mMobile);
        aadharNo = view.findViewById(R.id.aadharNo);
        email = view.findViewById(R.id.cEmail);
        password = view.findViewById(R.id.cPassword);
        cnfrmPassword = view.findViewById(R.id.cConfirmPassword);

        imageUrl = view.findViewById(R.id.imageView);

        viewFlipper = view.findViewById(R.id.viewFlipper);


        regBtn = view.findViewById(R.id.submitBtn);

        chooseImage = view.findViewById(R.id.chooseImage);

        nxtBtn = view.findViewById(R.id.nextBtn1);

        spinner = (Spinner) view.findViewById(R.id.mechanicVehicleInfo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, vehicle);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        regBtn = view.findViewById(R.id.submitBtn);

        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        // storageReference = FirebaseStorage.getInstance().getReference();

        myRef = FirebaseDatabase.getInstance().getReference().child("TemporaryMechanicRegistration");
        myRef1 = FirebaseDatabase.getInstance().getReference().child("PermanentOwner");

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                toast3();
            }
        });





        return view;
    }

    public void toast() {

        String lin = unionId.getText().toString().trim();
        if (lin.isEmpty()) {
            Toast.makeText(getActivity(), "Enter Union Id", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String shopN = unionName.getText().toString().trim();
        if (TextUtils.isEmpty(shopN)) {
            Toast.makeText(getActivity(), "Enter Union Name", Toast.LENGTH_LONG).show();
            return;
        }


        String mob = mobile.getText().toString().trim();
        if (mob.isEmpty()) {
            Toast.makeText(getActivity(), "Enter Mobile Number", Toast.LENGTH_LONG).show();
            return;
        }

        if (mob.length() != 10) {
            Toast.makeText(getActivity(), "Enter Valid Mobile Number", Toast.LENGTH_LONG).show();
            return;
        }

        viewFlipper.setDisplayedChild(1);
        toast1();

    }

    public void toast1(){
        String mecName = mechanicName.getText().toString().trim();
        if (mecName.isEmpty()) {
            Toast.makeText(getActivity(), "Enter Mechanic Name", Toast.LENGTH_LONG).show();
            return;
        }


        String aadhar = aadharNo.getText().toString().trim();
        if (aadhar.isEmpty()) {
            Toast.makeText(getActivity(), "Enter Aadhar Number", Toast.LENGTH_LONG).show();
            return;
        }

        if (aadhar.length() != 12) {
            Toast.makeText(getActivity(), "Enter Valid Aadhar Number", Toast.LENGTH_LONG).show();
            return;
        }

        viewFlipper.setDisplayedChild(2);
        toast2();
    }

    public void toast2(){
        if (filePath == null) {
            Toast.makeText(getActivity(), "Choose Image..!", Toast.LENGTH_LONG).show();
            return;
        }

        viewFlipper.setDisplayedChild(3);
        nxtBtn.setVisibility(View.GONE);
        regBtn.setVisibility(View.VISIBLE);
        toast3();
    }

    public void toast3(){
        String email1 = email.getText().toString().trim();
        if (TextUtils.isEmpty(email1)) {
            Toast.makeText(getActivity(), "Enter Email-Id", Toast.LENGTH_LONG).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()) {
            Toast.makeText(getActivity(), " Enter Valid Email-Id", Toast.LENGTH_LONG).show();
            return;
        }

        String pass = password.getText().toString().trim();
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_LONG).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(getActivity(), "Password is Too Short!", Toast.LENGTH_LONG).show();
            return;
        }


        String cnfmPassword1 = cnfrmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(cnfmPassword1)) {
            Toast.makeText(getActivity(), "Enter Confirm Password", Toast.LENGTH_LONG).show();
            return;
        }

        if (!pass.equals(cnfmPassword1)) {
            Toast.makeText(getActivity(), "Enter Correct Password", Toast.LENGTH_LONG).show();
            return;
        }


        mAuth.fetchSignInMethodsForEmail(email1).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                boolean check = !task.getResult().getSignInMethods().isEmpty();
                if(check){
                    Toast.makeText(getActivity(),"Email-id Already Registered!",Toast.LENGTH_LONG).show();return;
                }else {
                    help();
                }

            }});


    }

    public void help() {

        final String email1 = email.getText().toString().trim();
        final String unId = unionId.getText().toString().trim();
        final String unName = unionName.getText().toString().trim();
        final String mecName = mechanicName.getText().toString().trim();
        final String mob = mobile.getText().toString().trim();
        final String aadhar = aadharNo.getText().toString().trim();
        final String pass = password.getText().toString().trim();
        name = mecName;
        final String vehicle = spinner.getSelectedItem().toString().trim();


        String ownerEmail = mAuth.getCurrentUser().getEmail().toString().trim();
        String repOwnerEmail = ownerEmail.replace(".",",");

        myRef1.child(repOwnerEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    String keys=dataSnapshot.getKey();
                    System.out.println(keys);


                    String ownerName = dataSnapshot.child("ownerName").getValue().toString();
                    String ownerNo = dataSnapshot.child("ownerMobile").getValue().toString();
                    final   String shopName = dataSnapshot.child("shopName").getValue().toString();
                    String licenseNo = dataSnapshot.child("shopLicenseNo").getValue().toString();
                    String add = dataSnapshot.child("shopAddress").getValue().toString();

                    System.out.println(ownerName);
                    System.out.println(ownerNo);
                    System.out.println(shopName);
                    System.out.println(add);
                    System.out.println(licenseNo);
                    shop = shopName;

                    final MechanicDetails mechanicDetails = new MechanicDetails();

                    mechanicDetails.setMechanicEmail(email1);
                    mechanicDetails.setUnionId(unId);
                    mechanicDetails.setUnionName(unName);
                    mechanicDetails.setMechanicName(mecName);
                    mechanicDetails.setMechanicMobile(mob);
                    mechanicDetails.setMechanicAadharNum(aadhar);
                    mechanicDetails.setOwnerName(ownerName);
                    mechanicDetails.setOwnerMobile(ownerNo);
                    mechanicDetails.setShopName(shopName);
                    mechanicDetails.setShopLicenseNo(licenseNo);
                    mechanicDetails.setShopAddress(add);
                    mechanicDetails.setMechanicVehicleInfo(vehicle);

                    final MechanicDetails mechanicDetails1 = new MechanicDetails();
                    mechanicDetails1.setMechanicPassword(pass);

                myRefPassword = FirebaseDatabase.getInstance().getReference("TemporaryPassword");
                final String repMecEmail = email1.replace(".",",");


                   if (filePath == null) {
                        Toast.makeText(getActivity(), "Choose Image..!", Toast.LENGTH_LONG).show(); return;
                    }

                    if (filePath != null) {
                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setTitle("Registering...");
                        progressDialog.show();

                        storageReference = FirebaseStorage.getInstance().getReference();
                        final StorageReference ref = storageReference.child(repMecEmail);
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

                                                mechanicDetails.setMechanicImageUrl(imageReference);

                                                myRefPassword.child("MechanicPassword").child(repMecEmail).setValue(mechanicDetails1);

                                                myRef.child(repMecEmail).setValue(mechanicDetails);

                                                notification();

                                                Toast.makeText(getActivity(),"Registration Successful",Toast.LENGTH_LONG).show();

                                            }
                                        });
                                        // Toast.makeText(GMOwnerRegistration.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                              }
        });

    }

    public void notification(){

        NotificationDetails notificationDetails = new NotificationDetails();
        String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        String up = name.toUpperCase();

        final String approveMsg = "Hi! \n Your request of \n new Mechanic\n\t\t'"+up+"'\n\t as been Received! Verification on \n process!";

        databaseReference4 = FirebaseDatabase.getInstance().getReference().child("Notification");

        notificationDetails.setReason(approveMsg);
        notificationDetails.setTime(date);

        String ownerEmail = mAuth.getCurrentUser().getEmail().toString().trim();
        String repOwnerEmail = ownerEmail.replace(".",",");

        databaseReference4.child(repOwnerEmail).push().setValue(notificationDetails);
        clear();
        View view = null;
        ackDialog(view);
        regBtn.setVisibility(View.GONE);
    }

    public void clear(){

        email.setText("");
        unionId.setText("");
        unionName.setText("");
        mechanicName.setText("");
        mobile.setText("");
        aadharNo.setText("");
        password.setText("");
        cnfrmPassword.setText("");

    }

    public void ackDialog(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_ack, null);
        alert.setView(view1);

        final TextView textView = view1.findViewById(R.id.notification);
        ImageButton okBtn = view1.findViewById(R.id.okBtn);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        String mec = name.toUpperCase();
        String data = "Your request for \n\n"+mec+"\n\n will be Registered \n & We Approve it ASAP, \n Check your Notification \n panel for further Process";
        textView.setText(data);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

                //choose image from file
        private void chooseImage() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
        //Set image to ImageView
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && data != null && data.getData() != null) {
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    imageUrl.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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