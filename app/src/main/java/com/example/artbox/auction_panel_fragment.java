package com.example.artbox;

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


public class auction_panel_fragment extends Fragment {
    RecyclerView rv;
    private ArrayList<auctionPosts> auctionData;
    private auction_adapter auctionAdapter;
    FirebaseFirestore firebaseFirestore;
    public auction_panel_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_auction_panel_fragment, container, false);
        rv=(RecyclerView) view.findViewById(R.id.auction_recycler);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        auctionData=new ArrayList<auctionPosts>();
        auctionAdapter=new auction_adapter(auctionData,getActivity().getSupportFragmentManager());
        rv.setAdapter(auctionAdapter);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("AUCTION").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                    auctionData.add(up);
                    Log.d("data",auctionData.get(0).getPrice());
                }
                auctionAdapter.update(auctionData);
                auctionAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }
}
