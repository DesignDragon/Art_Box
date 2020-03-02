package com.example.ArtBox;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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


public class posts extends Fragment {

    private RecyclerView recyclerView;
    private user_post_adapter adapter;
    private ArrayList<userPosts> upload_post;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private FirebaseAuth firebaseAuth;
    public posts() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_posts, container, false);
        recyclerView=(RecyclerView) v.findViewById(R.id.post_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        upload_post=new ArrayList<userPosts>();
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        adapter=new user_post_adapter(upload_post,getActivity().getSupportFragmentManager());
        recyclerView.setAdapter(adapter);

        if(getArguments()!=null)
        {
            final String id=getArguments().getString("data");
            firebaseFirestore.collection("USERS").document(id).collection("POSTS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    upload_post.clear();
//                for(userPosts d :queryDocumentSnapshots.toObjects(userPosts.class)){
//                    userPosts p=d.getValue(userPosts.class);
//                    upload_post.add(p);
//                }

                    for(QueryDocumentSnapshot s :queryDocumentSnapshots) {
                        userPosts up=s.toObject(userPosts.class);
                        upload_post.add(up);
                    }
                    adapter.update(upload_post);
                    adapter.notifyDataSetChanged();
                }
            });
        }
        else
        {
            user_id=firebaseAuth.getUid().toString();
            firebaseFirestore.collection("USERS").document(user_id).collection("POSTS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    upload_post.clear();
//                for(userPosts d :queryDocumentSnapshots.toObjects(userPosts.class)){
//                    userPosts p=d.getValue(userPosts.class);
//                    upload_post.add(p);
//                }

                    for(QueryDocumentSnapshot s :queryDocumentSnapshots) {
                        userPosts up=s.toObject(userPosts.class);
                        upload_post.add(up);
                    }
                    adapter.update(upload_post);
                    adapter.notifyDataSetChanged();
                }
            });
        }
        return v;
    }
}
