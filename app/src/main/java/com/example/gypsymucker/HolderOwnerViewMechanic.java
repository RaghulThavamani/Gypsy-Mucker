package com.example.gypsymucker;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class HolderOwnerViewMechanic extends RecyclerView.ViewHolder {

    TextView unionId,unionName,mechanicMobile,mechanicName,aadharNoMechanic,emailMechanic,vehicleInfoMec11,shopName;
    ImageView imageUrl;
    Button update,delete;

    public HolderOwnerViewMechanic(@NonNull View itemView) {
        super(itemView);

        unionId = (TextView)itemView.findViewById(R.id.unionId1);
        unionName = (TextView)itemView.findViewById(R.id.unionName1);
        mechanicMobile = (TextView)itemView.findViewById(R.id.mechanicMobile1);
        mechanicName = (TextView)itemView.findViewById(R.id.mechanicName1);
        aadharNoMechanic = (TextView)itemView.findViewById(R.id.aadharNoMechanic1);
        emailMechanic = (TextView)itemView.findViewById(R.id.emailMechanic1);
        vehicleInfoMec11 = (TextView)itemView.findViewById(R.id.vehicleInfoMec1);
        shopName = (TextView)itemView.findViewById(R.id.shopName1);

        imageUrl=itemView.findViewById(R.id.imageUrl1);

        update=itemView.findViewById(R.id.update);
        delete=itemView.findViewById(R.id.delete);

    }
}
