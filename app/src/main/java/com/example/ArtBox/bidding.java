package com.example.ArtBox;

import android.icu.text.SymbolTable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class bidding extends Fragment {
    private ImageView auction_post;
    private NumberPicker price;
    private TextView desc;
    private TextView pr;
    private Button bid;
    int bid_placed;
    String bidder_name;
    String bid_amt;
    private FirebaseFirestore firebaseFirestore;
    public bidding() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_bidding, container, false);
        auction_post=v.findViewById(R.id.auctImage);
        price=v.findViewById(R.id.price_picker);
        desc=v.findViewById(R.id.desc_bid);
        bid=v.findViewById(R.id.bid);
        pr=v.findViewById(R.id.set_price);
        Glide.with(getContext()).load(getArguments().getString("auctPost").toString()).into(auction_post);
        final String bidder_uid=getArguments().getString("bidder_uid").toString();
        final String user_id=getArguments().getString("user_id").toString();
        final String ItemId= getArguments().getString("auction_id").toString(); //aur yaha fetch kiya
        price.setMinValue(Integer.parseInt(getArguments().getString("auctInitialPrice")));

        price.setMaxValue(10000);
        price.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                picker.setValue((newVal<oldVal)?oldVal-1000:newVal+1000);
                bid_placed=picker.getValue();
                bid_amt= String.valueOf(bid_placed);
            }
        });
        desc.setText(getArguments().getString("auctionDesc").toString());

        Log.d("bidder",bidder_uid);
        Log.d("user",user_id);
        Log.d("auctid",ItemId);
        /*placing bidding data into database*/
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
                firebaseFirestore.collection("USERS").document(user_id).
                        collection("AUCTION").document(ItemId).
                        collection("BIDDERS").document(bidder_uid).set(data);
            }
        });
        return v;
    }
}
