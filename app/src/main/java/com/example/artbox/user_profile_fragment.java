package com.example.artbox;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static java.security.AccessController.getContext;


public class user_profile_fragment extends Fragment {

    private RecyclerView recyclerView;
    private user_post_adapter adapter;
//    private StorageReference storageReference;
    private ArrayList<userPosts> upload_post;
    public View view;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private String username;
    private FirebaseAuth firebaseAuth;
    private TextView user;
    private ImageView setProfile;
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
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getUid().toString();
        adapter=new user_post_adapter(upload_post,getActivity().getSupportFragmentManager());
        recyclerView.setAdapter(adapter);
        setProfile=(ImageView) v.findViewById(R.id.set_profile);
        //storageReference= FirebaseStorage.getInstance().getReference("image_store/"+user_id.toString()+"jpg");


        user=(TextView) v.findViewById(R.id.user_name);
        firebaseFirestore.collection("USERS").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username=documentSnapshot.getString("username");
                user.setText(username);
            }
        });

        DatabaseReference d=FirebaseDatabase.getInstance().getReference();
        d.child("USERS").child(user_id).child("PROFILE").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    String profile=dataSnapshot1.getValue().toString();
                    Glide.with(getContext()).load(profile).into(setProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
