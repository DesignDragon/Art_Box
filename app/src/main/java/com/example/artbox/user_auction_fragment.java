package com.example.artbox;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class user_auction_fragment extends Fragment {
    ImageView i;
    EditText desc;
    EditText price;
    EditText hr;
    EditText min;
    Button submit;
    FirebaseFirestore firebaseFirestore;
    private String user_id;
    private FirebaseAuth firebaseAuth;
    public user_auction_fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_user_auction_fragment, container, false);

        firebaseAuth= FirebaseAuth.getInstance();
        user_id= firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore=FirebaseFirestore.getInstance();
        i=(ImageView) v.findViewById(R.id.img_auct);
        final String post_auct=getArguments().getString("auct");
        Glide.with(v.getContext()).load(post_auct).into(i);
        desc=(EditText) v.findViewById(R.id.auct_desc);
        price=(EditText) v.findViewById(R.id.init_price);
//        final String info=desc.getText().toString();
//        final String amt=price.getText().toString();

        hr=(EditText) v.findViewById(R.id.hour);
        hr.setFilters(new InputFilter[]{new MinMaxHour("1","7")});
        min=(EditText) v.findViewById(R.id.minute);
        min.setFilters(new InputFilter[]{new MinMaxMinute("0","59")});

        submit=(Button) v.findViewById(R.id.submit_auct);
        try{
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String,Object> data=new HashMap<>();
                    //data.put("image",post_auct.toString());
                    data.put("post",post_auct);
                    data.put("details",desc.getText().toString());
                    data.put("price",price.getText().toString());
                    data.put("hour",hr.getText().toString());
                    data.put("min",min.getText().toString());

                   firebaseFirestore.collection("AUCTION").add(data);
                   getFragmentManager().beginTransaction().replace(R.id.frag_container,new user_profile_fragment()).addToBackStack(null).commit();
                }
            });
        }
        catch (Exception e)
        {
            Log.d("err",e.toString());
        }
        return v;
    }
}
class MinMaxHour implements InputFilter
{
    private int minHr,maxHr;
    public MinMaxHour(int minHour,int maxHour)
    {
        this.minHr=minHour;
        this.maxHr=maxHour;
    }

    public MinMaxHour(String minHour,String maxHour)
    {
        this.minHr= Integer.parseInt(minHour);
        this.maxHr=Integer.parseInt(maxHour);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
      try {
          int input=Integer.parseInt(dest.toString()+source.toString());
          if(isInRange(minHr,maxHr,input)){
              return null;
          }
      }catch (NumberFormatException e){}
      return "";
    }
    private boolean isInRange(int a,int b,int c)
    {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
class MinMaxMinute implements InputFilter
{
    private int minHr,maxHr;
    public MinMaxMinute(int minHour,int maxHour)
    {
        this.minHr=minHour;
        this.maxHr=maxHour;
    }

    public MinMaxMinute(String minHour,String maxHour)
    {
        this.minHr= Integer.parseInt(minHour);
        this.maxHr=Integer.parseInt(maxHour);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input=Integer.parseInt(dest.toString()+source.toString());
            if(isInRange(minHr,maxHr,input)){
                return null;
            }
        }catch (NumberFormatException e){}
        return "";
    }
    private boolean isInRange(int a,int b,int c)
    {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
