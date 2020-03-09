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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class home_screen_fragment extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private FirebaseAuth firebaseAuth;
    private home_adapter adapter;
    home_screen_fragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home_screen_fragment,container,false);
        if (getActivity() != null && getActivity() instanceof side_menu) {
            ((side_menu) getActivity()).getNav().setVisibility(view.VISIBLE);
        }
        recyclerView= view.findViewById(R.id.home_recycler);
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        user_id=firebaseAuth.getUid().toString();
        final ArrayList<userPosts> user_list=new ArrayList<>();
        adapter=new home_adapter(user_list,getActivity().getSupportFragmentManager(),getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        firebaseFirestore.collection("USERS").document(user_id).collection("FOLLOWING").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot s:queryDocumentSnapshots)
                {
                    userProfileData user = s.toObject(userProfileData.class);
                    Log.d("id",user.getId());
                    firebaseFirestore.collection("USERS").document(user.getId()).collection("POSTS").orderBy("uploadDate")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                userPosts user = doc.toObject(userPosts.class);
                                Log.d("urls", user.getUrl());
                                Log.d("names",user.getUsername());
                                Log.d("capts",user.getCaption());
                                user_list.add(user);
                            }
                            adapter.update(user_list);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
        return view;
    }

}
