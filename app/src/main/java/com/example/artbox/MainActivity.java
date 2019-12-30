package com.example.artbox;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser user,current_user;
    private FirebaseAuth authenticate;
    private ImageView signup;
    private TextView signin;
    private ImageView go;

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
        signup = (ImageView) findViewById(R.id.gotosignup);
        signin = (TextView) findViewById(R.id.gotosignin);
        FirebaseApp.initializeApp(getApplicationContext());
        authenticate=FirebaseAuth.getInstance();
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
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, sign_in.class);
                startActivity(i);
                finish();
            }
        });

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
}
