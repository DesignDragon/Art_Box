package com.example.ArtBox;

import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class user_auction_fragment extends Fragment {
    ImageView i;
    EditText desc;
    EditText price;
    EditText hr;
    EditText min;
    Button submit;
    EditText title;
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
        title=(EditText) v.findViewById(R.id.auct_title);
        desc=(EditText) v.findViewById(R.id.auct_desc);
        price=(EditText) v.findViewById(R.id.init_price);
//        final String info=desc.getText().toString();
//        final String amt=price.getText().toString();

        hr=(EditText) v.findViewById(R.id.hour);
        hr.setFilters(new InputFilter[]{new MinMaxHour("0","7")});
        min=(EditText) v.findViewById(R.id.minute);
        min.setFilters(new InputFilter[]{new MinMaxMinute("0","59")});
        final String current_time= String.valueOf(System.currentTimeMillis());
        final String auction_id=UUID.randomUUID().toString();
        submit=(Button) v.findViewById(R.id.submit_auct);
        try{
            submit.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    Map<String,Object> data=new HashMap<>();
                    //data.put("image",post_auct.toString());
                    String uploadDate= DateFormat.getDateTimeInstance().format(new Date());
                    data.put("uid",user_id.toString());
                    data.put("post",post_auct);
                    data.put("title",title.getText().toString());
                    data.put("details",desc.getText().toString());
                    data.put("price",price.getText().toString());
                    data.put("hour",hr.getText().toString());
                    data.put("min",min.getText().toString());
                    data.put("uploadTime",current_time);
                    data.put("auctionId",auction_id);
                    data.put("uploadDate",uploadDate);
                   firebaseFirestore.collection("USERS").document(user_id).collection("AUCTION").document(auction_id).set(data);
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
