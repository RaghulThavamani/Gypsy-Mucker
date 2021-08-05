package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MechanicNotification extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<NotificationDetails, HolderNotification> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference, databaseReference2;
    DatabaseReference databaseReference3,databaseReference4,databaseReference5;
    FirebaseAuth mAuth;
    FloatingActionButton createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_notification);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();



        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        createButton = findViewById(R.id.fab);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    createButton.hide();
                else if (dy < 0)
                    createButton.show();
            }
        });


        String email = mAuth.getCurrentUser().getEmail().toString().trim();
        final String repEmail = email.replace(".",",");


        databaseReference=FirebaseDatabase.getInstance().getReference().child("Notification").child("Mechanic").child(repEmail);


        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<NotificationDetails>()
                        .setQuery(databaseReference, NotificationDetails.class)
                        .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<NotificationDetails, HolderNotification>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HolderNotification holderNotification, int i, @NonNull final NotificationDetails notificationDetails) {
                holderNotification.notification.setText(notificationDetails.getReason());
                holderNotification.date.setText(notificationDetails.getTime());
                holderNotification.okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String key = notificationDetails.getTime().toString();
                        databaseReference3=FirebaseDatabase.getInstance().getReference().child("Notification").child("Mechanic").child(repEmail);

                        databaseReference3.orderByChild("time").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot data: dataSnapshot.getChildren()){

                                    String keys=data.getKey();
                                    System.out.println(keys);

                                    databaseReference3=FirebaseDatabase.getInstance().getReference().child("Notification").child("Mechanic").child(repEmail).child(keys);

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
                                        Toast.makeText(MechanicNotification.this,"Message Deleted!",Toast.LENGTH_LONG).show();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(MechanicNotification.this);
                        builder.setMessage("Are you sure? You want delete this message!").setPositiveButton("Yes", dialogClickListener)
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



}
