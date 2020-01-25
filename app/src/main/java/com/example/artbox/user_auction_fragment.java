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
    EditText price;
    Button submit;
    FirebaseFirestore firebaseFirestore;
    private String user_id;
    private FirebaseAuth firebaseAuth;
    public user_auction_fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_user_auction_fragment, container, false);

        firebaseAuth= FirebaseAuth.getInstance();
        user_id= firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore=FirebaseFirestore.getInstance();
        i=(ImageView) v.findViewById(R.id.img_auct);
        final String post_auct=getArguments().getString("auct");
        Glide.with(v.getContext()).load(post_auct).into(i);
        desc=(EditText) v.findViewById(R.id.auct_desc);
        price=(EditText) v.findViewById(R.id.init_price);
//        final String info=desc.getText().toString();
//        final String amt=price.getText().toString();
        submit=(Button) v.findViewById(R.id.submit_auct);
        try{
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String,Object> data=new HashMap<>();
                    //data.put("image",post_auct.toString());
                    data.put("POST",post_auct);
                    data.put("DETAILS",desc.getText().toString());
                    data.put("PRICE",price.getText().toString());
                   firebaseFirestore.collection("AUCTION").add(data);
                   getFragmentManager().beginTransaction().replace(R.id.frag_container,new user_profile_fragment()).addToBackStack(null).commit();
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
