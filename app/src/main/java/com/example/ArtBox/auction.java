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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class auction extends Fragment {
    RecyclerView rv;
    private ArrayList<auctionPosts> auctionData;
    private auction_adapter auctionAdapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    public auction() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_auction, container, false);
        rv=(RecyclerView) view.findViewById(R.id.auction_recycler);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        auctionData=new ArrayList<auctionPosts>();
        auctionAdapter=new auction_adapter(auctionData,getActivity().getSupportFragmentManager(),getContext());
        rv.setAdapter(auctionAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();


        if(getArguments()!=null)
        {
            final String id=getArguments().getString("data");
            firebaseFirestore.collection("USERS").document(id).collection("AUCTION").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    auctionData.clear();
                    for(QueryDocumentSnapshot s :queryDocumentSnapshots) {
                        auctionPosts up=new auctionPosts();
                        up.setUrl(s.get("post").toString());
                        up.setDetails(s.get("details").toString());
                        up.setPrice(s.get("price").toString());
                        up.setHour(s.get("hour").toString());
                        up.setMin(s.get("min").toString());
                        up.setUploadTime(s.get("uploadTime").toString());
                        up.setUid(s.get("uid").toString());
                        up.setAuctionId(s.getString("auctionId").toString());
                        auctionData.add(up);
                        Log.d("data",auctionData.get(0).getPrice());
                    }
                    auctionAdapter.update(auctionData);
                    auctionAdapter.notifyDataSetChanged();

                }
            });
        }
        else
        {
            Log.d("call","t");
            final String userid=firebaseAuth.getUid().toString();
            firebaseFirestore.collection("USERS").document(userid).collection("AUCTION").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    auctionData.clear();
                    for(QueryDocumentSnapshot s :queryDocumentSnapshots) {
                        auctionPosts up=new auctionPosts();
                        up.setUrl(s.get("post").toString());
                        up.setDetails(s.get("details").toString());
                        up.setPrice(s.get("price").toString());
                        up.setHour(s.get("hour").toString());
                        up.setMin(s.get("min").toString());
                        up.setUploadTime(s.get("uploadTime").toString());
                        up.setAuctionId(s.getString("auctionId").toString());
                        up.setUid(s.get("uid").toString());
                        auctionData.add(up);
                        Log.d("datap",s.getString("auctionId").toString());
                    }
                    auctionAdapter.update(auctionData);
                    auctionAdapter.notifyDataSetChanged();
                }
            });
        }
        return view;
    }
}
