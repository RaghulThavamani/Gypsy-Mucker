package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MechanicViewCompletedWork extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<NotificationDetails, HolderServiceReview> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_view_completed_work);

        firebaseAuth = FirebaseAuth.getInstance();
        String repEmail = firebaseAuth.getCurrentUser().getEmail().toString().replace(".",",");

        databaseReference = FirebaseDatabase.getInstance().getReference("MechanicCompletedWork").child(repEmail);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions
                .Builder<NotificationDetails>()
                .setQuery(databaseReference, NotificationDetails.class)
                .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<NotificationDetails, HolderServiceReview>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HolderServiceReview holderServiceReview, int i, @NonNull NotificationDetails notificationDetails) {

                holderServiceReview.customerNameOfCompletedWork.setText(notificationDetails.getCustomerName());
                holderServiceReview.mechanicNameOfCompletedWork.setText(notificationDetails.getMechanicName());
                holderServiceReview.date.setText(notificationDetails.getTime());
                holderServiceReview.vehicleInfoOfCustomer.setText(notificationDetails.getVehicleInfo());

            }

            @NonNull
            @Override
            public HolderServiceReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_mechanic_view_completed_work, parent, false);
                return new HolderServiceReview(view);
            }
        };

        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);
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
