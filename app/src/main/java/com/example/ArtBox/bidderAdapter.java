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
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class bidderAdapter extends RecyclerView.Adapter<bidderAdapter.bidderHolder> {
    private ArrayList<biddersDetails> data;
    FragmentManager fragmentManager;
    Context c;
    public bidderAdapter(ArrayList<biddersDetails> bid_data, FragmentManager manager,Context context) {
        this.data=bid_data;
        this.c=context;
        this.fragmentManager=manager;
        Collections.sort(data, new Comparator<biddersDetails>() {
            @Override
            public int compare(biddersDetails o1, biddersDetails o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });
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
        holder.bid.setText("₹"+data.get(position).getBid_amount());
        holder.time.setText(data.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class bidderHolder extends RecyclerView.ViewHolder {
        public TextView bidder_name;
        public TextView bid;
        TextView time;
        public bidderHolder(@NonNull View itemView) {
            super(itemView);
            bidder_name=itemView.findViewById(R.id.bidder_name);
            bid=itemView.findViewById(R.id.price_placed);
            time=itemView.findViewById(R.id.time_placed);
        }
    }
//    public void update(ArrayList<biddersDetails> datalist){
//        this.data=datalist;
//        notifyDataSetChanged();
//    }
}
