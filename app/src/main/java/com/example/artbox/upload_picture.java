package com.example.artbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class upload_picture extends AppCompatActivity {

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private FirebaseAuth firebaseAuth;
    private Uri imageUri = null;
    private Bitmap compress;
    private ImageView upload;
    private Button submit;
    private EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);
        firebaseAuth= FirebaseAuth.getInstance();
        user_id= firebaseAuth.getCurrentUser().getUid();
        upload= (ImageView) findViewById(R.id.image_store);
        firebaseFirestore= FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(ContextCompat.checkSelfPermission(upload_picture.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(upload_picture.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(upload_picture.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
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
        submit= (Button) findViewById(R.id.upload);
        description=(EditText) findViewById(R.id.description);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String image_caption=description.getText().toString();
                if(!TextUtils.isEmpty(image_caption)&&imageUri!=null)
                {
                    File newFile= new File(imageUri.getPath());
                    try {
                        compress = new Compressor(upload_picture.this).setMaxHeight(125).setMaxWidth(125).setQuality(50).compressToBitmap(newFile);
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    compress.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] imgData = byteArrayOutputStream.toByteArray();
                    UploadTask imgPath = storageReference.child(user_id).child(description.getText().toString() +".jpg").putBytes(imgData);
                    imgPath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storeData(task);
                                startActivity(new Intent(upload_picture.this,side_menu.class));
                            } else {
                                Toast.makeText(upload_picture.this, "Somethng went wrong", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void storeData(Task<UploadTask.TaskSnapshot> task)
    {
        Uri download_uri;
        if (task != null) {
            download_uri = task.getResult().getUploadSessionUri();
        } else {
            download_uri = imageUri;
        }
        Map<String, Object> userData = new HashMap<>();
        userData.put("url", download_uri.toString());
        userData.put("caption",description.getText().toString());
        //firebaseFirestore.collection("USERS").document(user_id).collection("POSTS").add(userData);
        DatabaseReference db=FirebaseDatabase.getInstance().getReference();
        db.child("USERS").child(user_id).child("POSTS").child(description.getText().toString()).setValue(userData);
    }

    private void chooseImage()
    {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(upload_picture.this);
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
                    upload.setImageBitmap(b);
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
