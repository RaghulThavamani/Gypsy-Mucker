package com.example.gypsymucker;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HolderAdminApproveBusiness extends RecyclerView.ViewHolder {

    TextView shopLicenseNO,shopName,shopCity,shopAdd,ownerName,mobile,aadharNo,email,availability,vehicleInfo,latitude,longitude,imageString;
    ImageButton approve,decline,viewCertificate,viewLocation;
    Button delete,warning;
    public HolderAdminApproveBusiness(@NonNull View itemView) {
        super(itemView);

        shopLicenseNO = (TextView)itemView.findViewById(R.id.shopLicenseNO);
        shopName = (TextView)itemView.findViewById(R.id.shopName);
        shopCity = (TextView)itemView.findViewById(R.id.shopCity);
        shopAdd = (TextView)itemView.findViewById(R.id.shopAdd);
        ownerName = (TextView)itemView.findViewById(R.id.ownerName);
        mobile = (TextView)itemView.findViewById(R.id.mobile);
        aadharNo = (TextView)itemView.findViewById(R.id.aadharNo);
        email = (TextView)itemView.findViewById(R.id.email);
        availability = (TextView)itemView.findViewById(R.id.availability);
        vehicleInfo = (TextView)itemView.findViewById(R.id.vehicleInfo);
        latitude=itemView.findViewById(R.id.latitude);
        longitude=itemView.findViewById(R.id.longitude);
        imageString = itemView.findViewById(R.id.imageString);

        viewCertificate = itemView.findViewById(R.id.viewCertificate);
        viewLocation = itemView.findViewById(R.id.viewLocation);

        approve = itemView.findViewById(R.id.approve);
        decline = itemView.findViewById(R.id.decline);

        delete = itemView.findViewById(R.id.delete);
        warning = itemView.findViewById(R.id.warning);
    }
}
