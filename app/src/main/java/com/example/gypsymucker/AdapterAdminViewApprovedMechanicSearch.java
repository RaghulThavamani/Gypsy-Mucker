package com.example.gypsymucker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterAdminViewApprovedMechanicSearch extends RecyclerView.Adapter<AdapterAdminViewApprovedMechanicSearch.MyAdapterViewHolder> {

public  Context c;
public ArrayList<MechanicDetails> arrayList;
    public AdapterAdminViewApprovedMechanicSearch(Context c, ArrayList<MechanicDetails> arrayList){

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public MyAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull MyAdapterViewHolder holder, int position) {

    }

    public class MyAdapterViewHolder extends RecyclerView.ViewHolder {
        public TextView mechanicName;
        public MyAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            mechanicName = itemView.findViewById(R.id.mechanicName);
        }
    }
}
