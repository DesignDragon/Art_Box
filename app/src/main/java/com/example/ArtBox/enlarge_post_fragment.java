package com.example.ArtBox;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;


public class enlarge_post_fragment extends DialogFragment {

    private ImageView img;
    private ImageButton auct;
    private FirebaseAuth firebaseAuth;
    private String user_id;
    private ImageButton like;
    private ImageButton delete;
    private FirebaseFirestore firebaseFirestore;
    AlertDialog.Builder builder;
    public enlarge_post_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_enlarge_post_fragment, container, false);
        img=(ImageView) v.findViewById(R.id.enlarge_posts);
        like=(ImageButton) v.findViewById(R.id.like_post);
        delete=(ImageButton) v.findViewById(R.id.delete_post);

        builder=new AlertDialog.Builder(getContext());
        /* Intent i=getIntent();
        final String post=i.getExtras().getString("post");*/
       final String post=getArguments().getString("post");
       final String id=getArguments().getString("id");
       final String post_id=getArguments().getString("post_id");
        Glide.with(this).load(post).into(img);
        firebaseAuth= FirebaseAuth.getInstance();
        user_id=firebaseAuth.getUid().toString();


        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseFirestore.collection("USERS").document(id).
                collection("POSTS").document(post_id).
                collection("LIKES").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot s:queryDocumentSnapshots)
                {
                    if(user_id.equals(s.getId()))
                    {
                        like.setImageResource(R.drawable.like);
                    }
                }
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String,String> data= new HashMap<>();
                data.put("id",user_id);
                firebaseFirestore.collection("USERS").document(id).
                        collection("POSTS").document(post_id).
                        collection("LIKES").document(user_id).set(data);
                        like.setImageResource(R.drawable.like);
            }
        });



        auct=(ImageButton) v.findViewById(R.id.send_auction);
        if(id.equals(user_id))
        {

            auct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("auct",post);
                    user_auction_fragment f = new user_auction_fragment();
                    f.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.frag_container,f).addToBackStack(null).commit();
                    dismiss();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.setMessage("Are you sure you want to delete?").
                            setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    firebaseFirestore.collection("USERS").document(user_id).collection("POSTS").document(post_id).delete();
                                    Toast.makeText(getContext(),"Post deleted Successfully",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert=builder.create();
                    alert.setTitle("Log out?");
                    alert.show();
                }
            });
        }
        else{
            auct.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
