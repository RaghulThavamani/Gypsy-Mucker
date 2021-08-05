package com.example.gypsymucker;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HolderServiceReview extends RecyclerView.ViewHolder {

    TextView date,rating,content,email,mechanicNameOfCompletedWork,customerNameOfCompletedWork,vehicleInfoOfCustomer;

    public HolderServiceReview(@NonNull View itemView) {
        super(itemView);

        date = itemView.findViewById(R.id.date);
        rating = itemView.findViewById(R.id.rating);
        content = itemView.findViewById(R.id.content);
        email = itemView.findViewById(R.id.email);
        mechanicNameOfCompletedWork = itemView.findViewById(R.id.mechanicNameOfCompletedWork);
        customerNameOfCompletedWork = itemView.findViewById(R.id.customerNameOfCompletedWork);
        vehicleInfoOfCustomer = itemView.findViewById(R.id.vehicleInfoOfCustomer);
    }
}
