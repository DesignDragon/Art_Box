package com.example.artbox;

import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        change_picture=(ImageView) findViewById(R.id.change_profile);
        change_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(ContextCompat.checkSelfPermission(edit_profile.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(edit_profile.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(edit_profile.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else
                    {
                        chooseImage();
                    }
                }
                else {
                    chooseImage();
                }
            }
        });

        update=(Button) findViewById(R.id.update_data);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                storeData(task);
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


    private void storeData(Task<UploadTask.TaskSnapshot> task) {

        if (task != null) {
            try {
                task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("url", uri.toString());
                        //firebaseFirestore.collection("USERS").document(user_id).collection("POSTS").add(userData);
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child("USERS").child(user_id).child("PROFILE").setValue(userData);
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
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(edit_profile.this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                imageUri = result.getUri();

                try {
                    Bitmap b= null;
                    b = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                    change_picture.setImageBitmap(b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception exception= result.getError();
            }
        }
    }
}
