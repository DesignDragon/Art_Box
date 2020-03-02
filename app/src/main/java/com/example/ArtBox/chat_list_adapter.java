package com.example.ArtBox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class chat_list_adapter extends RecyclerView.Adapter<chat_list_adapter.ViewHolder> {
    private ArrayList<userProfileData> data_set;
    private Context context;
    chat_list_adapter(ArrayList<userProfileData> data,Context c)
    {
        data_set=data;
        context=c;
    }
    @NonNull
    @Override
    public chat_list_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.searchdata,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull chat_list_adapter.ViewHolder holder, int position) {
        holder.txt.setText(data_set.get(position).getUsername());
        Glide.with(context).load(data_set.get(position).getUrl()).into(holder.imageView);
        final String uid=data_set.get(position).getId();
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
