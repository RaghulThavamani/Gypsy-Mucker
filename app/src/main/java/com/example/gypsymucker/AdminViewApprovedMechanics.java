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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminViewApprovedMechanics extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<MechanicDetails, HolderAdminApproveMechanic> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference,databaseReference1,databaseReference2;

    FirebaseRecyclerAdapter<ServiceReviewDetails, HolderServiceReview> recyclerAdapter1;
    RecyclerView.LayoutManager layoutManager1;

    String repMecEmailReview;
    long mLastClickTime = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_approved_mechanics);

        databaseReference = FirebaseDatabase.getInstance().getReference("PermanentMechanic");
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
                holderAdminApproveMechanic.shopName1.setText(mechanicDetails.getShopName());

                String im = mechanicDetails.getMechanicImageUrl().toString().trim();
                Uri my_im = Uri.parse(im);
                Glide.with(AdminViewApprovedMechanics.this).load(my_im).into(holderAdminApproveMechanic.imageUrl);

                holderAdminApproveMechanic.review.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        String email = holderAdminApproveMechanic.emailMechanic.getText().toString();
                        String rep = email.replace(".",",");
                        repMecEmailReview = rep;
                        reviewService(v);
                    }
                });

            }

            @NonNull
            @Override
            public HolderAdminApproveMechanic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_admin_view_appproved_mechanics, parent, false);
                return new HolderAdminApproveMechanic(view);
            }
        };

        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);
    }



    public void reviewService(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_view_review, null);
        alert.setView(view1);

        final  RecyclerView recyclerView = view1.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager1 = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager1);

        final AlertDialog alertDialog = alert.create();

        databaseReference1 = FirebaseDatabase.getInstance().getReference("Review").child(repMecEmailReview);
        FirebaseRecyclerOptions options1 =
                new FirebaseRecyclerOptions.Builder<ServiceReviewDetails>()
                        .setQuery(databaseReference1, ServiceReviewDetails.class)
                        .build();

        recyclerAdapter1 = new FirebaseRecyclerAdapter<ServiceReviewDetails, HolderServiceReview>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull HolderServiceReview holderServiceReview, int i, @NonNull ServiceReviewDetails serviceReviewDetails) {

                holderServiceReview.date.setText(serviceReviewDetails.getDate());
                holderServiceReview.content.setText(serviceReviewDetails.getAbout());
                holderServiceReview.rating.setText(serviceReviewDetails.getRating());
                holderServiceReview.email.setText(serviceReviewDetails.getEmail());

            }

            @NonNull
            @Override
            public HolderServiceReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_for_review, parent, false);
                return new HolderServiceReview(view);
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
