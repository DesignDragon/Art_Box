package com.example.ArtBox;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class auction_adapter extends RecyclerView.Adapter<auction_adapter.auction_post_holder> {
    private ArrayList<auctionPosts> data;
    FragmentManager fragmentManager;
    private CountDownTimer countDownTimer;
    private Boolean timerRunning;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
//    private long endTime;
//    private long timeLeft;
//    private long startTime;
    long upTime;
    long tPassed;

    public class auction_post_holder extends RecyclerView.ViewHolder {
        public ImageView post;
        public TextView details;
        public TextView price;
        public TextView hour;
        public CardView auctionCard;
        public auction_post_holder(@NonNull View itemView) {
            super(itemView);
            post=(ImageView) itemView.findViewById(R.id.auction_item_img);
            details=(TextView) itemView.findViewById(R.id.auct_item_desc);
            price=(TextView) itemView.findViewById(R.id.auct_item_price);
            hour=(TextView) itemView.findViewById(R.id.h_left);
            auctionCard=(CardView) itemView.findViewById(R.id.auct_card);
        }
    }

    public auction_adapter(ArrayList<auctionPosts> auctionData, FragmentManager supportFragmentManager,Context c) {
        Log.d("data_auct","received");
        this.data=auctionData;
        this.fragmentManager=supportFragmentManager;
        this.context=c;
    }

    @NonNull
    @Override
    public auction_adapter.auction_post_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.auction_list,parent,false);
        return new auction_post_holder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final auction_post_holder holder, final int position) {
        RequestOptions requestOptions= RequestOptions.placeholderOf(R.color.bg_color).format(DecodeFormat.PREFER_ARGB_8888);
        Glide.with(holder.itemView).setDefaultRequestOptions(requestOptions).load(data.get(position).getUrl()).into(holder.post);

//                .setDefaultRequestOptions(requestOptions)
        holder.details.setText(data.get(position).getTitle());
        holder.price.setText("₹"+data.get(position).getPrice());
        upTime=Long.parseLong(data.get(position).getUploadTime());
        tPassed=System.currentTimeMillis() - upTime;
        firebaseFirestore=FirebaseFirestore.getInstance();
        final long tLeft=(Long.parseLong(data.get(position).getHour())*3600000 + Long.parseLong(data.get(position).getMin())*60000)-tPassed;
        countDownTimer = new CountDownTimer(tLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                final String time=String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                holder.hour.setText(time);
            }
            @Override
            public void onFinish() {
                if(tLeft<0)
                {
                    firebaseFirestore.collection("USERS").document(data.get(position).getUid()).
                            collection("AUCTION").document(data.get(position).getAuctionId()).delete();
                }
            }
        }.start();
        timerRunning=true;
        final String user_id=data.get(position).getUid().toString();
        final String biddeer_uid=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        holder.auctionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user_id.equals(biddeer_uid))
                {
                    bid_details f=new bid_details();
                    final Bundle b= new Bundle();
                    b.putString("auctionid",data.get(position).getAuctionId());
                    b.putString("bidder_uid",biddeer_uid);
                    b.putString("user_id",user_id);
                    f.setArguments(b);
                    fragmentManager.beginTransaction().replace(R.id.frag_container,f).addToBackStack(null).commit();
                }
                else{
                    bidding b=new bidding();
                    final Bundle bundle= new Bundle();
                    bundle.putString("auction_id",data.get(position).getAuctionId());
                    bundle.putString("auctPost",data.get(position).getUrl().toString());
                    bundle.putString("auctInitialPrice",data.get(position).getPrice().toString());
                    bundle.putString("auctTitle",data.get(position).getTitle().toString());
                    bundle.putString("auctionDesc",data.get(position).getDetails().toString());
                    bundle.putString("bidder_uid",biddeer_uid);
                    bundle.putString("user_id",user_id);
                    b.setArguments(bundle);
//                    FragmentManager manager=context.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frag_container,b).addToBackStack(null).commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public void update(ArrayList<auctionPosts> datalist){
        this.data=datalist;
        notifyDataSetChanged();
    }
}

