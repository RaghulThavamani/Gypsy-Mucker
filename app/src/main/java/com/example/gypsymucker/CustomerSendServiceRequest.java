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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class CustomerSendServiceRequest extends AppCompatActivity {


    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<MechanicDetails, HolderCustomerSendServiceRequest> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    Query databaseReference;
    DatabaseReference databaseReference1;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;
    DatabaseReference databaseReference4;
    DatabaseReference review;
    FirebaseAuth mAuth;
    String repMec,repMecEmailReview;
    long mLastClickTime = 0;

    FirebaseRecyclerAdapter<ServiceReviewDetails, HolderServiceReview> recyclerAdapter1;
    RecyclerView.LayoutManager layoutManager1;



    private RequestQueue requestQueue;
    private boolean notify = false;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_send_service_request);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        requestQueue = Volley.newRequestQueue(getApplicationContext());


        database = FirebaseDatabase.getInstance();
        mAuth= FirebaseAuth.getInstance();
        final String email = mAuth.getCurrentUser().getEmail().toString();
        myUid = mAuth.getUid();

        databaseReference1=FirebaseDatabase.getInstance().getReference("WorkBoard").child("PendingWork");

        final Bundle extras = getIntent().getExtras();
        if(extras!=null){
            String shop=extras.getString("shop");
            String lat = extras.getString("lat");
            String lng = extras.getString("lng");
            Toast.makeText(this, shop+"\n"+lat+"\n"+lng+"\n"+email, Toast.LENGTH_SHORT).show();

            databaseReference=database.getReference("PermanentMechanic").orderByChild("shopName").equalTo(shop);

            FirebaseRecyclerOptions options =
                    new FirebaseRecyclerOptions.Builder<MechanicDetails>()
                            .setQuery(databaseReference, MechanicDetails.class)
                            .build();

            recyclerAdapter = new FirebaseRecyclerAdapter<MechanicDetails, HolderCustomerSendServiceRequest>(options) {

                @Override
                protected void onBindViewHolder(@NonNull final HolderCustomerSendServiceRequest holderCustomerSendServiceRequest, int i, @NonNull MechanicDetails mechanicDetails) {

                    holderCustomerSendServiceRequest.unionId2.setText(mechanicDetails.getUnionId());
                    holderCustomerSendServiceRequest.unionName2.setText(mechanicDetails.getUnionName());
                    holderCustomerSendServiceRequest.mechanicName2.setText(mechanicDetails.getMechanicName());
                    holderCustomerSendServiceRequest.mechanicMobile2.setText(mechanicDetails.getMechanicMobile());
                    holderCustomerSendServiceRequest.emailMechanic2.setText(mechanicDetails.getMechanicEmail());
                    holderCustomerSendServiceRequest.aadharNoMechanic2.setText(mechanicDetails.getMechanicAadharNum());
                    holderCustomerSendServiceRequest.vehicleInfoMec112.setText(mechanicDetails.getMechanicVehicleInfo());
                    holderCustomerSendServiceRequest.shopName2.setText(mechanicDetails.getShopName());

                    String im=mechanicDetails.getMechanicImageUrl().toString().trim();
                    Uri my_im = Uri.parse(im);
                    Glide.with(CustomerSendServiceRequest.this).load(my_im).into(holderCustomerSendServiceRequest.imageUrl2);

                    holderCustomerSendServiceRequest.reviewBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            String email = holderCustomerSendServiceRequest.emailMechanic2.getText().toString();
                            String repMecEmail = email.replace(".",",");
                            repMecEmailReview = repMecEmail;
                            reviewService(v);
                        }
                    });

                    holderCustomerSendServiceRequest.moreInfoBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            holderCustomerSendServiceRequest.mobileNOLayout.setVisibility(View.VISIBLE);
                            holderCustomerSendServiceRequest.aadharNoLayout.setVisibility(View.VISIBLE);
                            holderCustomerSendServiceRequest.unionIdLayout.setVisibility(View.VISIBLE);
                            holderCustomerSendServiceRequest.unionNameLayout.setVisibility(View.VISIBLE);
                        }
                    });

                    holderCustomerSendServiceRequest.request.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            notify = true;
                            holderCustomerSendServiceRequest.message.setText("Your Request as Received by Required Mechanic.\n For more Info go to Service Hall Page.");
                            final CustomerDetails customerDetails = new CustomerDetails();
                            databaseReference3 = FirebaseDatabase.getInstance().getReference("PermanentCustomer");
                            final String repEmailCus = email.replace(".",",");


                            databaseReference3.child(repEmailCus).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(extras!=null){
                                            String shop=extras.getString("shop");
                                            String lat = extras.getString("lat");
                                            String lng = extras.getString("lng");


                                            String name = dataSnapshot.child("customerName").getValue().toString();
                                            String mobile = dataSnapshot.child("mobile").getValue().toString();
                                            String vehicle = dataSnapshot.child("vehicle").getValue().toString();
                                            String lic = dataSnapshot.child("licenseNo").getValue().toString();
                                            System.out.println(name+mobile+vehicle+lic);

                                            customerDetails.setLatitude(Double.valueOf(lat));
                                            customerDetails.setLongitude(Double.valueOf(lng));
                                            customerDetails.setEmail(email);
                                            customerDetails.setCustomerName(name);
                                            customerDetails.setMobile(mobile);
                                            customerDetails.setVehicle(vehicle);
                                            customerDetails.setLicenseNo(lic);
                                            customerDetails.setStatus("pending");



                                            String mecEmail = holderCustomerSendServiceRequest.emailMechanic2.getText().toString();
                                            String repEmailMec = mecEmail.replace(".",",");


                                            String un = holderCustomerSendServiceRequest.unionId2.getText().toString();
                                            String unN = holderCustomerSendServiceRequest.unionName2.getText().toString();
                                            String mecN = holderCustomerSendServiceRequest.mechanicName2.getText().toString();
                                            String mecM = holderCustomerSendServiceRequest.mechanicMobile2.getText().toString();
                                            String emMec = holderCustomerSendServiceRequest.emailMechanic2.getText().toString();
                                            String aadharMec = holderCustomerSendServiceRequest.aadharNoMechanic2.getText().toString();
                                            String vecMec = holderCustomerSendServiceRequest.vehicleInfoMec112.getText().toString();





                                           MechanicDetails mechanicDetails1 = new MechanicDetails();
                                            mechanicDetails1.setUnionId(un);
                                            mechanicDetails1.setUnionName(unN);
                                            mechanicDetails1.setMechanicName(mecN);
                                            mechanicDetails1.setMechanicMobile(mecM);
                                            mechanicDetails1.setMechanicEmail(emMec);
                                            mechanicDetails1.setMechanicAadharNum(aadharMec);
                                            mechanicDetails1.setMechanicVehicleInfo(vecMec);
                                            mechanicDetails1.setStatus("Pending.");

                                            customerDetails.setRequestTo(repEmailMec);
                                                //add data to customer log
                                            databaseReference1.child(repEmailCus).child(repEmailMec).setValue(mechanicDetails1);
                                                //add data for mechanic to view customer details
                                           databaseReference2=FirebaseDatabase.getInstance().getReference("RequestedCustomerData");
                                           databaseReference2.push().setValue(customerDetails);




                                            holderCustomerSendServiceRequest.request.setVisibility(View.GONE);
                                            Toast.makeText(CustomerSendServiceRequest.this,"Request Send", Toast.LENGTH_SHORT).show();
                                            holderCustomerSendServiceRequest.moreInfoBtn.setVisibility(View.VISIBLE);

                                            NotificationDetails notificationDetails = new NotificationDetails();
                                            String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                            String capName = name.toUpperCase();
                                            String message = "You get \n Service Request from \n\t'"+capName+"'\n Go to \n Work page \n for More Info.";
                                            notificationDetails.setReason(message);
                                            notificationDetails.setTime(date);
                                            databaseReference4 = FirebaseDatabase.getInstance().getReference("Notification").child("Mechanic");
                                            databaseReference4.child(repEmailMec).push().setValue(notificationDetails);



                                            FirebaseDatabase.getInstance().getReference().child("PermanentMechanic").child(repEmailMec).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    String userID = dataSnapshot.child("id").getValue().toString();
                                                    Toast.makeText(CustomerSendServiceRequest.this, userID, Toast.LENGTH_SHORT).show();
                                                    System.out.println(userID);
                                                    if(notify){ sendNotifications(userID, "working","good");}
                                                    notify = false;

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





                        }
                    });

                    UpdateToken();
                }

                @NonNull
                @Override
                public HolderCustomerSendServiceRequest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.view_customer_send_service_request, parent, false);
                    return new HolderCustomerSendServiceRequest(view);
                }


            };

            recyclerAdapter.notifyDataSetChanged();
            recyclerAdapter.startListening();
            recyclerView.setAdapter(recyclerAdapter);

        }
    }


    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);

    }

    public void sendNotifications(final String userID, final String title, final String message) {
       DatabaseReference token =  FirebaseDatabase.getInstance().getReference().child("Tokens");
       Query query = token.orderByKey().equalTo(userID);
       query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot ds:dataSnapshot.getChildren()){
                   Token token1 = ds.getValue(Token.class);
                   Data data = new Data(myUid,title,message,userID,R.drawable.notifications_mechanic);
                   NotificationSender sender = new NotificationSender(data, token1.getToken());

                   try {
                       JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
                       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://fcm.googlepis.com/fcm/send", senderJsonObj, new Response.Listener<JSONObject>() {
                           @Override
                           public void onResponse(JSONObject response) {

                               Log.d("JSON_RESPONSE", "onResponse:"+response.toString());
                           }
                       }, new Response.ErrorListener() {
                           @Override
                           public void onErrorResponse(VolleyError error) {
                               Log.d("JSON_RESPONSE", "onResponse:"+error.toString());

                           }
                       }){
                           @Override
                           public Map<String, String> getHeaders() throws AuthFailureError {
                               Map<String,String> headers = new HashMap<>();
                               headers.put("Contenr-Type","application/json");
                               headers.put("Authorization","key=AAAAdRe-wnA:APA91bGxIzW5FYbjfe1aqTsbnwwXdmKnJU8AhKemqRnXbpsmRKY_JALuj5IDWXw0wgmkjL6QDetRNeEez700ZboyWpDWn3vxMTb99CuWYVVDjs-BcpK7Yk4gc_S83AGj1GtDyz1E5pla");
                               return headers;
                           }
                       };
                       requestQueue.add(jsonObjectRequest);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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

        review = FirebaseDatabase.getInstance().getReference("Review").child(repMecEmailReview);
        FirebaseRecyclerOptions options1 =
                new FirebaseRecyclerOptions.Builder<ServiceReviewDetails>()
                        .setQuery(review, ServiceReviewDetails.class)
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
