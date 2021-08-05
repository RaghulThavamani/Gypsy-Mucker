package com.example.gypsymucker;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HolderAdminApproveMechanic extends RecyclerView.ViewHolder {

    TextView unionId,unionName,mechanicMobile,mechanicName,aadharNoMechanic,emailMechanic,
            vehicleInfoMec,shopLicenseNO1,shopName1,shopAdd1,ownerName1,mobile1,mechanicImageUrl,id,confirmationText,ownerEmail,date,acceptedDate;
    ImageButton approve1,reject1;
    ImageView imageUrl;
    Button review,existingProfile,updateProfile,deleteProfile,confirmation;

    public HolderAdminApproveMechanic(@NonNull View itemView) {
        super(itemView);

        shopLicenseNO1 = (TextView)itemView.findViewById(R.id.shopLicenseNO1);
        shopName1 = (TextView)itemView.findViewById(R.id.shopName1);
        shopAdd1 = (TextView)itemView.findViewById(R.id.shopAdd1);
        ownerName1 = (TextView)itemView.findViewById(R.id.ownerName1);
        mobile1 = (TextView)itemView.findViewById(R.id.mobileOwner);

        unionId = (TextView)itemView.findViewById(R.id.unionId);
        unionName = (TextView)itemView.findViewById(R.id.unionName);
        mechanicMobile = (TextView)itemView.findViewById(R.id.mobileMechanic);
        mechanicName = (TextView)itemView.findViewById(R.id.mechanicName);
        aadharNoMechanic = (TextView)itemView.findViewById(R.id.aadharNoMechanic);
        vehicleInfoMec = (TextView)itemView.findViewById(R.id.vehicleInfoMec);
        emailMechanic = (TextView)itemView.findViewById(R.id.emailMechanic);
        mechanicImageUrl = (TextView)itemView.findViewById(R.id.mechanicImageUrl);
        id = (TextView)itemView.findViewById(R.id.id);
        confirmationText = (TextView)itemView.findViewById(R.id.confirmationText);
        ownerEmail = (TextView)itemView.findViewById(R.id.ownerEmail);
        date = (TextView)itemView.findViewById(R.id.date);
        acceptedDate = (TextView)itemView.findViewById(R.id.acceptedDate);

        imageUrl=itemView.findViewById(R.id.imageUrl1);

        approve1 = itemView.findViewById(R.id.approve1);
        reject1 = itemView.findViewById(R.id.decline1);

        review = itemView.findViewById(R.id.review);
        existingProfile = itemView.findViewById(R.id.existingProfile);
        updateProfile = itemView.findViewById(R.id.updateProfile);
        confirmation = itemView.findViewById(R.id.confirmation);
        deleteProfile = itemView.findViewById(R.id.deleteProfile);
    }
}
