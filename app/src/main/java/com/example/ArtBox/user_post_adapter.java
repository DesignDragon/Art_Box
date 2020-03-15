package com.example.ArtBox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class user_post_adapter extends RecyclerView.Adapter<user_post_adapter.post_view_holder> {

    private ArrayList<userPosts> post_data;
    FragmentManager fragmentManager;
    public class post_view_holder extends RecyclerView.ViewHolder {
        /*public View v;*/
        public ImageView image_view;
        public post_view_holder(View t)
        {
            super(t);
            image_view=(ImageView) t.findViewById(R.id.imageView);
            /*cv=(CardView) t.findViewById(R.id.post_card);*/
        }
    }
    public user_post_adapter(ArrayList<userPosts> data,FragmentManager fragmentManager)
    {
        post_data=data;
        this.fragmentManager=fragmentManager;
        Collections.sort(data, new Comparator<userPosts>() {
            @Override
            public int compare(userPosts o1, userPosts o2) {
                return o1.getUploadTime().compareTo(o2.getUploadTime());
            }
        });
    };
    @NonNull
    @Override
    public user_post_adapter.post_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_posts,parent,false);
        return new post_view_holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final post_view_holder holder, final int position) {
        Glide.with(holder.itemView).load(post_data.get(position).getUrl()).into(holder.image_view);
        holder.image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent i= new Intent(context, post_enlarge.class);
                i.putExtra("post",post_data.get(position).getUrl());
                context.startActivity(i);*/
                Bundle b=new Bundle();
                b.putString("post",post_data.get(position).getUrl());
                b.putString("id",post_data.get(position).getUserID());
                b.putString("post_id",post_data.get(position).getPost_id());
                enlarge_post_fragment f=new enlarge_post_fragment();
                f.setArguments(b);

                f.show(fragmentManager,"pst_sent");
               /* FragmentManager fragmentManager=f.getFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container,f);
                fragmentTransaction.commit();*/
//                user_profile_fragment fragment=new user_profile_fragment();

//                context.getFragmentManager().beginTransaction().replace(R.id.frag_container,f).commit();
            }
        });
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
