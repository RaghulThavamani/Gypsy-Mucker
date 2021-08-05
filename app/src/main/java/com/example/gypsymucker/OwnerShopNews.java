package com.example.gypsymucker;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class OwnerShopNews extends Fragment {
    TextView scrollingText;
    EditText putMessage;
    Button putMessageBtn;
    DatabaseReference databaseReference,databaseReference1,databaseReference2;
    FirebaseAuth auth;
    String shopName;
    public OwnerShopNews() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_owner_shop_news, container, false); //pass the correct layout name for the fragment

        scrollingText = view.findViewById(R.id.scrollingText);
        putMessage = view.findViewById(R.id.putMessage);
        putMessageBtn = view.findViewById(R.id.putMessageBtn);
        auth = FirebaseAuth.getInstance();
        final String email = auth.getCurrentUser().getEmail().toString().trim();
        scrollingText.setSelected(true);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading pls wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);


        databaseReference = FirebaseDatabase.getInstance().getReference("PermanentOwner");

        String repEmail = email.replace(".",",");


        databaseReference.child(repEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    String key = dataSnapshot.child("shopName").getValue().toString();
                    shopName = key;

                    databaseReference2 = FirebaseDatabase.getInstance().getReference("OwnerMessage").child(shopName);

                    databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                 progressDialog.dismiss();
                                 String message = dataSnapshot.child("sendMessage").getValue().toString().trim();
                                 scrollingText.setText(message);
                            }

                              progressDialog.dismiss();
                               }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        putMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = putMessage.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(getActivity(), "Owner enter a Message!", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseReference1 = FirebaseDatabase.getInstance().getReference("OwnerMessage").child(shopName);
                OwnerDetails ownerSendMessage = new OwnerDetails();
                ownerSendMessage.setSendMessage(message);
                databaseReference1.setValue(ownerSendMessage);
                putMessage.setText("");

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(OwnerShopNews.this).attach(OwnerShopNews.this).commit();
                Toast.makeText(getActivity(), "Message Posted!", Toast.LENGTH_SHORT).show();
            }
        });


        // Inflate the layout for this fragment
        return view;
    }
}
