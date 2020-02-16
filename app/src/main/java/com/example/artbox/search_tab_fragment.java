package com.example.artbox;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class search_tab_fragment extends Fragment {
    private FirebaseFirestore db;
//    private FirebaseDatabase db;
//    private DatabaseReference reference;
    public View view;
    public RecyclerView rv;
    public RecyclerView.Adapter adapter;
    EditText searchuser;
    TextView user;
    private ArrayList<userProfileData> user_data;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_search_tab_fragment, container, false);
     /*  EditText e=(EditText) view.findViewById(R.id.search_box);
       String s=e.getText().toString();
       searchUser(s);*/
        db = FirebaseFirestore.getInstance();
        //db=FirebaseDatabase.getInstance();
        //reference=db.getReference().child("DETAILS");
        rv = (RecyclerView) view.findViewById(R.id.search_recycler_view);
        SearchView searchView = (SearchView) view.findViewById(R.id.search_box);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                searchUser(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchUser(s);
                return true;
            }
        });


        return view;
    }
    protected void searchUser (String s)
    {
        user_data=new ArrayList<userProfileData>();
        db.collection("USERS").whereGreaterThanOrEqualTo("username", s).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("No User Found!!!");
                    return;
                }
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    userProfileData user = doc.toObject(userProfileData.class);
                    Log.d("key", user.getUsername());
                    user_data.add(user);
                }
                adapter.notifyDataSetChanged();
            }
        });
        adapter = new list_adapter(user_data,getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
    }
}




