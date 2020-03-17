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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class auction_history extends Fragment {
    private RecyclerView rv;
    private ArrayList<auctionPosts> data;
    private auction_history_adapter adapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    public auction_history() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_auction_history, container, false);
        rv=v.findViewById(R.id.auct_hist_recycler);
        data=new ArrayList<auctionPosts>();
        adapter=new auction_history_adapter(data,getActivity().getSupportFragmentManager(),getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        String uid=firebaseAuth.getUid().toString();
        firebaseFirestore.collection("USERS").document(uid).collection("AUCTHISTORY").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                data.clear();
                for(QueryDocumentSnapshot s:queryDocumentSnapshots)
                {
                    auctionPosts up=new auctionPosts();
                    up.setUrl(s.get("post").toString());
                    up.setDetails(s.get("details").toString());
                    up.setHour(s.get("hour").toString());
                    up.setMin(s.get("min").toString());
                    up.setUploadTime(s.get("uploadTime").toString());
                    up.setAuctionId(s.get("auctionId").toString());
                    up.setTitle(s.get("title").toString());
                    up.setUploadDate(s.get("uploadDate").toString());
                    up.setUid(s.get("uid").toString());
                    data.add(up);
                    Log.d("h",s.get("post").toString());
                }
                Collections.sort(data, new Comparator<auctionPosts>() {
                    @Override
                    public int compare(auctionPosts o1, auctionPosts o2) {
                        return o1.getUploadDate().compareTo(o2.getUploadDate());
                    }
                });
                adapter.update(data);
                adapter.notifyDataSetChanged();
            }
        });
        return v;
    }
}
