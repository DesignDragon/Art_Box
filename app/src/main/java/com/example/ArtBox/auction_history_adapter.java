package com.example.ArtBox;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class auction_history_adapter extends RecyclerView.Adapter<auction_history_adapter.history_holder> {
    public static final int MSG=0;
    public static final int EMPTY_MSG=1;
    private ArrayList<auctionPosts> data;
    FragmentManager fragmentManager;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    public class history_holder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView desc;
        public TextView time;
        public Button claim;
        public history_holder(@NonNull View itemView) {
            super(itemView);
            img=(ImageView) itemView.findViewById(R.id.hist_img);
            title=itemView.findViewById(R.id.hist_title);
            desc=itemView.findViewById(R.id.hist_desc);
            claim=itemView.findViewById(R.id.claim);
            time=itemView.findViewById(R.id.tleft);
        }
    }

    public auction_history_adapter(ArrayList<auctionPosts> data, FragmentManager supportFragmentManager, Context context) {
        this.data=data;
        this.fragmentManager=supportFragmentManager;
        this.context=context;
    }

    @NonNull
    @Override
    public auction_history_adapter.history_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_auct, parent, false);
            return new history_holder(v);
        }
        else {
            View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_auct_history,parent,false);
            return new history_holder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final history_holder holder, int position) {
        RequestOptions requestOptions= RequestOptions.placeholderOf(R.color.bg_color).format(DecodeFormat.PREFER_ARGB_8888);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();
        final ArrayList<biddersDetails> winner=new ArrayList<>();
        final String user=auth.getUid().toString();
        final String auct_user=data.get(position).getUid().toString();
        final String auct_id=data.get(position).getAuctionId().toString();

        Glide.with(holder.itemView).setDefaultRequestOptions(requestOptions).load(data.get(position).getUrl()).into(holder.img);
        holder.title.setText(data.get(position).getTitle().toString());
        holder.desc.setText(data.get(position).getDetails().toString());
        Log.d("hist",data.get(position).getTitle().toString());
        long upTime=Long.parseLong(data.get(position).getUploadTime());
        long tPassed=System.currentTimeMillis() - upTime;
        final long tLeft=(Long.parseLong(data.get(position).getHour())*3600000 + Long.parseLong(data.get(position).getMin())*60000)-tPassed;
        CountDownTimer countDownTimer = new CountDownTimer(tLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                final String time=String.format("ENDS IN: %02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                holder.time.setText(time);
            }
            @Override
            public void onFinish() {
                if(tLeft<0)
                {
                    firebaseFirestore.collection("USERS").document(auct_user)
                            .collection("AUCTION").document(auct_id)
                            .collection("BIDDERS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot s:queryDocumentSnapshots)
                            {

                                biddersDetails b=s.toObject(biddersDetails.class);
                                b.setBidder_id(s.get("bidder_id").toString());
                                b.setBid_amount(s.get("bid_amount").toString());
                                b.setTime(s.get("time").toString());
                                Log.d("dlist",s.get("bidder_name").toString());
                                winner.add(b);
                                Collections.sort(winner, new Comparator<biddersDetails>() {
                                    @Override
                                    public int compare(biddersDetails o1, biddersDetails o2) {
                                        return o1.getBid_amount().compareTo(o2.getBid_amount()) ;
                                    }
                                });

                                Collections.sort(winner, new Comparator<biddersDetails>() {
                                    @Override
                                    public int compare(biddersDetails o1, biddersDetails o2) {
                                        return o1.getTime().compareTo(o2.getTime());
                                    }
                                });
                                Collections.reverse(winner);
                            }
                            Log.d("list",winner.get(0).getBidder_name().toString());
                            if(winner.get(0).getBidder_id().equals(user))
                                holder.claim.setVisibility(View.VISIBLE);
                            else
                                holder.time.setText("oops!!");
                        }
                    });


                }
            }
        }.start();
    }

    @Override
    public int getItemViewType(int position) {
        if(data.get(position).getUrl().isEmpty())
            return EMPTY_MSG;
        else
            return MSG;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void update(ArrayList<auctionPosts> data) {
        this.data=data;
        notifyDataSetChanged();
    }
}
