package com.example.gypsymucker;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class HolderCustomerSendServiceRequest extends RecyclerView.ViewHolder {
    TextView unionId2,unionName2,mechanicMobile2,mechanicName2,aadharNoMechanic2,emailMechanic2,vehicleInfoMec112,shopName2;
    ImageView imageUrl2;
    Button request,reviewBtn,moreInfoBtn;
    LinearLayout mobileNOLayout,aadharNoLayout,unionIdLayout,unionNameLayout,infoLayout;
    TextView message;
    public HolderCustomerSendServiceRequest(@NonNull View itemView) {
        super(itemView);

        unionId2 = (TextView)itemView.findViewById(R.id.unionId1);
        unionName2 = (TextView)itemView.findViewById(R.id.unionName1);
        mechanicMobile2 = (TextView)itemView.findViewById(R.id.mechanicMobile1);
        mechanicName2 = (TextView)itemView.findViewById(R.id.mechanicName1);
        aadharNoMechanic2 = (TextView)itemView.findViewById(R.id.aadharNoMechanic1);
        emailMechanic2 = (TextView)itemView.findViewById(R.id.emailMechanic1);
        vehicleInfoMec112 = (TextView)itemView.findViewById(R.id.vehicleInfoMec1);
        shopName2 = (TextView)itemView.findViewById(R.id.shopName1);

        imageUrl2=itemView.findViewById(R.id.imageUrl1);

        request=itemView.findViewById(R.id.request);

        reviewBtn=itemView.findViewById(R.id.reviewBtn);
        moreInfoBtn=itemView.findViewById(R.id.moreInfoBtn);

        mobileNOLayout = itemView.findViewById(R.id.mobileNOLayout);
        aadharNoLayout = itemView.findViewById(R.id.aadharNoLayout);
        unionIdLayout = itemView.findViewById(R.id.unionIdLayout);
        unionNameLayout = itemView.findViewById(R.id.unionNameLayout);

        message=itemView.findViewById(R.id.message);

    }
}
