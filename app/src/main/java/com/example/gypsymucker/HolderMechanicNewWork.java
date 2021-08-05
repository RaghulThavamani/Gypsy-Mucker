package com.example.gypsymucker;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class HolderMechanicNewWork extends RecyclerView.ViewHolder {
    TextView email,lat,lng,status,name,mobile,vehicle,licNo;
    Button acceptBtn,rejectBtn,getCusLocation,serviceCompleted,call;
    public HolderMechanicNewWork(@NonNull View itemView) {
        super(itemView);

        email = itemView.findViewById(R.id.email);
        lat = itemView.findViewById(R.id.lat);
        lng = itemView.findViewById(R.id.lng);
        status = itemView.findViewById(R.id.status);

        name = itemView.findViewById(R.id.name);
        mobile = itemView.findViewById(R.id.mobile);
        vehicle = itemView.findViewById(R.id.vehicle);
        licNo = itemView.findViewById(R.id.licNo);

        acceptBtn = itemView.findViewById(R.id.acceptBtn);
        rejectBtn = itemView.findViewById(R.id.rejectBtn);
        getCusLocation = itemView.findViewById(R.id.getCusLocation);
        serviceCompleted = itemView.findViewById(R.id.serviceCompleted);
        call = itemView.findViewById(R.id.call);


    }
}
