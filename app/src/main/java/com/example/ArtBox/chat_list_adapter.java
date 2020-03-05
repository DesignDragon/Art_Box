package com.example.ArtBox;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class chat_list_adapter extends RecyclerView.Adapter<chat_list_adapter.ViewHolder> {
    private ArrayList<userProfileData> data_set;
    private Context context;
    private FragmentManager fragmentManager;
    FirebaseFirestore db;
    chat_list_adapter(ArrayList<userProfileData> data,Context c,FragmentManager manager)
    {
        data_set=data;
        context=c;
        fragmentManager=manager;
    }
    @NonNull
    @Override
    public chat_list_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.searchdata,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull chat_list_adapter.ViewHolder holder, final int position) {
        holder.txt.setText(data_set.get(position).getUsername());
        Glide.with(context).load(data_set.get(position).getUrl()).into(holder.imageView);
        final String uid=data_set.get(position).getId();
        holder.txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b=new Bundle();
                b.putString("cname",data_set.get(position).getUsername());
                b.putString("cimg",data_set.get(position).getUrl());
                b.putString("cid",data_set.get(position).getId());
                new_chat chat=new new_chat();
                chat.setArguments(b);
                fragmentManager.beginTransaction().replace(R.id.frag_container,chat).addToBackStack(null).commit();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        View v;
        TextView txt;
        CircularImageView imageView;
        public ViewHolder(View item)
        {
            super(item);
            v=item;
            txt=(TextView) v.findViewById(R.id.sdata);
            imageView=(CircularImageView) v.findViewById(R.id.search_profile);
        }
    }

    @Override
    public int getItemCount() {
        return data_set.size();
    }
}
