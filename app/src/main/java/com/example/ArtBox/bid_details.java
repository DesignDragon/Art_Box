package com.example.ArtBox;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class bid_details extends Fragment {
    private RecyclerView rv;
    public RecyclerView.Adapter adapter;
    private FirebaseFirestore db;
    private ArrayList<biddersDetails> bid_data;
    public bid_details() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_bid_details, container, false);
        db = FirebaseFirestore.getInstance();
        rv=v.findViewById(R.id.bid_details_recycler);
        bid_data=new ArrayList<biddersDetails>();
        String user=getArguments().getString("user_id").toString();
        final String itemID=getArguments().getString("auctionid");
        db.collection("USERS").document(user).
                collection("AUCTION").document(itemID).
                collection("BIDDERS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot s:queryDocumentSnapshots)
                {
                    biddersDetails details=s.toObject(biddersDetails.class);
                    details.setBid_amount(s.get("bid_amount").toString());
                    details.setBidder_name(s.get("bidder_name").toString());
                    Log.d("amt",s.get("bid_amount").toString());
                    Log.d("amt",s.get("bidder_name").toString());
                    bid_data.add(details);
                }
                //adapter.update(bid_data);
                adapter.notifyDataSetChanged();
            }
        });
        rv = (RecyclerView) v.findViewById(R.id.bid_details_recycler);
        adapter = new bidderAdapter(bid_data,getFragmentManager(),getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        return v;
    }
}
