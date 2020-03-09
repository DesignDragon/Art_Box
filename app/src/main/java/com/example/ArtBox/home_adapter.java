package com.example.ArtBox;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class home_adapter extends RecyclerView.Adapter<home_adapter.MyViewHolder> {
    private ArrayList<userPosts> post_data;
    FragmentManager fragmentManager;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private FirebaseAuth firebaseAuth;
    Context c;
    public home_adapter(ArrayList<userPosts> data,FragmentManager fragmentManager,Context context)
    {
        this.c=context;
        this.post_data=data;
        this.fragmentManager=fragmentManager;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView name;
        public TextView caption;
        public ImageButton like;
        public TextView like_count;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img=(ImageView) itemView.findViewById(R.id.home_img);
            name=(TextView) itemView.findViewById(R.id.home_uname);
            caption=(TextView) itemView.findViewById(R.id.home_caption);
            like=(ImageButton) itemView.findViewById(R.id.like);
            like_count=(TextView) itemView.findViewById(R.id.like_count);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.home_posts,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.name.setText(post_data.get(position).getUsername());
        Glide.with(holder.itemView).load(post_data.get(position).getUrl()).into(holder.img);
        holder.caption.setText(post_data.get(position).getCaption());

        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        user_id=firebaseAuth.getUid().toString();
        final String uploader_id=post_data.get(position).getUserID();
        final String post_id=post_data.get(position).getPost_id();
        firebaseFirestore.collection("USERS").document(uploader_id).
                collection("POSTS").document(post_id).
                collection("LIKES").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                holder.like_count.setText(String.valueOf(queryDocumentSnapshots.size())+" Likes");
                for(QueryDocumentSnapshot s:queryDocumentSnapshots)
                {
                    if(user_id.equals(s.getId()))
                    {
                        holder.like.setImageResource(R.drawable.like);
                    }
                }
            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String,String> data= new HashMap<>();
                data.put("id",user_id);
                firebaseFirestore.collection("USERS").document(uploader_id).
                        collection("POSTS").document(post_id).
                        collection("LIKES").document(user_id).set(data);
                holder.like.setImageResource(R.drawable.like);
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
