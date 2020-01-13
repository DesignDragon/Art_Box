package com.example.artbox;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class enlarge_post_fragment extends DialogFragment {

    private ImageView img;
    private ImageButton auct;
    public enlarge_post_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_enlarge_post_fragment, container, false);
        img=(ImageView) v.findViewById(R.id.enlarge_posts);
       /* Intent i=getIntent();
        final String post=i.getExtras().getString("post");*/
       final String post=getArguments().getString("post");
        Glide.with(this).load(post).into(img);


        auct=(ImageButton) v.findViewById(R.id.send_auction);
        auct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("auct",post);
                user_auction_fragment f = new user_auction_fragment();
                f.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.frag_container,f).addToBackStack(null).commit();
            }
        });
        return v;
    }
}
