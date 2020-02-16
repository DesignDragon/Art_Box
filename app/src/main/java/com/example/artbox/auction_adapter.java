package com.example.artbox;

import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import java.util.ArrayList;
import java.util.Locale;

public class auction_adapter extends RecyclerView.Adapter<auction_adapter.auction_post_holder> {
    private ArrayList<auctionPosts> data;
    FragmentManager fragmentManager;
    private CountDownTimer countDownTimer;
    private Boolean timerRunning;
    private long endTime;
    private long timeLeft;
    private long startTime;
    public class auction_post_holder extends RecyclerView.ViewHolder {
        public ImageView post;
        public TextView details;
        public TextView price;
        public TextView hour;
        public TextView min;

        public auction_post_holder(@NonNull View itemView) {
            super(itemView);
            post=(ImageView) itemView.findViewById(R.id.auction_item_img);
            details=(TextView) itemView.findViewById(R.id.auct_item_desc);
            price=(TextView) itemView.findViewById(R.id.auct_item_price);
            hour=(TextView) itemView.findViewById(R.id.h_left);

        }
    }

    public auction_adapter(ArrayList<auctionPosts> auctionData, FragmentManager supportFragmentManager) {
        Log.d("data_auct","received");
        this.data=auctionData;
        this.fragmentManager=supportFragmentManager;
    }

    @NonNull
    @Override
    public auction_adapter.auction_post_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.auction_list,parent,false);
        return new auction_post_holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull auction_post_holder holder, int position) {
        RequestOptions requestOptions= RequestOptions.placeholderOf(R.color.bg_color).format(DecodeFormat.PREFER_ARGB_8888);
        Glide.with(holder.itemView).setDefaultRequestOptions(requestOptions).load(data.get(position).getUrl()).into(holder.post);

//                .setDefaultRequestOptions(requestOptions)
        holder.details.setText(data.get(position).getDetails());
        holder.price.setText(data.get(position).getPrice());
        long t=Long.parseLong(data.get(position).getHour())*3600000 + Long.parseLong(data.get(position).getMin())*60000;
        setTime(t);
    }
    private void setTime(long t)
    {
        this.startTime=t;
    }
    private void startTimer()
    {
        endTime=System.currentTimeMillis() + timeLeft;
        countDownTimer= new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft=millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                timerRunning=false;
            }
        }.start();
        timerRunning=true;
    }

    private void updateTimer()
    {
        int hours_left=(int) (timeLeft/1000)/3600;
        int mins_left=(int) ((timeLeft/1000)%3600)/60;
        String htime_left=String.format(Locale.getDefault(),"%d",hours_left);
        String mtime_left=String.format(Locale.getDefault(),"%d",mins_left);
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
