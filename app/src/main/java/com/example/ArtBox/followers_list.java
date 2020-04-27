package com.example.ArtBox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class followers_list extends Fragment {
    public RecyclerView r;
    private String uid;
    public RecyclerView.Adapter adapter;
    private ArrayList<userProfileData> data;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    public followers_list() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_followers_list, container, false);
        r=(RecyclerView) v.findViewById(R.id.followers_recycler);
        data=new ArrayList<userProfileData>();
        db = FirebaseFirestore.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid().toString();

        /*Shows the following list of searched user and current user*/
        if(getArguments()!=null)
        {
            final String searche_id=getArguments().getString("data");
            db.collection("USERS").document(searche_id).collection("FOLLOWERS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot s:queryDocumentSnapshots)
                    {
                        userProfileData user = s.toObject(userProfileData.class);
                        user.getUsername();
                        data.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            adapter = new list_adapter(data,getContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            r.setLayoutManager(layoutManager);
            r.setAdapter(adapter);
        }

        else {
            db.collection("USERS").document(uid).collection("FOLLOWERS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot s:queryDocumentSnapshots)
                    {
                        userProfileData user = s.toObject(userProfileData.class);
                        user.getUsername();
                        data.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            adapter = new list_adapter(data,getContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            r.setLayoutManager(layoutManager);
            r.setAdapter(adapter);
        }
        return v;
    }
}
