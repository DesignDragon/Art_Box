package com.example.ArtBox;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class upload_picture extends AppCompatActivity {

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private FirebaseAuth firebaseAuth;
    private Uri imageUri;
    private Bitmap compress;
    private ImageView uploadImg;
    private Button submit;
    private EditText description;
    private ProgressBar pb;
    private byte[] imgData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);


        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        uploadImg = (ImageView) findViewById(R.id.image_store);
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        pb = findViewById(R.id.progress);
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        submit = (Button) findViewById(R.id.upload);
        description = (EditText) findViewById(R.id.description);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String image_caption = description.getText().toString();
                if(image_caption.isEmpty())
                    Toast.makeText(upload_picture.this,"Discreption cannot be empty. Please write description.",Toast.LENGTH_LONG).show();
                if(imageUri==null)
                    Toast.makeText(upload_picture.this,"Please select an image!!!",Toast.LENGTH_LONG).show();
                if (!TextUtils.isEmpty(image_caption) && imageUri != null) {
                    pb.setVisibility(View.VISIBLE);
                    File newFile = new File(imageUri.getPath());
                    UploadTask imgPath = storageReference.child(user_id).child(description.getText().toString() + ".jpg").putFile(Uri.fromFile(newFile));
                    imgPath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                storeData(task);
                                startActivity(new Intent(upload_picture.this, side_menu.class));
                                finish();
                            } else {
                                Toast.makeText(upload_picture.this, "Somethng went wrong", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void storeData(Task<UploadTask.TaskSnapshot> task) {

        if (task != null) {
            try {
                task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {

                        firebaseFirestore.collection("USERS").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String name = documentSnapshot.getString("username").toString();
                                Map<String, Object> userData = new HashMap<>();
                                final String post_id = UUID.randomUUID().toString();
                                String uploadDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                String uploadTime = DateFormat.getDateTimeInstance().format(new Date());
                                userData.put("url", uri.toString());
                                userData.put("caption", description.getText().toString());
                                userData.put("uploadDate", uploadDate);
                                userData.put("uploadTime", uploadTime);
                                userData.put("post_id", post_id);
                                userData.put("username", name);
                                userData.put("userID", user_id);
                                firebaseFirestore.collection("USERS").document(user_id).collection("POSTS").document(post_id).set(userData);
                            }
                        });

                       /* DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child("USERS").child(user_id).child("POSTS").child(description.getText().toString()).setValue(userData);*/
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

    public void chooseImage() {
        CropImage.startPickImageActivity(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri img = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, img)) {
                imageUri = img;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                startCropImageActivity(img);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                uploadImg.setImageURI(result.getUri());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (imageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCropImageActivity(imageUri);
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCropImageActivity(Uri u) {
        CropImage.activity(u)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
        imageUri = u;
    }
}
