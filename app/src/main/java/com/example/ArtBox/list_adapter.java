package com.example.ArtBox;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class list_adapter extends RecyclerView.Adapter<list_adapter.MyViewHolder>
{
    private ArrayList<userProfileData> data_set;
    private Context context;

    public list_adapter(ArrayList<userProfileData> data,Context c){
        context=c;
        data_set=data;
    }

    @NonNull
    @Override
    public list_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.searchdata,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull list_adapter.MyViewHolder holder, int position) {

        holder.txt.setText(data_set.get(position).getUsername());
        String prof=data_set.get(position).getUrl();
        if(prof==null)
            Glide.with(context).load(R.drawable.profile2).into(holder.imageView);
        else
            Glide.with(context).load(prof).into(holder.imageView);
        final String uid=data_set.get(position).getId();
        holder.txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle=new Bundle();
                bundle.putString("searched_user",uid);
                Log.d("receive",uid.toString());
                user_profile_fragment f= new user_profile_fragment();
                f.setArguments(bundle);

                FragmentManager manager=((AppCompatActivity)context).getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.frag_container,f).addToBackStack(null).commit();
            }
        });

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        View v;
        TextView txt;
        CircularImageView imageView;
        public MyViewHolder(View t){
            super(t);
            v=t;
            txt=(TextView) v.findViewById(R.id.sdata);
            imageView=(CircularImageView) v.findViewById(R.id.search_profile);
        }
    }


    @Override
    public int getItemCount() {
        return data_set.size();
    }
}
