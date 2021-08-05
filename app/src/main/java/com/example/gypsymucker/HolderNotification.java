package com.example.gypsymucker;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class HolderNotification extends RecyclerView.ViewHolder {
    TextView notification,date,emailMechanic;
    ImageButton okBtn,decline,accept;
    LinearLayout confirmationLayout;
    public HolderNotification(@NonNull View itemView) {
        super(itemView);
        notification = itemView.findViewById(R.id.notification);
        date = itemView.findViewById(R.id.date);
        okBtn = itemView.findViewById(R.id.okBtn);
        decline = itemView.findViewById(R.id.decline);
        emailMechanic = itemView.findViewById(R.id.emailMechanic);
        accept = itemView.findViewById(R.id.accept);
        confirmationLayout = itemView.findViewById(R.id.confirmationLayout);
    }
}
