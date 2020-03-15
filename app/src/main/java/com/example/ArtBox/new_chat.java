package com.example.ArtBox;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;


public class new_chat extends Fragment {
    private CircularImageView img;
    private TextView uname;
    private Toolbar toolbar;
    private ImageButton sendButton;
    private RecyclerView msgRecycler;
    public Message_adapter adapter;
    private ArrayList<MessagePack> data;
    private EditText msg;
    DatabaseReference db;
    FirebaseAuth auth;
    public new_chat() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_new_chat, container, false);
        toolbar=(androidx.appcompat.widget.Toolbar) v.findViewById(R.id.chat_toolbar);
        msgRecycler=(RecyclerView) v.findViewById(R.id.message_recycler);
        msgRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        msgRecycler.setLayoutManager(linearLayoutManager);
        data=new ArrayList<MessagePack>();
        adapter = new Message_adapter(data,getContext(), getActivity().getSupportFragmentManager());
        msgRecycler.setAdapter(adapter);

        img=(CircularImageView) v.findViewById(R.id.chat_img);
        uname=(TextView) v.findViewById(R.id.chat_name);
        String uprof=getArguments().getString("cimg");
        if(uprof==null)
            Glide.with(getContext()).load(R.drawable.profile2).into(img);
        else
            Glide.with(getContext()).load(uprof).into(img);
        uname.setText(getArguments().getString("cname").toString());

        auth=FirebaseAuth.getInstance();
        final String receiver_uid=getArguments().getSerializable("cid").toString();
        final String sender_uid=auth.getUid().toString();
        sendButton=(ImageButton) v.findViewById(R.id.send);
        msg=(EditText) v.findViewById(R.id.new_msg);



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipMsg=msg.getText().toString().trim();
                if(TextUtils.isEmpty(ipMsg))
                {
                    Toast.makeText(getContext(),"Can't send empty message",Toast.LENGTH_SHORT).show();
                }
                else {
                    sendMessage(sender_uid,receiver_uid,ipMsg);
                }
                msg.setText("");
            }
        });
        retriveMessage(sender_uid,receiver_uid);
        return v;
    }

    private void sendMessage(String sender,String receiver,String message)
    {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        db = FirebaseDatabase.getInstance().getReference();
        db.child("CHATS").push().setValue(hashMap);
    }

    protected void retriveMessage(final String senderId, final String receiverId)
    {
        db = FirebaseDatabase.getInstance().getReference("CHATS");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for(DataSnapshot s:dataSnapshot.getChildren())
                {
                    MessagePack pack=s.getValue(MessagePack.class);
                    if(pack.getReceiver().equals(senderId) &&  pack.getSender().equals(receiverId) ||
                    pack.getReceiver().equals(receiverId) && pack.getSender().equals(senderId))
                    {
                        Log.d("receive",pack.getMessage());
                        data.add(pack);
                    }
                }
                adapter.update(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        db.collection("USERS").document(senderId).collection("CHATS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot documentSnapshot) {
//                for(QueryDocumentSnapshot s:documentSnapshot)
//                {
//                    MessagePack pack=s.toObject(MessagePack.class);
//                    if(pack.getReceiver().equals(senderId) &&  pack.getSender().equals(receiverId) ||
//                    pack.getReceiver().equals(receiverId) && pack.getSender().equals(senderId))
//                    {
//                        Log.d("receive",pack.getMessage());
//                        data.add(pack);
//                    }
//                }
//                adapter.update(data);
//
//
//            }
//        });
    }
}
