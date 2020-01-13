package com.example.artbox;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class user_auction_fragment extends Fragment {
    ImageView i;
    public user_auction_fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_user_auction_fragment, container, false);
        i=(ImageView) v.findViewById(R.id.img_auct);
        String post_auct=getArguments().getString("auct");
        Glide.with(v.getContext()).load(post_auct).into(i);
        return v;
    }
}
