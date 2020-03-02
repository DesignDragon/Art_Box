package com.example.ArtBox;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class Message extends Fragment {
    private FloatingActionButton fab;

    public Message() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_message, container, false);
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
}
