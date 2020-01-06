package com.example.artbox;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class user_post_adapter extends RecyclerView.Adapter<user_post_adapter.post_view_holder> {

    private ArrayList<userPosts> post_data;
    public class post_view_holder extends RecyclerView.ViewHolder {
        public View v;
        public ImageView image_view;

        public post_view_holder(View t)
        {
            super(t);
            image_view=(ImageView) t.findViewById(R.id.imageView);

        }
    }
    public user_post_adapter(ArrayList<userPosts> data)
    {
        post_data=data;
    };
    @NonNull
    @Override
    public user_post_adapter.post_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_posts,parent,false);
        return new post_view_holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull post_view_holder holder, int position) {
        Glide.with(holder.itemView).load(post_data.get(position).getUrl()).into(holder.image_view);
    }

    @Override
    public int getItemCount() {
        return post_data.size();
    }
    public void update(ArrayList<userPosts> datalist){
        this.post_data=datalist;
        notifyDataSetChanged();
    }
}
