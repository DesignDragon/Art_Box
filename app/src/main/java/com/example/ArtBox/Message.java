package com.example.ArtBox;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Message extends Fragment {
    private FloatingActionButton fab;
    private RecyclerView msgRecycler;
    public RecyclerView.Adapter adapter;
    private ArrayList<String> user_list;
    private ArrayList<userProfileData> user;
    DatabaseReference db;
    FirebaseAuth auth;
    FirebaseFirestore firedb;
    public Message() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_message, container, false);

        msgRecycler=view.findViewById(R.id.chat_recycler);
        msgRecycler.setHasFixedSize(true);


        user_list=new ArrayList<String>();
        auth= FirebaseAuth.getInstance();
        final String sender_uid=auth.getUid().toString();
        db= FirebaseDatabase.getInstance().getReference("CHATS");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_list.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    MessagePack pack=ds.getValue(MessagePack.class);
                    if(pack.getSender().equals(sender_uid))
                    {
                        user_list.add(pack.getReceiver());
                    }

                }

                firedb=FirebaseFirestore.getInstance();
                for(String i : user_list)
                {
                    user=new ArrayList<userProfileData>();
                    firedb.collection("USERS").document(sender_uid).collection("FOLLOWING").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            user.clear();
                            for(QueryDocumentSnapshot i : queryDocumentSnapshots)
                            {
                                userProfileData d = i.toObject(userProfileData.class);
                                d.getUsername();
                                d.getId();
                                user.add(d);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                    adapter=new chat_list_adapter(user,getContext(),getActivity().getSupportFragmentManager());
                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                    msgRecycler.setLayoutManager(linearLayoutManager);
                    msgRecycler.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab=(FloatingActionButton) view.findViewById(R.id.new_msg);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chats c= new Chats();
                getFragmentManager().beginTransaction().replace(R.id.frag_container,c).addToBackStack(null).commit();
            }
        });

        return view;
    }

    private void readChat(){

    }
}
