package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class CustomerServiceHall extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<MechanicDetails, HolderCustomerViewPendingMechanic> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference, databaseReference1, databaseReference2, databaseReference3, databaseReference4, databaseReference5,databaseReference6;
    FirebaseAuth mAuth;
    String repCusEmail, cusEmail, mecEmail, orgMecEmail;

    FirebaseRecyclerAdapter<MechanicDetails, HolderCustomerViewPendingMechanic> recyclerAdapter1;
    RecyclerView.LayoutManager layoutManager1;

    long mLastClickTime = 0;

    TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_hall);

        BottomNavigationView navigation1 = findViewById(R.id.bottom_navigation);
        navigation1.setSelectedItemId(R.id.waitingHall);
        navigation1.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener1);

        msg = findViewById(R.id.msg);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        layoutManager1 = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager1);

        mAuth = FirebaseAuth.getInstance();
        final String myEmail = mAuth.getCurrentUser().getEmail().toString().trim();
        final String repMyEmail = myEmail.replace(".", ",");
        repCusEmail = repMyEmail;
        cusEmail = myEmail;


        databaseReference = FirebaseDatabase.getInstance().getReference("WorkBoard").child("PendingWork").child(repMyEmail);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    pendingWork();
                } else {
                    acceptedWork();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }


    public void pendingWork() {

        databaseReference4 = FirebaseDatabase.getInstance().getReference("WorkBoard").child("AcceptedWork").child("Mechanic").child(repCusEmail);
        databaseReference4.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                        msg.setVisibility(View.VISIBLE);
                        msg.setText("Already mechanic accepted your request so cancel all other service request!"+"After Cancellation Accepted Mechanic request will Appear!");
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<MechanicDetails>()
                        .setQuery(databaseReference, MechanicDetails.class)
                        .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<MechanicDetails, HolderCustomerViewPendingMechanic>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderCustomerViewPendingMechanic holderCustomerViewPendingMechanic, int i, @NonNull MechanicDetails mechanicDetails) {

                holderCustomerViewPendingMechanic.unionId.setText(mechanicDetails.getUnionId());
                holderCustomerViewPendingMechanic.unionName.setText(mechanicDetails.getUnionName());
                holderCustomerViewPendingMechanic.mechanicName.setText(mechanicDetails.getMechanicName());
                holderCustomerViewPendingMechanic.mechanicMobile.setText(mechanicDetails.getMechanicMobile());
                holderCustomerViewPendingMechanic.emailMechanic.setText(mechanicDetails.getMechanicEmail());
                holderCustomerViewPendingMechanic.aadharNoMechanic.setText(mechanicDetails.getMechanicAadharNum());
                holderCustomerViewPendingMechanic.vehicleInfoMec11.setText(mechanicDetails.getMechanicVehicleInfo());
                holderCustomerViewPendingMechanic.status.setText(mechanicDetails.getStatus());


                holderCustomerViewPendingMechanic.cancelService.setOnClickListener(new View.OnClickListener() {
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

                                        String mecEmail = holderCustomerViewPendingMechanic.emailMechanic.getText().toString();
                                        final String repMecEmail = mecEmail.replace(".", ",");

                                        databaseReference1 = FirebaseDatabase.getInstance().getReference("WorkBoard").child("PendingWork").child(repCusEmail);
                                        databaseReference1.child(repMecEmail).removeValue();

                                        NotificationDetails notificationDetails = new NotificationDetails();
                                        databaseReference3 = FirebaseDatabase.getInstance().getReference("Notification").child("Mechanic");
                                        String msg = "Service Request from \n\t'"+cusEmail+"'\n will be Cancelled, \n Sorry for \n the Inconvenient.";
                                        String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                        notificationDetails.setReason(msg);
                                        notificationDetails.setTime(date);
                                        databaseReference3.child(repMecEmail).push().setValue(notificationDetails);

                                        databaseReference2 = FirebaseDatabase.getInstance().getReference("RequestedCustomerData");
                                        databaseReference2.orderByChild("email").equalTo(cusEmail).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {

                                                    databaseReference3 = FirebaseDatabase.getInstance().getReference("RequestedCustomerData");
                                                    databaseReference3.orderByChild("requestTo").equalTo(repMecEmail).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot data : dataSnapshot.getChildren()) {

                                                                String keys = data.getKey();
                                                                Toast.makeText(CustomerServiceHall.this, keys, Toast.LENGTH_SHORT).show();
                                                                databaseReference3.child(keys).removeValue();

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

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerServiceHall.this);
                        builder.setMessage("Are you sure? You want to cancel this request!").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }
                });

            }

            @NonNull
            @Override
            public HolderCustomerViewPendingMechanic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_customer_view_pending_mechanic, parent, false);
                return new HolderCustomerViewPendingMechanic(view);
            }
        };
        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void acceptedWork() {

        msg.setVisibility(View.GONE);
        databaseReference4 = FirebaseDatabase.getInstance().getReference("WorkBoard").child("AcceptedWork").child("Mechanic").child(repCusEmail);

        FirebaseRecyclerOptions options1 =
                new FirebaseRecyclerOptions.Builder<MechanicDetails>()
                        .setQuery(databaseReference4, MechanicDetails.class)
                        .build();

        recyclerAdapter1 = new FirebaseRecyclerAdapter<MechanicDetails, HolderCustomerViewPendingMechanic>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderCustomerViewPendingMechanic holderCustomerViewPendingMechanic, int i, @NonNull MechanicDetails mechanicDetails) {


                holderCustomerViewPendingMechanic.mechanicName.setText(mechanicDetails.getMechanicName());
                holderCustomerViewPendingMechanic.mechanicMobile.setText(mechanicDetails.getMechanicMobile());
                holderCustomerViewPendingMechanic.emailMechanic.setText(mechanicDetails.getMechanicEmail());
                holderCustomerViewPendingMechanic.aadharNoMechanic.setText(mechanicDetails.getMechanicAadharNum());
                holderCustomerViewPendingMechanic.vehicleInfoMec11.setText(mechanicDetails.getMechanicVehicleInfo());
                holderCustomerViewPendingMechanic.status.setText(mechanicDetails.getStatus());

                Uri my_im = Uri.parse(mechanicDetails.getMechanicImageUrl());
                Glide.with(CustomerServiceHall.this).load(my_im).into(holderCustomerViewPendingMechanic.imageUrl);

                if (mechanicDetails.getStatus().toString().equals("Completed.")) {
                    holderCustomerViewPendingMechanic.review.setVisibility(View.VISIBLE);
                    holderCustomerViewPendingMechanic.cancelService.setVisibility(View.GONE);
                    holderCustomerViewPendingMechanic.call.setVisibility(View.GONE);
                }


                holderCustomerViewPendingMechanic.cancelService.setOnClickListener(new View.OnClickListener() {
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

                                        String mecEmail = holderCustomerViewPendingMechanic.emailMechanic.getText().toString();
                                        final String repMecEmail = mecEmail.replace(".", ",");

                                        NotificationDetails notificationDetails = new NotificationDetails();
                                        databaseReference3 = FirebaseDatabase.getInstance().getReference("Notification").child("Mechanic");
                                        String msg = "Service Request from \n\t'"+cusEmail+"'\n will be Cancelled, \n Sorry for \n the Inconvenient.";
                                        String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                        notificationDetails.setReason(msg);
                                        notificationDetails.setTime(date);
                                        databaseReference3.child(repMecEmail).push().setValue(notificationDetails);

                                        databaseReference6 = FirebaseDatabase.getInstance().getReference("WorkBoard").child("AcceptedWork").child("Mechanic").child(repCusEmail);
                                        databaseReference6.orderByChild("mechanicEmail").equalTo(mecEmail).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                                                        String keys = data.getKey();
                                                        Toast.makeText(CustomerServiceHall.this, keys, Toast.LENGTH_SHORT).show();
                                                        databaseReference6.child(keys).removeValue();

                                                    }

                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                        databaseReference2 = FirebaseDatabase.getInstance().getReference("WorkBoard").child("AcceptedWork").child("Customer").child(repMecEmail);
                                        databaseReference2.orderByChild("email").equalTo(cusEmail).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                                                        String keys = data.getKey();
                                                        Toast.makeText(CustomerServiceHall.this, keys, Toast.LENGTH_SHORT).show();
                                                        databaseReference2.child(keys).removeValue();

                                                    }

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

                        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerServiceHall.this);
                        builder.setMessage("Are you sure? you want cancel this request!").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();



                    }
                });



                holderCustomerViewPendingMechanic.review.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        String em = holderCustomerViewPendingMechanic.emailMechanic.getText().toString().trim();
                        String repMec = em.replace(".", ",");
                        mecEmail = repMec;
                        orgMecEmail = em;
                        reviewService(v);

                    }
                });



                holderCustomerViewPendingMechanic.call.setOnClickListener(new View.OnClickListener() {
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
                                        String call = String.valueOf(holderCustomerViewPendingMechanic.mechanicMobile.getText());
                                        try {
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:" + call));
                                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerServiceHall.this);
                        builder.setMessage("You want to make a call?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }
                });

            }

            @NonNull
            @Override
            public HolderCustomerViewPendingMechanic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_customer_view_accepted_mechanic, parent, false);
                return new HolderCustomerViewPendingMechanic(view);
            }
        };

        recyclerAdapter1.notifyDataSetChanged();
        recyclerAdapter1.startListening();
        recyclerView.setAdapter(recyclerAdapter1);
    }

    public void reviewService(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_review_service, null);
        alert.setView(view1);

        final RatingBar ratingbar=(RatingBar)view1.findViewById(R.id.ratingBar);
        final EditText editText = view1.findViewById(R.id.aboutService);


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
                String con = editText.getText().toString().trim();
                String rating = String.valueOf(ratingbar.getRating());
                if(TextUtils.isEmpty(rating)){
                    Toast.makeText(CustomerServiceHall.this, "Please give a Star Rating!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(con)){
                    Toast.makeText(CustomerServiceHall.this, "Please say about Service it helps him lots!", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String myEmail = mAuth.getCurrentUser().getEmail().toString().trim();
                String repMyEmail = myEmail.replace(".",",");

                ServiceReviewDetails serviceReviewDetails = new ServiceReviewDetails();
                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                serviceReviewDetails.setEmail(myEmail);
                serviceReviewDetails.setRating(rating);
                serviceReviewDetails.setAbout(con);
                serviceReviewDetails.setDate(date);
                databaseReference5 = FirebaseDatabase.getInstance().getReference("Review").child(mecEmail);
                databaseReference5.push().setValue(serviceReviewDetails);

                databaseReference2 = FirebaseDatabase.getInstance().getReference("WorkBoard").child("AcceptedWork").child("Mechanic").child(repMyEmail);
                databaseReference2.orderByChild("mechanicEmail").equalTo(orgMecEmail).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for(DataSnapshot data: dataSnapshot.getChildren()){

                                        String keys=data.getKey();
                                        Toast.makeText(CustomerServiceHall.this, keys, Toast.LENGTH_SHORT).show();
                                        databaseReference2.child(keys).removeValue();

                                    }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






                Toast.makeText(CustomerServiceHall.this, "Thank You..\uD83D\uDE01", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }

        });

        alertDialog.show();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener1
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){

                case R.id.waitingHall:
                    return true;

                case R.id.chatBot:
                    startActivity(new Intent(getApplicationContext(),CustomerChatBot.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.map:
                    startActivity(new Intent(getApplicationContext(),CustomerHomePage.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.notification:
                    startActivity(new Intent(getApplicationContext(),CustomerNotification.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.settings:
                    PopupMenu pum = new PopupMenu(CustomerServiceHall.this, findViewById(R.id.settings));
                    pum.inflate(R.menu.customer_pop_up_menu);
                    pum.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.cusLogout:
                                    FirebaseAuth.getInstance().signOut();
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(),GmMainLoginPage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    break;
                            }
                            return true;
                        }
                    });
                    pum.show();
                    return true;

            }
            return true;
        }
    };




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.cusLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(getApplicationContext(),GmMainLoginPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
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
