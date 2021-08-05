package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerNotification extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<NotificationDetails, HolderNotification> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference, databaseReference2;
    DatabaseReference databaseReference3,databaseReference4,databaseReference5;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_notification);

        BottomNavigationView navigation1 = findViewById(R.id.bottom_navigation);
        navigation1.setSelectedItemId(R.id.notification);
        navigation1.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener1);



        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();



        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);


        String email = mAuth.getCurrentUser().getEmail().toString().trim();
        final String repEmail = email.replace(".",",");


        databaseReference=FirebaseDatabase.getInstance().getReference().child("Notification").child("Customer").child(repEmail);


        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<NotificationDetails>()
                        .setQuery(databaseReference, NotificationDetails.class)
                        .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<NotificationDetails, HolderNotification>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderNotification holderNotification, int i, @NonNull final NotificationDetails notificationDetails) {
                holderNotification.notification.setText(notificationDetails.getReason());
                holderNotification.date.setText(notificationDetails.getTime());
                holderNotification.okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String key = holderNotification.date.getText().toString();
                        databaseReference3=FirebaseDatabase.getInstance().getReference().child("Notification").child("Customer").child(repEmail);

                        databaseReference3.orderByChild("time").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot data: dataSnapshot.getChildren()){

                                    String keys=data.getKey();
                                    System.out.println(keys);

                                    databaseReference3=FirebaseDatabase.getInstance().getReference().child("Notification").child("Customer").child(repEmail).child(keys);


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            } });


                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        databaseReference3.removeValue();
                                        Toast.makeText(CustomerNotification.this,"Message Deleted!",Toast.LENGTH_LONG).show();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerNotification.this);
                        builder.setMessage("Are you sure? You want to delete is message!").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();



                    }
                });
            }

            @NonNull
            @Override
            public HolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_owner_notification, parent, false);
                return new HolderNotification(view);
            }
        };

        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);



    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener1
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){

                case R.id.notification:
                    return true;

                case R.id.chatBot:
                    startActivity(new Intent(getApplicationContext(),CustomerChatBot.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.waitingHall:
                    startActivity(new Intent(getApplicationContext(),CustomerServiceHall.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.map:
                    startActivity(new Intent(getApplicationContext(),CustomerHomePage.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.settings:
                    PopupMenu pum = new PopupMenu(CustomerNotification.this, findViewById(R.id.settings));
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
