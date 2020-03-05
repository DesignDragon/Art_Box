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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class Message_adapter extends RecyclerView.Adapter<Message_adapter.ViewHolder> {
    public static final int MSG_LEFT=0;
    public static final int MSG_RIGHT=1;
    private ArrayList<MessagePack> data_set;
    private Context context;
    private FragmentManager fragmentManager;

    Message_adapter(ArrayList<MessagePack> data,Context c,FragmentManager manager)
    {
        data_set=data;
        context=c;
        fragmentManager=manager;
    }
    @NonNull
    @Override
    public Message_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_RIGHT)
        {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.right_msgitem,parent,false);
            return new ViewHolder(view);
        }
        else{
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.left_msgitem,parent,false);
            return new ViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull Message_adapter.ViewHolder holder, final int position) {
        holder.sender_msg.setText(data_set.get(position).getMessage());
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        View v;
        TextView sender_msg;
        public ViewHolder(View item)
        {
            super(item);
            v=item;
            sender_msg=v.findViewById(R.id.sender_msg);
        }
    }

    @Override
    public int getItemViewType(int position) {
       if(data_set.get(position).getSender().equals(FirebaseAuth.getInstance().getUid().toString()))
       {
           return MSG_RIGHT;
       }
       else {
           return MSG_LEFT;
       }
    }

    @Override
    public int getItemCount() {
        return data_set.size();
    }

    public void update(ArrayList<MessagePack> dataSet)
    {
        this.data_set=dataSet;
        notifyDataSetChanged();
    }
}

