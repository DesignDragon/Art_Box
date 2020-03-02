package com.example.ArtBox;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class sign_in extends AppCompatActivity {
    private FirebaseAuth authenticate;
    private EditText Editemail;
    private EditText Editpass;
    private ImageView login;
    private TextView signup;
    private String email="";
    private String pass="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signup=(TextView) findViewById(R.id.signup);
        authenticate=FirebaseAuth.getInstance();
        login=findViewById(R.id.login);
        Editemail=(EditText) findViewById(R.id.emailid);
        Editpass=(EditText) findViewById(R.id.pass);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_user();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(sign_in.this,sign_up.class));
            }
        });
    }
    private void login_user()
    {
        email=Editemail.getText().toString();
        pass=Editpass.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please Enter Email",Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(pass))
        {
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_LONG).show();
            return;
        }
        authenticate.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(sign_in.this, side_menu.class));
                    finish();
                }
                else
                {
                    Toast.makeText(sign_in.this,"Fail",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}