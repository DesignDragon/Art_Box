package com.example.ArtBox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class user_profile_fragment extends Fragment {


//    private StorageReference storageReference;

    public View view;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private FirebaseAuth firebaseAuth;
    private TextView user;
    private ImageView setProfile;
    Button editB;
    private TextView followers;
    private TextView following;
    private TextView following_num;
    private TextView post_count;
    private ContentLoadingProgressBar pbar;
    public user_profile_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        final View v=inflater.inflate(R.layout.fragment_user_profile_fragment, container, false);
        Toolbar toolbar=v.findViewById(R.id.toolbarprofile);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        pbar=v.findViewById(R.id.progress_bar);
        pbar.setVisibility(v.VISIBLE);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getUid().toString();
        setProfile=(ImageView) v.findViewById(R.id.set_profile);
        //storageReference= FirebaseStorage.getInstance().getReference("image_store/"+user_id.toString()+"jpg");
        user=(TextView) v.findViewById(R.id.user_name);
        editB=(Button) v.findViewById(R.id.edit_profile);
        following=(TextView) v.findViewById(R.id.following);
        following_num=(TextView) v.findViewById(R.id.following_count);
        post_count=(TextView) v.findViewById(R.id.post_count);
        /*Checking for searched user data after clicking on the username in search tab.
          If the user id is there its profile will be visible else current logged in user profile will be visible
          */
        if(getArguments()!=null)
        {
            final String search_id=getArguments().getString("searched_user");
            firebaseFirestore.collection("USERS").document(search_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    final String username=documentSnapshot.getString("username");
                    final String profile=documentSnapshot.getString("url");
                    if(profile==null)
                        Glide.with(getContext()).load(R.drawable.profile2).into(setProfile);
                    else
                        Glide.with(getContext()).load(profile).into(setProfile);
                    user.setText(username);
                    Log.d("searchedId","found");

                    final Bundle b=new Bundle();
                    b.putString("data",search_id);
                    ViewPager viewPager= v.findViewById(R.id.viewPager);
                    viewPager.setAdapter(new searchedUser_adapter(getChildFragmentManager(),b));
                    TabLayout tabLayout=v.findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(viewPager);

                    /*displays the following list of current user or searched user*/
                    following.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            following_list f=new following_list();
                            f.setArguments(b);
                            getFragmentManager().beginTransaction().replace(R.id.frag_container,f).addToBackStack(null).commit();
                        }
                    });


                    /*Displays following count*/
                    firebaseFirestore.collection("USERS").document(search_id).collection("FOLLOWING").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            following_num.setText(String.valueOf(queryDocumentSnapshots.size()));
                            Log.d("count", String.valueOf(queryDocumentSnapshots.size()));
                        }
                    });

                    /*Displaying no of posts of searched user*/
                    firebaseFirestore.collection("USERS").document(search_id).collection("POSTS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            post_count.setText(String.valueOf(queryDocumentSnapshots.size()));
                        }
                    });


                    /*checking if the current user has followed the searched user or not
                    * if the user is followed by the curent user the button text is changed to unfollow else follow*/
                    firebaseFirestore.collection("USERS").document(user_id).collection("FOLLOWING").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot s:queryDocumentSnapshots)
                            {
                                String follower_id=s.get("id").toString();
                                if(search_id.equals(follower_id))
                                {
                                    editB.setText("Following");
                                }
                            }
                        }
                    });
                    /*storing username,id,profile if current user follows the searched user*/
                    editB.setText("Follow");
                    try{
                        editB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editB.setText("Following");
                                Map<String,Object> data=new HashMap<>();
                                data.put("username",username);
                                data.put("id",search_id);
                                data.put("url",profile);
                                firebaseFirestore.collection("USERS").document(user_id).collection("FOLLOWING").document(search_id).set(data);

                            }
                        });
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        }


        /*if the searched user id isnt present then the logged in users profile will be shown*/
        else {
            following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    following_list f=new following_list();
                    getFragmentManager().beginTransaction().replace(R.id.frag_container,f).addToBackStack(null).commit();
                }
            });

            /*Displays following count*/
            firebaseFirestore.collection("USERS").document(user_id).collection("FOLLOWING").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    following_num.setText(String.valueOf(queryDocumentSnapshots.size()));
                    Log.d("count", String.valueOf(queryDocumentSnapshots.size()));
                }
            });


            /*Displaying no of posts by user*/
            firebaseFirestore.collection("USERS").document(user_id).collection("POSTS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    post_count.setText(String.valueOf(queryDocumentSnapshots.size()));
                }
            });

            firebaseFirestore.collection("USERS").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String username=documentSnapshot.getString("username").toString();
                    String profile=documentSnapshot.getString("url");
                    if(profile==null)
                        Glide.with(getContext()).load(R.drawable.profile2).into(setProfile);
                    else
                        Glide.with(getContext()).load(profile).into(setProfile);
                    user.setText(username);
                    ViewPager viewPager= v.findViewById(R.id.viewPager);
                    viewPager.setAdapter(new viewPager_adapter(getChildFragmentManager()));

                    TabLayout tabLayout=v.findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(viewPager);
                }
            });
            editB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(),edit_profile.class));
                }
            });
        }




        /*DatabaseReference d=FirebaseDatabase.getInstance().getReference();
        d.child("USERS").child(user_id).child("DETAILS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profile=dataSnapshot.child("url").getValue(String.class);
                Glide.with(getContext()).load(profile).into(setProfile);
                String username=dataSnapshot.child("username").getValue(String.class);
                user.setText(username);
                *//*for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    String profile=dataSnapshot1.getValue().toString();
                    Glide.with(getContext()).load(profile).into(setProfile);
                }*//*
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        /*d.child("USERS").child(user_id).child("POSTS").addValueEventListener(new ValueEventListener() {
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
        });*/
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
