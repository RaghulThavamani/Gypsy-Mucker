package com.example.gypsymucker;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class HolderCustomerViewPendingMechanic extends RecyclerView.ViewHolder {

    TextView unionId,unionName,mechanicMobile,mechanicName,aadharNoMechanic,emailMechanic,vehicleInfoMec11,status;
    ImageView imageUrl;
    Button cancelService,review,call;
    public HolderCustomerViewPendingMechanic(@NonNull View itemView) {
        super(itemView);

        unionId = (TextView)itemView.findViewById(R.id.unionId1);
        unionName = (TextView)itemView.findViewById(R.id.unionName1);
        mechanicMobile = (TextView)itemView.findViewById(R.id.mechanicMobile1);
        mechanicName = (TextView)itemView.findViewById(R.id.mechanicName1);
        aadharNoMechanic = (TextView)itemView.findViewById(R.id.aadharNoMechanic1);
        emailMechanic = (TextView)itemView.findViewById(R.id.emailMechanic1);
        vehicleInfoMec11 = (TextView)itemView.findViewById(R.id.vehicleInfoMec1);
        status = (TextView)itemView.findViewById(R.id.status);
        imageUrl=itemView.findViewById(R.id.imageUrl1);

        cancelService=itemView.findViewById(R.id.cancelService);
        review=itemView.findViewById(R.id.review);
        call=itemView.findViewById(R.id.call);

    }
}
