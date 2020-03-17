package com.example.ArtBox;

import android.os.Bundle;

import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


public class progress extends Fragment {
    private ProgressBar bar;
    private Handler handler= new Handler();
    public progress() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_progress, container, false);
        bar=(ProgressBar) v.findViewById(R.id.progress);
        new Thread(new Runnable() {
            @Override
            public void run() {
                bar.setVisibility(View.VISIBLE);
            }
        }).start();
        try{
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return v;
    }
}
