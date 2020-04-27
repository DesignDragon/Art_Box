package com.example.ArtBox;

import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class bidding extends Fragment {
    private ImageView auction_post;
    private NumberPicker price;
    private TextView desc;
    private Button bid;
    private TextView title;
    ImageButton list;
    TextView bidAmt;
    int bid_placed;
    String bidder_name;
    String bid_amt;
    private FirebaseFirestore firebaseFirestore;
    public bidding() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_bidding, container, false);
        auction_post=v.findViewById(R.id.auctImage);
        price=v.findViewById(R.id.price_picker);
        title=v.findViewById(R.id.item_title);
        desc=v.findViewById(R.id.desc_bid);
        list=v.findViewById(R.id.list);
        bid=v.findViewById(R.id.bid);
        bidAmt=v.findViewById(R.id.bid_placed);
        Glide.with(getContext()).load(getArguments().getString("auctPost").toString()).into(auction_post);
        final String bidder_uid=getArguments().getString("bidder_uid").toString();
        final String user_id=getArguments().getString("user_id").toString();
        final String ItemId= getArguments().getString("auction_id").toString(); //aur yaha fetch kiya
        price.setMinValue(Integer.parseInt(getArguments().getString("auctInitialPrice")));

        price.setMaxValue(25000);
        price.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                picker.setValue((newVal<oldVal)?oldVal-1000:newVal+1000);
                bid_placed=picker.getValue();
                bid_amt= String.valueOf(bid_placed);
            }
        });
        bidAmt.setText(bid_amt);
        desc.setText(getArguments().getString("auctionDesc").toString());
        title.setText(getArguments().getString("auctTitle").toString());
        Log.d("bidder",bidder_uid);
        Log.d("user",user_id);
        Log.d("auctid",ItemId);
        /*placing bidding data into database*/
        final String bid_time= DateFormat.getDateTimeInstance().format(new Date());
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("USERS").document(bidder_uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 bidder_name=documentSnapshot.getString("username");
            }
        });
        bid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> data=new HashMap<>();
                data.put("bidder_id",bidder_uid);
                data.put("bid_amount",bid_amt);
                data.put("bidder_name",bidder_name);
                data.put("time",bid_time);
                firebaseFirestore.collection("USERS").document(user_id).
                        collection("AUCTION").document(ItemId).
                        collection("BIDDERS").document(bidder_uid).set(data);
                Toast.makeText(getContext(),"Bid placed successfully",Toast.LENGTH_LONG).show();

                Map<String,Object> data1=new HashMap<>();
                data1.put("title",getArguments().getString("auctionDesc").toString());
                data1.put("details",getArguments().getString("auctTitle").toString());
                data1.put("post",getArguments().getString("auctPost").toString());
                data1.put("uploadDate",getArguments().getString("upload_date") );
                data1.put("uploadTime",getArguments().getString("upload_time"));
                data1.put("hour",getArguments().getString("hour"));
                data1.put("min",getArguments().getString("min"));
                data1.put("auctionId",ItemId);
                data1.put("uid",user_id);
                firebaseFirestore.collection("USERS").document(bidder_uid).collection("AUCTHISTORY").document(ItemId).set(data1);
            }
        });
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bid_details f=new bid_details();
                final Bundle b= new Bundle();
                b.putString("auctionid",ItemId);
                b.putString("bidder_uid",bidder_uid);
                b.putString("user_id",user_id);
                f.setArguments(b);
                getFragmentManager().beginTransaction().replace(R.id.frag_container,f).addToBackStack(null).commit();
            }
        });
        return v;
    }
}
