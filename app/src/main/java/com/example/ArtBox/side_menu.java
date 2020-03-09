package com.example.ArtBox;

import
        androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

public class side_menu extends AppCompatActivity {
    BottomNavigationView bottomappbar;
    NavigationView nav;
    Fragment f=null;
    DrawerLayout d;
    CircularImageView profile;
    TextView name;
    FirebaseAuth auth;
    FirebaseFirestore db;
    View header;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);

        Toolbar toolbar=(Toolbar) findViewById(R.id.top_bar);
        d=(DrawerLayout) findViewById(R.id.side_menu);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,d,toolbar,R.string.open,R.string.close);
        d.addDrawerListener(toggle);
        toggle.syncState();
        /*Toolbar toolbar=(Toolbar) findViewById(R.id.top_bar);
        d=(DrawerLayout) findViewById(R.id.side_menu);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.openDrawer(Gravity.LEFT);
            }
        });*/

        nav=(NavigationView)findViewById(R.id.left_menu);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.user_profile_pic:
                        f=new user_profile_fragment();

                        break;
                    case R.id.auct_panel:
                        f=new auction_panel_fragment();
                        break;
                    case R.id.logout:
                        logout();
                }
                d.closeDrawer(GravityCompat.START);
                return loadFragment(f);
            }
        });



        bottomappbar = (BottomNavigationView) findViewById(R.id.btm_view);

        bottomappbar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.home:
                                f = new home_screen_fragment();
                                /*viewPager.setCurrentItem(0);*/
                                break;
                            case R.id.search:
                                f = new search_tab_fragment();
                                /*viewPager.setCurrentItem(1);*/
                                break;
                            case R.id.camera:
                                f= new camera();
                                bottomappbar.setVisibility(View.GONE);
                                break;
                            case R.id.add_picture:
                                startActivity(new Intent(side_menu.this, upload_picture.class));
                            case R.id.message_box:
                                f= new Message();
                                break;
                        }
                        return loadFragment(f);
                    }
                }
        );

        if(savedInstanceState==null)
        {
            bottomappbar.setSelectedItemId(R.id.home);
        }


        header= nav.getHeaderView(0);
        name=(TextView) header.findViewById(R.id.current_name);
        profile=(CircularImageView) header.findViewById(R.id.prof_pic);
        auth=FirebaseAuth.getInstance();
        String uid=auth.getUid().toString();

        db=FirebaseFirestore.getInstance();
        db.collection("USERS").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username=documentSnapshot.getString("username");
                String profile_pic=documentSnapshot.getString("url").toString();
                Log.d("name",username);
                Glide.with(side_menu.this).load(profile_pic).into(profile);
                name.setText(username);
            }
        });

        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if(m!=null)
                {
                    m.setChecked(false);
                }
                else
                {
                    bottomappbar.getMenu().getItem(0).setChecked(false);
                }
                bottomappbar.getMenu().getItem(position).setChecked(true);
                m=bottomappbar.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
    }*/

   /* private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        f1=new home_screen_fragment();
        f2=new search_tab_fragment();
        f11=new user_profile_fragment();

        adapter.addFragment(f1);
        adapter.addFragment(f2);
        adapter.addFragment(f11);

        viewPager.setAdapter(adapter);
    }
*/
    }
    public BottomNavigationView getNav()
    {
        return bottomappbar;
    }
    private boolean loadFragment(Fragment fragment)
    {
        if(fragment!=null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,fragment).addToBackStack(null).commit();
            return true;
        }
        return false;
    }
    private void logout ()
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(side_menu.this, sign_in.class));
        finish();
    }
}
