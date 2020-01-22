package com.example.artbox;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class user_auction_fragment extends Fragment {
    ImageView i;
    EditText desc;
    EditText init_price;
    Button submit;
    private String user_id;
    private FirebaseAuth firebaseAuth;
    private String info,amt;
    public user_auction_fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_user_auction_fragment, container, false);

        firebaseAuth= FirebaseAuth.getInstance();
        user_id= firebaseAuth.getCurrentUser().getUid();
        i=(ImageView) v.findViewById(R.id.img_auct);
        final String post_auct=getArguments().getString("auct");
        Glide.with(v.getContext()).load(post_auct).into(i);
        desc=(EditText) v.findViewById(R.id.post_desc);
        info=desc.getText().toString();
        init_price=(EditText) v.findViewById(R.id.init_price);
        amt=init_price.getText().toString();
        submit=(Button) v.findViewById(R.id.submit_auct);
        try{
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String,Object> data=new HashMap<>();
                    //data.put("image",post_auct.toString());
                    data.put("DETAILS",desc);
                    data.put("PRICE",amt);
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child("AUCTION").setValue(data);
                }
            });
        }
        catch (Exception e)
        {
            Log.d("err",e.toString());
        }

        return v;
    }
}
