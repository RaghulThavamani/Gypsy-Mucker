package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class OwnerUpdateMechanicDetails extends AppCompatActivity {

    EditText mobile,unionId,unionName;
    TextView name,email;
    ImageView imageUrl;
    ImageView uploadImage;
    Switch mobVisibleBtn,unionIdVisibleBtn,unionNameVisibleBtn,uploadImageVisibleBtn;
    Button updateRequest;
    String imageUri,mle,uId,uName;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    long mLastClickTime = 0;

    StorageReference storageReference;
    DatabaseReference myRef,myRef1;
    String repMecEmail,mecName,mecEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_update_mechanic_details);

        mobile = findViewById(R.id.mobile);
        unionId = findViewById(R.id.unionId);
        unionName = findViewById(R.id.unionName);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        imageUrl = findViewById(R.id.imageUrl);
        uploadImage = findViewById(R.id.uploadImage);
        updateRequest = findViewById(R.id.updateRequest);

        mobVisibleBtn = findViewById(R.id.mobVisibleBtn);
        unionIdVisibleBtn = findViewById(R.id.unionIdVisibleBtn);
        unionNameVisibleBtn = findViewById(R.id.unionNameVisibleBtn);
        uploadImageVisibleBtn = findViewById(R.id.uploadImageVisibleBtn);


        final Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            String email1 = extras.getString("Email");
            mecEmail = email1;
            String repEmail = email1.replace(".",",");
            repMecEmail = repEmail;


            email.setText(email1);

            myRef = FirebaseDatabase.getInstance().getReference().child("PermanentMechanic");
            myRef1 = FirebaseDatabase.getInstance().getReference().child("UpdateMechanicProfile");

            myRef.child(repEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name1 = dataSnapshot.child("mechanicName").getValue().toString();
                    mecName = name1;
                    name.setText(name1);
                    String mob = dataSnapshot.child("mechanicMobile").getValue().toString();
                    mobile.setText(mob);
                    mle=mob;
                    String unionId1 = dataSnapshot.child("unionId").getValue().toString();
                    unionId.setText(unionId1);
                    uId = unionId1;
                    String unionName1 = dataSnapshot.child("unionName").getValue().toString();
                    unionName.setText(unionName1);
                    uName = unionName1;
                    String imageUrl1 = dataSnapshot.child("mechanicImageUrl").getValue().toString();
                    imageUri = imageUrl1;
                    Uri my_im = Uri.parse(imageUrl1);
                    Glide.with(OwnerUpdateMechanicDetails.this).load(my_im).into(imageUrl);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        mobVisibleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){mobile.setEnabled(true);
                    mobile.setText("");
                    Toast.makeText(OwnerUpdateMechanicDetails.this, "Mobile turn into Editable!", Toast.LENGTH_SHORT).show();}

                if(!isChecked){mobile.setEnabled(false);
                    mobile.setText(mle);
                    Toast.makeText(OwnerUpdateMechanicDetails.this, "Mobile turn into Non-Editable!", Toast.LENGTH_SHORT).show();}

            }
        });


        unionIdVisibleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){unionId.setEnabled(true);
                    unionId.setText("");
                    Toast.makeText(OwnerUpdateMechanicDetails.this, "UnionId turn into Editable!", Toast.LENGTH_SHORT).show();}

                if(!isChecked){unionId.setEnabled(false);
                    unionId.setText(uId);
                    Toast.makeText(OwnerUpdateMechanicDetails.this, "UnionId turn into Non-Editable!", Toast.LENGTH_SHORT).show();}

            }
        });


        unionNameVisibleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){unionName.setEnabled(true);
                    unionName.setText("");
                    Toast.makeText(OwnerUpdateMechanicDetails.this, "UnionName turn into Editable!", Toast.LENGTH_SHORT).show();}

                if(!isChecked){unionName.setEnabled(false);
                    unionName.setText(uName);
                    Toast.makeText(OwnerUpdateMechanicDetails.this, "UnionName turn into Non-Editable!", Toast.LENGTH_SHORT).show();}

            }
        });


        uploadImageVisibleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){uploadImage.setVisibility(View.VISIBLE);
                    imageUrl.setImageDrawable(null);
                    Toast.makeText(OwnerUpdateMechanicDetails.this, "Add Image!", Toast.LENGTH_SHORT).show();}

                if(!isChecked){uploadImage.setVisibility(View.INVISIBLE);
                    Uri my_im = Uri.parse(imageUri);
                    Glide.with(OwnerUpdateMechanicDetails.this).load(my_im).into(imageUrl);
                    Toast.makeText(OwnerUpdateMechanicDetails.this, "You can't add image!", Toast.LENGTH_SHORT).show();}

            }
        });



        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                chooseImage();
            }
        });

        updateRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                toast();
            }
        });






    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    //Set image to ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageUrl.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void toast(){

            String mob = mobile.getText().toString().trim();
            if(mob.isEmpty()){
                Toast.makeText(OwnerUpdateMechanicDetails.this,"Enter Mobile Number",Toast.LENGTH_LONG).show();return;}

            if(mob.length() != 10){
                Toast.makeText(OwnerUpdateMechanicDetails.this,"Enter Valid Mobile Number",Toast.LENGTH_LONG).show();return;}


            String lin = unionId.getText().toString().trim();
            if (lin.isEmpty()) {
                Toast.makeText(OwnerUpdateMechanicDetails.this, "Enter Union Id", Toast.LENGTH_SHORT)
                        .show();
                return;
            }


            String shopN = unionName.getText().toString().trim();
            if (TextUtils.isEmpty(shopN)) {
                Toast.makeText(OwnerUpdateMechanicDetails.this, "Enter Union Name", Toast.LENGTH_LONG).show();
                return;
            }


        if (uploadImageVisibleBtn.isChecked()){
            if(imageUrl.getDrawable() == null || filePath == null){
                Toast.makeText(OwnerUpdateMechanicDetails.this, "Choose Image..!", Toast.LENGTH_LONG).show(); return;
            }
        }

        if (!mobVisibleBtn.isChecked() && !unionIdVisibleBtn.isChecked() && !unionNameVisibleBtn.isChecked() && !uploadImageVisibleBtn.isChecked()){
            Toast.makeText(OwnerUpdateMechanicDetails.this, "Update SomeThing!", Toast.LENGTH_LONG).show(); return;
        }


        update();

    }

    public void update(){

        final MechanicDetails mechanicDetails = new MechanicDetails();

        String mob = mobile.getText().toString().trim();
        String unionId1 = unionId.getText().toString().trim();
        String unionName1 = unionName.getText().toString().trim();

            mechanicDetails.setMechanicMobile(mob);
            mechanicDetails.setUnionId(unionId1);
            mechanicDetails.setUnionName(unionName1);
            mechanicDetails.setMechanicEmail(mecEmail);
            mechanicDetails.setMechanicName(mecName);



            if (uploadImageVisibleBtn.isChecked()){
           if (filePath != null) {
               storageReference = FirebaseStorage.getInstance().getReference();
               final StorageReference ref = storageReference.child("MechanicUpdated/"+repMecEmail);
               ref.putFile(filePath)
                       .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                               // StorageMetadata snapshotMetadata = taskSnapshot.getMetadata();

                               //to get image url from firebase storage...
                               Task<Uri> downloadUrl = ref.getDownloadUrl();
                               downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {

                                   @Override
                                   public void onSuccess(Uri uri) {
                                       String imageReference = uri.toString();

                                       mechanicDetails.setMechanicImageUrl(imageReference);
                                       myRef1.child(repMecEmail).setValue(mechanicDetails);
                                   }
                               });

                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {

                               Toast.makeText(OwnerUpdateMechanicDetails.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       })
                       .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                               double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                       .getTotalByteCount());

                           }
                       });
               }
            }else{
                mechanicDetails.setMechanicImageUrl(imageUri);
                myRef1.child(repMecEmail).setValue(mechanicDetails);
            }

        Toast.makeText(OwnerUpdateMechanicDetails.this,"Update Request Send Successfully",Toast.LENGTH_LONG).show();
        finish();

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
