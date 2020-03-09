package com.example.ArtBox;

import android.os.Bundle;

import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Chats extends Fragment {
    public RecyclerView r;
    private String uid;
    public RecyclerView.Adapter adapter;
    private ArrayList<userProfileData> data;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private ContentLoadingProgressBar pbar;
    public Chats() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_chats, container, false);
        /*pbar=v.findViewById(R.id.progress_bar);
        pbar.setVisibility(v.VISIBLE);*/
        data=new ArrayList<userProfileData>();
        r=(RecyclerView) v.findViewById(R.id.chat_list_recycler);
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        uid= auth.getInstance().getUid().toString();
        firebaseFirestore.collection("USERS").document(uid).collection("FOLLOWING").
                get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot s:queryDocumentSnapshots)
                        {
                            userProfileData user = s.toObject(userProfileData.class);
                            user.getUsername();
                            user.getId();
                            data.add(user);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        adapter = new chat_list_adapter(data,getContext(), getActivity().getSupportFragmentManager());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        r.setLayoutManager(layoutManager);
        r.setAdapter(adapter);
        return v;
    }
}
