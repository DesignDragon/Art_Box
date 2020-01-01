package com.example.artbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static java.security.AccessController.getContext;


public class user_profile_fragment extends Fragment {

    private RecyclerView recyclerView;
    private user_post_adapter adapter;
    private StorageReference storageReference;
    private ArrayList<userPosts> upload_post;
    public View view;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private FirebaseAuth firebaseAuth;

    public user_profile_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View v=inflater.inflate(R.layout.fragment_user_profile_fragment, container, false);
        recyclerView=(RecyclerView) v.findViewById(R.id.post_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        upload_post=new ArrayList<userPosts>();
        //FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getUid().toString();
        adapter=new user_post_adapter(upload_post);
        recyclerView.setAdapter(adapter);
        //storageReference= FirebaseStorage.getInstance().getReference("image_store/"+user_id.toString()+"jpg");
        DatabaseReference d=FirebaseDatabase.getInstance().getReference();
        d.child("USERS").child(user_id).child("POSTS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                upload_post.clear();
                for(DataSnapshot d :dataSnapshot.getChildren()){
                    userPosts p=d.getValue(userPosts.class);
                    upload_post.add(p);
                }
                adapter.update(upload_post);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }
}
