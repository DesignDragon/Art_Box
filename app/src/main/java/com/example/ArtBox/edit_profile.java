package com.example.ArtBox;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class edit_profile extends AppCompatActivity {

    private Uri imageUri = null;
    private Bitmap compress;
    public ImageView change_picture;
    private byte[] imgData;
    private Button update;
    private EditText uname;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firebaseFirestore= FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseAuth= FirebaseAuth.getInstance();
        user_id= firebaseAuth.getCurrentUser().getUid();
        uname=(EditText) findViewById(R.id.username);
        change_picture=(ImageView) findViewById(R.id.change_profile);

        firebaseFirestore.collection("USERS").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String profile=documentSnapshot.getString("url");
                if(profile==null)
                    Glide.with(getApplicationContext()).load(R.drawable.profile2).into(change_picture);
                else
                    Glide.with(getApplicationContext()).load(documentSnapshot.getString("url").toString()).into(change_picture);
                uname.setText(documentSnapshot.getString("username"));
            }
        });

        change_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        update=(Button) findViewById(R.id.update_data);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name=uname.getText().toString();
                if(imageUri!=null) {
                    File newFile = new File(imageUri.getPath());
                    try {
                        compress = new Compressor(edit_profile.this).setMaxHeight(125).setMaxWidth(125).setQuality(50).compressToBitmap(newFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    compress.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    imgData = byteArrayOutputStream.toByteArray();
                    UploadTask imgPath = storageReference.child(user_id).child("profile.jpg").putBytes(imgData);
                    imgPath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storeData(task,name);
                            } else {
                                Toast.makeText(edit_profile.this, "Somethng went wrong", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    Log.d("fail","update fail");
                }
                startActivity(new Intent(edit_profile.this,side_menu.class));
                finish();
            }
        });

    }


    private void storeData(Task<UploadTask.TaskSnapshot> task,String n) {

        if (task != null || n!=null)  {
            try {
                final String name=n;
                task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("url", uri.toString());
                        userData.put("username",name);
                        firebaseFirestore.collection("USERS").document(user_id).update(userData);
                       /* DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child("USERS").child(user_id).child("DETAILS").updateChildren(userData);*/

                    }
                });
            } catch (Exception e) {
                Log.d("ee", e.toString());
            }

//        Map<String, Object> userData = new HashMap<>();
//        userData.put("url", download_uri.toString());
//        userData.put("caption",description.getText().toString());
//        //firebaseFirestore.collection("USERS").document(user_id).collection("POSTS").add(userData);
//        DatabaseReference db=FirebaseDatabase.getInstance().getReference();
//        db.child("USERS").child(user_id).child("POSTS").child(description.getText().toString()).setValue(userData);
        }
    }

    public void chooseImage()
    {
        CropImage.startPickImageActivity(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            Uri img=CropImage.getPickImageResultUri(this,data);
            if(CropImage.isReadExternalStoragePermissionsRequired(this,img))
            {
                imageUri=img;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }
            else
            {
                startCropImageActivity(img);
            }
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                imageUri=result.getUri();
                change_picture.setImageURI(result.getUri());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(imageUri!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            startCropImageActivity(imageUri);
        }
        else{
            Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
        }
    }

    private void startCropImageActivity(Uri u)
    {
        CropImage.activity(u)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
        imageUri=u;
    }
}
