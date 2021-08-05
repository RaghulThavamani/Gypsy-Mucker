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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class AdminApproveMechanic extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<MechanicDetails, HolderAdminApproveMechanic> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference,databaseReference1;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3,databaseReference4,myRefPassword,myRefEmail;
    NotificationDetails notificationDetails;
    MechanicDetails mechanicDetails;
    String declineMechanic,key,declineOwnerEmail;
    String approveMechanic,approveShop,approveEmail,approveOwnerEmail;
    private FirebaseAuth mAuth;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_mechanic);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = database.getReference("TemporaryMechanicRegistration");
        databaseReference3=database.getReference().child("PermanentOwner");
        databaseReference1=database.getReference().child("PermanentMechanic");
        myRefPassword = database.getReference().child("TemporaryPassword").child("MechanicPassword");
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
            protected void onBindViewHolder(@NonNull final HolderAdminApproveMechanic holderAdminApproveMechanic, int i, @NonNull MechanicDetails mechanicDetails) {

                holderAdminApproveMechanic.unionId.setText(mechanicDetails.getUnionId());
                holderAdminApproveMechanic.unionName.setText(mechanicDetails.getUnionName());
                holderAdminApproveMechanic.mechanicMobile.setText(mechanicDetails.getMechanicMobile());
                holderAdminApproveMechanic.mechanicName.setText(mechanicDetails.getMechanicName());
                holderAdminApproveMechanic.aadharNoMechanic.setText(mechanicDetails.getMechanicAadharNum());
                holderAdminApproveMechanic.emailMechanic.setText(mechanicDetails.getMechanicEmail());
                holderAdminApproveMechanic.vehicleInfoMec.setText(mechanicDetails.getMechanicVehicleInfo());
                holderAdminApproveMechanic.shopLicenseNO1.setText(mechanicDetails.getShopLicenseNo());
                holderAdminApproveMechanic.shopName1.setText(mechanicDetails.getShopName());
                holderAdminApproveMechanic.shopAdd1.setText(mechanicDetails.getShopAddress());
                holderAdminApproveMechanic.ownerName1.setText(mechanicDetails.getOwnerName());
                holderAdminApproveMechanic.mobile1.setText(mechanicDetails.getOwnerMobile());

                String im = mechanicDetails.getMechanicImageUrl().toString().trim();
                Uri my_im = Uri.parse(im);
                Glide.with(AdminApproveMechanic.this).load(my_im).into(holderAdminApproveMechanic.imageUrl);

                holderAdminApproveMechanic.reject1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        String mecName = holderAdminApproveMechanic.mechanicName.getText().toString().trim();
                        declineMechanic = mecName;
                        String shopName = holderAdminApproveMechanic.shopName1.getText().toString().trim();
                        String mecEmail = holderAdminApproveMechanic.emailMechanic.getText().toString().trim();
                        String emailMec = mecEmail.replace(".",",");
                        key = emailMec;
                        Toast.makeText(AdminApproveMechanic.this, key, Toast.LENGTH_SHORT).show();
                       databaseReference3.orderByChild("shopName").equalTo(shopName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot data: dataSnapshot.getChildren()){

                                    String keys=data.getKey();
                                    System.out.println(keys);

                                   declineOwnerEmail = keys;

                                    Toast.makeText(AdminApproveMechanic.this, keys, Toast.LENGTH_SHORT).show();
                                    declineMechanicDialog(v);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            } });
                    }
                });

                holderAdminApproveMechanic.approve1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        String mecName = holderAdminApproveMechanic.mechanicName.getText().toString().trim();
                        approveMechanic = mecName;
                        String shopName = holderAdminApproveMechanic.shopName1.getText().toString().trim();
                        approveShop = shopName;
                        String mecEmail = holderAdminApproveMechanic.emailMechanic.getText().toString().trim();
                        approveEmail = mecEmail;
                        databaseReference3.orderByChild("shopName").equalTo(shopName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot data: dataSnapshot.getChildren()){

                                    String keys=data.getKey();
                                    System.out.println(keys);

                                    approveOwnerEmail = keys;

                                    Toast.makeText(AdminApproveMechanic.this, keys, Toast.LENGTH_SHORT).show();
                                    approveMechanicDialog(v);
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
                        .inflate(R.layout.view_admin_approve_mechanic, parent, false);
                return new HolderAdminApproveMechanic(view);
            }
        };
        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);

    }

    public void declineMechanicDialog(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(AdminApproveMechanic.this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_decline_mechanic, null);
        alert.setView(view1);
        final CheckBox checkBox1 = view1.findViewById(R.id.checkBox);
        final CheckBox checkBox2 = view1.findViewById(R.id.checkBox2);
        final CheckBox checkBox3 = view1.findViewById(R.id.checkBox3);
        final CheckBox checkBox4 = view1.findViewById(R.id.checkBox4);

        ImageButton okBtn = view1.findViewById(R.id.okBtn1);
        final AlertDialog alertDialog = alert.create();


        okBtn.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(AdminApproveMechanic.this, "Click Any One Reason!", Toast.LENGTH_LONG).show();
                    return;

                }


               notificationDetails = new NotificationDetails();


                final String finalNo = no1;
                final String finalNo1 = no2;
                final String finalNo2 = no3;
                final String finalNo3 = no4;

                String up = declineMechanic.toUpperCase();

                final String deleteMsg = "Your Request For \n Adding Mechanic\n '" + up + "' \n will Be Decline \n Due to the reason of \n" + finalNo + finalNo1 + finalNo2 + finalNo3;

                databaseReference4 = FirebaseDatabase.getInstance().getReference().child("Notification");
                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                notificationDetails.setReason(deleteMsg);
                notificationDetails.setTime(date);
                databaseReference4.child(declineOwnerEmail).push().setValue(notificationDetails);

                storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ref = storageReference.child(key);
                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseReference.child(key).removeValue();
                        Toast.makeText(AdminApproveMechanic.this, "Rejected", Toast.LENGTH_LONG).show();
                        myRefPassword.child(key).removeValue();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });


                alertDialog.dismiss();


            }
        });

        alertDialog.show();
    }

    public void approveMechanicDialog(View view){

        final AlertDialog.Builder alert = new AlertDialog.Builder(AdminApproveMechanic.this);
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

        final String repEmail = approveEmail.replace(".",",");
        myRefPassword = database.getReference().child("TemporaryPassword").child("MechanicPassword").child(repEmail);
        myRefPassword.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    String keys = data.getKey();
                    System.out.println(keys);


                    String pass = data.getValue().toString();

                    System.out.println(pass);
                    Toast.makeText(AdminApproveMechanic.this, pass+"\n"+approveEmail, Toast.LENGTH_LONG).show();

                    mAuth.createUserWithEmailAndPassword(approveEmail, pass)
                            .addOnCompleteListener(AdminApproveMechanic.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    addMechanicData();
                                                    Toast.makeText(AdminApproveMechanic.this, "Verification Send", Toast.LENGTH_LONG).show();
                                                    myRefPassword = database.getReference().child("TemporaryPassword").child("MechanicPassword");
                                                    myRefPassword.child(repEmail).removeValue();

                                                }else {
                                                    Toast.makeText(AdminApproveMechanic.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });



                                    } else {
                                        Toast.makeText(AdminApproveMechanic.this, "Registration Failed or Email-Id Already Registered", Toast.LENGTH_LONG).show();
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

    public void addMechanicData() {
        final String repEmail = approveEmail.replace(".",",");
        databaseReference.child(repEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                    String unionId = dataSnapshot.child("unionId").getValue().toString();
                    String unionName = dataSnapshot.child("unionName").getValue().toString();
                    String mechanicName = dataSnapshot.child("mechanicName").getValue().toString();
                    String mechanicMobile = dataSnapshot.child("mechanicMobile").getValue().toString();
                    String aadharNo = dataSnapshot.child("mechanicAadharNum").getValue().toString();
                    String email = dataSnapshot.child("mechanicEmail").getValue().toString();
                    String vehicleInfo = dataSnapshot.child("mechanicVehicleInfo").getValue().toString();
                    String imageURL = dataSnapshot.child("mechanicImageUrl").getValue().toString();
                    String shopName = dataSnapshot.child("shopName").getValue().toString();



                    ArrayList<String> userList = new ArrayList<String>();
                    userList.add(unionId);
                    userList.add(unionName);
                    userList.add(mechanicName);
                    userList.add(mechanicMobile);
                    userList.add(aadharNo);
                    userList.add(email);
                    userList.add(vehicleInfo);
                    userList.add(imageURL);
                    System.out.println(userList);



                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                assert firebaseUser != null;
                String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                    MechanicDetails mechanicDetails1 = new MechanicDetails();
                    mechanicDetails1.setId(id);
                    mechanicDetails1.setUnionId(unionId);
                    mechanicDetails1.setUnionName(unionName);
                    mechanicDetails1.setMechanicName(mechanicName);
                    mechanicDetails1.setMechanicMobile(mechanicMobile);
                    mechanicDetails1.setMechanicAadharNum(aadharNo);
                    mechanicDetails1.setMechanicEmail(email);
                    mechanicDetails1.setMechanicVehicleInfo(vehicleInfo);
                    mechanicDetails1.setMechanicImageUrl(imageURL);
                    mechanicDetails1.setShopName(shopName);

                myRefEmail = FirebaseDatabase.getInstance().getReference("Email");
                MechanicDetails mechanicDetails3 = new MechanicDetails();
                mechanicDetails3.setMechanicEmail(approveEmail);
                myRefEmail.child("MechanicEmail").push().setValue(mechanicDetails3);


                    databaseReference1.child(repEmail).setValue(mechanicDetails1);

                    NotificationDetails notificationDetails1 = new NotificationDetails();
                    String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    String up = approveMechanic.toUpperCase();

                    final String approveMsg = "Your Request \n For Adding \n Mechanic\n '" + up + "' \n will Be \n Approved!";

                    databaseReference4 = FirebaseDatabase.getInstance().getReference().child("Notification");

                      notificationDetails1.setReason(approveMsg);
                      notificationDetails1.setTime(date);
                    databaseReference4.child(approveOwnerEmail).push().setValue(notificationDetails1);
                    Toast.makeText(AdminApproveMechanic.this, "Approved", Toast.LENGTH_LONG).show();
                    databaseReference.child(repEmail).removeValue();
                    notification();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void notification(){

       NotificationDetails notificationDetails2 = new NotificationDetails();
        String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        final String approveMsg = "Hi! Welcome to \n Gypsy Mucker Family!";

        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Notification");
        notificationDetails2.setTime(date);
        notificationDetails2.setReason(approveMsg);
        String em = approveEmail;
        String repEmail = em.replace(".",",");
        databaseReference1.child("Mechanic").child(repEmail).push().setValue(notificationDetails2);
        approvedEmail();

    }

    public void approvedEmail(){
        String shopUP = approveMechanic.toUpperCase();

        String subject = "Welcome to Grease monkey Family!!!";

        final String message = shopUP + ",\n\t Your Request For Joining Mechanic of \t"+approveShop+"\t under GYPSY MUCKER Family will Be Approved. \n"+"\n Verification mail reach you Soon!"
                +"\n Explore your Work... \n Welcome to Gypsy Mucker Family!!!";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ approveEmail});
        //Toast.makeText(ApproveBusiness.this, email, Toast.LENGTH_LONG).show();
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT,message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Choose an email client"));



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
