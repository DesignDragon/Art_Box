package com.example.ArtBox;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private FirebaseUser user,current_user;
    private FirebaseAuth authenticate;
    private ImageView signup;
    private TextView signin;
    private ImageView go;
    private GoogleApiClient client;
    private SignInButton login;
    private FirebaseFirestore db;
    private static final int REQ_CODE=9001;
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(getApplicationContext());
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        if (current_user != null) {
            Intent i3 = new Intent(MainActivity.this, side_menu.class);
            startActivity(i3);
            finish();
        }
    }



    @Override
    protected void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login=(SignInButton) findViewById(R.id.google);
        GoogleSignInOptions options= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        client=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,options)
                .build();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent= Auth.GoogleSignInApi.getSignInIntent(client);
                startActivityForResult(signIntent,REQ_CODE);
            }
        });


        signup = (ImageView) findViewById(R.id.gotosignup);
        FirebaseApp.initializeApp(getApplicationContext());
        authenticate=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = authenticate.getCurrentUser();
              /* if(user!=null)
               {
                   Intent i1=new Intent(MainActivity.this,home_screen.class);
                   startActivity(i1);
                   finish();
               }*/
                if (user == null) {
                    Intent i2 = new Intent(MainActivity.this, sign_up.class);
                    startActivity(i2);
                    finish();
                }
            }
        });

        /*signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, sign_in.class);
                startActivity(i);
                finish();
            }
        });*/

    }

   /* public void checkCurrentUser()
    {
        user=authenticate.getCurrentUser();
        if(user!=null)
        {
            //go=(ImageView)findViewById(R.id.gotosignup);
            //go.setImageResource(R.drawable.go);

        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_CODE)
        {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }

    private void handleResult(GoogleSignInResult taskComplete)
    {
        if(taskComplete.isSuccess())
        {
            GoogleSignInAccount acc=taskComplete.getSignInAccount();
            String uid=acc.getId().toString();
            String email=acc.getEmail().toString();
            String username=acc.getDisplayName().toString();
            String url=acc.getPhotoUrl().toString();
            HashMap<String,Object> data=new HashMap<>();
            data.put("uid",uid);
            data.put("username",username);
            data.put("email",email);
            data.put("url",url);
            db.collection("USERS").document(uid).set(data);
            //FirebaseGoogleAuth(acc);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential= GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        authenticate.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this,"Successful",Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(MainActivity.this,"Unsuccessful",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
