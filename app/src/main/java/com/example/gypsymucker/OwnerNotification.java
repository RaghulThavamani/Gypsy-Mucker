package com.example.gypsymucker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class OwnerNotification extends Fragment {
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<NotificationDetails, HolderNotification> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference, databaseReference2;
    DatabaseReference databaseReference3,databaseReference4,databaseReference5;
    FirebaseAuth mAuth;
    long mLastClickTime = 0;
    public OwnerNotification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = lf.inflate(R.layout.fragment_owner_notification, container, false); //pass the correct layout name for the fragment

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        databaseReference2=FirebaseDatabase.getInstance().getReference().child("PermanentOwnerRegistration");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView1);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);


        String email = mAuth.getCurrentUser().getEmail().toString().trim();
        final String repEmail = email.replace(".",",");


                    databaseReference=FirebaseDatabase.getInstance().getReference().child("Notification").child(repEmail);


                    FirebaseRecyclerOptions options =
                            new FirebaseRecyclerOptions.Builder<NotificationDetails>()
                                    .setQuery(databaseReference, NotificationDetails.class)
                                    .build();

                    recyclerAdapter = new FirebaseRecyclerAdapter<NotificationDetails, HolderNotification>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull final HolderNotification holderNotification, int i, @NonNull final NotificationDetails notificationDetails) {
                            holderNotification.notification.setText(notificationDetails.getReason());
                            holderNotification.date.setText(notificationDetails.getTime());
                            try {
                                if(notificationDetails.getConfirmation().equals("Enable")){
                                    holderNotification.emailMechanic.setVisibility(View.VISIBLE);
                                    holderNotification.emailMechanic.setText(notificationDetails.getMechanicName());
                                    holderNotification.confirmationLayout.setVisibility(View.VISIBLE);
                                    holderNotification.okBtn.setVisibility(View.GONE);
                                }
                            }catch (Exception e){}

                            holderNotification.okBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                        return;
                                    }
                                    mLastClickTime = SystemClock.elapsedRealtime();

                                    String key = notificationDetails.getTime().toString();
                                    databaseReference3=FirebaseDatabase.getInstance().getReference().child("Notification").child(repEmail);

                                    databaseReference3.orderByChild("time").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot data: dataSnapshot.getChildren()){

                                                String keys=data.getKey();
                                                System.out.println(keys);

                                                databaseReference3=FirebaseDatabase.getInstance().getReference().child("Notification").child(repEmail).child(keys);

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
                                                    Toast.makeText(getActivity(),"Message Deleted!",Toast.LENGTH_LONG).show();
                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    dialog.dismiss();
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage("Are you sure? You want delete this message!").setPositiveButton("Yes", dialogClickListener)
                                            .setNegativeButton("No", dialogClickListener).show();



                                }
                            });

                           holderNotification.decline.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                       return;
                                   }
                                   mLastClickTime = SystemClock.elapsedRealtime();
                                   final String repMecEmail = holderNotification.emailMechanic.getText().toString();
                                   databaseReference4 = FirebaseDatabase.getInstance().getReference("DeleteMechanicProfile").child(repMecEmail);

                                   try {
                                       String key = holderNotification.date.getText().toString();
                                       databaseReference3=FirebaseDatabase.getInstance().getReference().child("Notification").child(repEmail);

                                       databaseReference3.orderByChild("time").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                               for(DataSnapshot data: dataSnapshot.getChildren()){

                                                   String keys=data.getKey();
                                                   System.out.println(keys);

                                                   databaseReference3=FirebaseDatabase.getInstance().getReference().child("Notification").child(repEmail).child(keys);

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
                                                       databaseReference4.removeValue();
                                                       Toast.makeText(getActivity(),"Message Deleted!",Toast.LENGTH_LONG).show();
                                                       break;

                                                   case DialogInterface.BUTTON_NEGATIVE:
                                                       dialog.dismiss();
                                                       break;
                                               }
                                           }
                                       };

                                       AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                       builder.setMessage("Are you sure? You don't want to remove mechanic!").setPositiveButton("Yes", dialogClickListener)
                                               .setNegativeButton("No", dialogClickListener).show();
                                   }catch (Exception e){}
                               }
                           });




                           holderNotification.accept.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                       return;
                                   }
                                   mLastClickTime = SystemClock.elapsedRealtime();

                                   String key = holderNotification.date.getText().toString();
                                   databaseReference3=FirebaseDatabase.getInstance().getReference().child("Notification").child(repEmail);

                                   databaseReference3.orderByChild("time").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           for(DataSnapshot data: dataSnapshot.getChildren()){

                                               String keys=data.getKey();
                                               System.out.println(keys);

                                               databaseReference3=FirebaseDatabase.getInstance().getReference().child("Notification").child(repEmail).child(keys);

                                           }
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                       } });


                                   final String repMecEmail = holderNotification.emailMechanic.getText().toString();
                                   databaseReference4 = FirebaseDatabase.getInstance().getReference("DeleteMechanicProfile");

                                   databaseReference4.child(repMecEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           if (dataSnapshot.exists()){
                                               DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       switch (which){
                                                           case DialogInterface.BUTTON_POSITIVE:
                                                               databaseReference5 = FirebaseDatabase.getInstance().getReference("DeleteMechanicProfile").child(repMecEmail);
                                                               databaseReference5.child("confirmation").setValue("Accepted.");
                                                               String time = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                                               databaseReference5.child("acceptedDate").setValue(time);
                                                               databaseReference3.removeValue();
                                                               Toast.makeText(getActivity(),"Request Confirmed!",Toast.LENGTH_LONG).show();
                                                               break;

                                                           case DialogInterface.BUTTON_NEGATIVE:
                                                               dialog.dismiss();
                                                               break;
                                                       }
                                                   }
                                               };

                                               AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                               builder.setMessage("Are you sure? You want to remove mechanic!").setPositiveButton("Yes", dialogClickListener)
                                                       .setNegativeButton("No", dialogClickListener).show();
                                           }else{
                                               databaseReference3.removeValue();
                                               Toast.makeText(getActivity(), "Already it as been deleted!", Toast.LENGTH_SHORT).show();
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
                        public HolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.view_owner_notification, parent, false);
                            return new HolderNotification(view);
                        }
                    };

                    recyclerAdapter.notifyDataSetChanged();
                    recyclerAdapter.startListening();
                    recyclerView.setAdapter(recyclerAdapter);



        // Inflate the layout for this fragment
        return view;
    }
}
