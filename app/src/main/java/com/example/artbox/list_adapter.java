package com.example.artbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class list_adapter extends RecyclerView.Adapter<list_adapter.MyViewHolder>
{
    private ArrayList<userProfileData> data_set;
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View v;
        public MyViewHolder(View t){
            super(t);
            v=t;
        }
    }
    public list_adapter(ArrayList<userProfileData> data){
        data_set=data;
    }
    @NonNull
    @Override
    public list_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.searchdata,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull list_adapter.MyViewHolder holder, int position) {
        TextView txt=holder.v.findViewById(R.id.sdata);
        txt.setText(data_set.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return data_set.size();
    }
}
