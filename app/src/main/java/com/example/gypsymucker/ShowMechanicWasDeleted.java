package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import java.util.Calendar;

public class ShowMechanicWasDeleted extends AppCompatActivity {

    Button exit;
    long mLastClickTime = 0;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_mechanic_was_deleted);

        exit = findViewById(R.id.exit);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
               /* FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(getApplicationContext(),GmMainLoginPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);  */

                firebaseAuth = FirebaseAuth.getInstance();
                final FirebaseUser user  = firebaseAuth.getCurrentUser();


                if(user!=null){

                    final String email = firebaseAuth.getCurrentUser().getEmail().toString();
                    final String repEmail = email.replace(".",",");


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowMechanicWasDeleted.this);

                    //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    // Setting Dialog Title
                    alertDialog.setTitle("PASSWORD");

                    // Setting Dialog Message
                    alertDialog.setMessage("Enter Password");
                    final EditText input = new EditText(ShowMechanicWasDeleted.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    input.setLayoutParams(lp);
                    input.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    alertDialog.setView(input);
                    //alertDialog.setView(input);
                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {

                                    final String password = input.getText().toString().trim();
                                    if(TextUtils.isEmpty(password)){
                                        Toast.makeText(ShowMechanicWasDeleted.this,"Enter Password!", Toast.LENGTH_SHORT).show(); return;
                                    }

                                    firebaseAuth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(ShowMechanicWasDeleted.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {

                                                        AuthCredential credential = EmailAuthProvider
                                                                .getCredential(email, password);

                                                        // Prompt the user to re-provide their sign-in credentials
                                                        user.reauthenticate(credential)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        user.delete()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {


                                                                                            databaseReference3=FirebaseDatabase.getInstance().getReference().child("Email").child("MechanicEmail");

                                                                                            databaseReference3.orderByChild("mechanicEmail").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                    for(DataSnapshot data: dataSnapshot.getChildren()){

                                                                                                        String keys=data.getKey();
                                                                                                        Toast.makeText(ShowMechanicWasDeleted.this, keys, Toast.LENGTH_SHORT).show();

                                                                                                        databaseReference3=FirebaseDatabase.getInstance().getReference().child("Email").child("MechanicEmail");
                                                                                                        //databaseReference3.child(keys).removeValue();
                                                                                                        Toast.makeText(ShowMechanicWasDeleted.this, "User account deleted.", Toast.LENGTH_SHORT).show();
                                                                                                        startActivity(new Intent(getApplicationContext(),GmMainLoginPage.class));
                                                                                                        finish();

                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                } });



                                                                                        }
                                                                                    }
                                                                                });

                                                                    }
                                                                });


                                                    }else {
                                                        Toast.makeText(getApplicationContext(),"Password Not Matched!",Toast.LENGTH_LONG).show();

                                                    }

                                                }
                                            });
                                }
                            });
                    //Setting Negative "NO" Button
                    alertDialog.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog
                                    dialog.cancel();
                                }
                            });
                    // Showing Alert Message
                    alertDialog.show();




                }

            }
        });
    }
}
