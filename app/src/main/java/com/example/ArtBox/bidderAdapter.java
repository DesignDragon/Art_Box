package com.example.ArtBox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class bidderAdapter extends RecyclerView.Adapter<bidderAdapter.bidderHolder> {
    private ArrayList<biddersDetails> data;
    FragmentManager fragmentManager;
    Context c;
    public bidderAdapter(ArrayList<biddersDetails> bid_data, FragmentManager manager,Context context) {
        this.data=bid_data;
        this.c=context;
        this.fragmentManager=manager;
    }

    @NonNull
    @Override
    public bidderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.bidder_list,parent,false);
        return new bidderHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull bidderHolder holder, int position) {
        holder.bidder_name.setText(data.get(position).getBidder_name());
        holder.bid.setText(data.get(position).getBid_amount());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class bidderHolder extends RecyclerView.ViewHolder {
        public TextView bidder_name;
        public TextView bid;
        public bidderHolder(@NonNull View itemView) {
            super(itemView);
            bidder_name=itemView.findViewById(R.id.bidder_name);
            bid=itemView.findViewById(R.id.price_placed);
        }
    }
//    public void update(ArrayList<biddersDetails> datalist){
//        this.data=datalist;
//        notifyDataSetChanged();
//    }
}
