package com.example.artbox;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;




public class progress_bar extends Fragment {

    private ContentLoadingProgressBar bar;
    private Handler handler= new Handler();

    public progress_bar() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_progress_bar, container, false);

        bar=(ContentLoadingProgressBar) v.findViewById(R.id.progress_bar);
        new Thread(new Runnable() {
            @Override
            public void run() {
               bar.show();
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
