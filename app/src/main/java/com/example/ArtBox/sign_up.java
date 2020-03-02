package com.example.ArtBox;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class sign_up extends AppCompatActivity {
    private static final String USERNAME ="username";
    private static final String EMAIL="email";
    private static final String UID="id";
    private static final String url="url";
    private FirebaseAuth authenticate;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String id="";
    private EditText EditName;
    private EditText Editemail;
    private EditText Editpass;
    private ImageView s_up;
    private ImageView setProfile;
    Uri imageUri=null;
    private Bitmap compress;
    Context context;
    private byte[] imgData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        authenticate = FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        s_up=findViewById(R.id.registeraccount);
        db=FirebaseFirestore.getInstance();
        EditName=(EditText) findViewById(R.id.fname);
        Editemail=(EditText) findViewById(R.id.email);
        Editpass=(EditText)findViewById(R.id.password);
        s_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
        TextView tv1 = (TextView) findViewById(R.id.signin);
        tv1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i3 = new Intent(sign_up.this, sign_in.class);
                startActivity(i3);
            }
        });
    }

    public void Users(String tname,String temail)
    {
        String name=tname;
        String email=temail;
        id=FirebaseAuth.getInstance().getUid();

        Uri u=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources().getResourcePackageName(R.drawable.profile)+'/'+getResources().getResourceTypeName(R.drawable.profile)+'/'+getResources().getResourceEntryName(R.drawable.profile) );
        if(u!=null) {
            File newFile = new File(u.getPath());
            UploadTask imgPath = storageReference.child(id).child("profile.jpg").putFile(u);
            imgPath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("store","image stored");
                    }
                }
            });
        }

        context=getApplicationContext();
        Map<String, Object> user=new HashMap<>();
        user.put(USERNAME,name);
        user.put(EMAIL,email);
        user.put(UID,id);
        user.put(url,u.toString());
        DocumentReference DocRef=db.collection("USERS").document(id);
        DocRef.set(user);
        /* DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("USERS").child(id).child("DETAILS").setValue(user);*/
//        db.collection("USERS").document("user_names").set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(sign_up.this,"successful",Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(sign_up.this,"error",Toast.LENGTH_SHORT).show();
//            }
//        });
    }
    private void createAccount ()
    {
        final String email = Editemail.getText().toString().trim();
        final String pass = Editpass.getText().toString().trim();
        final String name = EditName.getText().toString().trim();

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Please Enter Email or Password", Toast.LENGTH_LONG).show();
            return;
        }


        authenticate.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Users(name,email);
                    Toast.makeText(sign_up.this, "Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(sign_up.this, side_menu.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(sign_up.this, "FAIL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
